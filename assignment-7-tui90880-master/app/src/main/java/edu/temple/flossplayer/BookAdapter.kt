package edu.temple.flossplayer

import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(_books: BookList, _clickEvent: (Book) -> Unit) : RecyclerView.Adapter<BookAdapter.ViewHolder>(){

    private val books = _books
    val clickEvent = _clickEvent

    class ViewHolder(_layout: LinearLayout) : RecyclerView.ViewHolder(_layout) {
        val layout = _layout
        val titleView = layout.getChildAt(0) as TextView
        val authorView = layout.getChildAt(1) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val linearLayout = LinearLayout(parent.context)
        with (linearLayout) {
            orientation = LinearLayout.VERTICAL
            addView(TextView(parent.context).apply { textSize = 24f })
            addView(TextView(parent.context).apply { textSize = 16f })
        }

        return ViewHolder(linearLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var book = books.get(position)
        holder.titleView.text = (book.title)
        holder.authorView.text = (book.author)

        // Assign on click listener when binding
        holder.layout.setOnClickListener{clickEvent(book)}
    }

    override fun getItemCount(): Int {
        return books.size()
    }

}