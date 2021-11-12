package com.everymomentholy.ui.adapter

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.everymomentholy.R
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.folioreader.Config
import com.folioreader.FolioReader
import com.folioreader.util.AppUtil

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

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.collection_raw, parent, false)
        return GetLiturgiesFromBookIDAdapter.MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

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
                                val folioReader = FolioReader.get()

                                var config = AppUtil.getSavedConfig(context);
                                if (config == null) {
                                    //   config : Config ()
                                }
                                config?.setThemeColorRes(R.color.loginbg)
                                config?.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL)
                                folioReader.setConfig(config, true)

                                folioReader.openBook(context?.filesDir?.absolutePath + "/" + "test_" + liturgyList[position].chapterId + ".epub")
                            }

                            override fun onError(error: com.downloader.Error?) {

                            }
                        })
                Log.e("id", downloadId.toString())

            }
        }
    }

    override fun getItemCount(): Int {
        return liturgyList.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }
}