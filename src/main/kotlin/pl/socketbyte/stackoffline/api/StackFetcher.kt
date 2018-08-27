package pl.socketbyte.stackoffline.api

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.*
import com.gargoylesoftware.htmlunit.javascript.host.HTMLAnchorElement
import com.gargoylesoftware.htmlunit.javascript.host.HTMLDivElement
import com.gargoylesoftware.htmlunit.javascript.host.HTMLUListElement
import pl.socketbyte.stackoffline.TimeUtil
import pl.socketbyte.stackoffline.io.StackLTStorage
import pl.socketbyte.stackoffline.io.StackLogger
import java.util.concurrent.TimeUnit
import java.io.FileOutputStream
import java.io.BufferedOutputStream
import java.io.PrintStream



const val loginUrl = "https://stackoverflow.com/users/login?ssrc=head&returnurl=https%3a%2f%2fstackoverflow.com%2f"
const val questionsUrl = "https://stackoverflow.com/questions/tagged/{tag}?page={page}&sort={sort}&pagesize={pageSize}"
const val baseUrl = "https://stackoverflow.com/"


class StackFetcher(private val tag: String,
                   private val sortType: SortType,
                   private val pageSize: Int) {

    private val storage = StackLTStorage()
    private val connection = StackConnection(baseUrl)
    private var questions = mutableMapOf<String, Question>()

    fun connect() {
        println("Loading fetched questions...")
        questions = storage.get().toMutableMap()
        println("Connecting to the StackOverflow...       ")
        connection.connect()
        println()
    }

    fun authorize(login: String, password: String) {
        val page = connection.redirect(loginUrl)

        val inputLogin = page.getHtmlElementById<HtmlInput>("email")
        val inputPass = page.getHtmlElementById<HtmlInput>("password")

        inputLogin.type(login)
        inputPass.type(password)

        val button = page.getHtmlElementById<HtmlButtonInput>("submit-button")
        button.click<HtmlPage>()
    }

    fun fetchAll(login: String, password: String, startFrom: Int = storage.getPage()) {
        iteration = startFrom

        println("Authorizing to StackOverflow...")
        authorize(login, password)
        println()

        println("[>>> Starting Fetch Operation <<<]")
        println("WARNING: Slow processing times are not because of application performance.")
        println("They are performed artifically to prevent `Too many requests` error on StackOverflow.")
        println()
        println()

        startR = System.currentTimeMillis()
        while (true) {
            fetch(iteration)
        }
    }

    var startR = 0L
    var iteration = 1
    var allQuestions = "-1"
    var timeLeftAll = 0L
    var timeLeftIt = 0L
    fun fetch(pageNumber: Int) {
        val page = connection.redirect(replaceAll(tag, sortType, pageNumber, pageSize))
        val qList = page.getElementsByIdAndOrName("questions")

        try {
            if (allQuestions == "-1" && pageNumber == 1) {
                val mainbar = page.getHtmlElementById<HtmlDivision>("mainbar")
                allQuestions = mainbar
                        .childElements.elementAt(6)
                        .childElements.elementAt(6)
                        .childElements.elementAt(0).textContent
                storage.saveCache(pageNumber, allQuestions.toInt())
            }
        } catch (e: Exception) {}

        StackLogger.log("[============> Fetch Iteration $iteration <============]              ")
        StackLogger.log("Questions to fetch: $pageSize")
        StackLogger.log("Page: $pageNumber / ${storage.getPageMax()}")
        StackLogger.log("Time left: ${formatInterval(timeLeftAll)}")
        StackLogger.log("")

        storage.saveCache(pageNumber, storage.getPageMax())

        iteration++
        var index = 0
        for (questionTable in qList) {
            val start = System.currentTimeMillis()
            for (question in questionTable.childElements) {
                try {
                    index++

                    val id = question.id.replace("question-summary-", "").toLong()
                    val contentPage = connection.redirect("$baseUrl/questions/$id")
                    val header = contentPage.getHtmlElementById<HtmlDivision>("question-header")

                    val title = header.childElements.elementAt(0).childElements.elementAt(0).textContent

                    Thread.sleep(50)
                    // Check if the question was added already
                    if (questions.containsKey(title))
                        continue

                    val main = contentPage.getHtmlElementById<HtmlDivision>("mainbar")
                    val author = main
                            .childElements.elementAt(0)
                            .childElements.elementAt(0)
                            .childElements.elementAt(1)
                            .childElements.elementAt(2)
                            .childElements.elementAt(2)
                            .childElements.elementAt(0)
                            .childElements.elementAt(2)
                            .childElements.elementAt(0).textContent
                    val votes = main
                            .childElements.elementAt(0)
                            .childElements.elementAt(0)
                            .childElements.elementAt(0)
                            .childElements.elementAt(0)
                            .childElements.elementAt(2).textContent.toInt()
                    print("Processing question (${String.format("%02d", index)} / $pageSize)   Time left: ${formatInterval(timeLeftIt)}                        \r")

                    val questionData = Question(id, author, votes, title)

                    Thread.sleep(450)

                    val questionContent = main.getElementById<HtmlDivision>("question")
                    val answersContent = main.getElementById<HtmlDivision>("answers")

                    var builder = StringBuilder()
                    val content = questionContent.childElements.elementAt(0).childElements.elementAt(1).childElements.elementAt(0)
                    for (p in content.childElements)
                        builder.append(p.textContent).append("\n")
                    questionData.content = builder.toString()

                    val comments = questionContent
                            .childElements.elementAt(0)
                            .childElements.elementAt(2)
                            .childElements.elementAt(0)
                            .childElements.elementAt(0)
                    for (comment in comments.childElements) {
                        val commentContent = comment.childElements.elementAt(1).childElements.elementAt(0)
                        val commentText = commentContent.childElements.elementAt(0).textContent
                        val commentAuthor = commentContent.childElements.elementAt(1).textContent

                        val commentData = Comment(commentAuthor, commentText)

                        questionData.comments.add(commentData)
                    }

                    for (answer in answersContent.childElements) {
                        if (!answer.hasAttribute("data-answerid"))
                            continue

                        val answerContent = answer
                                .childElements.elementAt(0)
                                .childElements.elementAt(1)
                        val answerAuthor = answerContent
                                .childElements.elementAt(1)
                                .childElements.elementAt(1)
                                .childElements.elementAt(0)
                                .childElements.elementAt(2)
                                .childElements.elementAt(0).textContent
                        val answerVotes = answer
                                .childElements.elementAt(0)
                                .childElements.elementAt(0)
                                .childElements.elementAt(0)
                                .childElements.elementAt(2).textContent.toInt()

                        builder = StringBuilder()
                        for (a in answerContent.childElements.elementAt(0).childElements) {
                            builder.append(a.textContent).append("\n")
                        }
                        val reply = Reply(answerAuthor, answerVotes, builder.toString())

                        val answerComments = answer
                                .childElements.elementAt(0)
                                .childElements.elementAt(2)
                                .childElements.elementAt(0)
                                .childElements.elementAt(0)
                        for (c in answerComments.childElements) {
                            val commentContents = c.childElements.elementAt(1).childElements.elementAt(0)
                            val commentText = commentContents.childElements.elementAt(0).textContent
                            val commentAuthor = commentContents.childElements.elementAt(1).textContent

                            val comment = Comment(commentAuthor, commentText)
                            reply.comments.add(comment)
                        }

                        questionData.replies.add(reply)
                    }
                    storage.save(questionData)
                } catch (e: Exception) {

                }
                val stop = System.currentTimeMillis()
                val elapsedTime = stop - start
                val elapsedTimeAll = stop - startR
                val estimatedTime = (elapsedTime.toDouble() * pageSize.toDouble() / if (index.toDouble() == 0.0) 1.0 else index.toDouble()).toLong() - elapsedTime
                timeLeftIt = estimatedTime

                val estimatedTimeAll = (elapsedTimeAll.toDouble() * storage.getPageMax().toDouble() / if (pageNumber.toDouble() == 0.0) 1.0 else pageNumber.toDouble()).toLong() - elapsedTimeAll
                timeLeftAll = estimatedTimeAll

            }
        }
    }

    fun formatInterval(l: Long): String {
        return TimeUtil().toYYYYHHmmssS(l)
    }

    companion object {
        fun replaceAll(tag: String, sortType: SortType, page: Int, pageSize: Int): String {
            return questionsUrl
                    .replace("{tag}", tag)
                    .replace("{page}", page.toString())
                    .replace("{sort}", sortType.urlp)
                    .replace("{pageSize}", pageSize.toString())
        }
    }

}