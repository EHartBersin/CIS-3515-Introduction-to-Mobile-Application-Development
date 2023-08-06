package edu.temple.imagedisplayapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class SelectionFragment : Fragment() {

    private lateinit var images: Array<Item>

    private val IMAGE_KEY = "images"

    private lateinit var recyclerView: RecyclerView

    private lateinit var imageViewModel : ImageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //get the view model object
        imageViewModel = ViewModelProvider(requireActivity())[(ImageViewModel::class.java)]

        arguments?.let {
            it.getParcelableArray(IMAGE_KEY)?.let{
                images = it as Array<Item>
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_selection, container, false).also {
            recyclerView = it.findViewById(R.id.recyclerView)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = { image: Int, name: String -> imageViewModel.setImage(image); imageViewModel.setName(name); (requireActivity() as ImageSelectedInterface).imageSelected(image, name)
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = ImageAdapter(images, callback)


    }

    companion object {

        fun newInstance(images: Array<Item>) =
            SelectionFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArray(IMAGE_KEY, images)
                }
            }
    }

    interface ImageSelectedInterface {
        fun imageSelected(image: Int, name: String)
    }
}