package com.everymomentholy.ui.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.MyLiturgiesRequestVo
import com.everymomentholy.api.response.GetLiturgiesDataVo
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.everymomentholy.api.response.MyLiturgiesResponseVo
import com.everymomentholy.ui.adapter.BottomSliderLiturgiesAdapter
import com.everymomentholy.ui.adapter.GetLiturgiesFromBookIDAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
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
    lateinit var dialog : BottomSheetDialog

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liturgies_list_dialog)

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

        getMyLiturgiesList(liturgiesData.bookId)
    }


    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    private fun showBottomSheetDialog() {
        dialog = this?.let { BottomSheetDialog(it) }
        val view = layoutInflater.inflate(R.layout.activity_bottom_slider, null)

        val buttomRcv = view.findViewById<RecyclerView>(R.id.buttomRecyclerView)

        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(this)
        buttomRcv.layoutManager = layoutManager
        buttomRcv.adapter = bottomSliderLiturgiesAdapter

        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        dialog.dismiss()
        finish()
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
                    Toast.makeText(this@LiturgiesListDialogActivity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}