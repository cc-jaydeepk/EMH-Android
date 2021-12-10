package com.everymomentholy.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.request.PurchaseRequestVo
import com.everymomentholy.api.response.CollectionDataVo
import com.everymomentholy.ui.activity.LiturgiesListActivity
import com.everymomentholy.ui.activity.SelectOptionActivity
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.InAppUtils
import com.everymomentholy.utils.ProductTypes
import com.everymomentholy.utils.Utils
import kotlinx.coroutines.GlobalScope

class BottomSliderCollectionAdapter(
    var context: Context,
    var liturgyList: List<CollectionDataVo>,
) : RecyclerView.Adapter<BottomSliderCollectionAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imgFreeLiturgiescover = view.findViewById<ImageView>(R.id.imgFreeLiturgiescover)
        var txtfreeLiturgiesTitle = view.findViewById<TextView>(R.id.txtFreeLiturgiesTitle)
        var btnReadNow = view.findViewById<TextView>(R.id.btnReadNow)
        var txtLiturgiesPrice = view.findViewById<TextView>(R.id.txtLiturgiesPrice)
        var llBottomSliderGetLiturgiesAbout =
            view.findViewById<LinearLayout>(R.id.llBottomSliderGetLiturgiesAbout)
        var imageBook = view.findViewById<ImageView>(R.id.imageBook)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.raw_bottom_slider_get_liturgies_about, parent, false)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val freeLiturgies = liturgyList[position]

        if (position == 0) {
            if (freeLiturgies.isPurchased == "Yes") {
                if (freeLiturgies.bookAmount == "0.0" || freeLiturgies.bookAmount == "0.00" || freeLiturgies.isPurchased == "Yes") {
                    holder.btnReadNow.text = "Read Now"
                    var sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.btnReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                        holder.btnReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
                    } else {
                        holder.btnReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                        holder.btnReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
                    }
                    if (freeLiturgies.isPurchased == "Yes") {
                        holder.txtLiturgiesPrice.text = "Purchased"
                    } else {
                        holder.txtLiturgiesPrice.text = "Free"
                    }
                }
            } else {
                if (freeLiturgies.bookAmount == "0.0" || freeLiturgies.bookAmount == "0.00") {
                    holder.btnReadNow.text = "Read Now"
                    var sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.btnReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                        holder.btnReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
                    } else {
                        holder.btnReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                        holder.btnReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
                    }
                    if (freeLiturgies.isPurchased == "Yes") {
                        holder.txtLiturgiesPrice.text = "Purchased"
                    } else {
                        holder.txtLiturgiesPrice.text = "Free"
                    }
                } else {
                    holder.btnReadNow.text = "Unlock Volume"
                    holder.imageBook.setImageDrawable(context.resources.getDrawable(R.drawable.ic_volume))
                    holder.txtLiturgiesPrice.text = "$" + freeLiturgies.bookAmount
                }
            }
        } else {
            if (freeLiturgies.bookAmount == "0.0" || freeLiturgies.bookAmount == "0.00" || freeLiturgies.isPurchased == "Yes") {
                holder.btnReadNow.text = "Read Now"
                var sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.btnReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    holder.btnReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
                } else {
                    holder.btnReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    holder.btnReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
                }
                if (freeLiturgies.isPurchased == "Yes") {
                    holder.txtLiturgiesPrice.text = "Purchased"
                } else {
                    holder.txtLiturgiesPrice.text = "Free"
                }
            } else {
                holder.btnReadNow.text = "Unlock Collection"
                holder.txtLiturgiesPrice.text = "$" + freeLiturgies.bookAmount
            }
        }
        holder.txtfreeLiturgiesTitle.text = freeLiturgies.bookTitle

        Glide.with(context)
            .load(freeLiturgies.bookCoverPageImage)
            .into(holder.imgFreeLiturgiescover)

        /*  if (freeLiturgies.isPurchased == "Yes") {

          } else {
              holder.btnReadNow.text = "Unlock Collection"
          }*/

        holder.btnReadNow.setOnClickListener() {
            if (holder.btnReadNow.text == "Unlock Collection") {
                freeLiturgies.productTypes = ProductTypes.BOOK
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(freeLiturgies)
                } else {
                    startPurchaseFlow(freeLiturgies)
                }
            } else if (holder.btnReadNow.text == "Unlock Volume") {
                freeLiturgies.productTypes = ProductTypes.VOLUME
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(freeLiturgies)
                } else {
                    if (!freeLiturgies.discountAmount.isNullOrEmpty() && freeLiturgies.discountAmount != "0.00") {
                        freeLiturgies.bookAmount = freeLiturgies.discountAmount
                    }
                    startPurchaseFlow(freeLiturgies)
                }
            } else if (holder.btnReadNow.text == "Read Now") {
                transferToLiturgyList(freeLiturgies)
            } else {
                if (freeLiturgies.isPurchased == "Yes") {
                    //if (position > 0) {
                    transferToLiturgyList(freeLiturgies)
                    //}
                } else {
                    if (position == 0 && freeLiturgies.bookAmount == "0.00" || freeLiturgies.bookAmount == "0.0") {
                        transferToLiturgyList(freeLiturgies)
                    }
                }
            }
        }

        holder.llBottomSliderGetLiturgiesAbout.setOnClickListener() {

            if (freeLiturgies.isPurchased == "Yes") {
                //if (position > 0) {
                transferToLiturgyList(freeLiturgies)
                //}
            } else {
                if (position == 0 && freeLiturgies.bookAmount == "0.00" || freeLiturgies.bookAmount == "0.0") {
                    transferToLiturgyList(freeLiturgies)
                } else {
                    if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                        if (holder.btnReadNow.text == "Read Now") {
                            transferToLiturgyList(freeLiturgies)
                        } else {
                            showDialogForUnlockWithoutLogin(freeLiturgies)
                        }
                    } else {
                        if (position > 0) {
                            transferToLiturgyList(freeLiturgies)
                        }
                    }
                }

            }
        }

    }

    override fun getItemCount(): Int {
        return liturgyList.size

    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    private fun transferToLiturgyList(freeLiturgies: CollectionDataVo) {
        var intent = Intent(context, LiturgiesListActivity::class.java)
        intent.putExtra("bookID", freeLiturgies.bookId)
        intent.putExtra("collection", freeLiturgies)
        context.startActivity(intent)
    }

    private fun startPurchaseFlow(collectionDataVo: CollectionDataVo) {
        val deviceId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val userId = Utils.readIntData(
            context,
            Constants.PrefUserID,
            0
        )!!
        val purchaseRequestVo = PurchaseRequestVo(
            userId,
            bookId = collectionDataVo.bookId,
            amount = collectionDataVo.bookAmount,
            deviceId = deviceId,
            liturgyId = 0,
            volumeId = collectionDataVo.volumeId,
            productType = collectionDataVo.productTypes
        )

        val inAppUtils =
            InAppUtils.getInstance((context as Activity).application, GlobalScope)
        inAppUtils.initiatePurchaseFlow(context as Activity, purchaseRequestVo)
    }

    fun showDialogForUnlockWithoutLogin(myLiturgyDataVo: CollectionDataVo) {
        val alertDialog = AlertDialog.Builder(
            context
        )
        val inflater = (context as Activity).layoutInflater
        val alertView: View = inflater.inflate(R.layout.purchase_without_login_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val alertButtonCancel = alertView.findViewById<View>(R.id.txtCancel) as TextView
        val alertButtonLoginRegister =
            alertView.findViewById<View>(R.id.txtPurchaseRegisterLogin) as TextView
        val alertButtonPurchase =
            alertView.findViewById<View>(R.id.txtPurchaseWithoutRegisterLogin) as TextView


        alertButtonLoginRegister.setOnClickListener {
            val intent = Intent(context, SelectOptionActivity::class.java)
            context.startActivity(intent)
        }

        alertButtonCancel.setOnClickListener {
            show.dismiss()
        }

        alertButtonPurchase.setOnClickListener() {
            show.dismiss()
            startPurchaseFlow(myLiturgyDataVo)
        }
        show.setCanceledOnTouchOutside(false)
    }
}