package com.everymomentholy.ui.activity

import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.CollectionRequestVo
import com.everymomentholy.api.response.CollectionDataVo
import com.everymomentholy.api.response.CollectionListResponseVo
import com.everymomentholy.api.response.GetLiturgiesDataVo
import com.everymomentholy.ui.adapter.BottomSliderCollectionAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CollectionListActivity : AppCompatActivity() {

    lateinit var rvCollectionList: RecyclerView
    var prefeUserId: Int = 0
    var android_id: String = ""
    var liturgies: GetLiturgiesDataVo = GetLiturgiesDataVo()
    lateinit var bottomSliderAdapter: BottomSliderCollectionAdapter
    lateinit var ivToolbarNotification: ImageView
    lateinit var txtToolbarName: TextView
    lateinit var ivToolbarDrawer: ImageView

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_list)

        rvCollectionList = findViewById(R.id.rvCollectionList)
        ivToolbarDrawer = findViewById(R.id.iv_toolbar_drawer)
        txtToolbarName = findViewById(R.id.txt_toolbar_name)
        ivToolbarNotification = findViewById(R.id.iv_toolbar_notification)

        txtToolbarName.text = "Get Collection"
        ivToolbarNotification.visibility = View.GONE
        ivToolbarDrawer.setImageDrawable(resources.getDrawable(R.drawable.ic_back))

        ivToolbarDrawer.setOnClickListener() {
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

        getCollectionList(liturgies.volumeId)
    }

    fun getCollectionList(volumeId: Int) {
        var collectionRequestVo: CollectionRequestVo = CollectionRequestVo()
        collectionRequestVo.volumeId = volumeId
        collectionRequestVo.deviceId = android_id

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

                        var wholeCollection: CollectionDataVo = CollectionDataVo()
                        if (liturgies.isVolume == "Yes") {
                            wholeCollection.bookCoverPageImage = liturgies.volumeCoverPageImage
                            wholeCollection.bookTitle = liturgies.volumeTitle
                            wholeCollection.bookAmount = liturgies.volumeAmount
                        } else {
                            wholeCollection.bookCoverPageImage = liturgies.bookCoverPageImage
                            wholeCollection.bookTitle = liturgies.bookTitle
                            wholeCollection.bookAmount =
                                liturgies.bookAmount
                        }

                        var arrCollectionList: ArrayList<CollectionDataVo> = ArrayList()
                        arrCollectionList.add(wholeCollection)
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

}