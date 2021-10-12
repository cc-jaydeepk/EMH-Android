package com.everymomentholy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.response.LiturgiesDataVo

class BottomSliderAdapter(
    var context: Context,
    var liturgyList: List<LiturgiesDataVo>,
   // var freeLiturgyListClickListner: FreeLiturgyLitstClickListner
) : RecyclerView.Adapter<BottomSliderAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imgShare = view.findViewById<ImageView>(R.id.imgShare)
        var imgFreeLiturgiescover = view.findViewById<ImageView>(R.id.imgFreeLiturgiescover)
        var txtfreeLiturgiesTitle = view.findViewById<TextView>(R.id.txtFreeLiturgiesTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.buttomslider_raw, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // val context = holder.itemView.context
        val freeLiturgies = liturgyList[position]

        holder.txtfreeLiturgiesTitle.text = freeLiturgies.chapterTitle
        // holder.txtFree.text = freeLiturgies.isFree

        Glide.with(context)
            .load(freeLiturgies.chapterPageImage)
            .into(holder.imgFreeLiturgiescover)

        holder.imgShare.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val view: View = LayoutInflater.from(context).inflate(R.layout.share_dialog, null)
            builder.setView(view)
            builder.show()
        }


    }

    override fun getItemCount(): Int {
        return liturgyList.size
        //return 3
        //return freeLiturgiesList.size
    }
}