package edu.temple.assignment5

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //references to views
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        //setting up grid manager for recycler view
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        //array of image objects
        val images = arrayOf(
            ImageObject("Ahri", R.drawable.ahri),
            ImageObject("Akali", R.drawable.akali),
            ImageObject("Camille", R.drawable.camille),
            ImageObject("Graves", R.drawable.graves),
            ImageObject("Irelia", R.drawable.irelia),
            ImageObject("Jhin", R.drawable.jhin),
            ImageObject("Kayle", R.drawable.kayle),
            ImageObject("Nunu", R.drawable.nunu),
            ImageObject("Pyke", R.drawable.pyke),
            ImageObject("Rakan", R.drawable.rakan),
            ImageObject("Xayah", R.drawable.xayah),
            ImageObject("Zoe", R.drawable.zoe)
        )

        val myRecyclerViewFunc = {imageObject : ImageObject ->
            val intent = Intent(this, DisplayActivity::class.java)
            intent.putExtra("name", imageObject.name)
            intent.putExtra("image", imageObject.image)
            startActivity(intent)
        }

        recyclerView.adapter = ImageAdapter(images, myRecyclerViewFunc)

    }

    class ImageAdapter(images: Array<ImageObject>, myRecyclerViewFunc: (ImageObject) -> Unit) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

        val imageObjects = images
        val eventHandler = myRecyclerViewFunc

        inner class ImageViewHolder(_view: View) : RecyclerView.ViewHolder(_view){
            val imageDisplayView = _view.findViewById<ImageView>(R.id.recycler_icon)
            val imageDisplayText = _view.findViewById<TextView>(R.id.recycler_text)

            lateinit var imageObject: ImageObject
            init{
                _view.setOnClickListener{eventHandler(imageObject)}
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            //inflating layout file
            val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_layout, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            holder.imageDisplayView.setImageResource(imageObjects[position].image)
            holder.imageDisplayText.text = (imageObjects[position].name)

            holder.imageObject = imageObjects[position]
        }

        override fun getItemCount(): Int {
            return imageObjects.size
        }

    }

}