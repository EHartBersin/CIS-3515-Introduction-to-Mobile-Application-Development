package edu.temple.flossplayer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BookControlFragment : Fragment() {

    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var bookViewModel : BookViewModel
    private lateinit var statusViewModel : ButtonsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_book_control, container, false).apply {
            playButton = findViewById(R.id.playButton)
            pauseButton = findViewById(R.id.pauseButton)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookViewModel = ViewModelProvider(requireActivity())[BookViewModel::class.java]
        statusViewModel = ViewModelProvider(requireActivity())[ButtonsViewModel::class.java]

        playButton.setOnClickListener(){
            statusViewModel.playBook()
        }

        pauseButton.setOnClickListener(){
            statusViewModel.pauseBook()
        }

    }


}