package com.everymomentholy.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R

class OrderBookAdapter : RecyclerView.Adapter<OrderBookAdapter.MyViewHolder>() {

    var orderBookList: ArrayList<Fragment> = ArrayList()

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.order_book_raw, parent, false)
        return OrderBookAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 3
        //return liturgiesList.size
    }
}