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
import com.everymomentholy.api.response.PurchaseDataVo
import com.everymomentholy.interfaces.LiturgyLitstClickListner

class PastPurchaseAdapter(
    var context: Context,
    var purchaseList: List<PurchaseDataVo>,
    var liturgyListClickListner: LiturgyLitstClickListner
) : RecyclerView.Adapter<PastPurchaseAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var liturgyImage: ImageView = view.findViewById(R.id.imgLiturgycoverImage)
        var liturgyTitle: TextView = view.findViewById(R.id.txtLiturgiesTitle)
        var txtLiturgiesCount: TextView = view.findViewById(R.id.txtLiturgiesCount)
        var btnOpen: Button = view.findViewById(R.id.btnOpen)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.purchase_history_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val purchaseData = purchaseList[position]

        if (purchaseData.isVolume.equals("Yes", true)) {
            holder.liturgyTitle.text = purchaseData.volumeTitle
            holder.txtLiturgiesCount.text = "Liturgies: " + purchaseData.liturgyCount
            Glide.with(context)
                .load(purchaseData.volumeCoverPageImage)
                .into(holder.liturgyImage)
        } else {
            holder.liturgyTitle.text = purchaseData.bookTitle
            holder.txtLiturgiesCount.text = "Liturgies: " + purchaseData.liturgyCount
            Glide.with(context)
                .load(purchaseData.bookCoverPageImage)
                .into(holder.liturgyImage)
        }

        holder.btnOpen.setOnClickListener() {
            liturgyListClickListner.onMyLiturgiesListClick(position, purchaseData.bookId, false)
        }

    }

    override fun getItemCount(): Int {
        return purchaseList.size
    }
}