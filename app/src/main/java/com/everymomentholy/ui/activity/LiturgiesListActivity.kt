package com.everymomentholy.ui.activity

import android.content.SharedPreferences
import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.MyLiturgiesRequestVo
import com.everymomentholy.api.response.CollectionDataVo
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.everymomentholy.api.response.MyLiturgiesResponseVo
import com.everymomentholy.ui.adapter.GetLiturgiesFromBookIDAdapter
import com.everymomentholy.ui.adapter.MyLiturgyAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class LiturgiesListActivity : AppCompatActivity() {

    lateinit var rvLiturgiesList: RecyclerView
    private lateinit var android_id: String
    var prefeUserId: Int = 0
    lateinit var getLiturgiesFromBookIDAdapter: GetLiturgiesFromBookIDAdapter
    lateinit var ivToolbarDrawer: ImageView
    lateinit var txtToolbarName: TextView
    lateinit var ivToolbarNotification: ImageView
    lateinit var collectionData: CollectionDataVo

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liturgies_list)

        rvLiturgiesList = findViewById(R.id.rvLiturgiesList)
        ivToolbarDrawer = findViewById(R.id.iv_toolbar_drawer)
        ivToolbarNotification = findViewById(R.id.iv_toolbar_notification)
        txtToolbarName = findViewById(R.id.txt_toolbar_name)

        ivToolbarNotification.visibility = View.GONE
        ivToolbarDrawer.setImageDrawable(resources.getDrawable(R.drawable.ic_back))
        txtToolbarName.text = "Liturgies"

        ivToolbarDrawer.setOnClickListener() {
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

        var bookId = intent.getIntExtra("bookID", 0)
        collectionData = intent.getSerializableExtra("collection") as CollectionDataVo

        if (Utils.isNetworkAvailable(this)) {
            getMyLiturgiesList(bookId)
        } else {
            Toast.makeText(
                this@LiturgiesListActivity,
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }

    }

    private fun getMyLiturgiesList(bookID: Int) {
        var myLiturgiesRequestVo: MyLiturgiesRequestVo = MyLiturgiesRequestVo()
        if (Constants.USER_LOGIN_STATUS == Constants.LOGIN) {
            myLiturgiesRequestVo.appUserId = prefeUserId
        } else {
            myLiturgiesRequestVo.appUserId = Constants.SKIP_LOGIN_USER_ID
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
                        liturgie.bookId = collectionData.bookId
                        liturgie.chapterPageImage = collectionData.bookCoverPageImage
                        liturgie.price = collectionData.bookAmount
                        liturgie.chapterTitle = collectionData.bookTitle

                        if (collectionData.isPurchased == "Yes" || collectionData.bookAmount == "0.00" || collectionData.bookAmount == "0.0") {

                        } else {
                            liturgiesList.add(liturgie)
                        }
                        liturgiesList.addAll(response.body()?.response?.data!!)

                        rvLiturgiesList.layoutManager =
                            LinearLayoutManager(this@LiturgiesListActivity)
                        getLiturgiesFromBookIDAdapter = GetLiturgiesFromBookIDAdapter(
                            this@LiturgiesListActivity,
                            liturgiesList
                        )
                        rvLiturgiesList.adapter = getLiturgiesFromBookIDAdapter

                    } else {
                        Toast.makeText(
                            this@LiturgiesListActivity,
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("litu", response.body()!!.response.message)
                    }
                }

                override fun onFailure(call: Call<MyLiturgiesResponseVo>, t: Throwable) {
                    Toast.makeText(this@LiturgiesListActivity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}