package com.everymomentholy.ui.adapter

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.everymomentholy.R
import com.everymomentholy.api.response.CollectionDataVo
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
        var btnUnlock = view.findViewById<Button>(R.id.btnUnlock)
        var imageBook = view.findViewById<ImageView>(R.id.imageBook)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.collection_raw, parent, false)
        return GetLiturgiesFromBookIDAdapter.MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (position == 0) {
            holder.imageBook.visibility = View.VISIBLE
            holder.btnUnlock.text = "Unlock Collection"
        } else {
            holder.imageBook.visibility = View.GONE
            if (liturgyList[position].isFree == "Yes") {
                holder.btnUnlock.text = "Read Now"
            } else {
                holder.btnUnlock.text = "Unlock"
            }
        }

        holder.txtLiturgyName.text = liturgyList[position].chapterTitle

        if (liturgyList[position].isFree == "Yes") {
            holder.txtPrice.text = "Free"
        } else {
            holder.txtPrice.text = "$" + liturgyList[position].price
        }

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
                        "test_" +  liturgyList[position].chapterId + ".epub"
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
                                config?.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL)
                                folioReader.setConfig(config, true)

                                folioReader.openBook(context?.filesDir?.absolutePath + "/" + "test_" +  liturgyList[position].chapterId + ".epub")
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
}