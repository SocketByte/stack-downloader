package pl.socketbyte.stackoffline.ui

import com.sun.javafx.application.PlatformImpl
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.layout.AnchorPane
import javafx.scene.web.WebView
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JScrollPane
import java.io.IOException
import javax.swing.JEditorPane
import javafx.scene.web.WebEngine
import javafx.embed.swing.JFXPanel
import javax.swing.SwingUtilities








fun main(args: Array<String>) {
    SwingUtilities.invokeLater(StackViewer())
}

class StackViewer : Runnable {

    private var webEngine: WebEngine? = null

    fun loadURL(url: String) {
        Platform.runLater { webEngine!!.load(url) }
    }

    override fun run() {
        // setup UI
        val frame = JFrame()
        frame.isVisible = true
        frame.preferredSize = Dimension(1024, 600)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        val jfxPanel = JFXPanel()
        frame.contentPane.add(jfxPanel)
        frame.pack()

        Platform.runLater {
            val view = WebView()
            webEngine = view.engine

            jfxPanel.scene = Scene(view)
        }



        loadURL("http://www.google.com")
    }

}