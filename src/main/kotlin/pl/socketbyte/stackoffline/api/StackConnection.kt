package pl.socketbyte.stackoffline.api

import com.gargoylesoftware.htmlunit.*
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.html.HTMLParserListener
import java.net.MalformedURLException
import org.w3c.css.sac.CSSException
import org.w3c.css.sac.CSSParseException
import org.apache.commons.logging.LogFactory
import org.w3c.css.sac.ErrorHandler
import java.net.URL
import java.util.logging.Level


class StackConnection(private val address: String) {

    lateinit var client: WebClient

    fun connect() {
        client = WebClient()
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")

        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").level = Level.OFF
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").level = Level.OFF

        client.isCssEnabled = false

        client.setIncorrectnessListener { _, _ -> }
        client.cssErrorHandler = object : ErrorHandler {

            override fun warning(exception: CSSParseException) {
            }

            override fun fatalError(exception: CSSParseException) {
            }

            override fun error(exception: CSSParseException) {
            }
        }
        client.htmlParserListener = object : HTMLParserListener {

            override fun warning(arg0: String, arg1: URL, arg2: Int, arg3: Int, arg4: String) {
            }

            override fun error(arg0: String, arg1: URL, arg2: Int, arg3: Int, arg4: String) {
            }
        }

        client.isThrowExceptionOnFailingStatusCode = false
        client.isThrowExceptionOnScriptError = false
    }

    fun redirect(url: String): HtmlPage {
        return client.getPage(url)
    }

}