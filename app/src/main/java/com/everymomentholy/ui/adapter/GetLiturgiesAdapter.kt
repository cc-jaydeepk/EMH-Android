package com.everymomentholy.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.response.GetLiturgiesDataVo
import com.everymomentholy.ui.fragments.AboutBookLiturgiesFragment
import com.everymomentholy.ui.activity.CollectionListActivity
import com.everymomentholy.ui.activity.LiturgiesListDialogActivity
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import kotlinx.coroutines.GlobalScope


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
        var btnGetLiturgiesAbout: TextView = view.findViewById(R.id.txtGetLiturgiesAbout)
        var btnUnlock: TextView = view.findViewById(R.id.txtUnlock)
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
                var sdk = Build.VERSION.SDK_INT;
                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
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
                if (!getLiturgies.discountAmount.isNullOrEmpty() && getLiturgies.discountAmount != "0.00") {
                    txtLiturgyPrice.text = "$" + getLiturgies.discountAmount
                } else {
                    txtLiturgyPrice.text = "$ " + getLiturgies.bookAmount
                }
            }

        } else {

            if (getLiturgies.bookAmount == "0.0" || getLiturgies.bookAmount == "0.00" || getLiturgies.isPurchased == "Yes") {
                btnUnlock.text = "Read Now"
                var sdk = Build.VERSION.SDK_INT;
                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
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
                if (!getLiturgies.discountAmount.isNullOrEmpty() && getLiturgies.discountAmount != "0.00") {
                    txtLiturgyPrice.text = "$" + getLiturgies.discountAmount
                } else {
                    txtLiturgyPrice.text = "$ " + getLiturgies.bookAmount
                }
                btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_unlock));
                btnUnlock.setTextColor(context.resources.getColor(R.color.white))
                btnUnlock.text = "Unlock"

            }
            txtLiturgyTitle.text = getLiturgies.bookTitle

            Glide.with(context)
                .load(getLiturgies.bookCoverPageImage)
                .into(imgGetLiturge)
        }

        container.addView(view)

        imgGetLiturge.setOnClickListener {
            if (getLiturgies.isFreeLiturgyAvailable == "No") {
                (context as MainActivity).showLiturgyDialog()
            } else {
                val bundle = Bundle()
                bundle.putSerializable("liturgies", getLiturgies)
                var fragment: Fragment = AboutBookLiturgiesFragment()
                (context as MainActivity).replaceFragment(fragment, "Get Liturgies", bundle)
            }
        }

        btnGetLiturgiesAbout.setOnClickListener() {
            val intent = Intent(context, AboutBookLiturgiesFragment::class.java)
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
                    if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                        Utils.showDialogForUnlockWithoutLogin(context)
                    } else {
                        startPurchaseFlow(getLiturgies.bookAmount)
                    }
                }
            }
        }
        return view
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    fun getLiturgiesData(): List<GetLiturgiesDataVo> {
        return getLiturgiesList
    }

    private fun startPurchaseFlow(price: String) {
        /*val inAppUtils =
            InAppUtils.getInstance((context as Activity).application, GlobalScope)*/
        // inAppUtils.initiatePurchaseFlow(context as Activity, price)
    }
}