import java.awt.Color
import javax.swing.JFrame
import javax.swing.SwingUtilities

object Program {

    @JvmStatic
    fun main(args: Array<String>) {
        SwingUtilities.invokeLater {
            val content = Viewport()
            val frame = JFrame("Sutherland-Hodgman - Homogeneous Clipping Algorithm")
            frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            frame.background = Color.BLACK
            frame.contentPane.add(content)
            frame.pack()
            frame.isResizable = false
            frame.setLocationRelativeTo(null)
            frame.isVisible = true
        }
    }
}
