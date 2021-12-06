package com.everymomentholy.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.response.BookStoreDataVo


class OrderBookAdapter(
    var context: Context,
    var bookStoreList: List<BookStoreDataVo>,
) : RecyclerView.Adapter<OrderBookAdapter.MyViewHolder>() {

    var orderBookList: ArrayList<Fragment> = ArrayList()

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var ivOrderbook = view.findViewById<ImageView>(R.id.iv_orderbook)
        var txtOrderBookTitle = view.findViewById<TextView>(R.id.txt_order_book_title)
        var btnOrderBbookRabbitRoom = view.findViewById<Button>(R.id.btn_order_book_rabbit_room)
        var btnOrderBbookAmazon = view.findViewById<Button>(R.id.btn_order_book_amazon)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.order_book_raw, parent, false)
        return OrderBookAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var bookStore = bookStoreList[position]

        holder.txtOrderBookTitle.text = bookStore.bookTitle

        Glide.with(context)
            .load(bookStore.bookCoverPageImage)
            .into(holder.ivOrderbook)

        holder.btnOrderBbookAmazon.setOnClickListener() {
            val httpIntent = Intent(Intent.ACTION_VIEW)
            httpIntent.data = Uri.parse(bookStore.amazonUrl)
            context.startActivity(httpIntent)
        }

        holder.btnOrderBbookRabbitRoom.setOnClickListener() {
            val httpIntent = Intent(Intent.ACTION_VIEW)
            httpIntent.data = Uri.parse(bookStore.rabbitRoomUrl)
            context.startActivity(httpIntent)
        }

    }

    override fun getItemCount(): Int {
        return bookStoreList.size
        //return liturgiesList.size
    }
}