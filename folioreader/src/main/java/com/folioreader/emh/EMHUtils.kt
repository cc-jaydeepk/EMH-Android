package com.folioreader.emh

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.folioreader.emh.APIService
import com.folioreader.emh.APIInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import com.folioreader.R
import com.folioreader.ui.folio.activity.FolioActivity

class EMHUtils {

    companion object {

        var favoriteFlagChange: Boolean = false
        var favoriteStatusChange: Boolean = false

        fun setLiturgiesFavourite(
            context: android.content.Context,
            liturgiesDataVo: MyLiturgiesDataVo
        ) {

            var setFavouriteRequestVo = SetFavouriteRequestVo()

            setFavouriteRequestVo.userId = liturgiesDataVo.userId
            setFavouriteRequestVo.isFavorite =
                !(liturgiesDataVo.isFavorite == "True" || liturgiesDataVo.isFavorite == "true")
            setFavouriteRequestVo.bookId = liturgiesDataVo.bookId
            setFavouriteRequestVo.chapterId = liturgiesDataVo.chapterId

            val request = APIService.buildService(APIInterface::class.java)
            val call =
                request.setFavorite(setFavouriteRequestVo, liturgiesDataVo.token)

            try {
                call.enqueue(object : retrofit2.Callback<BaseResponseVo> {
                    @androidx.annotation.RequiresApi(android.os.Build.VERSION_CODES.CUPCAKE)
                    override fun onResponse(
                        call: retrofit2.Call<BaseResponseVo>,
                        response: retrofit2.Response<BaseResponseVo>
                    ) {
                        if (response.body()?.statusCode == 1) {
                            if (liturgiesDataVo.isFavorite == "True" || liturgiesDataVo.isFavorite == "true") {
                                (context as FolioActivity).markFavorite(false, context)
                                liturgiesDataVo.isFavorite = "false"
                                favoriteFlagChange = true
                                favoriteStatusChange = false
                            } else {
                                (context as FolioActivity).markFavorite(true, context)
                                liturgiesDataVo.isFavorite = "true"
                                favoriteFlagChange = true
                                favoriteStatusChange = true
                            }
                        } else {
                            android.widget.Toast.makeText(
                                context,
                                response.message().toString(),
                                android.widget.Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<BaseResponseVo>, t: Throwable) {
                        android.widget.Toast.makeText(
                            context,
                            "${t.message}",
                            android.widget.Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
            } catch (exception: java.lang.Exception) {
                exception.printStackTrace()
            }
        }

        fun privateShareLiturgy(
            context: android.content.Context,
            liturgyDataVo: MyLiturgiesDataVo
        ) {

            var privateSharingRequest: PrivateSharingRequestVo = PrivateSharingRequestVo()
            privateSharingRequest.deviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            privateSharingRequest.userId =
                liturgyDataVo.userId
            privateSharingRequest.liturgyId = liturgyDataVo.chapterId

            val request = APIService.buildService(APIInterface::class.java)
            val call =
                request.privateSharing(privateSharingRequest, liturgyDataVo.token)

            try {
                call.enqueue(object : Callback<PrivateShareResponseVo> {
                    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
                    override fun onResponse(
                        call: Call<PrivateShareResponseVo>,
                        response: Response<PrivateShareResponseVo>
                    ) {
                        if (response.body()?.statusCode == 1) {

                            val builder = AlertDialog.Builder(context)
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

        fun showDialogForUnlockWithoutLogin(context: Context) {
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
                /*val intent = Intent(context, SelectOptionActivity::class.java)
                context.startActivity(intent)*/
            }

            alertButtonCancel.setOnClickListener {
                show.dismiss()
            }

            alertButtonPurchase.setOnClickListener() {

            }
            show.setCanceledOnTouchOutside(false)
        }
    }

}