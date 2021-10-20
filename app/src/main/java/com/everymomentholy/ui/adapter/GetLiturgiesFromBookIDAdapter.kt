package com.everymomentholy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.response.CollectionDataVo
import com.everymomentholy.api.response.MyLiturgiesDataVo

class GetLiturgiesFromBookIDAdapter(
    var context: Context,
    var liturgyList: List<MyLiturgiesDataVo>,
) : RecyclerView.Adapter<GetLiturgiesFromBookIDAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var txtLiturgyName = view.findViewById<TextView>(R.id.txtLiturgyName)
        var coverImage = view.findViewById<ImageView>(R.id.coverImage)
        var txtPrice = view.findViewById<TextView>(R.id.txtPrice)
        var btnUnlock = view.findViewById<Button>(R.id.btnUnlock)
        var imageBook = view.findViewById<ImageView>(R.id.imageBook)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.collection_raw, parent, false)
        return GetLiturgiesFromBookIDAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (position == 0) {
            holder.imageBook.visibility = View.VISIBLE
            holder.btnUnlock.text = "Unlock Collection"
        } else {
            holder.imageBook.visibility = View.GONE
            holder.btnUnlock.text = "Unlock"
        }

        holder.txtLiturgyName.text = liturgyList[position].chapterTitle

        if (liturgyList[position].isFree == "Yes") {
            holder.txtPrice.text = "Free"
        } else {

            holder.txtPrice.text = liturgyList[position].price
        }

        Glide.with(context)
            .load(liturgyList[position].chapterPageImage)
            .into(holder.coverImage)


    }

    override fun getItemCount(): Int {
        return liturgyList.size
    }
}