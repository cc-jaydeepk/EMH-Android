package com.everymomentholy.ui.activity

import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.CollectionRequestVo
import com.everymomentholy.api.request.PurchaseRequestVo
import com.everymomentholy.api.response.*
import com.everymomentholy.interfaces.OnInAppPurchaseListener
import com.everymomentholy.ui.adapter.BottomSliderCollectionAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.ProductTypes
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CollectionListActivity : AppCompatActivity(), OnInAppPurchaseListener {

    lateinit var rvCollectionList: RecyclerView
    var prefeUserId: Int = 0
    var android_id: String = ""
    var liturgies: GetLiturgiesDataVo = GetLiturgiesDataVo()
    lateinit var bottomSliderAdapter: BottomSliderCollectionAdapter
    lateinit var ivToolbarNotification: ImageView
    lateinit var txtToolbarName: TextView
    lateinit var ivToolbarDrawer: ImageView
    lateinit var ivToolbarBack: ImageView
    var isPurchaseSuccess: Boolean = false
    var volumePurchaseCode: String = ""

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_list)

        rvCollectionList = findViewById(R.id.rvCollectionList)
        ivToolbarDrawer = findViewById(R.id.iv_toolbar_drawer)
        txtToolbarName = findViewById(R.id.txt_toolbar_name)
        ivToolbarNotification = findViewById(R.id.iv_toolbar_notification)
        ivToolbarBack = findViewById(R.id.iv_toolbar_backImage)

        txtToolbarName.text = "Get Collection"
        ivToolbarNotification.visibility = View.GONE
        // ivToolbarDrawer.setImageDrawable(resources.getDrawable(R.drawable.ic_back))
        ivToolbarDrawer.visibility = View.GONE
        ivToolbarBack.visibility = View.VISIBLE

        ivToolbarBack.setOnClickListener() {
            onBackPressed()
        }

        prefeUserId = Utils.readIntData(
            this,
            Constants.PrefUserID,
            0
        )!!

        android_id = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
        liturgies = intent.getSerializableExtra("liturgies") as GetLiturgiesDataVo

        if (Utils.isNetworkAvailable(this)) {
            if (liturgies.isVolume == "Yes")
                getAboutVolume(liturgies.volumeId)
            getCollectionList(liturgies.volumeId)

        } else {
            Toast.makeText(
                this@CollectionListActivity,
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun getCollectionList(volumeId: Int) {
        var collectionRequestVo: CollectionRequestVo = CollectionRequestVo()
        collectionRequestVo.volumeId = volumeId
        collectionRequestVo.deviceId = android_id

        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
            prefeUserId = Constants.SKIP_LOGIN_USER_ID
        }

        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getCollectionList(
            collectionRequestVo.deviceId,
            collectionRequestVo.volumeId,
            prefeUserId
        )

        try {
            call.enqueue(object : Callback<CollectionListResponseVo> {
                @RequiresApi(Build.VERSION_CODES.CUPCAKE)
                override fun onResponse(
                    call: Call<CollectionListResponseVo>,
                    response: Response<CollectionListResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        var totalPriceCollection: Double = 0.0

                        /*for (i in response.body()!!.response.data) {
                            totalPriceCollection += i.bookAmount
                        }*/

                        var wholeCollection = CollectionDataVo()
                        if (liturgies.isVolume == "Yes" && !liturgies.isPurchased.equals(
                                "Yes",
                                false
                            )
                        ) {
                            wholeCollection.bookCoverPageImage = liturgies.volumeCoverPageImage
                            wholeCollection.bookTitle = liturgies.volumeTitle
                            wholeCollection.bookAmount = liturgies.volumeAmount
                            wholeCollection.discountAmount = liturgies.discountAmount
                            wholeCollection.bookPurchaseCode = volumePurchaseCode
                            wholeCollection.volumeId = liturgies.volumeId
                        } else {
                            wholeCollection.bookCoverPageImage = liturgies.bookCoverPageImage
                            wholeCollection.bookTitle = liturgies.bookTitle
                            wholeCollection.bookAmount =
                                liturgies.bookAmount
                            wholeCollection.bookPurchaseCode = liturgies.bookPurchaseCode
                        }

                        var arrCollectionList: ArrayList<CollectionDataVo> = ArrayList()
                        if (liturgies.isPurchased != "Yes") {
                            arrCollectionList.add(wholeCollection)
                        }
                        arrCollectionList.addAll(response.body()!!.response.data)
                        bottomSliderAdapter = BottomSliderCollectionAdapter(
                            this@CollectionListActivity,
                            arrCollectionList
                        )

                        val layoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(this@CollectionListActivity)
                        rvCollectionList.layoutManager = layoutManager
                        rvCollectionList.adapter = bottomSliderAdapter

                        // showBottomSheetDialog()

                    } else {
                        Toast.makeText(
                            this@CollectionListActivity,
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<CollectionListResponseVo>, t: Throwable) {
                    Toast.makeText(
                        this@CollectionListActivity,
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }


    private fun getAboutVolume(volumeID: Int) {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getAboutVolume(volumeID)

        try {
            call.enqueue(object : Callback<AboutVolumeResponseVo> {
                override fun onResponse(
                    call: Call<AboutVolumeResponseVo>,
                    response: Response<AboutVolumeResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        volumePurchaseCode = response.body()!!.response.volumePurchaseCode
                    } else {
                        Toast.makeText(
                            this@CollectionListActivity,
                            response.body()!!.status.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AboutVolumeResponseVo>, t: Throwable) {
                    Toast.makeText(
                        this@CollectionListActivity,
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    /**
     * This callback will acknowledge the successful in-app purchase to the backend server.
     */
    override fun onPurchaseComplete(purchaseRequestVo: PurchaseRequestVo) {

        /* runOnUiThread {
             Toast.makeText(this, "In app purchase complete", Toast.LENGTH_LONG).show()
         }*/

        val request = APIService.buildService(APIInterface::class.java)
        lateinit var call: Call<PrivateShareResponseVo>

        when (purchaseRequestVo.productType) {
            ProductTypes.LITURGY -> {
                call = request.purchaseLiturgyAcknowledge(
                    purchaseRequestVo,
                    "bearer " + Utils.readStringFromSharedPref(
                        this,
                        Constants.SHARED_PREF_TOKEN,
                        ""
                    )
                )
            }
            ProductTypes.BOOK -> {
                call = request.purchaseBookAcknowledge(
                    purchaseRequestVo,
                    "bearer " + Utils.readStringFromSharedPref(
                        this,
                        Constants.SHARED_PREF_TOKEN,
                        ""
                    )
                )
                if(liturgies.isVolume != "Yes") liturgies.isPurchased = "Yes"
            }
            ProductTypes.VOLUME -> {
                call = request.purchaseVolumeAcknowledge(
                    purchaseRequestVo,
                    "bearer " + Utils.readStringFromSharedPref(
                        this,
                        Constants.SHARED_PREF_TOKEN,
                        ""
                    )
                )
                liturgies.isPurchased = "Yes"
            }
        }

        try {
            call.enqueue(object : Callback<PrivateShareResponseVo> {
                override fun onResponse(
                    call: Call<PrivateShareResponseVo>,
                    response: Response<PrivateShareResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        isPurchaseSuccess = true
                        if (Utils.isNetworkAvailable(this@CollectionListActivity)) {
                            isPurchaseSuccess = false
                            if (liturgies.isVolume == "Yes")
                                getAboutVolume(liturgies.volumeId)
                            getCollectionList(liturgies.volumeId)
                        } else {
                            Toast.makeText(
                                this@CollectionListActivity,
                                resources.getString(R.string.check_internet),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            this@CollectionListActivity,
                            response.body()!!.response,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PrivateShareResponseVo>, t: Throwable) {
                    Toast.makeText(this@CollectionListActivity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()

        if (Utils.isNetworkAvailable(this)) {
            if (liturgies.isVolume == "Yes")
                getAboutVolume(liturgies.volumeId)
            getCollectionList(liturgies.volumeId)
        } else {
            Toast.makeText(
                this@CollectionListActivity,
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }

    }
}