package edu.temple.flossplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookListViewModel : ViewModel() {

    private var books = BookList()

    fun getBooks(): BookList {
        return books
    }

    fun setBooks(givenBooks: BookList){
        books = givenBooks
    }

}