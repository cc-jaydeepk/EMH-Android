package com.everymomentholy.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.activity.PlayAudioActivity
import com.everymomentholy.ui.activity.SelectOptionActivity
import com.everymomentholy.ui.activity.SelectSubscriptionPlan
import com.everymomentholy.ui.fragments.SubscriptionPlanListFragment
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.ProductTypes
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class GetLiturgiesFromBookIDAdapter(
    var context: Context,
    var liturgyList: List<MyLiturgiesDataVo>
) : RecyclerView.Adapter<GetLiturgiesFromBookIDAdapter.MyViewHolder>() {

    lateinit var progressDialog: ProgressDialog

    lateinit var mediaPlayer: MediaPlayer

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var txtLiturgyName = view.findViewById<TextView>(R.id.txtLiturgyName)
        var coverImage = view.findViewById<ImageView>(R.id.coverImage)
        var txtPrice = view.findViewById<TextView>(R.id.txtPrice)
        var btnUnlock = view.findViewById<TextView>(R.id.txtUnlock)
        var imageBook = view.findViewById<ImageView>(R.id.imageBook)
        var llCollectionRaw = view.findViewById<LinearLayout>(R.id.ll_collection_raw)

        var btnPlayNow = view.findViewById<TextView>(R.id.txtPlayNow)
        var txtIncludedIn = view.findViewById<TextView>(R.id.txtIncludedIn)
        var txtIncludedText = view.findViewById<TextView>(R.id.txtIncludedText)
        var imgShare = view.findViewById<ImageView>(R.id.imgShare)
        var imgFavorite = view.findViewById<ImageView>(R.id.imgFavorite)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.collection_raw_new, parent, false)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        if (position == 0) {

            if (liturgyList[position].isFree.equals(
                    "Yes",
                    true
                ) || liturgyList[position].price == "0.0" || liturgyList[position].price == "0.00"
            ) {
                holder.imageBook.visibility = View.GONE
                holder.txtPrice.text = "Free"
                holder.btnUnlock.text = "Read Now"
                holder.txtIncludedText.visibility = View.GONE
                holder.txtIncludedIn.visibility = View.GONE
                holder.imgFavorite.visibility = View.VISIBLE
                holder.imgShare.visibility = View.VISIBLE

                holder.btnUnlock.setBackground(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_read_now
                    )
                );
                holder.btnUnlock.setTextColor(ContextCompat.getColor(context, R.color.loginbg))
            } else if (liturgyList[position].isPurchased.equals("Yes", true)) {
                holder.imageBook.visibility = View.GONE
                holder.txtIncludedText.visibility = View.GONE
                holder.txtIncludedIn.visibility = View.GONE
                holder.imgFavorite.visibility = View.VISIBLE
                holder.imgShare.visibility = View.VISIBLE
                holder.txtPrice.text = "Purchased"
                holder.btnUnlock.text = "Read Now"
                /* var sdk = android.os.Build.VERSION.SDK_INT;
                 if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                     holder.btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                     holder.btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                 } else {
                     holder.btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                     holder.btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                 }*/
                holder.btnUnlock.setBackground(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_read_now
                    )
                );
                holder.btnUnlock.setTextColor(ContextCompat.getColor(context, R.color.loginbg))
            } else {
                holder.imageBook.visibility = View.VISIBLE
                //holder.btnUnlock.text = "Subscribe Collection"
                holder.btnUnlock.text = "Subscribe"
                holder.txtPrice.text = "$" + liturgyList[position].price
                holder.imgFavorite.visibility = View.GONE
                holder.imgShare.visibility = View.GONE

                // holder.btnUnlock.visibility = View.GONE
                // holder.imgFavorite.visibility = View.GONE
              //  holder.imgShare.visibility = View.GONE

            }
        } else {
            holder.imageBook.visibility = View.GONE
            //DENISHA
            if (liturgyList[position].isFree.equals("Yes", true)) {
                holder.txtPrice.text = "Free"
                holder.btnUnlock.text = "Read Now"
                holder.btnUnlock.setBackground(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_read_now
                    )
                );
                holder.imgFavorite.visibility = View.VISIBLE
                holder.imgShare.visibility = View.VISIBLE
                holder.btnUnlock.setTextColor(ContextCompat.getColor(context, R.color.loginbg))
                //   holder.btnPlayNow.visibility = View.VISIBLE
                /*var sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    holder.btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                } else {
                    holder.btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    holder.btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                }*/
            } else if (liturgyList[position].isPurchased.equals("Yes", true)) {
                holder.txtPrice.text = "Purchased"
                holder.btnUnlock.text = "Read Now"
                holder.btnUnlock.setBackground(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_read_now
                    )
                );
                holder.imgFavorite.visibility = View.VISIBLE
                holder.imgShare.visibility = View.VISIBLE
                holder.btnUnlock.setTextColor(ContextCompat.getColor(context, R.color.loginbg))
                //  holder.btnPlayNow.visibility = View.VISIBLE
                /* var sdk = android.os.Build.VERSION.SDK_INT;
                 if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                     holder.btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                     holder.btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                 } else {
                     holder.btnUnlock.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                     holder.btnUnlock.setTextColor(context.resources.getColor(R.color.loginbg))
                 }*/
            } else {
                holder.txtPrice.text = "$" + liturgyList[position].price
                holder.btnUnlock.text = "Subscribe"
                holder.btnUnlock.setBackground(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_unlock
                    )
                );
                holder.imgFavorite.visibility = View.VISIBLE
                holder.imgShare.visibility = View.VISIBLE
                holder.btnUnlock.setTextColor(ContextCompat.getColor(context, R.color.white))


            }
        }

        holder.txtLiturgyName.text = liturgyList[position].chapterTitle
        val spannable = SpannableString(liturgyList[position].volumeTags)
        spannable.setSpan(
            UnderlineSpan(),
            0, // start
            spannable.length, // end
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )

        if (liturgyList[position].volumeTags == "") {
            holder.txtIncludedIn.visibility = View.GONE
            holder.txtIncludedText.visibility = View.GONE
            holder.txtIncludedIn.visibility = View.GONE
        } else {
            holder.txtIncludedText.visibility = View.VISIBLE
            holder.txtIncludedIn.visibility = View.VISIBLE
            holder.txtIncludedIn.text = spannable
        }

        Glide.with(context)
            .load(liturgyList[position].chapterPageImage)
            .into(holder.coverImage)

        mediaPlayer = MediaPlayer()

        if (liturgyList[position].isPurchased.equals("Yes", true)) {
            if (TextUtils.isEmpty(liturgyList[position].audio_file) || holder.btnUnlock.text.toString()
                    .equals("Subscribe", true)
            ) {
                holder.btnPlayNow.visibility = View.GONE
            } else {
                holder.btnPlayNow.visibility = View.VISIBLE
            }
        } else {
            holder.btnPlayNow.visibility = View.GONE
        }

        holder.btnPlayNow.setOnClickListener {
            Log.e("AUDIOURL", "onBindViewHolder: " + liturgyList[position].chapterPageImage)
            val intent = Intent(context, PlayAudioActivity::class.java)
            intent.putExtra("title", liturgyList[position].chapterTitle);
            intent.putExtra("audio", liturgyList[position].audio_file);
            intent.putExtra("URL", liturgyList[position].chapterPageImage);
            context.startActivity(intent)
        }

        if (liturgyList[position].isFavorite.equals(
                "True",
                true
            ) || liturgyList[position].isFavorite.equals("true", true)
        ) {
            holder.imgFavorite.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favourite_fill))
        } else {
            holder.imgFavorite.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favorite))
        }


        holder.imgFavorite.setOnClickListener {
            if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                showDialogForUnlockWithoutLogin(liturgyList[position])
            } else {
                setLiturgiesFavourite(holder.imgFavorite, liturgyList[position], position)
            }
        }

        holder.imgShare.setOnClickListener() {
            if (Utils.isNetworkAvailable(context)) {
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(liturgyList[position])
                } else {
                    privateShareLiturgy(liturgyList[position])
                }
            } else {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.internet_required),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        holder.btnUnlock.setOnClickListener() {
            if (holder.btnUnlock.text.toString().equals("Read Now", true)) {
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
                                Log.e("complete", "complete::")
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss()
                                }
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

//(holder.btnUnlock.text == "Subscribe Collection"
            } else if (holder.btnUnlock.text.toString().equals("Subscribe", true)) {
                liturgyList[position].productType = ProductTypes.BOOK
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(liturgyList[position])
                } else {
                    val intent = Intent(context, SelectSubscriptionPlan::class.java)
                    context?.startActivity(intent)
                    /*val bundle = Bundle()
                    bundle.putBoolean("onPress", true);
                    bundle.putBoolean("onPressHome", false);
                    var fragment: Fragment = SubscriptionPlanListFragment()
                    (context as MainActivity).replaceFragment(fragment, "Subscription Plans", bundle)*/
                    // startPurchaseFlow(liturgyList[position])
                }
            } else if (holder.btnUnlock.text.toString().equals("Subscribe", true)) {
                liturgyList[position].productType = ProductTypes.LITURGY
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(liturgyList[position])
                } else {
                    val intent = Intent(context, SelectSubscriptionPlan::class.java)
                    context?.startActivity(intent)
                    /*val bundle = Bundle()
                    bundle.putBoolean("onPress", true);
                    bundle.putBoolean("onPressHome", false);
                    var fragment: Fragment = SubscriptionPlanListFragment()
                    (context as MainActivity).replaceFragment(fragment, "Subscription Plans", bundle)*/
                    // startPurchaseFlow(liturgyList[position])
                }
            }
        }

        holder.llCollectionRaw.setOnClickListener() {
            if (holder.btnUnlock.text.toString().equals("Read Now", true)) {
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
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss()
                                }
                                Utils.invokeBookReader(
                                    context,
                                    context?.filesDir?.absolutePath + "/" + "test_" + liturgyList[position].chapterId + ".epub",
                                    liturgyList[position],
                                )

                            }

                            override fun onError(error: com.downloader.Error?) {

                            }
                        })
                Log.e("id", downloadId.toString())
                //holder.btnUnlock.text == "Subscribe Collection"
            } else if (holder.btnUnlock.text.toString().equals("Subscribe", true)) {
                liturgyList[position].productType = ProductTypes.BOOK
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(liturgyList[position])
                } else {
                    //  startPurchaseFlow(liturgyList[position])
                    val intent = Intent(context, SelectSubscriptionPlan::class.java)
                    context?.startActivity(intent)
                    /*val bundle = Bundle()
                    bundle.putBoolean("onPress", true);
                    bundle.putBoolean("onPressHome", false);
                    var fragment: Fragment = SubscriptionPlanListFragment()
                    (context as MainActivity).replaceFragment(fragment, "Subscription Plans", bundle)*/
                }
            } else if (holder.btnUnlock.text.toString().equals("Subscribe", true)) {
                liturgyList[position].productType = ProductTypes.LITURGY
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(liturgyList[position])
                } else {
                    //  startPurchaseFlow(liturgyList[position])

                    val intent = Intent(context, SelectSubscriptionPlan::class.java)
                    context?.startActivity(intent)

                  /*  val bundle = Bundle()
                    bundle.putBoolean("onPress", true);
                    bundle.putBoolean("onPressHome", false);
                    var fragment: Fragment = SubscriptionPlanListFragment()
                    (context as MainActivity).replaceFragment(fragment, "Subscription Plans", bundle)*/
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

    private fun setLiturgiesFavourite(
        ivfav: ImageView,
        liturgiesDataVo: MyLiturgiesDataVo,
        position: Int
    ) {

        var setFavouriteRequestVo = SetFavouriteRequestVo()

        //  var productType = liturgyList[position].productType = ProductTypes.BOOK

        setFavouriteRequestVo.userId = Utils.readIntData(context, Constants.PrefUserID, -1)
        setFavouriteRequestVo.isFavorite = liturgiesDataVo.isFavorite != "True"
        setFavouriteRequestVo.bookId = liturgiesDataVo.bookId
        setFavouriteRequestVo.chapterId = liturgiesDataVo.chapterId
        setFavouriteRequestVo.type = "liturgy"
        //setFavouriteRequestVo.type = "liturgy"


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
                            Toast.makeText(
                                context,
                                "Removed from favorite",
                                Toast.LENGTH_LONG
                            ).show()
                            ivfav.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favorite))
                        } else {
                            Toast.makeText(
                                context,
                                "Added to favorite",
                                Toast.LENGTH_LONG
                            ).show()
                            ivfav.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favourite_fill))
                        }
                        updateList(setFavouriteRequestVo.isFavorite, position)
                    } else {
                        /*Toast.makeText(
                            context,
                            response.message().toString(),
                            Toast.LENGTH_LONG
                        ).show()*/

                        Toast.makeText(
                            context,
                            "Error",
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
        //notifyDataSetChanged()
        notifyItemChanged(position)
    }

    private fun productType(type: String, position: Int) {
        if (type == "book") {
            liturgyList[position].productType = ProductTypes.BOOK
        } else {
            liturgyList[position].productType = ProductTypes.LITURGY
        }
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
            // startPurchaseFlow(myLiturgyDataVo)

            val intent = Intent(context, SelectSubscriptionPlan::class.java)
            context?.startActivity(intent)

           /* val bundle = Bundle()
            bundle.putBoolean("onPress", true);
            bundle.putBoolean("onPressHome", false);
            var fragment: Fragment = SubscriptionPlanListFragment()
            (context as MainActivity).replaceFragment(fragment, "Subscription Plans", bundle)*/
        }
        show.setCanceledOnTouchOutside(false)
    }

}