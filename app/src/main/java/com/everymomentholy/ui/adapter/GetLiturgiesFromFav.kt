package com.everymomentholy.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.response.MyLiturgiesDataVo

class GetLiturgiesFromFav(
    var context: Context,
    var liturgyList: List<MyLiturgiesDataVo>,
) : RecyclerView.Adapter<GetLiturgiesFromFav.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtLiturgyName = view.findViewById<TextView>(R.id.txtLiturgyName)
        var coverImage = view.findViewById<ImageView>(R.id.coverImage)
        var txtPrice = view.findViewById<TextView>(R.id.txtPrice)
        var btnUnlock = view.findViewById<TextView>(R.id.txtUnlock)
        var btnPlayNow = view.findViewById<TextView>(R.id.txtPlayNow)

        var imgShare = view.findViewById<ImageView>(R.id.imgShare)

        // var btnPauseNow = view.findViewById<TextView>(R.id.txtPauseNow)
        var imageBook = view.findViewById<ImageView>(R.id.imageBook)
        var imgFavorite = view.findViewById<ImageView>(R.id.imgFavorite)
        var llCollectionRaw = view.findViewById<LinearLayout>(R.id.ll_collection_raw)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.collection_raw_new, parent, false)
        return GetLiturgiesFromFav.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val favLiturgy = liturgyList[position]
        holder.txtLiturgyName.text = favLiturgy.chapterTitle

        Glide.with(context)
            .load(liturgyList[position].chapterPageImage)
            .into(holder.coverImage)

    }

    override fun getItemCount(): Int {
        Log.e("SIZE", "getItemCount: " + liturgyList.size)
        return liturgyList.size
    }


}