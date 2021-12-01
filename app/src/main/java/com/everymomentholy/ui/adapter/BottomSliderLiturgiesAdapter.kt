package com.everymomentholy.ui.adapter

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
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
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.everymomentholy.utils.Utils
import com.everymomentholy.ui.activity.ForgotPasswordActivity
import com.everymomentholy.ui.activity.LiturgiesListActivity
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.utils.Constants
import com.folioreader.Config
import com.folioreader.FolioReader
import com.folioreader.util.AppUtil

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
            var sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.btnReadNow.background =
                    context.resources.getDrawable(R.drawable.bg_read_now);
                holder.btnReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
            } else {
                holder.btnReadNow.background =
                    context.resources.getDrawable(R.drawable.bg_read_now);
                holder.btnReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
            }
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
            if (holder.btnReadNow.text == "Purchase Collection") {
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    Utils.showDialogForUnlockWithoutLogin(context)
                }
            } else if (holder.btnReadNow.text == "Unlock") {
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    Utils.showDialogForUnlockWithoutLogin(context)
                }
            } else if (holder.btnReadNow.text == "Read Now") {
                readBook(freeLiturgies)
            }
        }

        holder.llBottomSliderGetLiturgiesAbout.setOnClickListener() {
            if (holder.btnReadNow.text == "Purchase Collection") {
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
}