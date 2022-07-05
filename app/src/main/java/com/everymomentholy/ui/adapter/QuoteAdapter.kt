package com.everymomentholy.ui.adapter

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.response.QuotePreviousquoteVo
import com.everymomentholy.ui.fragments.HomeFragment
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class QuoteAdapter(
    var context: Context,
    var quoteList: ArrayList<QuotePreviousquoteVo>,
    var shareClickListner: HomeFragment
) : RecyclerView.Adapter<QuoteAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var quoteText = view.findViewById<TextView>(R.id.quoteTxt)
        var parentLiturgyText = view.findViewById<TextView>(R.id.parentLiturgyTxt)
        var dateText = view.findViewById<TextView>(R.id.dateTxt)
       // var dateText1 = view.findViewById<TextView>(R.id.dateTxt1)
        var share = view.findViewById<ImageView>(R.id.shareImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.quote_raw, parent, false)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var currentquote = quoteList[position]

        holder.quoteText.text = currentquote.quote
        holder.parentLiturgyText.text = currentquote.parentLiturgy
        // holder.dateText.text = currentquote.date
        var dateQuote = currentquote.date

        val inSDF = SimpleDateFormat("yyyy-mm-dd")
        val outSDF = SimpleDateFormat("mm-dd-yyyy")
        val formeteddate = inSDF.parse(dateQuote)
        val quoteDate = outSDF.format(formeteddate)
        holder.dateText.text = quoteDate

        val current = LocalDate.now()
        val formetCurrentdate = inSDF.parse(current.toString())
        val currentDate = outSDF.format(formetCurrentdate)

        if (quoteDate == currentDate) {
            holder.dateText.text = "Today"
        } else {
            holder.dateText.text = quoteDate
        }

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
        notifyItemRangeInserted(quoteList.size - 1, quoteList.size - 1)
    }

}