package com.everymomentholy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.response.QuotePreviousquoteVo
import com.everymomentholy.ui.fragments.HomeFragment

class QuoteAdapter(
    var context: Context,
    var quoteList: ArrayList<QuotePreviousquoteVo>,
    var shareClickListner: HomeFragment
) : RecyclerView.Adapter<QuoteAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var quoteText = view.findViewById<TextView>(R.id.quoteTxt)
        var parentLiturgyText = view.findViewById<TextView>(R.id.parentLiturgyTxt)
        var dateText = view.findViewById<TextView>(R.id.dateTxt)
        var share = view.findViewById<ImageView>(R.id.shareImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.quote_raw, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currentquote = quoteList[position]

        holder.quoteText.text = currentquote.quote
        holder.parentLiturgyText.text = currentquote.parentLiturgy
        holder.dateText.text = currentquote.date

        holder.share.setOnClickListener {
            shareClickListner.shareQuote(position, currentquote)
        }
    }

    override fun getItemCount(): Int {
        return quoteList.size
    }

    fun setQuote(quoteData: ArrayList<QuotePreviousquoteVo>) {
        this.quoteList = quoteData
        notifyDataSetChanged()
    }

    fun getQuotes(): ArrayList<QuotePreviousquoteVo> {
        return quoteList
    }

    fun addQuote(quoteData: QuotePreviousquoteVo) {
        this.quoteList.add(quoteData)
        notifyItemRangeInserted(quoteList.size-1, quoteList.size-1)
    }

}