package com.everymomentholy.ui.activity.adapter

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.model.LiturgiesModel

class MyLiturgyAdapter : RecyclerView.Adapter<MyLiturgyAdapter.MyViewHolder>() {

    var liturgiesList: ArrayList<Fragment> = ArrayList()

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var coverImage: ImageView = view.findViewById(R.id.coverImage)
        var liturgiesTitle: TextView = view.findViewById(R.id.liturgiesTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.liturgy_raw, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //val liturgies = liturgiesList[position]
        // holder.liturgiesTitle.text = liturgies.getTitle()
        //holder.coverImage.text = liturgies.getImage()
    }

    override fun getItemCount(): Int {
        return 3
        //return liturgiesList.size
    }
}

/*
class MyLiturgyAdapter(private var liturgiesList: List<LiturgiesModel>) :
    RecyclerView.Adapter<MyLiturgyAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var coverImage: Image = view.findViewById(R.id.coverImage)
        var liturgiesTitle: TextView = view.findViewById(R.id.liturgiesTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.liturgy_raw, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //val liturgies = liturgiesList[position]
        // holder.liturgiesTitle.text = liturgies.getTitle()
        //holder.coverImage.text = liturgies.getImage()
    }

    override fun getItemCount(): Int {
        return liturgiesList.size
    }
}*/
