package pl.socketbyte.stackoffline.io

import java.io.File

object StackLogger {

    private val file = File("log.txt")

    init {
        if (!file.exists())
            file.createNewFile()
    }

    fun log(text: String) {
        println(text)

        file.appendText(text + "\n")
    }

}