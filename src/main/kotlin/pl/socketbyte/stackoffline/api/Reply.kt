package pl.socketbyte.stackoffline.api

data class Reply(val author: String, val votes: Int, val content: String) {

    val comments = mutableListOf<Comment>()

}