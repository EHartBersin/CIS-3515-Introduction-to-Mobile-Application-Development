package edu.temple.flossplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookViewModel : ViewModel() {

    private val book: MutableLiveData<Book> by lazy{
        MutableLiveData<Book>()
    }

    //getter
    fun getBook(): LiveData<Book> {
        return book
    }

    //setter
    fun setBook(selectedBook: Book?){
        this.book.value = selectedBook
    }

}