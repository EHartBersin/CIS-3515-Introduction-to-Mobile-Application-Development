package edu.temple.assignment3

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Spinner
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var colorSpinner: Spinner
    lateinit var layout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind views
        colorSpinner = findViewById(R.id.colorSpinner)
        layout = findViewById(R.id.layout)

        val colors = arrayOf("Choose a color", "Magenta", "Purple", "Maroon", "Grey", "Olive", "Lime", "Yellow", "Cyan", "Teal", "Navy")

        colorSpinner.adapter = ColorAdapter(this, colors)

        colorSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (parent != null) {
                    if(parent.getItemAtPosition(position) == "Choose a color"){
                        layout.setBackgroundColor(Color.parseColor("white"))
                    }else{
                        layout.setBackgroundColor(Color.parseColor(parent!!.getItemAtPosition(position).toString()))
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }
        
    }
}

class ColorAdapter(_context: Context, _colors: Array<String>) : BaseAdapter(){

    private val context = _context
    private val colors = _colors

    override fun getCount(): Int {
        return colors.size
    }

    override fun getItem(position: Int): Any {
        return colors[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView : TextView

        if (convertView != null) {
            textView = convertView as TextView
        } else {
            textView = TextView(context)
            textView.textSize = 22f
            textView.setPadding(5, 10, 0, 10)
        }

        textView.text = colors[position]
        textView.setBackgroundColor(Color.parseColor("white"))

        return textView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup) : View{

        return super.getDropDownView(position, convertView, parent).apply{
            if(position == 0){
                setBackgroundColor(Color.parseColor("white"))
            }else {
                setBackgroundColor(Color.parseColor(colors[position]))
            }
        }

    }


}