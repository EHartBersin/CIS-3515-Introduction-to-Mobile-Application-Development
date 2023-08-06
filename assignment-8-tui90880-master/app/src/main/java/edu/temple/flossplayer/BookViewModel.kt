package edu.temple.flossplayer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.json.JSONArray

class BookViewModel : ViewModel() {

    private val selectedBook: MutableLiveData<Book>? by lazy {
        MutableLiveData()
    }

    private val bookList: MutableLiveData<BookList> by lazy {
        MutableLiveData()
    }

    // Flag to determine if one-off event should fire
    private var viewedBook = false

    fun getSelectedBook(): LiveData<Book>? {
        return selectedBook
    }

    fun setSelectedBook(selectedBook: Book) {
        viewedBook = false
        this.selectedBook?.value = selectedBook
    }

    fun clearSelectedBook () {
        selectedBook?.value = null
    }

    fun markSelectedBookViewed () {
        viewedBook = true
    }

    fun hasViewedSelectedBook() : Boolean {
        return viewedBook
    }

    fun getBookList(): LiveData<BookList> {
        return bookList
    }

    fun setBookList(bookList: BookList) {
        this.bookList.value = bookList
    }

    //takes in the JSON array recieved from the search
    //iterates through it and creates a book object for each object
    //add that book object to a temp book list
    //when complete,
    fun populateBookList(JSONArray: JSONArray){
        val newBookList = BookList()
        //Log.d("testing", JSONArray.toString())
        for (i in 0 until JSONArray.length()) {
            val title = JSONArray.getJSONObject(i).getString("book_title")
            val author = JSONArray.getJSONObject(i).getString("author_name")
            val id = JSONArray.getJSONObject(i).getInt("book_id")
            val coverURI = JSONArray.getJSONObject(i).getString("cover_uri")
            val book = Book(title, author, id, coverURI)
            newBookList.add(book)
            //Log.d("testing", JSONArray.getJSONObject(i).toString())
        }
        setBookList(newBookList)
    }

}