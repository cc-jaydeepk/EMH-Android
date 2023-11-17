package com.everymomentholy.ui.activity

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.MyLiturgiesRequestVo
import com.everymomentholy.api.request.PurchaseRequestVo
import com.everymomentholy.api.response.GetFavoritesDataVo
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.everymomentholy.api.response.MyLiturgiesResponseVo
import com.everymomentholy.api.response.PrivateShareResponseVo
import com.everymomentholy.ui.adapter.GetLiturgiesFromBookIDAdapter
import com.everymomentholy.ui.adapter.GetLiturgiesFromFav
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.ProductTypes
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavLiturgyListActivity : AppCompatActivity() {

    lateinit var rvLiturgiesList: RecyclerView
    private lateinit var android_id: String
    var prefeUserId: Int = 0
    lateinit var getLiturgiesFromBookIDAdapter: GetLiturgiesFromBookIDAdapter
   // lateinit var getLiturgiesFromBookIDAdapter: GetLiturgiesFromFav
    lateinit var ivToolbarDrawer: ImageView
    lateinit var txtToolbarName: TextView
    lateinit var ivToolbarNotification: ImageView
    lateinit var collectionFavData: GetFavoritesDataVo
    var bookId = 0
    var isPurchaseSuccess: Boolean = false
    lateinit var ivToolbarBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fav_liturgies_list)

        rvLiturgiesList = findViewById(R.id.rvLiturgiesList)
        ivToolbarDrawer = findViewById(R.id.iv_toolbar_drawer)
        ivToolbarNotification = findViewById(R.id.iv_toolbar_notification)
        txtToolbarName = findViewById(R.id.txt_toolbar_name)
        ivToolbarBack = findViewById(R.id.iv_toolbar_backImage)

        ivToolbarNotification.visibility = View.GONE
        //ivToolbarDrawer.setImageDrawable(resources.getDrawable(R.drawable.ic_back))
        ivToolbarDrawer.visibility = View.GONE
        ivToolbarBack.visibility = View.VISIBLE
        txtToolbarName.text = "Liturgies"

        ivToolbarBack.setOnClickListener() {
            onBackPressed()
        }

        android_id = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )

        prefeUserId = Utils.readIntData(
            this,
            Constants.PrefUserID,
            0
        )!!

        bookId = intent.getIntExtra("favBookID", 0)
        collectionFavData = intent.getSerializableExtra("favCollection") as GetFavoritesDataVo

        if (Utils.isNetworkAvailable(this)) {
            getMyLiturgiesList(bookId)
        } else {
            Toast.makeText(
                this@FavLiturgyListActivity,
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }


    }

    private fun getMyLiturgiesList(bookID: Int) {
        var myLiturgiesRequestVo: MyLiturgiesRequestVo = MyLiturgiesRequestVo()
        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
            myLiturgiesRequestVo.appUserId = Constants.SKIP_LOGIN_USER_ID
        } else {
            myLiturgiesRequestVo.appUserId = prefeUserId
        }
        myLiturgiesRequestVo.deviceId = android_id

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.getLiturgiesFromBookId(
                myLiturgiesRequestVo.appUserId,
                myLiturgiesRequestVo.deviceId,
                bookID
            )

        try {
            call.enqueue(object : Callback<MyLiturgiesResponseVo> {
                @RequiresApi(Build.VERSION_CODES.CUPCAKE)
                override fun onResponse(
                    call: Call<MyLiturgiesResponseVo>,
                    response: Response<MyLiturgiesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        var liturgiesList: ArrayList<MyLiturgiesDataVo> = ArrayList()


                        var liturgie: MyLiturgiesDataVo = MyLiturgiesDataVo()
                        liturgie.bookId = collectionFavData.bookId
                        liturgie.chapterPageImage = collectionFavData.bookCoverPageImage
                        liturgie.price = collectionFavData.bookAmount
                        liturgie.chapterTitle = collectionFavData.bookTitle
                        liturgie.liturgyPurchaseCode = collectionFavData.bookPurchaseCode

//                        if (collectionFavData.isPurchased == "Yes" || collectionFavData.bookAmount == "0.00" || collectionFavData.bookAmount == "0.0") {
//
//                        } else {
//                            liturgiesList.add(liturgie)
//                        }
                        liturgiesList.addAll(response.body()?.response?.data!!)

                        rvLiturgiesList.layoutManager =
                            LinearLayoutManager(this@FavLiturgyListActivity)
                        getLiturgiesFromBookIDAdapter = GetLiturgiesFromBookIDAdapter(
                            this@FavLiturgyListActivity,
                            liturgiesList
                        )
                        rvLiturgiesList.adapter = getLiturgiesFromBookIDAdapter

                    } else {
                        Toast.makeText(
                            this@FavLiturgyListActivity,
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("litu", response.body()!!.response.message)
                    }
                }

                override fun onFailure(call: Call<MyLiturgiesResponseVo>, t: Throwable) {
                    Toast.makeText(this@FavLiturgyListActivity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun onPurchaseComplete(purchaseRequestVo: PurchaseRequestVo) {

        /*runOnUiThread {
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
                collectionFavData.isPurchased = "Yes"
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
                        if (Utils.isNetworkAvailable(this@FavLiturgyListActivity)) {
                            isPurchaseSuccess = false
                            getMyLiturgiesList(bookId)
                        } else {
                            Toast.makeText(
                                this@FavLiturgyListActivity,
                                resources.getString(R.string.check_internet),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@FavLiturgyListActivity,
                            response.body()!!.response,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PrivateShareResponseVo>, t: Throwable) {
                    Toast.makeText(this@FavLiturgyListActivity, "${t.message}", Toast.LENGTH_SHORT)
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
            getMyLiturgiesList(bookId)
        } else {
            Toast.makeText(
                this@FavLiturgyListActivity,
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }
    }
}