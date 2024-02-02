package com.everymomentholy.ui.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.PurchaseRequestVo
import com.everymomentholy.api.request.SetFavouriteRequestVo
import com.everymomentholy.api.response.BaseResponseVo
import com.everymomentholy.api.response.CollectionDataVo
import com.everymomentholy.ui.activity.*
import com.everymomentholy.ui.fragments.CollectionListFragment
import com.everymomentholy.ui.fragments.LiturgiesListFragment
import com.everymomentholy.ui.fragments.SubscriptionPlanListFragment
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.InAppSubscription
import com.everymomentholy.utils.ProductTypes
import com.everymomentholy.utils.Utils
import kotlinx.coroutines.GlobalScope
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import kotlin.math.roundToInt

class BottomSliderCollectionAdapter(
    var context: Context,
    var liturgyList: List<CollectionDataVo>,
) : RecyclerView.Adapter<BottomSliderCollectionAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var imgFreeLiturgiescover = view.findViewById<ImageView>(R.id.imgFreeLiturgiescover)
        var txtfreeLiturgiesTitle = view.findViewById<TextView>(R.id.txtFreeLiturgiesTitle)
        var btnReadNow = view.findViewById<TextView>(R.id.btnReadNow)
        var txtLiturgiesPrice = view.findViewById<TextView>(R.id.txtLiturgiesPrice)
        var llBottomSliderGetLiturgiesAbout =
            view.findViewById<LinearLayout>(R.id.llBottomSliderGetLiturgiesAbout)
        var imageBook = view.findViewById<ImageView>(R.id.imageBook)
        var imgFavorite = view.findViewById<ImageView>(R.id.imgFavorite)
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
        holder.imgFavorite.visibility = View.VISIBLE

        if (position == 0) {
            if (freeLiturgies.isPurchased.equals("Yes", true)) {
                if (freeLiturgies.bookAmount == "0.0" || freeLiturgies.bookAmount == "0.00" || freeLiturgies.isPurchased == "Yes") {
                    holder.btnReadNow.text = "OPEN"
                    // holder.btnPlayNow.visibility = View.VISIBLE

                    var sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        holder.btnReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                        holder.btnReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
                    } else {
                        holder.btnReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                        holder.btnReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
                    }
                    if (freeLiturgies.isPurchased == "Yes") {
                        holder.txtLiturgiesPrice.text = "Purchased"

                    } else {
                        holder.txtLiturgiesPrice.text = "Free"

                    }
                }
            } else {
                if (freeLiturgies.bookAmount == "0.0" || freeLiturgies.bookAmount == "0.00") {
                    holder.btnReadNow.text = "OPEN"
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
                    if (freeLiturgies.isPurchased == "Yes") {
                        holder.txtLiturgiesPrice.text = "Purchased"
                    } else {
                        holder.txtLiturgiesPrice.text = "Free"
                    }
                } else {
                    // holder.btnReadNow.text = "Unlock Volume"
                    //this is volume part
                    holder.btnReadNow.text = "Subscribe"
                    holder.imgFavorite.visibility = View.GONE
                    holder.imageBook.visibility = View.GONE
                    holder.imageBook.setImageDrawable(context.resources.getDrawable(R.drawable.ic_volume))

                    if (!freeLiturgies.discountAmount.isNullOrEmpty() && freeLiturgies.discountAmount != "0.00") {

                        var discountPrice = 0f
                        liturgyList.subList(1, liturgyList.size).forEach { col ->
                            if (col.isPurchased != "Yes")
                                discountPrice += col.bookAmount.toFloat()
                        }

                        discountPrice = discountPrice.roundToInt() - 0.01f
                        liturgyList[0].discountAmount =
                            discountPrice.toString() // update new discount price in the volume object too.
                        holder.txtLiturgiesPrice.text = "$" + discountPrice.toString()

                    } else {
                        holder.txtLiturgiesPrice.text = "$" + freeLiturgies.bookAmount
                    }

                }
            }
        } else {
            if (freeLiturgies.bookAmount == "0.0" || freeLiturgies.bookAmount == "0.00" || freeLiturgies.isPurchased.equals(
                    "Yes",
                    true
                )
            ) {
                holder.btnReadNow.text = "OPEN"
                // holder.btnReadNow.text = "OPEN"
                var sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    holder.btnReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    holder.btnReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
                } else {
                    holder.btnReadNow.setBackground(context.resources.getDrawable(R.drawable.bg_read_now));
                    holder.btnReadNow.setTextColor(context.resources.getColor(R.color.loginbg))
                }
                if (freeLiturgies.isPurchased.equals("Yes", true)) {
                    holder.txtLiturgiesPrice.text = "Purchased"
                } else {
                    holder.txtLiturgiesPrice.text = "Free"
                }
            } else {
                // holder.btnReadNow.text = "Unlock Collection"
                //this is collection part
                holder.btnReadNow.text = "Subscribe"
                holder.txtLiturgiesPrice.text = "$" + freeLiturgies.bookAmount
            }
        }
        holder.txtfreeLiturgiesTitle.text = freeLiturgies.bookTitle

        Glide.with(context)
            .load(freeLiturgies.bookCoverPageImage)
            .into(holder.imgFreeLiturgiescover)

        holder.imgFavorite.setOnClickListener {
            if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                showDialogForUnlockWithoutLogin(freeLiturgies)
            } else {
                setLiturgiesFavourite(holder.imgFavorite, liturgyList[position], position)
            }
        }

        if (freeLiturgies.isFavorite == "True" || freeLiturgies.isFavorite == "true") {
            holder.imgFavorite.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favourite_fill))
        } else {
            holder.imgFavorite.setImageDrawable(context.resources.getDrawable(R.drawable.ic_favorite))
        }

        holder.btnReadNow.setOnClickListener() {
            if (holder.btnReadNow.text.toString().equals("Subscribe", true)) {
                // holder.btnReadNow.text == "Unlock Collection"
                // this is collection part
                freeLiturgies.productTypes = ProductTypes.BOOK
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(freeLiturgies)
                } else {
                    // startPurchaseFlow(freeLiturgies)

                    /*val inAppSubscription = InAppSubscription.getInstance((context as Activity).application, GlobalScope)
                    inAppSubscription.establishConnection()*/

                    //FOR TESTING PORPOSE
                    val intent = Intent(context, SelectSubscriptionPlan::class.java)
                    context?.startActivity(intent)

                    /*val bundle = Bundle()
                    bundle.putBoolean("onPress", true);
                    bundle.putBoolean("onPressHome", false);
                    var fragment: Fragment = SubscriptionPlanListFragment()
                    (context as MainActivity).replaceFragment(
                        fragment,
                        "Subscription Plans",
                        bundle
                    )*/
                }
            } else if (holder.btnReadNow.text.toString().equals("Subscribe", true)) {
                // holder.btnReadNow.text == "Unlock Volume"
                //this is volume paart
                freeLiturgies.productTypes = ProductTypes.VOLUME
                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    showDialogForUnlockWithoutLogin(freeLiturgies)
                } else {
                    if (!freeLiturgies.discountAmount.isNullOrEmpty() && freeLiturgies.discountAmount != "0.00") {
                        freeLiturgies.bookAmount = freeLiturgies.discountAmount
                    }
                    //startPurchaseFlow(freeLiturgies)

                    val bundle = Bundle()
                    var fragment: Fragment = SubscriptionPlanListFragment()
                    bundle.putBoolean("onPress", true);
                    bundle.putBoolean("onPressHome", false);
                    (context as MainActivity).replaceFragment(
                        fragment,
                        "Subscription Plans",
                        bundle
                    )


                }
            } else if (holder.btnReadNow.text.toString().equals("OPEN", true)) {
                transferToLiturgyList(freeLiturgies)
            } else {
                if (freeLiturgies.isPurchased.equals("Yes", true)) {
                    //if (position > 0) {
                    transferToLiturgyList(freeLiturgies)
                    //}
                } else {
                    if (position == 0 && freeLiturgies.bookAmount == "0.00" || freeLiturgies.bookAmount == "0.0") {
                        transferToLiturgyList(freeLiturgies)
                    }
                }
            }
        }

        holder.llBottomSliderGetLiturgiesAbout.setOnClickListener() {

            if (freeLiturgies.isPurchased.equals("Yes", true)) {
                //if (position > 0) {
                transferToLiturgyList(freeLiturgies)
                //}
            } else {
                if (position == 0 && freeLiturgies.bookAmount == "0.00" || freeLiturgies.bookAmount == "0.0") {
                    transferToLiturgyList(freeLiturgies)
                } else {
                    if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                        if (holder.btnReadNow.text.toString().equals("OPEN", true)) {
                            transferToLiturgyList(freeLiturgies)
                        } else {
                            showDialogForUnlockWithoutLogin(freeLiturgies)
                        }
                    } else {
                        if (position > 0) {
                            transferToLiturgyList(freeLiturgies)
                        }
                    }
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

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    private fun setLiturgiesFavourite(
        ivfav: ImageView,
        liturgiesDataVo: CollectionDataVo,
        position: Int
    ) {

        var setFavouriteRequestVo = SetFavouriteRequestVo()

        setFavouriteRequestVo.userId = Utils.readIntData(context, Constants.PrefUserID, -1)
        setFavouriteRequestVo.isFavorite = liturgiesDataVo.isFavorite != "True"
        setFavouriteRequestVo.bookId = liturgiesDataVo.bookId
        setFavouriteRequestVo.type = "book"

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


    private fun transferToLiturgyList(freeLiturgies: CollectionDataVo) {
        var intent = Intent(context, LiturgiesListActivity::class.java)
        intent.putExtra("bookID", freeLiturgies.bookId)
        intent.putExtra("collection", freeLiturgies)
        context.startActivity(intent)

        /*val bundle = Bundle()
        bundle.putInt("bookID", freeLiturgies.bookId)
        bundle.putSerializable("collection", freeLiturgies)
        var fragment: Fragment = LiturgiesListFragment()

        (context as MainActivity).replaceFragment(fragment, "Liturgies", bundle)*/
    }

    private fun startPurchaseFlow(collectionDataVo: CollectionDataVo) {
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
            bookId = collectionDataVo.bookId,
            amount = collectionDataVo.bookAmount,
            deviceId = deviceId,
            liturgyId = 0,
            volumeId = collectionDataVo.volumeId,
            productType = collectionDataVo.productTypes,
            productId = collectionDataVo.bookPurchaseCode,
            discountAmount = collectionDataVo.discountAmount
        )

        /*val inAppUtils =
            InAppUtils.getInstance((context as Activity).application, GlobalScope)
        inAppUtils.initiatePurchaseFlow(context as Activity, purchaseRequestVo)*/
    }

    fun showDialogForUnlockWithoutLogin(myLiturgyDataVo: CollectionDataVo) {
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
            //startPurchaseFlow(myLiturgyDataVo)
            val bundle = Bundle()
            bundle.putBoolean("onPress", true);
            bundle.putBoolean("onPressHome", false);
            var fragment: Fragment = SubscriptionPlanListFragment()
            (context as MainActivity).replaceFragment(fragment, "Subscription Plans", bundle)
        }
        show.setCanceledOnTouchOutside(false)
    }
}