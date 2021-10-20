package com.everymomentholy.ui.activity

import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.CollectionRequestVo
import com.everymomentholy.api.request.GetLiturgiesRequestVo
import com.everymomentholy.api.request.MyLiturgiesRequestVo
import com.everymomentholy.api.response.*
import com.everymomentholy.ui.adapter.*
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AboutBookLiturgiesActivity : AppCompatActivity() {

    var liturgies: GetLiturgiesDataVo = GetLiturgiesDataVo()
    lateinit var txtAboutDescription: TextView
    lateinit var txtTitle: TextView
    lateinit var ivAboutImage: ImageView
    lateinit var llAboutBookBottomSheet: LinearLayout
    lateinit var bottomSliderAdapter: BottomSliderCollectionAdapter
    lateinit var bottomSliderLiturgiesAdapter: BottomSliderLiturgiesAdapter
    var prefeUserId: Int = 0
    var android_id: String = ""
    lateinit var ivToolbarNotification: ImageView
    lateinit var txtToolbarName: TextView
    lateinit var ivToolbarDrawer: ImageView


    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_book_liturgies)

        txtAboutDescription = findViewById(R.id.txtAboutDescription)
        txtTitle = findViewById(R.id.txtTitle)
        ivAboutImage = findViewById(R.id.ivAboutImage)
        llAboutBookBottomSheet = findViewById(R.id.ll_about_book_bottom_sheet)
        ivToolbarDrawer = findViewById(R.id.iv_toolbar_drawer)
        txtToolbarName = findViewById(R.id.txt_toolbar_name)
        ivToolbarNotification = findViewById(R.id.iv_toolbar_notification)

        ivToolbarNotification.visibility = View.GONE
        ivToolbarDrawer.setImageDrawable(resources.getDrawable(R.drawable.ic_back))

        ivToolbarDrawer.setOnClickListener() {
            onBackPressed()
        }

        liturgies = intent.getSerializableExtra("liturgies") as GetLiturgiesDataVo

        if (liturgies.isVolume == "Yes") {
            getAboutVolumn(liturgies.volumeId)
        } else {
            getAboutBookLiturgies(liturgies.bookId)
        }
        txtTitle.text = liturgies.volumeTitle

        llAboutBookBottomSheet.setOnClickListener {
            if (liturgies.isVolume == "Yes") {
                getCollectionList(liturgies.volumeId)
            } else {
                getMyLiturgiesList(liturgies.bookId)
            }

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
    }

    fun getAboutBookLiturgies(bookId: Int) {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.aboutBook(bookId)

        try {
            call.enqueue(object : Callback<AboutBookResponseVo> {
                override fun onResponse(
                    call: Call<AboutBookResponseVo>,
                    response: Response<AboutBookResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        Glide.with(this@AboutBookLiturgiesActivity)
                            .load(response.body()!!.response.bookCoverPageImage)
                            .into(ivAboutImage)
                        txtTitle.text = response.body()!!.response.bookTitle
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txtAboutDescription.setText(
                                Html.fromHtml(
                                    response.body()!!.response.bookDescription,
                                    Html.FROM_HTML_MODE_LEGACY
                                )
                            )
                        } else
                            txtAboutDescription.setText(Html.fromHtml(response.body()!!.response.bookDescription))
                        // setAdapter(this@NotificationListActivity, response.body()!!)

                    } else {
                        Toast.makeText(
                            this@AboutBookLiturgiesActivity,
                            response.body()!!.status.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AboutBookResponseVo>, t: Throwable) {
                    Toast.makeText(
                        this@AboutBookLiturgiesActivity,
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun getAboutVolumn(volumnID: Int) {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getAboutVolume(volumnID)

        try {
            call.enqueue(object : Callback<AboutVolumeResponseVo> {
                override fun onResponse(
                    call: Call<AboutVolumeResponseVo>,
                    response: Response<AboutVolumeResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        Glide.with(this@AboutBookLiturgiesActivity)
                            .load(response.body()!!.response.volumeCoverPageImage)
                            .into(ivAboutImage)
                        txtTitle.text = response.body()!!.response.volumeTitle
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txtAboutDescription.setText(
                                Html.fromHtml(
                                    response.body()!!.response.volumeDescription,
                                    Html.FROM_HTML_MODE_LEGACY
                                )
                            )
                        } else
                            txtAboutDescription.setText(Html.fromHtml(response.body()!!.response.volumeDescription))
                        // setAdapter(this@NotificationListActivity, response.body()!!)

                    } else {
                        Toast.makeText(
                            this@AboutBookLiturgiesActivity,
                            response.body()!!.status.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AboutVolumeResponseVo>, t: Throwable) {
                    Toast.makeText(
                        this@AboutBookLiturgiesActivity,
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    private fun showBottomSheetDialog() {
        val dialog = this?.let { BottomSheetDialog(it) }
        val view = layoutInflater.inflate(R.layout.activity_bottom_slider, null)

        val buttomRcv = view.findViewById<RecyclerView>(R.id.buttomRecyclerView)

        /*   val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
               "MySharedPref",
               MODE_PRIVATE
           )

           //prefeUserId = sharedPreferences.getInt("userId", 0)

           bottomSliderAdapter = BottomSliderAdapter(
               requireContext(),
               freeLiturgies,
               )*/

        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)
        buttomRcv.layoutManager = layoutManager
        buttomRcv.adapter = bottomSliderAdapter


        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    private fun showBottomSheetForLiturgiesDialog() {
        val dialog = this?.let { BottomSheetDialog(it) }
        val view = layoutInflater.inflate(R.layout.activity_bottom_slider, null)

        val buttomRcv = view.findViewById<RecyclerView>(R.id.buttomRecyclerView)

        /*   val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
               "MySharedPref",
               MODE_PRIVATE
           )

           //prefeUserId = sharedPreferences.getInt("userId", 0)

           bottomSliderAdapter = BottomSliderAdapter(
               requireContext(),
               freeLiturgies,
               )*/

        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)
        buttomRcv.layoutManager = layoutManager
        buttomRcv.adapter = bottomSliderLiturgiesAdapter


        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()
    }


    private fun getCollectionList(volumeId: Int) {
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
                            this@AboutBookLiturgiesActivity,
                            arrCollectionList
                        )
                        showBottomSheetDialog()

                    } else {
                        Toast.makeText(
                            this@AboutBookLiturgiesActivity,
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<CollectionListResponseVo>, t: Throwable) {
                    Toast.makeText(
                        this@AboutBookLiturgiesActivity,
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

    private fun getMyLiturgiesList(bookID: Int) {
        var myLiturgiesRequestVo: MyLiturgiesRequestVo = MyLiturgiesRequestVo()
        myLiturgiesRequestVo.appUserId = prefeUserId
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
                        liturgie.bookId = liturgies.bookId
                        liturgie.chapterPageImage = liturgies.bookCoverPageImage
                        liturgie.price = liturgies.bookAmount
                        liturgie.chapterTitle = liturgies.bookTitle

                        liturgiesList.add(liturgie)
                        liturgiesList.addAll(response.body()?.response?.data!!)

                        /*rvLiturgiesList.layoutManager =
                            LinearLayoutManager(this@LiturgiesListActivity)
                        getLiturgiesFromBookIDAdapter = GetLiturgiesFromBookIDAdapter(
                            this@LiturgiesListActivity,
                            liturgiesList
                        )
                        rvLiturgiesList.adapter = getLiturgiesFromBookIDAdapter*/
                        bottomSliderLiturgiesAdapter = BottomSliderLiturgiesAdapter(
                            this@AboutBookLiturgiesActivity,
                            liturgiesList
                        )
                        showBottomSheetForLiturgiesDialog()

                    } else {
                        Toast.makeText(
                            this@AboutBookLiturgiesActivity,
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("litu", response.body()!!.response.message)
                    }
                }

                override fun onFailure(call: Call<MyLiturgiesResponseVo>, t: Throwable) {
                    Toast.makeText(
                        this@AboutBookLiturgiesActivity,
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        } catch (exception: java.lang.Exception) {
            exception.printStackTrace()
        }
    }
}