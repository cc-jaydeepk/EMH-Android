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
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.PrivateSharingRequestVo
import com.everymomentholy.api.request.SetFavouriteRequestVo
import com.everymomentholy.api.response.BaseResponseVo
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.everymomentholy.api.response.PrivateShareResponseVo
import com.everymomentholy.interfaces.LiturgyLitstClickListner
import com.everymomentholy.interfaces.PlayAudioClickListner
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.activity.PlayAudioActivity
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.folioreader.emh.EMHUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class FeaturedAdapter(
    var context: Context,
    var featuredLiturgiesList: ArrayList<MyLiturgiesDataVo>,
    var playAudioClickListner: PlayAudioClickListner
) : RecyclerView.Adapter<FeaturedAdapter.MyViewHolder>() {
    public var bookOpenPosition = -1
    lateinit var progressDialog: ProgressDialog

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var btnFeaturedReadNow = view.findViewById<Button>(R.id.btn_featured_read_now)
        var imgFeaturedFav = view.findViewById<ImageView>(R.id.img_featured_fav)
        var imgFeaturedShare = view.findViewById<ImageView>(R.id.img_featured_share)
        var txtFeaturedTitle = view.findViewById<TextView>(R.id.txtFeaturedTitle)
        var txtIncludedIn = view.findViewById<TextView>(R.id.txtIncludedIn)
        var txtInclude = view.findViewById<TextView>(R.id.txtInclude)
        var imgFeatured = view.findViewById<ImageView>(R.id.imgFeatured)
        var btnPlayNow = view.findViewById<Button>(R.id.btnPlayNow)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.featured_raw, parent, false)
        return FeaturedAdapter.MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var featuredLiturgyData = featuredLiturgiesList[position]

        holder.txtFeaturedTitle.text = featuredLiturgyData.chapterTitle
        //holder.txtIncludedIn.text = featuredLiturgyData.volumeTags
        val spannable = SpannableString(featuredLiturgyData.volumeTags)
        spannable.setSpan(
            UnderlineSpan(),
            0, // start
            spannable.length, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        if (featuredLiturgyData.volumeTags == "") {
            holder.txtIncludedIn.visibility = View.GONE
            holder.txtInclude.visibility = View.GONE
        } else {
            holder.txtInclude.visibility = View.VISIBLE
            holder.txtIncludedIn.text = spannable
        }
        Glide.with(context)
            .load(featuredLiturgyData.chapterPageImage)
            .into(holder.imgFeatured)

        holder.btnFeaturedReadNow.setOnClickListener() {
            bookOpenPosition = position
            progressDialog = Utils.showProgressDialog(context)!!
            progressDialog.show()
            val cw = ContextWrapper(context)
            val directory = cw.getDir("files", AppCompatActivity.MODE_PRIVATE)
            if (!directory.exists()) {
                directory.mkdir()
            }
            var path = context?.filesDir?.absolutePath
            val downloadId =
                PRDownloader.download(
                    featuredLiturgyData.chapterUrl,
                    path,
                    "test_" + featuredLiturgyData.chapterId + ".epub"
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
                            Utils.invokeBookReader(
                                context,
                                context?.filesDir?.absolutePath + "/" + "test_" + featuredLiturgyData.chapterId + ".epub",
                                featuredLiturgyData
                            )
                        }

                        override fun onError(error: com.downloader.Error?) {

                        }
                    })
            Log.e("id", downloadId.toString())

        }



        holder.imgFeaturedShare.setOnClickListener() {
            if (Utils.isNetworkAvailable(context)) {
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    (context as MainActivity).showLoginDialog()
                } else {
                    privateShareLiturgy(featuredLiturgyData)
                }
            } else {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.check_internet),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        if(featuredLiturgiesList[position].isPurchased.equals("Yes", true)){
            if (TextUtils.isEmpty(featuredLiturgiesList[position].audio_file)){
                holder.btnPlayNow.visibility = View.GONE
            }else{
                holder.btnPlayNow.visibility = View.VISIBLE
            }
        }else{
            holder.btnPlayNow.visibility = View.GONE
        }

        holder.btnPlayNow.setOnClickListener {


            playAudioClickListner.onPlayAudio(
                featuredLiturgiesList[position].chapterTitle,
                featuredLiturgiesList[position].audio_file,
                false
            )
        }

        holder.imgFeaturedFav.setOnClickListener() {
            if (Utils.isNetworkAvailable(context)) {
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    (context as MainActivity).showLoginDialog()
                } else {
                    setLiturgiesFavourite(holder.imgFeaturedFav, featuredLiturgyData, position)
                }
            } else {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.check_internet),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        if (featuredLiturgyData.isFavorite == "True") {
            holder.imgFeaturedFav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favourite_fill))
        } else {
            holder.imgFeaturedFav.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite))
        }
    }

    override fun getItemCount(): Int {
        return featuredLiturgiesList.size
        //return favLiturgyList.size
    }

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    private fun privateShareLiturgy(liturgyDataVo: MyLiturgiesDataVo) {

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

    private fun setLiturgiesFavourite(
        ivfav: ImageView,
        liturgiesDataVo: MyLiturgiesDataVo,
        position: Int
    ) {

        var setFavouriteRequestVo = SetFavouriteRequestVo()

        setFavouriteRequestVo.userId = Utils.readIntData(context, Constants.PrefUserID, -1)
        setFavouriteRequestVo.isFavorite = liturgiesDataVo.isFavorite != "True"
        setFavouriteRequestVo.bookId = liturgiesDataVo.bookId
        setFavouriteRequestVo.chapterId = liturgiesDataVo.chapterId
        setFavouriteRequestVo.type = "liturgy"

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
                        if (liturgiesDataVo.isFavorite == "True") {
                            ivfav.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favorite))
                            Toast.makeText(
                                context,
                                "Removed from favorite",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            ivfav.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favourite_fill))
                            Toast.makeText(
                                context,
                                "Added to favorite",
                                Toast.LENGTH_LONG
                            ).show()
                        }
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
            featuredLiturgiesList[position].isFavorite = "True"
        } else {
            featuredLiturgiesList[position].isFavorite = "False"
        }
        notifyDataSetChanged()
    }

    public fun updateFavoriteStatusFromBookReadFeatured() {
        if (EMHUtils.favoriteFlagChange) {
            if (bookOpenPosition != -1) {
                if (EMHUtils.favoriteStatusChange) {
                    featuredLiturgiesList[bookOpenPosition].isFavorite = "True"
                } else {
                    featuredLiturgiesList[bookOpenPosition].isFavorite = "False"
                }

            }
            notifyDataSetChanged()
        }
    }
}