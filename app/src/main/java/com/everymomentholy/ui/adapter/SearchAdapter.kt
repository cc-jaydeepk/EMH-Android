package com.everymomentholy.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.response.MyLiturgiesDataVo

class SearchAdapter(var context: Context, var searchedLiturgies: ArrayList<MyLiturgiesDataVo>) :
    RecyclerView.Adapter<SearchAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var ivSearchLiturgies = view.findViewById<ImageView>(R.id.iv_search_liturgies)
        var txtSearchLiturgyFree = view.findViewById<TextView>(R.id.txt_search_liturgy_free)
        var txtSearchLiturgyReadNow = view.findViewById<TextView>(R.id.txt_search_liturgy_read_now)
        var txtSearchLiturgyTitle = view.findViewById<TextView>(R.id.txt_search_liturgy_title)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.search_raw, parent, false)
        return SearchAdapter.MyViewHolder(itemView)
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var myLiturgiesDataVo = searchedLiturgies[position]

        holder.txtSearchLiturgyTitle.text = myLiturgiesDataVo.chapterTitle
        Glide.with(context)
            .load(myLiturgiesDataVo.chapterPageImage)
            .into(holder.ivSearchLiturgies)

        if (myLiturgiesDataVo.isPurchased == "Yes" || myLiturgiesDataVo.isFree == "Yes" ||
            myLiturgiesDataVo.isFeatured == "Yes"
        ) {
            holder.txtSearchLiturgyReadNow.text = "Read Now"
            var sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.txtSearchLiturgyReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                holder.txtSearchLiturgyReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
            } else {
                holder.txtSearchLiturgyReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                holder.txtSearchLiturgyReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
            }
        } else {
            holder.txtSearchLiturgyReadNow.text = "Unlock"
            holder.txtSearchLiturgyReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_unlock));
            holder.txtSearchLiturgyReadNow.setTextColor(context.resources.getColor(R.color.white))
        }

        when {
            myLiturgiesDataVo.isPurchased == "Yes" -> {
                holder.txtSearchLiturgyFree.text = "Purchased"
            }
            myLiturgiesDataVo.isFree == "Yes" -> {
                holder.txtSearchLiturgyFree.text = "Free"
            }
            myLiturgiesDataVo.isFeatured == "Yes" -> {
                holder.txtSearchLiturgyFree.text = "Featured"
            }
            else -> {
                holder.txtSearchLiturgyFree.text = "$" + myLiturgiesDataVo.price
            }
        }
    }

    override fun getItemCount(): Int {
        return searchedLiturgies.size
    }
}