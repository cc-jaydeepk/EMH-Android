package com.everymomentholy.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
import com.everymomentholy.utils.Utils
import kotlinx.coroutines.GlobalScope

class SearchAdapter(var context: Context, var searchedLiturgies: ArrayList<MyLiturgiesDataVo>) :
    RecyclerView.Adapter<SearchAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var ivSearchLiturgies = view.findViewById<ImageView>(R.id.iv_search_liturgies)
        var txtSearchLiturgyFree = view.findViewById<TextView>(R.id.txt_search_liturgy_free)
        var txtSearchLiturgyReadNow = view.findViewById<TextView>(R.id.txt_search_liturgy_read_now)
        var txtSearchLiturgyTitle = view.findViewById<TextView>(R.id.txt_search_liturgy_title)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.search_raw, parent, false)
        return SearchAdapter.MyViewHolder(itemView)
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var myLiturgiesDataVo = searchedLiturgies[position]

        holder.txtSearchLiturgyTitle.text = myLiturgiesDataVo.chapterTitle
        Glide.with(context)
            .load(myLiturgiesDataVo.chapterPageImage)
            .into(holder.ivSearchLiturgies)

        if (myLiturgiesDataVo.isPurchased == "Yes" || myLiturgiesDataVo.isFree == "Yes" ||
            myLiturgiesDataVo.isFeatured == "Yes"
        ) {
            holder.txtSearchLiturgyReadNow.text = "Read Now"
            var sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.txtSearchLiturgyReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                holder.txtSearchLiturgyReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
            } else {
                holder.txtSearchLiturgyReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                holder.txtSearchLiturgyReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
            }
        } else {
            holder.txtSearchLiturgyReadNow.text = "Buy Now"
            holder.txtSearchLiturgyReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_unlock));
            holder.txtSearchLiturgyReadNow.setTextColor(context.resources.getColor(R.color.white))
        }

        when {
            myLiturgiesDataVo.isPurchased == "Yes" -> {
                holder.txtSearchLiturgyFree.text = "Purchased"
            }
            myLiturgiesDataVo.isFree == "Yes" -> {
                holder.txtSearchLiturgyFree.text = "Free"
            }
            myLiturgiesDataVo.isFeatured == "Yes" -> {
                holder.txtSearchLiturgyFree.text = "Featured"
            }
            else -> {
                holder.txtSearchLiturgyFree.text = "$" + myLiturgiesDataVo.price
            }
        }

        holder.txtSearchLiturgyReadNow.setOnClickListener() {
            if (holder.txtSearchLiturgyReadNow.text.toString().trim() == "Read Now") {
                readBook(myLiturgiesDataVo)
            } else if (holder.txtSearchLiturgyReadNow.text.toString().trim() == "Buy Now") {
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(myLiturgiesDataVo)
                } else {
                    startPurchaseFlow(myLiturgiesDataVo)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return searchedLiturgies.size
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

    @SuppressLint("HardwareIds")
    private fun startPurchaseFlow(myLiturgyDataVo: MyLiturgiesDataVo) {
        val deviceId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val userId = Utils.readIntData(
            context,
            Constants.PrefUserID,
            0
        )
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
        val dialog = alertDialog.show()
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
            dialog.dismiss()
        }

        alertButtonPurchase.setOnClickListener() {
            dialog.dismiss()
            startPurchaseFlow(myLiturgyDataVo)
        }
        dialog.setCanceledOnTouchOutside(false)
    }
}