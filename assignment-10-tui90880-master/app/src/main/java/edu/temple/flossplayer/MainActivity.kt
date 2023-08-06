package edu.temple.flossplayer

import android.Manifest
import android.annotation.TargetApi
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.os.storage.StorageManager
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.temple.audlibplayer.PlayerService
import java.io.File


class MainActivity : AppCompatActivity(), BookControlFragment.BookControlInterface {

    private val searchURL = "https://kamorris.com/lab/flossplayer/search.php?query="
    private val downloadURL = "https://kamorris.com/lab/flossplayer/download.php?id="
    private lateinit var progressSeekBar: SeekBar
    lateinit var bookServiceIntent: Intent

    //saving progress and search results
    private var listenProgress = HashMap<Int, Int>()
    private var savedSearchResults = ArrayList<Book>()

    //variables for file
    private lateinit var savedProgressFile: File
    private lateinit var savedSearchResultsFile: File
    private val gson = Gson()


    companion object {
        private const val PROGRESS_SAVE_FILE = "saved_progress"
        private const val SEARCH_RESULTS_SAVE_FILE = "saved_search_results"
    }

    var mediaControllerBinder: PlayerService.MediaControlBinder? = null

    val bookProgressHandler = Handler(Looper.getMainLooper()) {

        var currentBookProgress = 0
        var currentBookId = 0

        with (it.obj as PlayerService.BookProgress) {

            // Update ViewModel state based on whether we're seeing the currently playing book
            // from the service for the first time
            if (!bookViewModel.hasBookBeenPlayed()) {
                bookViewModel.setBookPlayed(true)
                bookViewModel.setPlayingBook(book as Book)
                bookViewModel.setSelectedBook(book as Book)
            }

            currentBookId = (book as Book).book_id

            if (listenProgress.get(currentBookId) != null) {
                currentBookProgress = listenProgress[currentBookId]!!
            }else{
                currentBookProgress = progress
            }

            // Update seekbar with progress of current book as a percentage
            progressSeekBar.progress = ((progress.toFloat() / (book as Book).duration) * 100).toInt()

            listenProgress.put((book as Book).book_id, progressSeekBar.progress)
            //Log.d("mylogs", listenProgress.toString())
        }
        true
    }

