package edu.temple.flossplayer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BookListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var bookViewModel : BookViewModel
    private lateinit var bookListViewModel : BookListViewModel

    private lateinit var books: BookList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bookViewModel = ViewModelProvider(requireActivity())[BookViewModel::class.java]
        bookListViewModel = ViewModelProvider(requireActivity())[BookListViewModel::class.java]

        books = bookListViewModel.getBooks()

        var book = books.get(0)
        Log.d("Books", book.title)

        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_list, container, false).also {
            recyclerView = it.findViewById(R.id.recyclerView)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = { book: Book -> bookViewModel.setBook(book); (requireActivity() as EventInterface).bookSelected()
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        recyclerView.adapter = BookAdapter(books, callback)
    }

    interface EventInterface{
        fun bookSelected()
    }

}