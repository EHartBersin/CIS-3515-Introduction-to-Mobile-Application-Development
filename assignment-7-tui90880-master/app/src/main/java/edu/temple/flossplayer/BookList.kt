package edu.temple.flossplayer

class BookList {

    private var books = arrayListOf<Book>()

    fun add(book: Book){
        books.add(book)
    }

    fun remove(book: Book){
        books.remove(book)
    }

    fun get(int: Int): Book {
        return books[int]
    }

    fun size(): Int {
        val size = books.size;
        return size
    }
}