package com.everymomentholy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.response.QuotePreviousquoteVo
import com.everymomentholy.interfaces.LiturgyLitstClickListner
import com.everymomentholy.interfaces.ShareLiturgy

class QuoteAdapter(
    var context: Context,
    var quoteList: ArrayList<QuotePreviousquoteVo>,
) : RecyclerView.Adapter<QuoteAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var quoteText = view.findViewById<TextView>(R.id.quoteTxt)
        var parentLiturgyText = view.findViewById<TextView>(R.id.parentLiturgyTxt)
        var dateText = view.findViewById<TextView>(R.id.dateTxt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.quote_raw, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var quote = quoteList[position]

        holder.quoteText.text = quote.quote
        holder.parentLiturgyText.text = quote.parentLiturgy
        holder.dateText.text = quote.date

        //.onNotificationListClick(position, quote)
    }

    override fun getItemCount(): Int {
        return quoteList.size
    }

    fun setQuote(quoteData: ArrayList<QuotePreviousquoteVo>) {
        this.quoteList = quoteData
        // notifyDataSetChanged()
    }

    fun getQuote(): ArrayList<QuotePreviousquoteVo> {
        return quoteList
    }

}