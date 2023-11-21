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
import android.widget.*
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
import com.everymomentholy.api.response.BaseResponseVo
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.everymomentholy.api.response.PrivateShareResponseVo
import com.everymomentholy.interfaces.PlayAudioClickListner
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.activity.PlayAudioActivity
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.folioreader.emh.EMHUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class BottomSliderAdapter(
    var context: Context,
    var liturgyList: List<MyLiturgiesDataVo>,
    var playAudioClickListner: PlayAudioClickListner
) : RecyclerView.Adapter<BottomSliderAdapter.MyViewHolder>() {

    public var bookOpenPosition = -1
    lateinit var progressDialog: ProgressDialog

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imgShare = view.findViewById<ImageView>(R.id.imgShare)
        var imgFreeLiturgiescover = view.findViewById<ImageView>(R.id.imgFreeLiturgiescover)
        var txtfreeLiturgiesTitle = view.findViewById<TextView>(R.id.txtFreeLiturgiesTitle)
        var btnReadNow = view.findViewById<TextView>(R.id.btnReadNow)
        var txtFree = view.findViewById<TextView>(R.id.txt_free)
        var txtPlayNow = view.findViewById<TextView>(R.id.txtPlayNow)
        var txtIncludedIn = view.findViewById<TextView>(R.id.txtIncludedIn)
        var txtIncludedTag = view.findViewById<TextView>(R.id.txtIncludedTag)
        var imgFavorite = view.findViewById<ImageView>(R.id.imgFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.bottom_slider_raw, parent, false)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val freeLiturgy = liturgyList[position]

        holder.txtfreeLiturgiesTitle.text = freeLiturgy.chapterTitle
        holder.txtIncludedIn.text = freeLiturgy.volumeTags
        val spannable = SpannableString(freeLiturgy.volumeTags)
        spannable.setSpan(
            UnderlineSpan(),
            0, // start
            spannable.length, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        if (freeLiturgy.volumeTags == "") {
            holder.txtIncludedIn.visibility = View.GONE
            holder.txtIncludedTag.visibility = View.GONE
        } else {
            holder.txtIncludedTag.visibility = View.VISIBLE
            holder.txtIncludedIn.text = spannable
        }

        Glide.with(context)
            .load(freeLiturgy.chapterPageImage)
            .into(holder.imgFreeLiturgiescover)

        holder.imgFavorite.setOnClickListener() {
            if (Utils.isNetworkAvailable(context)) {
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    (context as MainActivity).showLoginDialog()
                } else {
                    setLiturgiesFavourite(holder.imgFavorite, liturgyList[position], position)
                }
            } else {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.internet_required),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

       /* if(liturgyList[position].isPurchased == "Yes"){
            if (liturgyList[position].audio_file == ""){
                holder.txtPlayNow.visibility = View.GONE
            }else{
                holder.txtPlayNow.visibility = View.VISIBLE
            }
        }else{
            holder.txtPlayNow.visibility = View.GONE
        }*/

        if(liturgyList[position].isPurchased.equals("Yes", true)){
            if (TextUtils.isEmpty(liturgyList[position].audio_file)){
                holder.txtPlayNow.visibility = View.GONE
            }else{
                holder.txtPlayNow.visibility = View.VISIBLE
            }
        }else{
            holder.txtPlayNow.visibility = View.GONE
        }

        holder.txtPlayNow.setOnClickListener {
           /* val intent = Intent(context, PlayAudioActivity::class.java)
            intent.putExtra("title", liturgyList[position].chapterTitle);
            intent.putExtra("audio", liturgyList[position].audio_file);
            intent.putExtra("URL", liturgyList[position].chapterPageImage);
            context.startActivity(intent)*/

            playAudioClickListner.onPlayAudio(
                liturgyList[position].chapterTitle,
                liturgyList[position].audio_file,
                false
            )

            /*val bundle = Bundle()
            bundle.putString("title", liturgyList[position].chapterTitle)
            bundle.putString("audio", liturgyList[position].audio_file);
            var fragment: Fragment = PlayAudioFragment()
            (context as MainActivity).replaceFragment(
                fragment,
                "Audio",
                bundle
            )*/

            /* holder.itemView.setOnClickListener {
                 val optionsFrag = PlayAudioFragment()
                 (context as MainActivity).getSupportFragmentManager().beginTransaction()
                     .replace(android.R.id.mainFrameLayout, optionsFrag, "OptionsFragment")
                     .addToBackStack(null).commit()
             }*/
        }

        holder.btnReadNow.setOnClickListener() {
            bookOpenPosition = position

            val cw = ContextWrapper(context)
            val directory = cw.getDir("files", AppCompatActivity.MODE_PRIVATE)
            if (!directory.exists()) {
                directory.mkdir()
            }
            var path = context?.filesDir?.absolutePath
            val bookFile = File(path + "/test_" + freeLiturgy.chapterId + ".epub")
            if (bookFile.exists() && bookFile.length() > 0) {
                Utils.invokeBookReader(
                    context,
                    context?.filesDir?.absolutePath + "/" + "test_" + freeLiturgy.chapterId + ".epub",
                    freeLiturgy
                )
            } else if (Utils.isNetworkAvailable(context)) {
                progressDialog = Utils.showProgressDialog(context)!!
                progressDialog.show()
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
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss()
                                }
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
            } else {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.liturgy_not_downloaded),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        if (freeLiturgy.isPurchased == "Yes") {
            holder.txtFree.text = "Purchased"
        } else if (freeLiturgy.isFree == "Yes") {
            holder.txtFree.text = "Free"
        }

        if (freeLiturgy.isFavorite == "True" || freeLiturgy.isFavorite == "true") {
            holder.imgFavorite.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favourite_fill))
        } else {
            holder.imgFavorite.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favorite))
        }

        holder.imgShare.setOnClickListener() {
            if (Utils.isNetworkAvailable(context)) {
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    (context as MainActivity).showLoginDialog()
                } else {
                    privateShareLiturgy(freeLiturgy)
                }
            } else {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.internet_required),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return liturgyList.size

    }

    private fun showUnderDevDialog() {
        AlertDialog.Builder(context)
            .setMessage("This part is under Development.")
            .setPositiveButton(android.R.string.yes) { dialog, which ->
            }.show()
    }

    // var liturgyList: List<MyLiturgiesDataVo>,


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
                        } else {
                            ivfav.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favourite_fill))
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
            liturgyList[position].isFavorite = "True"
        } else {
            liturgyList[position].isFavorite = "False"
        }
        notifyDataSetChanged()
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

    fun updateFavoriteStatusFromBookRead() {
        if (EMHUtils.favoriteFlagChange) {
            if (bookOpenPosition != -1) {
                if (EMHUtils.favoriteStatusChange) {
                    liturgyList[bookOpenPosition].isFavorite = "True"
                } else {
                    liturgyList[bookOpenPosition].isFavorite = "False"
                }
                notifyDataSetChanged()
            }
        }
    }
}