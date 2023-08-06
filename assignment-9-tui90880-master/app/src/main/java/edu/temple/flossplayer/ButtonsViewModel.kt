package edu.temple.flossplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ButtonsViewModel : ViewModel() {

    //true for playing, false for paused
    private val bookStatus: MutableLiveData<Boolean>? by lazy{
        MutableLiveData()
    }

    val status: MutableLiveData<Boolean>? get() = bookStatus

    fun getStatus(): LiveData<Boolean>? {
        return bookStatus
    }

    fun playBook(){
        this.bookStatus?.value = true
    }

    fun pauseBook(){
        this.bookStatus?.value = false
    }
}