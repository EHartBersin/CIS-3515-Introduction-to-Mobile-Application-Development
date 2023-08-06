package edu.temple.imagedisplayapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageViewModel : ViewModel() {

    private val selectedImage : MutableLiveData<Int> by lazy {
        MutableLiveData<Int>()
    }

    private val selectedName : MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getImage () : LiveData<Int> {
        return selectedImage
    }

    fun setImage (image: Int) {
        selectedImage.value = image
    }

    fun getName () : LiveData<String> {
        return selectedName
    }

    fun setName (name: String) {
        selectedName.value = name
    }

}