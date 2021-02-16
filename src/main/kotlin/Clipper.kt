class Clipper {
    private var src = Array(8) { Vector4(0.0, 0.0, 0.0, 0.0) }
    private var dst = Array(8) { Vector4(0.0, 0.0, 0.0, 0.0) }

    fun process(vertices: Array<Vector4>) = when {
        isCompletelyInside(vertices) -> vertices
        isCompletelyOutside(vertices) -> emptyArray()
        else -> clip(vertices)
    }

    private fun clip(vertices: Array<Vector4>): Array<Vector4> {
        var dstCount = 0
        var srcCount = vertices.size
        System.arraycopy(vertices, 0, src, 0, vertices.size)
        for (boundary in 0..5) {
            val normal = NORMALS[boundary]
            var v = 0
            while (v < srcCount) {
                val a1 = src[v]
                val a2 = src[++v % srcCount]
                val sectA1 = intersection(a1, normal)
                val sectA2 = intersection(a2, normal)
                when (determineCase(sectA1, sectA2)) {
                    INSIDE -> dst[dstCount++] = a2
                    LEAVING -> dst[dstCount++] = Vector4.lerp(a1, a2, sectA1 / (sectA1 - sectA2))
                    ENTERING -> {
                        dst[dstCount++] = Vector4.lerp(a1, a2, sectA1 / (sectA1 - sectA2))
                        dst[dstCount++] = a2
                    }
                }
            }
            if (dstCount < 3) return emptyArray()
            val temp = src
            src = dst
            dst = temp
            srcCount = dstCount
            dstCount = 0
        }
        return src.copyOfRange(0, srcCount)
    }

    companion object {
        private const val INSIDE = 1
        private const val LEAVING = 2
        private const val OUTSIDE = 3
        private const val ENTERING = 4

        private val NORMALS = arrayOf(
            Vector4(0.0, 0.0, -1.0, 0.0),
            Vector4(0.0, 0.0, 1.0, 1.0),
            Vector4(0.0, -1.0, 0.0, 1.0),
            Vector4(0.0, 1.0, 0.0, 1.0),
            Vector4(-1.0, 0.0, 0.0, 1.0),
            Vector4(1.0, 0.0, 0.0, 1.0)
        )

        private fun determineCase(s: Double, f: Double) = when {
            s < 0.0 -> if (f < 0.0) INSIDE else LEAVING
            else -> if (f < 0.0) ENTERING else OUTSIDE
        }

        private fun isCompletelyInside(vertices: Array<Vector4>): Boolean {
            for (vertex in vertices) {
                if (vertex.z < 0.0 || vertex.z > vertex.w) return false
                if (vertex.y < -vertex.w || vertex.y > vertex.w) return false
                if (vertex.x < -vertex.w || vertex.x > vertex.w) return false
            }
            return true
        }

        private fun isCompletelyOutside(vertices: Array<Vector4>): Boolean {
            var xnc = 0
            var xpc = 0
            var ync = 0
            var ypc = 0
            var znc = 0
            var zpc = 0
            for (vertex in vertices) {
                if (vertex.z < 0.0) ++znc
                if (vertex.z > vertex.w) ++zpc
                if (vertex.y < -vertex.w) ++ync
                if (vertex.y > vertex.w) ++ypc
                if (vertex.x < -vertex.w) ++xnc
                if (vertex.x > vertex.w) ++xpc
            }
            return znc == vertices.size || zpc == vertices.size ||
                   ync == vertices.size || ypc == vertices.size ||
                   xnc == vertices.size || xpc == vertices.size
        }

        private fun intersection(v: Vector4, normal: Vector4): Double {
            val x = normal.x * (v.x - v.w * normal.x * normal.w)
            val y = normal.y * (v.y - v.w * normal.y * normal.w)
            val z = normal.z * (v.z - v.w * normal.z * normal.w)
            return x + y + z
        }
    }
}
