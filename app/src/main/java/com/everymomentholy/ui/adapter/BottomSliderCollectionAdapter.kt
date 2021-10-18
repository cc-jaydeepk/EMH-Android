package com.everymomentholy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.response.CollectionDataVo
import com.everymomentholy.api.response.MyLiturgiesDataVo

class BottomSliderCollectionAdapter(
    var context: Context,
    var liturgyList: List<CollectionDataVo>,
) : RecyclerView.Adapter<BottomSliderCollectionAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imgFreeLiturgiescover = view.findViewById<ImageView>(R.id.imgFreeLiturgiescover)
        var txtfreeLiturgiesTitle = view.findViewById<TextView>(R.id.txtFreeLiturgiesTitle)
        var btnReadNow = view.findViewById<Button>(R.id.btnReadNow)
        var txtLiturgiesPrice = view.findViewById<TextView>(R.id.txtLiturgiesPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.raw_bottom_slider_get_liturgies_about, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val freeLiturgies = liturgyList[position]

        holder.txtfreeLiturgiesTitle.text = freeLiturgies.bookTitle

        Glide.with(context)
            .load(freeLiturgies.bookCoverPageImage)
            .into(holder.imgFreeLiturgiescover)

        if (freeLiturgies.isPurchased == "Yes") {

        } else {
            holder.btnReadNow.text = "Purchase Collection"
        }

        holder.btnReadNow.setOnClickListener() {
            if (holder.btnReadNow.text == "Purchase Collection") {
            }
        }

        holder.txtLiturgiesPrice.text = "$" + freeLiturgies.bookAmount

    }

    override fun getItemCount(): Int {
        return liturgyList.size

    }
}