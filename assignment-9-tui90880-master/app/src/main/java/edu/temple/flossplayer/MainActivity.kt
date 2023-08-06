package edu.temple.flossplayer

import android.app.SearchManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import edu.temple.audlibplayer.PlayerService

class MainActivity : AppCompatActivity() {

    var nowPlayingText = ""
    private val NOW_PLAYING = "playing"

    private lateinit var seekBar: SeekBar
    private lateinit var nowPlaying: TextView


    private var isDrag = false
    private var change = 0
    private var startPoint = 0
    private var endPoint = 0

    lateinit var binder: PlayerService.MediaControlBinder
    var isConnected = false

    private val binderHandler = Handler(Looper.getMainLooper()) {
        val progress: PlayerService.BookProgress = it.obj as PlayerService.BookProgress
        val currProgress = progress.progress
        val bookDuration = bookViewModel.getSelectedBook()?.value?.duration

        val position = ((currProgress.toDouble()/ bookDuration!!)*100).toInt()
        //Log.d("mylog", position.toString())

        seekBar.setProgress(position, false)
        true
    }

    val serviceConnection = object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isConnected = true
            binder = service as PlayerService.MediaControlBinder
            binder.setProgressHandler(binderHandler)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConnected = false
        }

    }

    private val searchURL = "https://kamorris.com/lab/flossplayer/search.php?query="

    private val requestQueue : RequestQueue by lazy {
        Volley.newRequestQueue(this)
    }

    private val isSingleContainer : Boolean by lazy{
        findViewById<View>(R.id.container2) == null
    }

    private val bookViewModel : BookViewModel by lazy {
        ViewModelProvider(this)[BookViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seekBar = findViewById(R.id.seekBar)
        nowPlaying = findViewById(R.id.nowPlayingText)

        bindService(Intent(this, PlayerService::class.java)
            , serviceConnection
            , BIND_AUTO_CREATE
        )
        val intent = Intent(this, PlayerService::class.java)
        startService(intent)

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
        } else {
            // If activity loaded previously, there's already a BookListFragment
            // If we have a single container and a selected book, place it on top
            nowPlayingText = savedInstanceState.getString(NOW_PLAYING, "")
            if (isSingleContainer && bookViewModel.getSelectedBook()?.value != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container1, BookPlayerFragment())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // If we have two containers but no BookPlayerFragment, add one to container2
        if (!isSingleContainer && supportFragmentManager.findFragmentById(R.id.container2) !is BookPlayerFragment)
            supportFragmentManager.beginTransaction()
                .add(R.id.container2, BookPlayerFragment())
                .commit()


        // Respond to selection in portrait mode using flag stored in ViewModel
        bookViewModel.getSelectedBook()?.observe(this){
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

        nowPlaying.text = nowPlayingText

        findViewById<View>(R.id.searchImageButton).setOnClickListener {
            onSearchRequested()
        }
        val statusViewModel: ButtonsViewModel by lazy{
            ViewModelProvider(this)[ButtonsViewModel::class.java]
        }

        //waiting for pause or play from fragment using viewmodel
        statusViewModel.status?.observe(this, Observer { Boolean ->
            if(statusViewModel.status!!.value == true){
                //true means play book
                Log.d("mylog", "the value is true")

                val currBook = bookViewModel.getSelectedBook()?.value
                if(isConnected){

                    if (currBook != null) {
                        binder.play(currBook)
                        nowPlayingText = "Now playing: ${bookViewModel.getSelectedBookTitle().toString()}"
                        nowPlaying.text = nowPlayingText
                    }
                }
            }else{
                //false then pause the book
                Log.d("myLog", "the value is false")
                if(isConnected) {
                    binder.pause()
                    nowPlayingText = ""
                    nowPlaying.text = nowPlayingText
                }
            }

            //if(isConnected){
            //    binder.setProgressHandler(binderHandler)
            //}

            //waiting for user to drag progress bar
            with (findViewById(R.id.seekBar) as SeekBar) {
                setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, user: Boolean) {

                        if(change == progress){
                            return
                        }

                        //if user dragged, then change progress of audio
                        //if the user didnt drag, then audio book is changing seek bar
                        if(isDrag){
                            if(binder.isPlaying == true) {
                                if (seekBar != null) {
                                    seekBar.progress = endPoint
                                }
                                Log.d("my log user", progress.toString())

                                val currBook = bookViewModel.getSelectedBook()?.value
                                val bookDuration = currBook?.duration

                                val position = ((endPoint.toDouble()/100)*bookDuration!!).toInt()
                                Log.d("mylog", position.toString())
                                binder.seekTo(position)
                                isDrag = false
                            }
                        }else{
                            Log.d("mylog audiobook", progress.toString())
                        }

                    }
                    override fun onStartTrackingTouch(p0: SeekBar?) {
                        if(seekBar!=null){
                            startPoint = seekBar.progress
                        }
                    }
                    override fun onStopTrackingTouch(p0: SeekBar?) {
                        if(seekBar!=null){
                            endPoint = seekBar.progress
                        }
                        change = Math.abs(startPoint-endPoint)
                        Log.d("mylog change", change.toString())
                        isDrag = true
                    }
                })
            }

        })

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(NOW_PLAYING, nowPlayingText)
    }

    override fun onBackPressed() {
        // BackPress clears the selected book
        bookViewModel.clearSelectedBook()
        super.onBackPressed()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (Intent.ACTION_SEARCH == intent!!.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also {
                searchBooks(it)

                // Unselect previous book selection
                bookViewModel.clearSelectedBook()

                // Remove any unwanted DisplayFragments instances from the stack
                supportFragmentManager.popBackStack()
            }
        }

    }

    private fun searchBooks(searchTerm: String) {
        requestQueue.add(
            JsonArrayRequest(searchURL + searchTerm,
                { bookViewModel.updateBooks(it) },
                { Toast.makeText(this, it.networkResponse.toString(), Toast.LENGTH_SHORT).show() })
        )
    }

}