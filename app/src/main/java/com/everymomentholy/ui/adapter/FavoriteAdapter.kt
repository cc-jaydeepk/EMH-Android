package com.everymomentholy.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.PrivateSharingRequestVo
import com.everymomentholy.api.request.SetFavouriteRequestVo
import com.everymomentholy.api.response.*
import com.everymomentholy.ui.activity.FavLiturgyListActivity
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.folioreader.emh.EMHUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class FavoriteAdapter(
    var context: Context,
    // var getLiturgiesList: List<LiturgiesDataVo>
    var favLiturgiesList: ArrayList<GetFavoritesDataVo>
) : RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {

    public var bookOpenPosition = -1
    lateinit var progressDialog: ProgressDialog

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgFavCover = view.findViewById<ImageView>(R.id.imgFavCover)
        var btnFavReadNow = view.findViewById<TextView>(R.id.btn_fav_read_now)
        var imgFavorite = view.findViewById<ImageView>(R.id.img_favorite)
        var imgFavShareImg = view.findViewById<ImageView>(R.id.imgFavShareImg)
        var txtFavLiturgyName = view.findViewById<TextView>(R.id.txtFavLiturgyName)
        var txtFavFree = view.findViewById<TextView>(R.id.txt_fav_free)
        var txtLiturgy = view.findViewById<TextView>(R.id.txtLiturgy)
        var txtIncludedIn = view.findViewById<TextView>(R.id.txtIncludedIn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.raw_favorites, parent, false)
        return FavoriteAdapter.MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        val favLiturgy = favLiturgiesList[position]

        var subscriptionStatus = Utils.readStringFromSharedPref(
            context, Constants.USER_SUBSCRIPTIONSTATUS,
            ""
        )

        holder.txtFavLiturgyName.text = favLiturgiesList[position].chapterTitle
        //holder.txtIncludedIn.text = favLiturgiesList[position].volumeTags
        val spannable = SpannableString(favLiturgiesList[position].volumeTags)
        spannable.setSpan(
            UnderlineSpan(),
            0, // start
            spannable.length, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        holder.txtIncludedIn.text = spannable
        Glide.with(context)
            .load(favLiturgiesList[position].chapterPageImage)
            .into(holder.imgFavCover)

        holder.imgFavorite.setOnClickListener() {
            if (Utils.isNetworkAvailable(context)) {
                setLiturgiesFavourite(holder.imgFavorite, favLiturgiesList[position], position)
            } else {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.internet_required),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        holder.btnFavReadNow.setOnClickListener() {

            if (favLiturgiesList[position].chapterId == 0) {
                if (holder.btnFavReadNow.text == "SUBSCRIBE") {

                } else {
                    transferToLiturgyList(favLiturgy)
                }

            } else {

                bookOpenPosition = position

                val cw = ContextWrapper(context)
                val directory = cw.getDir("files", AppCompatActivity.MODE_PRIVATE)
                if (!directory.exists()) {
                    directory.mkdir()
                }

                var path = context?.filesDir?.absolutePath
                val bookFile =
                    File(path + "/test_" + favLiturgiesList[position].chapterId + ".epub")

                if (bookFile.exists() && bookFile.length() > 0) {
                    var myLiturgiesDataVo = MyLiturgiesDataVo()
                    myLiturgiesDataVo.bookId = favLiturgiesList[position].bookId
                    myLiturgiesDataVo.chapterId = favLiturgiesList[position].chapterId
                    myLiturgiesDataVo.isFavorite = favLiturgiesList[position].isFavorite
                    myLiturgiesDataVo.audio_file = favLiturgiesList[position].audio_file
                    myLiturgiesDataVo.isPurchased = favLiturgiesList[position].isPurchased

                    Utils.invokeBookReader(
                        context,
                        context?.filesDir?.absolutePath + "/" + "test_" + favLiturgiesList[position].chapterId + ".epub",
                        myLiturgiesDataVo,
                    )
                } else if (Utils.isNetworkAvailable(context)) {
                    progressDialog = Utils.showProgressDialog(context)!!
                    progressDialog.show()
                    val downloadId =
                        PRDownloader.download(
                            favLiturgiesList[position].chapterUrl,
                            path,
                            "test_" + favLiturgiesList[position].chapterId + ".epub"
                        )
                            .build()
                            .setOnStartOrResumeListener { }
                            .setOnPauseListener { }
                            .setOnCancelListener { }
                            .setOnProgressListener { }
                            .start(object : OnDownloadListener {
                                override fun onDownloadComplete() {
                                    Log.e("complete", "complete")
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss()
                                    }
                                    var myLiturgiesDataVo = MyLiturgiesDataVo()
                                    myLiturgiesDataVo.bookId = favLiturgiesList[position].bookId
                                    myLiturgiesDataVo.chapterId =
                                        favLiturgiesList[position].chapterId
                                    myLiturgiesDataVo.isFavorite =
                                        favLiturgiesList[position].isFavorite

                                    Utils.invokeBookReader(
                                        context,
                                        context?.filesDir?.absolutePath + "/" + "test_" + favLiturgiesList[position].chapterId + ".epub",
                                        myLiturgiesDataVo)
                                }

                                override fun onError(error: com.downloader.Error?) {

                                }
                            })
                    Log.e("id", downloadId.toString())
                } else {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.liturgy_not_downloaded),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        if (favLiturgiesList[position].isPurchased == "Yes") {
            holder.txtFavFree.text = "Purchased"
        } else if (favLiturgiesList[position].isFree == "Yes") {
            holder.txtFavFree.text = "Free"
        } else if (favLiturgiesList[position].isFeatured == "Yes") {
            holder.txtFavFree.text = "Featured"
        }

        if (favLiturgiesList[position].chapterId == 0) {
            holder.txtLiturgy.text = "Book"
            holder.imgFavShareImg.visibility = View.INVISIBLE
            holder.btnFavReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_unlock));
            holder.btnFavReadNow.setTextColor(context.resources.getColor(R.color.white))

            if (subscriptionStatus == "Yes") {
                holder.btnFavReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                holder.btnFavReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
                holder.btnFavReadNow.text = "READ NOW"
            } else {
                holder.btnFavReadNow.text = "SUBSCRIBE"
            }
        } else {
            holder.txtLiturgy.text = "Liturgy"
            holder.imgFavShareImg.visibility = View.VISIBLE
        }

        holder.imgFavShareImg.setOnClickListener() {
            if (Utils.isNetworkAvailable(context)) {
                privateShareLiturgy(favLiturgiesList[position])
            } else {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.internet_required),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    private fun transferToLiturgyList(favLiturgies: GetFavoritesDataVo) {
        var intent = Intent(context, FavLiturgyListActivity::class.java)
        intent.putExtra("favBookID", favLiturgies.bookId)
        intent.putExtra("favCollection", favLiturgies)
        intent.putExtra("onPressReadNow", true)
        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return favLiturgiesList.size
        //return favLiturgyList.size
    }

    private fun setLiturgiesFavourite(
        ivfav: ImageView,
        liturgiesDataVo: GetFavoritesDataVo,
        position: Int
    ) {

        var setFavouriteRequestVo = SetFavouriteRequestVo()

        setFavouriteRequestVo.userId = Utils.readIntData(context, Constants.PrefUserID, -1)
        setFavouriteRequestVo.isFavorite = liturgiesDataVo.isFavorite != "True"
        setFavouriteRequestVo.bookId = liturgiesDataVo.bookId
        setFavouriteRequestVo.chapterId = liturgiesDataVo.chapterId

        if (setFavouriteRequestVo.chapterId == 0) {
            setFavouriteRequestVo.type = "book"
        } else {
            setFavouriteRequestVo.type = "liturgy"
        }

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.setFavorite(
                setFavouriteRequestVo,
                "bearer " + Utils.readStringFromSharedPref(context, Constants.SHARED_PREF_TOKEN, "")
            )

        try {
            call.enqueue(object : Callback<BaseResponseVo> {
                @RequiresApi(Build.VERSION_CODES.CUPCAKE)
                override fun onResponse(
                    call: Call<BaseResponseVo>,
                    response: Response<BaseResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        /*if (liturgiesDataVo.isFavorite == "True") {
                            ivfav.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favorite))
                        } else {
                            ivfav.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favourite_fill))
                        }*/
                        updateList(setFavouriteRequestVo.isFavorite, position)
                    } else {
                        Toast.makeText(
                            context,
                            response.message().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponseVo>, t: Throwable) {
                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun updateList(favourite: Boolean, position: Int) {
        if (favourite) {
            favLiturgiesList[position].isFavorite = "True"
        } else {
            favLiturgiesList[position].isFavorite = "False"
            favLiturgiesList.remove(favLiturgiesList[position])
        }
        notifyDataSetChanged()
    }

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    private fun privateShareLiturgy(liturgyDataVo: GetFavoritesDataVo) {

        var privateSharingRequest: PrivateSharingRequestVo = PrivateSharingRequestVo()
        privateSharingRequest.deviceId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        privateSharingRequest.userId =
            Utils.readIntFromSharedPref(context, Constants.PrefUserID, -1)
        privateSharingRequest.liturgyId = liturgyDataVo.chapterId

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.privateSharing(
                privateSharingRequest,
                "bearer " + Utils.readStringFromSharedPref(context, Constants.SHARED_PREF_TOKEN, "")
            )

        try {
            call.enqueue(object : Callback<PrivateShareResponseVo> {
                @RequiresApi(Build.VERSION_CODES.CUPCAKE)
                override fun onResponse(
                    call: Call<PrivateShareResponseVo>,
                    response: Response<PrivateShareResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        val builder = AlertDialog.Builder(
                            context
                        )
                        val inflater = (context as Activity).layoutInflater
                        val view: View =
                            inflater.inflate(R.layout.share_dialog, null)
                        builder.setView(view)
                        val bottom = builder.show()

                        val edtShareDialogUrl =
                            view.findViewById<View>(R.id.edt_share_dialog_url) as TextView

                        val btnShareDialogShareLink =
                            view.findViewById<View>(R.id.btn_share_dialog_share_link) as TextView
                        val btnShareDialogCancel =
                            view.findViewById<View>(R.id.btn_share_dialog_cancel) as TextView

                        edtShareDialogUrl.text = response.body()!!.response
                        bottom.setCanceledOnTouchOutside(false);
                        btnShareDialogCancel.setOnClickListener() {
                            bottom.dismiss()
                        }

                        btnShareDialogShareLink.setOnClickListener() {
                            val intent = Intent()
                            intent.action = Intent.ACTION_SEND
                            intent.type = "text/plain"
                            intent.putExtra(Intent.EXTRA_TEXT, response.body()!!.response)
                            context.startActivity(Intent.createChooser(intent, "Share With"))
                        }

                    } else {
                        Toast.makeText(
                            context,
                            response.message().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PrivateShareResponseVo>, t: Throwable) {
                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun updateFavoriteStatusFromBookRead() {
        if (EMHUtils.favoriteFlagChange) {
            if (bookOpenPosition != -1) {
                if (EMHUtils.favoriteStatusChange) {
                    favLiturgiesList[bookOpenPosition].isFavorite = "True"
                } else {
                    favLiturgiesList[bookOpenPosition].isFavorite = "False"
                    favLiturgiesList.removeAt(bookOpenPosition)
                }

            }
            notifyDataSetChanged()
        }
    }
}