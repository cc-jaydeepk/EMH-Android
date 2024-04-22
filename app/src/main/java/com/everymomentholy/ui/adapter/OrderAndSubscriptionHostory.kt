package com.everymomentholy.ui.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.response.OrderAndSubHistoryVo
import java.util.*

class OrderAndSubscriptionHostory(
    var context: Context,
    var orderandSubList: List<OrderAndSubHistoryVo>,
) : RecyclerView.Adapter<OrderAndSubscriptionHostory.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var orderId = view.findViewById<TextView>(R.id.txtOrderId)
        var title = view.findViewById<TextView>(R.id.txtTitle)
        var orderDate = view.findViewById<TextView>(R.id.txtOrderDate)
        var startDate = view.findViewById<TextView>(R.id.txtStartDate)
        var expireDate = view.findViewById<TextView>(R.id.txtExpireDate)
        var price = view.findViewById<TextView>(R.id.txtPrice)
        var priceStatic = view.findViewById<TextView>(R.id.txtpr)
        var ivOrderHistoryCover = view.findViewById<ImageView>(R.id.iv_order_history_cover)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.order_sub_history_item, parent, false)
        return OrderAndSubscriptionHostory.MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var orderAndSubHistoryData = orderandSubList[position]

        Glide.with(context)
            .load(orderAndSubHistoryData.coverPageImage)
            .placeholder(R.drawable.no_book)
            .into(holder.ivOrderHistoryCover)

        if (orderAndSubHistoryData.price == "") {
            holder.price.visibility = View.GONE
            holder.priceStatic.visibility = View.GONE
        } else {
            holder.price.visibility = View.VISIBLE
            holder.priceStatic.visibility = View.VISIBLE
        }

        if (orderAndSubHistoryData.started_at == "") {
            holder.startDate.visibility = View.GONE
            holder.expireDate.visibility = View.GONE
        } else {
            holder.startDate.visibility = View.VISIBLE
            holder.expireDate.visibility = View.VISIBLE
        }


        val startdateTime = orderAndSubHistoryData.started_at
        val startdateOnly = startdateTime?.replace(Regex("\\s-\\s\\d{2}:\\d{2}\\s[AP]M"), "")

        val expiredateTime = orderAndSubHistoryData.expire_at
        val expiredateOnly = expiredateTime?.replace(Regex("\\s-\\s\\d{2}:\\d{2}\\s[AP]M"), "")


        holder.orderId.text = orderAndSubHistoryData.displayOrderId
        holder.title.text = orderAndSubHistoryData.title
        holder.startDate.text = "Start Date: " + " " + startdateOnly
        holder.expireDate.text = "Expire Date: " + " " + expiredateOnly
        holder.orderDate.text = "Order Date: " + " " + orderAndSubHistoryData.orderDate
        holder.price.text = "$" + "" + orderAndSubHistoryData.price
    }

    override fun getItemCount(): Int {
        return orderandSubList.size
    }
}