package edu.temple.flossplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.lifecycle.ViewModelProvider
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.android.volley.Request.Method
import com.android.volley.toolbox.JsonArrayRequest
import android.app.SearchManager

class MainActivity : AppCompatActivity() {

    private val isSingleContainer: Boolean by lazy {
        findViewById<View>(R.id.container2) == null
    }

    private val bookViewModel: BookViewModel by lazy {
        ViewModelProvider(this)[BookViewModel::class.java]
    }

    private var bookList = BookList()
    private lateinit var button: ImageButton
    private lateinit var requestQueue: RequestQueue
    private lateinit var url: String
    //private var bookListAdapter = BookListFragment.BookListAdapter(bookList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bookViewModel.setBookList(bookList)

        //setting up the request queue
        //when button clicked, do onSearchRequested
        //send intent to intent handler function
        requestQueue = Volley.newRequestQueue(this)

        button = findViewById(R.id.button)
        button.setOnClickListener {
            onSearchRequested()
        }
        //Log.d("testing","${intent.getStringExtra(SearchManager.QUERY)}")
        onNewIntent(intent)

        // If we're switching from one container to two containers
        // clear BookPlayerFragment from container1
        if (supportFragmentManager.findFragmentById(R.id.container1) is BookPlayerFragment) {
            supportFragmentManager.popBackStack()
        }

        // If this is the first time the activity is loading, go ahead and add a BookListFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container1, BookListFragment())
                .commit()
        } else
        // If activity loaded previously, there's already a BookListFragment
        // If we have a single container and a selected book, place it on top
            if (isSingleContainer && bookViewModel.getSelectedBook()?.value != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container1, BookPlayerFragment())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit()
            }

        // If we have two containers but no BookPlayerFragment, add one to container2
        if (!isSingleContainer && supportFragmentManager.findFragmentById(R.id.container2) !is BookPlayerFragment)
            supportFragmentManager.beginTransaction()
                .add(R.id.container2, BookPlayerFragment())
                .commit()


        // Respond to selection in portrait mode using flag stored in ViewModel
        bookViewModel.getSelectedBook()?.observe(this) {
            if (!bookViewModel.hasViewedSelectedBook()) {
                if (isSingleContainer) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container1, BookPlayerFragment())
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit()
                }
                bookViewModel.markSelectedBookViewed()
            }
        }
    }

    override fun onBackPressed() {
        // BackPress clears the selected book
        bookViewModel.clearSelectedBook()
        super.onBackPressed()
    }

    //intent handler functions
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    //handles the intent by taking the user search information and adding that to url
    //sends the entire url to the doSearch
    private fun handleIntent(intent: Intent){
        url = "https://kamorris.com/lab/flossplayer/search.php?query="
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                doSearch(url+query)
                //Log.d("testing", "$query")
            }
        }
    }

    //takes the url, request data, and sends that data to the view model
    //view model has function that will loop through the JSON array and populate the book list itself
    private fun doSearch(url : String) {
        //Log.d("testing", url)
        requestQueue.add(
            JsonArrayRequest(Method.GET, url, null, {
                //Log.d("testing", it.toString())
                bookViewModel.populateBookList(it)
            }, {})
        )
    }
}
