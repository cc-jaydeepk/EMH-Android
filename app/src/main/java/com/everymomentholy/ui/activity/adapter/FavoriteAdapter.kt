package com.everymomentholy.ui.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R

class FavoriteAdapter: RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {
    var favLiturgyList: ArrayList<Fragment> = ArrayList()

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.favfragment, parent, false)
        return FavoriteAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 3
        //return favLiturgyList.size
    }
}