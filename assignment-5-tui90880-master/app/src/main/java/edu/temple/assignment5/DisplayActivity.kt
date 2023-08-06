package edu.temple.assignment5

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class DisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        //adding the image and name to the activity display
        val name = intent.getStringExtra("name")
        val image = intent.getIntExtra("image", 0)

        val textView = findViewById<TextView>(R.id.textView)
        val imageView = findViewById<ImageView>(R.id.imageView)

        textView.text = name
        imageView.setImageResource(image)

        //button on click listener to go back
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            val intent = Intent(this, SelectionActivity::class.java)
            startActivity(intent)
        }
    }
}