package edu.temple.flossplayer

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.InvalidObjectException
import java.io.Serializable

class BookList : Serializable{

    // change to var so that the variable can be rewritten
    private var bookList = ArrayList<Book>()

    fun add(book: Book) {
        bookList.add(book)
    }

    fun remove (book: Book) {
        bookList.remove(book)
    }

    fun clear() {
        bookList.clear()
    }

    operator fun get(index: Int) = bookList[index]

    fun size() = bookList.size

    // get the booklist in array list of books
    fun getBookListValue(): ArrayList<Book>{
        return bookList
    }

    // update the booklist with saved value
    fun updateBookListValue(books : ArrayList<Book>){
        this.bookList = books
    }
}