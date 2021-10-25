package com.everymomentholy.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.response.GetLiturgiesDataVo
import com.everymomentholy.ui.activity.AboutBookLiturgiesActivity
import com.everymomentholy.ui.activity.CollectionListActivity


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
        var btnGetLiturgiesAbout: Button = view.findViewById(R.id.btnGetLiturgiesAbout)
        var btnUnlock: Button = view.findViewById(R.id.btnUnlock)


        val getLiturgies = getLiturgiesList[position]

        if (getLiturgies.isVolume == "Yes") {

            txtLiturgyTitle.text = getLiturgies.volumeTitle
            txtLiturgyPrice.text = getLiturgies.volumeAmount

            Glide.with(context)
                .load(getLiturgies.volumeCoverPageImage)
                .into(imgGetLiturge)
        } else {

            txtLiturgyTitle.text = getLiturgies.bookTitle
            txtLiturgyPrice.text = getLiturgies.bookAmount

            Glide.with(context)
                .load(getLiturgies.bookCoverPageImage)
                .into(imgGetLiturge)
        }

        container.addView(view)


        btnGetLiturgiesAbout.setOnClickListener() {
            val intent = Intent(context, AboutBookLiturgiesActivity::class.java)
            intent.putExtra("liturgies", getLiturgies)
            context.startActivity(intent)
        }

        btnUnlock.setOnClickListener() {
            if (getLiturgies.isVolume == "Yes") {
                val intent = Intent(context, CollectionListActivity::class.java)
                intent.putExtra("liturgies", getLiturgies)
                context.startActivity(intent)
            }
            else
            {
                AlertDialog.Builder(context)
                    .setMessage("This part is under Development.")
                    .setPositiveButton(android.R.string.yes) { dialog, which ->
                    }.show()
            }
        }
        return view
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }
}