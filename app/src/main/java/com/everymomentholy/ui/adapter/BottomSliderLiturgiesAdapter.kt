package com.everymomentholy.ui.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.PrivateSharingRequestVo
import com.everymomentholy.api.request.PurchaseRequestVo
import com.everymomentholy.api.request.SetFavouriteRequestVo
import com.everymomentholy.api.response.BaseResponseVo
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.everymomentholy.api.response.PrivateShareResponseVo
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.activity.PlayAudioActivity
import com.everymomentholy.ui.activity.SelectOptionActivity
import com.everymomentholy.ui.fragments.SubscriptionPlanListFragment
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.ProductTypes
import com.everymomentholy.utils.Utils
import kotlinx.coroutines.GlobalScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.lang.Exception

class BottomSliderLiturgiesAdapter(
    var context: Context,
    var liturgyList: List<MyLiturgiesDataVo>,
) : RecyclerView.Adapter<BottomSliderLiturgiesAdapter.MyViewHolder>() {

    lateinit var progressDialog: ProgressDialog

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imgFreeLiturgiescover = view.findViewById<ImageView>(R.id.imgFreeLiturgiescover)
        var txtfreeLiturgiesTitle = view.findViewById<TextView>(R.id.txtFreeLiturgiesTitle)
        var btnReadNow = view.findViewById<TextView>(R.id.btnReadNow)
        var txtLiturgiesPrice = view.findViewById<TextView>(R.id.txtLiturgiesPrice)
        var llBottomSliderGetLiturgiesAbout =
            view.findViewById<LinearLayout>(R.id.llBottomSliderGetLiturgiesAbout)
        var imageBook = view.findViewById<ImageView>(R.id.imageBook)
        var imgFavorite = view.findViewById<ImageView>(R.id.imgFavorite)
        var txtPlayNow = view.findViewById<TextView>(R.id.txtPlayNow)
        var imgShare = view.findViewById<ImageView>(R.id.imgShare)
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
        holder.imgShare.visibility = View.VISIBLE

        holder.imageBook.visibility = View.GONE


        if (freeLiturgies.price == "0.00" || freeLiturgies.isPurchased == "Yes") {
            if (freeLiturgies.isPurchased == "Yes") {
                holder.txtLiturgiesPrice.text = "Purchased"
            } else {
                holder.txtLiturgiesPrice.text = "Free"
            }
            holder.btnReadNow.text = "Read Now"
            // holder.btnReadNow.text = "Open"

            holder.btnReadNow.background =
                context.resources.getDrawable(R.drawable.bg_read_now);
            holder.btnReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
        } else {
            if (position == 0) {
                holder.imgShare.visibility = View.GONE
                holder.imgFavorite.visibility = View.GONE
                //this is collection part
                //unlock collection
                holder.btnReadNow.text = "Subscribe"
                holder.txtLiturgiesPrice.text = "$" + freeLiturgies.price
            } else {
                holder.btnReadNow.text = "Subscribe"
                holder.txtLiturgiesPrice.text = "$" + freeLiturgies.price
            }
        }
//        }
        holder.txtfreeLiturgiesTitle.text = freeLiturgies.chapterTitle

        Glide.with(context)
            .load(freeLiturgies.chapterPageImage)
            .into(holder.imgFreeLiturgiescover)

        if (liturgyList[position].audio_file == "" || holder.btnReadNow.text == "Subscribe") {
            holder.txtPlayNow.visibility = View.GONE
        } else {
            holder.txtPlayNow.visibility = View.VISIBLE
        }

        holder.txtPlayNow.setOnClickListener {
            val intent = Intent(context, PlayAudioActivity::class.java)
            intent.putExtra("title", liturgyList[position].chapterTitle);
            intent.putExtra("audio", liturgyList[position].audio_file);
            // intent.putExtra("image", liturgyList[position].chapterPageImage)
            intent.putExtra("URL", liturgyList[position].chapterPageImage);
            context.startActivity(intent)

        }
       /* if () {
            holder.txtPlayNow.visibility = View.GONE
        } else {
            holder.txtPlayNow.visibility = View.VISIBLE
        }*/

        holder.imgShare.setOnClickListener() {
            if (Utils.isNetworkAvailable(context)) {
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    (context as MainActivity).showLoginDialog()
                } else {
                    privateShareLiturgy(freeLiturgies)
                }
            } else {
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.internet_required),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        holder.imgFavorite.setOnClickListener {
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

        if (freeLiturgies.isFavorite == "True" || freeLiturgies.isFavorite == "true") {
            holder.imgFavorite.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favourite_fill))
        } else {
            holder.imgFavorite.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favorite))
        }

        holder.btnReadNow.setOnClickListener() {
            if (holder.btnReadNow.text == "Subscribe") {
                //  holder.btnReadNow.text == "Subscribe Collection
                // this is the collection part
                freeLiturgies.productType = ProductTypes.BOOK
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(freeLiturgies)
                } else {
                    // startPurchaseFlow(freeLiturgies)
                    val bundle = Bundle()
                    bundle.putBoolean("onPress", true);
                    var fragment: Fragment = SubscriptionPlanListFragment()
                    /*  (context as MainActivity).replaceFragment(
                          fragment,
                          "subscription",
                          bundle
                      )*/
                    (context as MainActivity).replaceFragment(fragment, "subscription", bundle)
                }
            } else if (holder.btnReadNow.text == "Subscribe") {
                freeLiturgies.productType = ProductTypes.LITURGY
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(freeLiturgies)
                } else {
                    //startPurchaseFlow(freeLiturgies)
                    val bundle = Bundle()
                    bundle.putBoolean("onPress", true);
                    var fragment: Fragment = SubscriptionPlanListFragment()
                    (context as MainActivity).replaceFragment(
                        fragment,
                        "subscription",
                        bundle
                    )
                }
            } else if (holder.btnReadNow.text == "Read Now") {
                readBook(freeLiturgies)
            }
        }

        holder.llBottomSliderGetLiturgiesAbout.setOnClickListener() {
            if (holder.btnReadNow.text == "Subscribe") {
                //if (holder.btnReadNow.text == "Unlock Collection")
                freeLiturgies.productType = ProductTypes.BOOK
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(freeLiturgies)
                } else {
                    startPurchaseFlow(freeLiturgies)
                }
            } else if (holder.btnReadNow.text == "Open") {
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
        val bookFile = File(path + "/test_" + freeLiturgy.chapterId + ".epub")
        if (bookFile.exists() && bookFile.length() > 0) {
            Utils.invokeBookReader(
                context,
                context?.filesDir?.absolutePath + "/" + "test_" + freeLiturgy.chapterId + ".epub",
                freeLiturgy
            )
        } else {
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
            productType = myLiturgyDataVo.productType,
            productId = myLiturgyDataVo.liturgyPurchaseCode
        )

        /*val inAppUtils =
            InAppUtils.getInstance((context as Activity).application, GlobalScope)
        inAppUtils.initiatePurchaseFlow(context as Activity, purchaseRequestVo)*/
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
            startPurchaseFlow(myLiturgyDataVo)
        }
        show.setCanceledOnTouchOutside(false)
    }
}