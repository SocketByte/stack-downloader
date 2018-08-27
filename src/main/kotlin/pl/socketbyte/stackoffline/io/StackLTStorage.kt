package pl.socketbyte.stackoffline.io

import com.google.gson.GsonBuilder
import pl.socketbyte.stackoffline.api.Question
import java.io.File

/**
 * Stack Long-Term Storage
 * Saves processed questions into files to fetch in the future
 */
class StackLTStorage {

    private val directory = File("data")
    private val gson = GsonBuilder().setPrettyPrinting().create()

    private val tempCache = File(directory, "cache.tmp")

    init {
        directory.mkdirs()
        tempCache.createNewFile()
    }

    fun saveCache(page: Int, pageMax: Int) {
        tempCache.writeText(page.toString() + "\n" + pageMax.toString())
    }

    fun getPage(): Int {
        if (tempCache.readText().isEmpty())
            return 1

        val text = tempCache.readLines()
        return text[0].toInt()
    }

    fun getPageMax(): Int {
        if (tempCache.readText().isEmpty())
            return -1

        val text = tempCache.readLines()
        return text[1].toInt()
    }

    fun save(question: Question) {
        val file = File(directory, question.id.toString() + ".json")
        file.createNewFile()

        val json = gson.toJson(question)
        file.writeText(json)
    }

    /**
     * Can take some time!
     */
    fun get(): Map<String, Question> {
        val questions = mutableMapOf<String, Question>()
        for ((index, file) in directory.listFiles().withIndex()) {
            if (file.name.endsWith(".tmp"))
                continue

            val json = file.readText()
            val question = gson.fromJson<Question>(json, Question::class.java)

            print("Loading question ${question.id} ($index / ${directory.listFiles().size})\r")

            questions[question.title] = question
        }
        return questions
    }

}