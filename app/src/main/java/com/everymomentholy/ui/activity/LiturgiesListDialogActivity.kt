package com.everymomentholy.ui.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.MyLiturgiesRequestVo
import com.everymomentholy.api.response.GetLiturgiesDataVo
import com.everymomentholy.api.response.MyLiturgiesResponseVo
import com.everymomentholy.ui.adapter.BottomSliderLiturgiesAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class LiturgiesListDialogActivity : AppCompatActivity() {

    lateinit var liturgiesData: GetLiturgiesDataVo
    lateinit var bottomSliderLiturgiesAdapter: BottomSliderLiturgiesAdapter
    private lateinit var android_id: String
    var prefeUserId: Int = 0
    lateinit var dialog: BottomSheetDialog
    lateinit var txtLiturgyListDialogTitle: TextView
    lateinit var ivLiturgyListDialogBack: ImageView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liturgies_list_dialog)

        txtLiturgyListDialogTitle = findViewById(R.id.txt_liturgy_list_dialog_title)
        ivLiturgyListDialogBack = findViewById(R.id.iv_liturgy_list_dialog_back)

        liturgiesData = intent.getSerializableExtra("liturgies") as GetLiturgiesDataVo

        android_id = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )

        prefeUserId = Utils.readIntData(
            this,
            Constants.PrefUserID,
            0
        )!!

        if (liturgiesData != null) {
            txtLiturgyListDialogTitle.text = liturgiesData.bookTitle
        }

        ivLiturgyListDialogBack.setOnClickListener() {
            onBackPressed()
        }

        if (Utils.isNetworkAvailable(this)) {
            getMyLiturgiesList(liturgiesData.bookId)
        } else {
            Toast.makeText(
                this@LiturgiesListDialogActivity,
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showBottomSheetDialog() {
        val rvBottomSheet = findViewById<RecyclerView>(R.id.buttomRecyclerView)

        val topCurveAnchor = findViewById<ImageView>(R.id.topCurveAnchor)
        var bottomSheet = findViewById<RelativeLayout>(R.id.bottom_sheet) as RelativeLayout
        val ivSlideUp = findViewById<ImageView>(R.id.ivSlideUp)

        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheet.z = 10.0F
        //  bottomSheetBehavior.peekHeight = 340
        bottomSheetBehavior.peekHeight = 160

        bottomSheetBehavior.isHideable = false

        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    //update my bottomsheet state.
                    ivSlideUp?.setImageResource(R.drawable.ic_down_arrow)

                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    ivSlideUp?.setImageResource(R.drawable.slideup_arrow)
                    onBackPressed()
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        android_id = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        ivSlideUp?.setOnClickListener() {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                onBackPressed()
            }

        }

        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)
        rvBottomSheet.layoutManager = layoutManager
        rvBottomSheet.adapter = bottomSliderLiturgiesAdapter

    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
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

                override fun onResponse(
                    call: Call<MyLiturgiesResponseVo>,
                    response: Response<MyLiturgiesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        bottomSliderLiturgiesAdapter = BottomSliderLiturgiesAdapter(
                            this@LiturgiesListDialogActivity,
                            response.body()?.response?.data!!
                        )
                        showBottomSheetDialog()

                    } else {
                        Toast.makeText(
                            this@LiturgiesListDialogActivity,
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("litu", response.body()!!.response.message)
                    }
                }

                override fun onFailure(call: Call<MyLiturgiesResponseVo>, t: Throwable) {
                    Toast.makeText(
                        this@LiturgiesListDialogActivity,
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