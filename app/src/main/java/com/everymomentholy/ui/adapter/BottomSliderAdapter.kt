package com.everymomentholy.ui.adapter

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.downloader.OnCancelListener
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.everymomentholy.R
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.folioreader.Config
import com.folioreader.FolioReader
import com.folioreader.util.AppUtil
import java.io.File


class BottomSliderAdapter(
    var context: Context,
    var liturgyList: List<MyLiturgiesDataVo>,
) : RecyclerView.Adapter<BottomSliderAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imgShare = view.findViewById<ImageView>(R.id.imgShare)
        var imgFreeLiturgiescover = view.findViewById<ImageView>(R.id.imgFreeLiturgiescover)
        var txtfreeLiturgiesTitle = view.findViewById<TextView>(R.id.txtFreeLiturgiesTitle)
        var btnReadNow = view.findViewById<Button>(R.id.btnReadNow)
        var txtFree = view.findViewById<TextView>(R.id.txt_free)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.bottom_slider_raw, parent, false)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val freeLiturgies = liturgyList[position]

        holder.txtfreeLiturgiesTitle.text = freeLiturgies.chapterTitle

        Glide.with(context)
            .load(freeLiturgies.chapterPageImage)
            .into(holder.imgFreeLiturgiescover)

        holder.imgShare.setOnClickListener {
           /* val builder = AlertDialog.Builder(context)
            val view: View = LayoutInflater.from(context).inflate(R.layout.share_dialog, null)
            builder.setView(view)
            builder.show()*/
        }

        holder.btnReadNow.setOnClickListener() {
            val cw = ContextWrapper(context)
            val directory = cw.getDir("files", AppCompatActivity.MODE_PRIVATE)
            if (!directory.exists()) {
                directory.mkdir()
            }
            var path = context?.filesDir?.absolutePath
            val downloadId =
                PRDownloader.download(
                    freeLiturgies.chapterUrl,
                    path,
                    "test_" + freeLiturgies.chapterId + ".epub"
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

                            folioReader.openBook(context?.filesDir?.absolutePath + "/" + "test_" + freeLiturgies.chapterId + ".epub")

                            /*   var config = AppUtil.getSavedConfig(context);
                               if (config == null) {
                                   //   config : Config ()
                               }
                               config?.setAllowedDirection(Config.AllowedDirection.VERTICAL_AND_HORIZONTAL)*/

                            //folioReader.setConfig(config, true)

                            /*folioReader.openBook(path + File.pathSeparator + "test" + ".epub")*/
                            /*folioReader.openBook((Environment.getExternalStorageDirectory().absolutePath + "/mnt/sdcard/before_shopping.epub"))*/
                            /* var path = context.getFilesDir()
                                 .getAbsolutePath() + "/" + "the_first_hearthfire_of_the_season.epub"*/
                            /* val folioReader = FolioReader.get()

                             folioReader.openBook(path + File.pathSeparator + "test" + ".epub")
                             //folioReader.openBook((Environment.getExternalStorageDirectory().absolutePath + "/before_shopping.epub"))
                             folioReader.openBook(R.raw.those_who_covet_the_latest_technology)*/

                        }

                        override fun onError(error: com.downloader.Error?) {

                        }
                    })
            Log.e("id", downloadId.toString())

        }

        if (freeLiturgies.isPurchased == "Yes") {
            holder.txtFree.text = "Purchased"
        } else {
            holder.txtFree.text = "Free"
        }

    }

    override fun getItemCount(): Int {
        return liturgyList.size

    }
}