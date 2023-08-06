package edu.temple.imagedisplayapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider

class DisplayFragment : Fragment() {

    private lateinit var canvas: View
    private lateinit var textView: TextView

    private lateinit var imageViewModel: ImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageViewModel = ViewModelProvider(requireActivity())[ImageViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_display, container, false).also {
            canvas = it.findViewById(R.id.canvas)
            textView = it.findViewById(R.id.textView)
            textView.text = ""
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageViewModel.getName().observe(requireActivity()){
            changeText(it)
        }
        imageViewModel.getImage().observe(requireActivity()){
            changeImage(it)
        }

    }

    fun changeImage (image: Int) {
        canvas.setBackgroundResource(image)
    }

    fun changeText(name: String){
        textView.text = name
    }

}