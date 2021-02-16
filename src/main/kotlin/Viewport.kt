import java.awt.*
import javax.swing.JPanel
import javax.swing.Timer

class Viewport : JPanel() {
    private val clipper = Clipper()
    private val camera: Camera
    private val mesh: Mesh

    @Override
    override fun paintComponent(g: Graphics) {
        g as Graphics2D
        g.background = Color(23, 39, 60)
        g.clearRect(0, 0, width, height)

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)

        g.stroke = BasicStroke(0.75f)
        mesh.renderMesh(g, camera, clipper, size)
    }

    init {
        size = Dimension(1440, 900)
        preferredSize = size
        background = Color.BLACK

        camera = Camera(Math.PI / 4.0, width.toDouble() / height.toDouble(), 120.0, 170.0)
        camera.position = Vector3(0.0, 10.0, 150.0)
        camera.target = Vector3(0.0, 10.0, 0.0)

        mesh = Mesh.load("beethoven.obj", 10.0)

        Timer(16) { repaint() }.start()
    }
}
