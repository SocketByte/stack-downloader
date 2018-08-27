package pl.socketbyte.stackoffline.api

enum class SortType(val urlp: String) {
    SORTED("sorted"),
    FEATURED("featured"),
    FREQUENT("frequent"),
    VOTES("votes"),
    ACTIVE("active")
}