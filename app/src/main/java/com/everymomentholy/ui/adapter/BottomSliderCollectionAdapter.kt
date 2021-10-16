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
    //var liturgyList: List<LiturgiesDataVo>,
    var liturgyList: List<CollectionDataVo>,
    // var freeLiturgyListClickListner: FreeLiturgyLitstClickListner
) : RecyclerView.Adapter<BottomSliderCollectionAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imgShare = view.findViewById<ImageView>(R.id.imgShare)
        var imgFreeLiturgiescover = view.findViewById<ImageView>(R.id.imgFreeLiturgiescover)
        var txtfreeLiturgiesTitle = view.findViewById<TextView>(R.id.txtFreeLiturgiesTitle)
        var btnReadNow = view.findViewById<Button>(R.id.btnReadNow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.bottom_slider_raw, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val freeLiturgies = liturgyList[position]

        holder.txtfreeLiturgiesTitle.text = freeLiturgies.bookTitle

        Glide.with(context)
            .load(freeLiturgies.bookCoverPageImage)
            .into(holder.imgFreeLiturgiescover)

        holder.imgShare.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val view: View = LayoutInflater.from(context).inflate(R.layout.share_dialog, null)
            builder.setView(view)
            builder.show()
        }

        if(freeLiturgies.isPurchased=="Yes"){

        }else{
            holder.btnReadNow.text = "Purchase Collection"
        }


    }

    override fun getItemCount(): Int {
        return liturgyList.size

    }
}