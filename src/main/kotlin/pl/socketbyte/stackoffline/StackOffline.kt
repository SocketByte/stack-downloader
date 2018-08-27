package pl.socketbyte.stackoffline

import pl.socketbyte.stackoffline.api.SortType
import pl.socketbyte.stackoffline.api.StackFetcher

fun main(args: Array<String>) {
    println("Starting stack fetching for tag ${args[2]}, with sort type of ${args[3]} and pagesize of ${args[4]}...")
    val fetcher = StackFetcher(args[2], SortType.valueOf(args[3]), args[4].toInt())

    fetcher.connect()
    fetcher.fetchAll(args[0], args[1])
}