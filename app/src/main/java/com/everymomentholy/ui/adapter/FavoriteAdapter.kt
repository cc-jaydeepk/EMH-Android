package com.everymomentholy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.response.GetFavoritesDataVo
import com.everymomentholy.api.response.GetLiturgiesDataVo

class FavoriteAdapter(
    var context: Context,
    // var getLiturgiesList: List<LiturgiesDataVo>
    var favLiturgiesList: List<GetFavoritesDataVo>
) : RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgFavCover = view.findViewById<ImageView>(R.id.imgFavCover)
        var btnFavReadNow = view.findViewById<Button>(R.id.btn_fav_read_now)
        var imgFavorite = view.findViewById<ImageView>(R.id.img_favorite)
        var imgFavShareImg = view.findViewById<ImageView>(R.id.imgFavShareImg)
        var txtFavLiturgyName = view.findViewById<TextView>(R.id.txtFavLiturgyName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.favfragment, parent, false)
        return FavoriteAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.txtFavLiturgyName.text = favLiturgiesList[position].chapterTitle
        Glide.with(context)
            .load(favLiturgiesList[position].chapterPageImage)
            .into(holder.imgFavCover)

    }

    override fun getItemCount(): Int {
        return favLiturgiesList.size
        //return favLiturgyList.size
    }
}