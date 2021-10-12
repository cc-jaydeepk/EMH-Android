package com.everymomentholy.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.response.GetLiturgiesDataVo


class GetLiturgiesAdapter(
    var context: Context,
   // var getLiturgiesList: List<LiturgiesDataVo>
    var getLiturgiesList: List<GetLiturgiesDataVo>
) :
    PagerAdapter() {
    override fun getCount(): Int {
        return getLiturgiesList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.getliturgies_raw, container, false)
        var imgGetLiturge: ImageView = view.findViewById(R.id.imgGetLiturge)
        var txtLiturgyTitle: TextView = view.findViewById(R.id.txtLiturgyTitle)
        var txtLiturgyPrice: TextView = view.findViewById(R.id.txtLiturgyPrice)

        val getLiturgies = getLiturgiesList[position]

        txtLiturgyTitle.text = getLiturgies.volumeTitle
        txtLiturgyPrice.text = getLiturgies.volumeAmount

        Glide.with(context)
            .load(getLiturgies.volumeCoverPageImage)
            .into(imgGetLiturge)

        container.addView(view)
        return view
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }
}