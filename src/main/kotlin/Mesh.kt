import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.geom.Path2D
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.*

class Mesh(private val vertices: Array<Vector3>, private val polygons: Array<Polygon>) {

    fun renderMesh(g2d: Graphics2D, camera: Camera, clipper: Clipper, size: Dimension) {
        val transform = Matrix4.createRotationY(System.nanoTime() / 2000000000.0) *
                        Matrix4.createRotationX(System.nanoTime() / 1000000000.0) *
                        camera.computeTransform()

        for (polygon in polygons) {
            val vertices = polygon.indices.map { this.vertices[it] }.toTypedArray()
            val clipped = clipper.process(vertices.map { transform * Vector4(it) }.toTypedArray())
            if (clipped.isNotEmpty()) {
                val projected = clipped.map { it / it.w }.toTypedArray()
                g2d.color = computeColor(projected, polygon)
                val path = Path2D.Double()
                for (vertex in projected) {
                    val x = size.width * (vertex.x + 1.0) * 0.5
                    val y = size.height * (vertex.y + 1.0) * 0.5
                    if (vertex === projected[0]) {
                        path.moveTo(x, size.height - y)
                    } else {
                        path.lineTo(x, size.height - y)
                    }
                }
                path.closePath()
                g2d.draw(path)
            }
        }
    }

    private fun computeColor(projected: Array<Vector4>, polygon: Polygon): Color {
        val c = (64 + (1.0 - projected[0].z) * 512).coerceIn(0.0, 255.0).toInt()
        return if (projected.size != polygon.indices.size) Color(0, c, 0) else Color(c, 0, c / 6)
    }

    companion object {

        @JvmStatic
        fun load(filename: String, scale: Double): Mesh {
            var cog = Vector3(0.0, 0.0, 0.0)
            val vertices = mutableListOf<Vector3>()
            val polygons = mutableListOf<Polygon>()
            val resource = Mesh::class.java.classLoader.getResource(filename)
            val file = File(Objects.requireNonNull(resource).file)
            BufferedReader(InputStreamReader(FileInputStream(file))).use {
                var line = it.readLine()
                while (line != null) {
                    if (line.startsWith("v ")) {
                        val data = line.split(" ").toTypedArray()
                        vertices.add(Vector3(data[1].toDouble(), data[2].toDouble(), data[3].toDouble()) * scale)
                        cog += vertices.last()
                    }
                    if (line.startsWith("f ")) {
                        val split = line.split(" ").toTypedArray()
                        val indices = Arrays.stream(split).skip(1).mapToInt { s: String -> s.split("/").toTypedArray()[0].toInt() - 1 }.toArray()
                        polygons.add(Polygon(indices))
                    }
                    line = it.readLine()
                }
            }
            cog /= vertices.size.toDouble()
            return Mesh(vertices.map { it - cog }.toList().toTypedArray(), polygons.toTypedArray())
        }
    }
}
