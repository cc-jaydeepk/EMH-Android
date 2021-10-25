package com.everymomentholy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.response.GetLiturgiesDataVo
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.everymomentholy.interfaces.LiturgyLitstClickListner

class MyLiturgyAdapter(
    var context: Context,
    // var liturgyList: List<LiturgiesDataVo>,
    var liturgyList: List<GetLiturgiesDataVo>,
    var liturgyListClickListner: LiturgyLitstClickListner
) : RecyclerView.Adapter<MyLiturgyAdapter.ViewHolder>() {


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgLiturgycoverImage: ImageView = view.findViewById(R.id.imgLiturgycoverImage)
        var txtLiturgiesTitle: TextView = view.findViewById(R.id.txtLiturgiesTitle)
        var mainRelative: RelativeLayout = view.findViewById(R.id.mainRelative)
        var btnOpen: Button = view.findViewById(R.id.btnOpen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.myliturgies_raw, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myLiturgies = liturgyList[position]

        if (myLiturgies.isVolume == "Yes") {
            holder.txtLiturgiesTitle.text = myLiturgies.volumeTitle
            Glide.with(context)
                .load(myLiturgies.volumeCoverPageImage)
                .into(holder.imgLiturgycoverImage)
        } else {
            holder.txtLiturgiesTitle.text = myLiturgies.bookTitle
            Glide.with(context)
                .load(myLiturgies.bookCoverPageImage)
                .into(holder.imgLiturgycoverImage)
        }

        holder.mainRelative.setOnClickListener() {
            liturgyListClickListner.onMyLiturgiesListClick(position, myLiturgies.bookId, true)
        }

        holder.btnOpen.setOnClickListener() {
            liturgyListClickListner.onMyLiturgiesListClick(position, myLiturgies.bookId, false)
        }

        if (myLiturgies.isClicked) {
            holder.mainRelative.setBackgroundColor(context.resources.getColor(R.color.app_gray))
        } else {
            holder.mainRelative.setBackgroundColor(context.resources.getColor(R.color.white))
        }

    }

    override fun getItemCount(): Int {
        return liturgyList.size
    }

    fun setLiturgiesClick(clickListner: LiturgyLitstClickListner) {
        this.liturgyListClickListner = clickListner
    }
}