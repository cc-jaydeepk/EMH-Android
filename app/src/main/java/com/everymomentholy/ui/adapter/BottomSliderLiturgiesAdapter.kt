package com.everymomentholy.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.everymomentholy.R
import com.everymomentholy.api.request.PurchaseRequestVo
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.everymomentholy.ui.activity.SelectOptionActivity
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.InAppUtils
import com.everymomentholy.utils.ProductTypes
import com.everymomentholy.utils.Utils
import kotlinx.coroutines.GlobalScope

class BottomSliderLiturgiesAdapter(
    var context: Context,
    var liturgyList: List<MyLiturgiesDataVo>,
) : RecyclerView.Adapter<BottomSliderLiturgiesAdapter.MyViewHolder>() {

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

        holder.imageBook.visibility = View.GONE

        if (freeLiturgies.price == "0.00" || freeLiturgies.isPurchased == "Yes") {
            if (freeLiturgies.isPurchased == "Yes") {
                holder.txtLiturgiesPrice.text = "Purchased"
            } else {
                holder.txtLiturgiesPrice.text = "Free"
            }
            holder.btnReadNow.text = "Read Now"

            holder.btnReadNow.background =
                context.resources.getDrawable(R.drawable.bg_read_now);
            holder.btnReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
        } else {
            if (position == 0) {
                holder.btnReadNow.text = "Unlock Collection"
                holder.txtLiturgiesPrice.text = "$" + freeLiturgies.price
            } else {
                holder.btnReadNow.text = "Unlock"
                holder.txtLiturgiesPrice.text = "$" + freeLiturgies.price
            }
        }
//        }
        holder.txtfreeLiturgiesTitle.text = freeLiturgies.chapterTitle

        Glide.with(context)
            .load(freeLiturgies.chapterPageImage)
            .into(holder.imgFreeLiturgiescover)

        holder.btnReadNow.setOnClickListener() {
            if (holder.btnReadNow.text == "Unlock Collection") {
                freeLiturgies.productType = ProductTypes.BOOK
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(freeLiturgies)
                } else {
                    startPurchaseFlow(freeLiturgies)
                }
            } else if (holder.btnReadNow.text == "Unlock") {
                freeLiturgies.productType = ProductTypes.LITURGY
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(freeLiturgies)
                } else {
                    startPurchaseFlow(freeLiturgies)
                }
            } else if (holder.btnReadNow.text == "Read Now") {
                readBook(freeLiturgies)
            }
        }

        holder.llBottomSliderGetLiturgiesAbout.setOnClickListener() {
            if (holder.btnReadNow.text == "Unlock Collection") {
                freeLiturgies.productType = ProductTypes.BOOK
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(freeLiturgies)
                } else {
                    startPurchaseFlow(freeLiturgies)
                }
            } else if (holder.btnReadNow.text == "Read Now") {
                readBook(freeLiturgies)
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

    private fun readBook(freeLiturgy: MyLiturgiesDataVo) {
        val cw = ContextWrapper(context)
        val directory = cw.getDir("files", AppCompatActivity.MODE_PRIVATE)
        if (!directory.exists()) {
            directory.mkdir()
        }
        var path = context?.filesDir?.absolutePath
        val downloadId =
            PRDownloader.download(
                freeLiturgy.chapterUrl,
                path,
                "test_" + freeLiturgy.chapterId + ".epub"
            )
                .build()
                .setOnStartOrResumeListener { }
                .setOnPauseListener { }
                .setOnCancelListener { }
                .setOnProgressListener { }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        Log.e("complete", "complete")
                        Utils.invokeBookReader(
                            context,
                            context?.filesDir?.absolutePath + "/" + "test_" + freeLiturgy.chapterId + ".epub",
                            freeLiturgy
                        )
                    }

                    override fun onError(error: com.downloader.Error?) {

                    }
                })
        Log.e("id", downloadId.toString())
    }

    private fun startPurchaseFlow(myLiturgyDataVo: MyLiturgiesDataVo) {
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
            bookId = myLiturgyDataVo.bookId,
            amount = myLiturgyDataVo.price,
            deviceId = deviceId,
            liturgyId = myLiturgyDataVo.chapterId,
            volumeId = 0,
            productType = myLiturgyDataVo.productType
        )

        val inAppUtils =
            InAppUtils.getInstance((context as Activity).application, GlobalScope)
        inAppUtils.initiatePurchaseFlow(context as Activity, purchaseRequestVo)
    }

    fun showDialogForUnlockWithoutLogin(myLiturgyDataVo: MyLiturgiesDataVo) {
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