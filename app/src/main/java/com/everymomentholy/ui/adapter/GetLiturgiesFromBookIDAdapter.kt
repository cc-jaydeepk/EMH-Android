package com.everymomentholy.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.everymomentholy.R
import com.everymomentholy.api.request.PurchaseRequestVo
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.InAppUtils
import com.everymomentholy.utils.ProductTypes
import com.everymomentholy.utils.Utils
import kotlinx.coroutines.GlobalScope

class GetLiturgiesFromBookIDAdapter(
    var context: Context,
    var liturgyList: List<MyLiturgiesDataVo>,
) : RecyclerView.Adapter<GetLiturgiesFromBookIDAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var txtLiturgyName = view.findViewById<TextView>(R.id.txtLiturgyName)
        var coverImage = view.findViewById<ImageView>(R.id.coverImage)
        var txtPrice = view.findViewById<TextView>(R.id.txtPrice)
        var btnUnlock = view.findViewById<TextView>(R.id.txtUnlock)
        var imageBook = view.findViewById<ImageView>(R.id.imageBook)
        var llCollectionRaw = view.findViewById<LinearLayout>(R.id.ll_collection_raw)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.collection_raw, parent, false)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        if (position == 0) {
            if (liturgyList[position].isFree == "Yes" || liturgyList[position].price == "0.0" || liturgyList[position].price == "0.00") {
                holder.imageBook.visibility = View.GONE
                holder.txtPrice.text = "Free"
                holder.btnUnlock.text = "Read Now"
                var sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    holder.btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                } else {
                    holder.btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    holder.btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                }
            } else if (liturgyList[position].isPurchased == "Yes") {
                holder.imageBook.visibility = View.GONE
                holder.txtPrice.text = "Purchased"
                holder.btnUnlock.text = "Read Now"
                var sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    holder.btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                } else {
                    holder.btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    holder.btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                }
            } else {
                holder.imageBook.visibility = View.VISIBLE
                holder.btnUnlock.text = "Unlock Collection"
                holder.txtPrice.text = "$" + liturgyList[position].price
            }
        } else {
            holder.imageBook.visibility = View.GONE
            if (liturgyList[position].isFree == "Yes") {
                holder.txtPrice.text = "Free"
                holder.btnUnlock.text = "Read Now"
                var sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    holder.btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                } else {
                    holder.btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    holder.btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                }
            } else if (liturgyList[position].isPurchased == "Yes") {
                holder.txtPrice.text = "Purchased"
                holder.btnUnlock.text = "Read Now"
                var sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    holder.btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                } else {
                    holder.btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    holder.btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                }
            } else {
                holder.txtPrice.text = "$" + liturgyList[position].price
                holder.btnUnlock.text = "Unlock"
                holder.btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_unlock));
                holder.btnUnlock.setTextColor(context.resources.getColor(R.color.white))
            }
        }

        holder.txtLiturgyName.text = liturgyList[position].chapterTitle

        Glide.with(context)
            .load(liturgyList[position].chapterPageImage)
            .into(holder.coverImage)

        holder.btnUnlock.setOnClickListener() {
            if (holder.btnUnlock.text == "Read Now") {
                val cw = ContextWrapper(context)
                val directory = cw.getDir("files", AppCompatActivity.MODE_PRIVATE)
                if (!directory.exists()) {
                    directory.mkdir()
                }
                var path = context?.filesDir?.absolutePath
                val downloadId =
                    PRDownloader.download(
                        liturgyList[position].chapterUrl,
                        path,
                        "test_" + liturgyList[position].chapterId + ".epub"
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
                                    context?.filesDir?.absolutePath + "/" + "test_" + liturgyList[position].chapterId + ".epub",
                                    liturgyList[position]
                                )

                            }

                            override fun onError(error: com.downloader.Error?) {

                            }
                        })
                Log.e("id", downloadId.toString())

            }
        }

        holder.llCollectionRaw.setOnClickListener() {
            if (holder.btnUnlock.text == "Read Now") {
                val cw = ContextWrapper(context)
                val directory = cw.getDir("files", AppCompatActivity.MODE_PRIVATE)
                if (!directory.exists()) {
                    directory.mkdir()
                }
                var path = context?.filesDir?.absolutePath
                val downloadId =
                    PRDownloader.download(
                        liturgyList[position].chapterUrl,
                        path,
                        "test_" + liturgyList[position].chapterId + ".epub"
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
                                    context?.filesDir?.absolutePath + "/" + "test_" + liturgyList[position].chapterId + ".epub",
                                    liturgyList[position]
                                )

                            }

                            override fun onError(error: com.downloader.Error?) {

                            }
                        })
                Log.e("id", downloadId.toString())
            } else if (holder.btnUnlock.text == "Unlock Collection") {
                liturgyList[position].productType = ProductTypes.BOOK
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    Utils.showDialogForUnlockWithoutLogin(context)
                } else {
                    startPurchaseFlow(liturgyList[position])
                }
            } else if (holder.btnUnlock.text == "Unlock") {
                liturgyList[position].productType = ProductTypes.LITURGY
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    Utils.showDialogForUnlockWithoutLogin(context)
                } else {
                    startPurchaseFlow(liturgyList[position])
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

}