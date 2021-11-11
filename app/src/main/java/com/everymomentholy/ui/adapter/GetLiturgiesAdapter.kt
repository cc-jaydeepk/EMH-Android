package com.everymomentholy.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.response.GetLiturgiesDataVo
import com.everymomentholy.ui.activity.AboutBookLiturgiesActivity
import com.everymomentholy.ui.activity.CollectionListActivity
import com.everymomentholy.ui.activity.LiturgiesListActivity
import com.everymomentholy.ui.activity.LiturgiesListDialogActivity


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

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.getliturgies_raw, container, false)
        var imgGetLiturge: ImageView = view.findViewById(R.id.imgGetLiturge)
        var txtLiturgyTitle: TextView = view.findViewById(R.id.txtLiturgyTitle)
        var txtLiturgyPrice: TextView = view.findViewById(R.id.txtLiturgyPrice)
        var btnGetLiturgiesAbout: TextView = view.findViewById(R.id.btnGetLiturgiesAbout)
        var btnUnlock: TextView = view.findViewById(R.id.btnUnlock)
        var txtDollar: TextView = view.findViewById(R.id.txtDollar)


        val getLiturgies = getLiturgiesList[position]

        if (getLiturgies.isVolume == "Yes") {

            txtLiturgyTitle.text = getLiturgies.volumeTitle

            Glide.with(context)
                .load(getLiturgies.volumeCoverPageImage)
                .into(imgGetLiturge)

           /* if (getLiturgies.isPurchased == "Yes") {
                txtLiturgyPrice.text = "Purchased"
            } else if (getLiturgies.bookAmount == "0.0" || getLiturgies.bookAmount == "0.00") {
                txtLiturgyPrice.text = "Free"
            } else {
                txtLiturgyPrice.text = "$ " + getLiturgies.volumeAmount
            }*/

            if (getLiturgies.bookAmount == "0.0" || getLiturgies.bookAmount == "0.00" || getLiturgies.isPurchased == "Yes") {
                btnUnlock.text = "Read Now"
                var sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                } else {
                    btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                }
                if (getLiturgies.isPurchased == "Yes") {
                    txtLiturgyPrice.text = "Purchased"
                } else {
                    txtLiturgyPrice.text = "Free"
                }
                txtDollar.text = ""
            } else {
                txtLiturgyPrice.text = "$ " + getLiturgies.bookAmount
            }

        } else {

            if (getLiturgies.bookAmount == "0.0" || getLiturgies.bookAmount == "0.00" || getLiturgies.isPurchased == "Yes") {
                btnUnlock.text = "Read Now"
                var sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                } else {
                    btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                }
                if (getLiturgies.isPurchased == "Yes") {
                    txtLiturgyPrice.text = "Purchased"
                } else {
                    txtLiturgyPrice.text = "Free"
                }
                txtDollar.text = ""
            } else {
                txtLiturgyPrice.text = "$ " + getLiturgies.bookAmount
            }
            txtLiturgyTitle.text = getLiturgies.bookTitle

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
            } else {
                if (btnUnlock.text == "Read Now") {
                    val intent = Intent(context, LiturgiesListDialogActivity::class.java)
                    intent.putExtra("liturgies", getLiturgies)
                    context.startActivity(intent)
                } else {
                    AlertDialog.Builder(context)
                        .setMessage("This part is under Development.")
                        .setPositiveButton(android.R.string.yes) { dialog, which ->
                        }.show()
                }
            }
        }
        return view
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }
}