package com.everymomentholy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.response.LiturgiesDataVo
import com.everymomentholy.interfaces.LiturgyLitstClickListner

class MyLiturgyAdapter(
    var context: Context,
    var liturgyList: List<LiturgiesDataVo>,
    var liturgyListClickListner: LiturgyLitstClickListner
) : RecyclerView.Adapter<MyLiturgyAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgLiturgycoverImage: ImageView = view.findViewById(R.id.imgLiturgycoverImage)
        var txtLiturgiesTitle: TextView = view.findViewById(R.id.txtLiturgiesTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.myliturgies_raw, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myLiturgies = liturgyList[position]

        holder.txtLiturgiesTitle.text = myLiturgies.chapterTitle

        Glide.with(context)
            .load(myLiturgies.chapterPageImage)
            .into(holder.imgLiturgycoverImage)

    }

    override fun getItemCount(): Int {
        return liturgyList.size
    }
}