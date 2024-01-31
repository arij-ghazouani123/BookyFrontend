package com.example.booky.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.booky.R


class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val BookPic : ImageView
    val BookDescription : TextView
    val title: TextView
    val BookType: TextView
    init {
        BookPic = itemView.findViewById<ImageView>(R.id.title_image)
        BookDescription = itemView.findViewById<TextView>(R.id.book_description)
        title = itemView.findViewById<TextView>(R.id.book_title)
        BookType = itemView.findViewById<TextView>(R.id.book_offer)
    }

}