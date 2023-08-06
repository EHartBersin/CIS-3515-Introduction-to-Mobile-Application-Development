package edu.temple.flossplayer

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(), BookListFragment.EventInterface{

    private lateinit var bookViewModel: BookViewModel
    private lateinit var bookListViewModel: BookListViewModel

    var landscape = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //check if in landscape or portrait
        landscape = findViewById<View>(R.id.container2) != null

        //setting up view model
        bookViewModel = ViewModelProvider(this)[BookViewModel::class.java]
        bookListViewModel = ViewModelProvider(this)[BookListViewModel::class.java]


        //popping stack if book was previously selected, but user cleared selection
        if (supportFragmentManager.findFragmentById(R.id.container1) is BookPlayerFragment && (bookViewModel.getBook().value == null))
            supportFragmentManager.popBackStack()

        //remove extra fragment if switching orientation
        if (supportFragmentManager.findFragmentById(R.id.container1) is BookPlayerFragment && landscape)
            supportFragmentManager.popBackStack();

        //first time starting activity
        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container1, BookListFragment())
                .commit()
        }

        if (landscape) {
            Log.d("mylog",landscape.toString())
            if (supportFragmentManager.findFragmentById(R.id.container2) == null)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container2, BookPlayerFragment())
                    .commit()
        } else if(bookViewModel.getBook().value != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container1, BookPlayerFragment())
                .addToBackStack(null)
                .commit()
        }

        //setting up book list
        val bookList = BookList()
        bookList.add(Book("The Great Gatsby", "F. Scott Fitzgerald"))
        bookList.add(Book("1984", "George Orwell"))
        bookList.add(Book("Frankenstein", "Mary Shelley"))
        bookList.add(Book("Atlas Shrugged", "Ayn Rand"))
        bookList.add(Book("Fahrenheit 451","Ray Bradbury"))
        bookList.add(Book("Animal Farm","George Orwell"))
        bookList.add(Book("The Catcher in the Rye","J. D. Salinger"))
        bookList.add(Book("Of Mice and Men","John Steinbeck"))
        bookList.add(Book("To Kill a Mocking Bird","Harper Lee"))
        bookList.add(Book("The Odyssey","Homer"))
        bookListViewModel.setBooks(bookList)

    }

    //function for when book is selected
    override fun bookSelected(){
        if(!landscape) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container1, BookPlayerFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        supportFragmentManager.popBackStack()
    }

}