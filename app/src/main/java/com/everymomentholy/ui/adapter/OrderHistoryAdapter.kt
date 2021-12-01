package com.everymomentholy.ui.adapter

import android.content.Context
import android.media.Image
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
import com.everymomentholy.api.response.OrderHistoryVo

class OrderHistoryAdapter(
    var context: Context,
    var orderHistoryList: List<OrderHistoryVo>,
) : RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var txtOrderHistoryPrice = view.findViewById<TextView>(R.id.txt_order_history_price)
        var txtOrderHistoryDateTime = view.findViewById<TextView>(R.id.txt_order_history_date_time)
        var txtOrderHistoryTitle = view.findViewById<TextView>(R.id.txt_order_history_title)
        var ivOrderHistoryCover = view.findViewById<ImageView>(R.id.iv_order_history_cover)
        var ivOrderHistory = view.findViewById<ImageView>(R.id.iv_order_history)
        var txtOrderHistoryOrderID = view.findViewById<TextView>(R.id.txt_order_history_orderID)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.raw_order_history, parent, false)
        return OrderHistoryAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var orderHistoryData = orderHistoryList[position]

        holder.txtOrderHistoryDateTime.text = orderHistoryData.orderDate
        holder.txtOrderHistoryPrice.text = "$" + orderHistoryData.price
        holder.txtOrderHistoryTitle.text = orderHistoryData.title
        holder.txtOrderHistoryOrderID.text = orderHistoryData.displayOrderId
        Glide.with(context)
            .load(orderHistoryData.coverPageImage)
            .into(holder.ivOrderHistoryCover)

        when (orderHistoryData.purchaseType) {
            "Volume" -> {
                holder.ivOrderHistory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_volume))
            }
            "Book" -> {
                holder.ivOrderHistory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_book))
            }
            "Liturgy" -> {
                holder.ivOrderHistory.setImageDrawable(context.resources.getDrawable(R.drawable.ic_liturgy_order_history))
            }
        }

    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }
}