    // Callback that is invoked when (un)binding is complete
    private val bookServiceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mediaControllerBinder = service as PlayerService.MediaControlBinder
            mediaControllerBinder?.setProgressHandler(bookProgressHandler)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mediaControllerBinder = null
        }

    }

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

        savedSearchResultsFile = File(filesDir, SEARCH_RESULTS_SAVE_FILE)
        savedProgressFile = File(filesDir, PROGRESS_SAVE_FILE)

        if(savedSearchResultsFile.exists()){
            savedSearchResults = getSavedSearchResults()
            bookViewModel.bookList.updateBookListValue(savedSearchResults)
            bookViewModel.notifyUpdatedBookList()
        }
        if(savedProgressFile.exists()){
            listenProgress = getSavedProgress()
        }

        val nowPlayingTextView = findViewById<TextView>(R.id.nowPlayingTextView)
        progressSeekBar = findViewById<SeekBar?>(R.id.progressSeekBar).apply {
            setOnSeekBarChangeListener(object: OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {

                        // If the user is dragging the SeekBar, convert progress percentage
                        // to value in seconds and seek to position
                        bookViewModel.getSelectedBook()?.value?.let {book ->
                            mediaControllerBinder?.run {
                                if (isPlaying) {
                                    seekTo(((progress.toFloat() / 100) * book.duration).toInt())
                                }
                            }
                        }
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            })
        }

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
        bookViewModel.getSelectedBook()?.observe(this){
            if (!bookViewModel.hasViewedSelectedBook()) {
                if (isSingleContainer && supportFragmentManager.findFragmentById(R.id.container1) !is BookPlayerFragment) {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container1, BookPlayerFragment())
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit()
                }
                bookViewModel.markSelectedBookViewed()
            }
        }

        // Always show currently playing book
        bookViewModel.getPlayingBook()?.observe(this){
            nowPlayingTextView.text = String.format(getString(R.string.now_playing), it.title)
        }

        findViewById<View>(R.id.searchImageButton).setOnClickListener {
            onSearchRequested()
        }

        bookServiceIntent = Intent(this, PlayerService::class.java)

        // Bind in order to send commands
        bindService(bookServiceIntent, bookServiceConnection, BIND_AUTO_CREATE)
    }

    //when app stopped, save the search results
    override fun onStop(){
        saveSearchResults(bookViewModel.bookList.getBookListValue())
        saveProgress(listenProgress)
        super.onStop()
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
        saveSearchResults(bookViewModel.bookList.getBookListValue())
    }

    override fun playBook() {
        bookViewModel.getSelectedBook()?.value?.apply {
            mediaControllerBinder?.run {

                val id = bookViewModel.getSelectedBook()!!.value?.book_id
                val duration = bookViewModel.getSelectedBook()!!.value?.duration

                val storageManager = getSystemService(STORAGE_SERVICE) as StorageManager
                val storageVolumes = storageManager.getStorageVolumes()
                val storageVolume = storageVolumes[0]
                val file = File(
                    storageVolume.directory!!.path +
                            "/Download/" + id
                )

                //if audio book file already exists, play from the downloaded file
                //if not, download the file and play the audio book from start
                if(file.exists()){
                    Log.d("mylog: playBook()", "audio book already downloaded")

                    var currentProgress = 0

                    //get the progress of the book and translate to seconds
                    if (id != null && duration != null) {
                        val progress = getCurrentBookProgress(id)
                        currentProgress = ((progress.toDouble()/100)*duration).toInt()
                        Log.d("mylog: playBook()", currentProgress.toString())
                    }

                    play(this@apply, file, currentProgress)

                }else{
                    Log.d("mylog: playBook()", "audio book doesn't exist")
                    play(this@apply)

                    //download the book
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        askPermissions()
                    }else{
                        val id = bookViewModel.getSelectedBook()!!.value?.book_id
                        if (id != null) {
                            downloadBookAudio(id)
                        }
                    }
                }

                bookViewModel.setBookPlayed(false)

                // Start service to ensure it keeps playing even if the activity is destroyed
                startService(bookServiceIntent)
            }
        }
    }

    override fun pauseBook() {
        saveProgress(listenProgress)
        Log.d("mylog: pauseBook()", listenProgress.toString())
        mediaControllerBinder?.run {
            if (isPlaying) stopService(bookServiceIntent)
            else startService(bookServiceIntent)
            pause()
        }
    }

    override fun onDestroy() {
        unbindService(bookServiceConnection)
        saveSearchResults(bookViewModel.bookList.getBookListValue())
        saveProgress(listenProgress)
        super.onDestroy()
    }

    //saves the current list of books from search results
    private fun saveSearchResults(bookList:ArrayList<Book>){
        val file = File(filesDir, SEARCH_RESULTS_SAVE_FILE)
        val jsonString = gson.toJson(bookList)
        file.bufferedWriter().use{it.write(jsonString)}
        Log.d("mylog: saveSearchResults", "saved results")
    }

    //returns the search search results book list
    private fun getSavedSearchResults() : ArrayList<Book>{
        val file = File(filesDir, SEARCH_RESULTS_SAVE_FILE)
        val data = file.bufferedReader().readText()
        val arrayType = object : TypeToken<ArrayList<Book>>(){}.type
        Log.d("mylog: getSavedSearchResults", "retrieved saved results")
        return gson.fromJson(data,arrayType)
    }

    //saves the current book progress
    private fun saveProgress(listenProgress: HashMap<Int, Int>) {
        val file = File(filesDir, PROGRESS_SAVE_FILE)
        val jsonString = gson.toJson(listenProgress)
        file.bufferedWriter().use { it.write(jsonString) }
        Log.d("mylog: saveProgress()", "saved progress")
    }

    //returns the saved book progress
    private fun getSavedProgress(): HashMap<Int, Int> {
        val file = File(filesDir, PROGRESS_SAVE_FILE)
        val data = file.bufferedReader().readText()
        val mapType = object : TypeToken<HashMap<Int, Int>>() {}.type
        Log.d("mylog: getSavedProgress()", "retrieved saved progress")
        return gson.fromJson(data, mapType)
    }

    private fun getCurrentBookProgress(bookId: Int): Int {
        val allProgress = getSavedProgress()
        val progress = allProgress.get(bookId)
        if(progress!=null){
            return progress
        }
        Log.d("mylog: getCurrentBookProgress()", progress.toString())
        return 0
    }

    private fun downloadBookAudio(bookid: Int) {
        Log.d("mylog: downloadBookAudio()", "downloading audio book")
        //Log.d("mylog", bookid.toString())

        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(downloadURL+bookid)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(bookid.toString())
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    bookid.toString()
                )

        }
        val downloadId = downloadManager.enqueue(request)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun askPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Permission required")
                    .setMessage("Permission required to save photos from the Web.")
                    .setPositiveButton("Allow") { dialog, id ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            1000
                        )
                        finish()
                    }
                    .setNegativeButton("Deny") { dialog, id -> dialog.cancel() }
                    .show()
            } else {
                //Request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1000
                )
            }
        } else {
            // Permission has already been granted
            val id = bookViewModel.getSelectedBook()!!.value?.book_id
            if (id != null) {
                downloadBookAudio(id)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted
                    // Download the audio
                    val id = bookViewModel.getSelectedBook()!!.value?.book_id
                    if (id != null) {
                        downloadBookAudio(id)
                    }
                } else {
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

}