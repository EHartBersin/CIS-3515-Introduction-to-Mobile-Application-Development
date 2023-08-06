package edu.temple.imagedisplayapp
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter(_items: Array<Item>, _clickEvent: (Int, String) -> Unit) : RecyclerView.Adapter<ImageAdapter.ViewHolder>(){

    private val items = _items
    val clickEvent = _clickEvent

    class ViewHolder(_layout: LinearLayout) : RecyclerView.ViewHolder(_layout) {
        val layout = _layout
        val imageView = layout.getChildAt(0) as ImageView
        val textView = layout.getChildAt(1) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val linearLayout = LinearLayout(parent.context)
        with (linearLayout) {
            orientation = LinearLayout.VERTICAL
            addView(ImageView(parent.context).apply { layoutParams = ViewGroup.LayoutParams(200, 200) })
            addView(TextView(parent.context).apply { textSize = 22f })
        }

        return ViewHolder(linearLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imageView.setImageResource(items[position].resourceId)
        holder.textView.text = items[position].description

        // Assign on click listener when binding
        val clickedImage = items[position].resourceId
        val clickedName = items[position].description
        holder.layout.setOnClickListener{clickEvent(clickedImage, clickedName)}
    }

    override fun getItemCount(): Int {
        return items.size
    }

}

