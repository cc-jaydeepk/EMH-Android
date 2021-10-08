package com.everymomentholy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.response.DataVo
import com.everymomentholy.interfaces.BookListClickListner
import com.everymomentholy.interfaces.LiturgyLitstClickListner

class GetBooksAdapter(
    var context: Context,
    var getBooklist: List<DataVo>,
    var bookListClickListner: BookListClickListner
): RecyclerView.Adapter<GetBooksAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgLiturgycoverImage: ImageView = view.findViewById(R.id.imgLiturgycoverImage)
        var txtLiturgiesTitle: TextView = view.findViewById(R.id.txtLiturgiesTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.liturgy_raw, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       // val getBook = bookList[position]
        val bookList = getBooklist[position]
        holder.txtLiturgiesTitle.text = bookList.chapterTitle
        Glide.with(context)
            .load(bookList.chapterPageImage)
            .into(holder.imgLiturgycoverImage)
    }

    override fun getItemCount(): Int {
        return getBooklist.size
    }
}