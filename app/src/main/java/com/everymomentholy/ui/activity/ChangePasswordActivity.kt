package com.everymomentholy.ui.activity

import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.ChangePasswordRequestVo
import com.everymomentholy.api.response.ChangePasswordResponseVo
import com.everymomentholy.api.response.LogoutResponseVo
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var edtOldPassword: EditText
    private lateinit var edtNewpPassword: EditText
    private lateinit var edtConformPsw: EditText
    private lateinit var btnSubmitPassword: Button
    private lateinit var iv_toolbar_backImage: ImageView
    private lateinit var iv_toolbar_drawer: ImageView
    private lateinit var iv_toolbar_notification: ImageView
    private lateinit var txt_toolbar_name: TextView
    private lateinit var android_id: String
    var prefeUserId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password)

        iv_toolbar_drawer = findViewById(R.id.iv_toolbar_drawer)
        iv_toolbar_backImage = findViewById(R.id.iv_toolbar_backImage)
        txt_toolbar_name = findViewById(R.id.txt_toolbar_name)
        iv_toolbar_notification = findViewById(R.id.iv_toolbar_notification)


        iv_toolbar_backImage.visibility = View.VISIBLE
        iv_toolbar_drawer.visibility = View.GONE
        iv_toolbar_notification.visibility = View.GONE
        txt_toolbar_name.text = "Change Password"

        android_id = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        prefeUserId = Utils.readIntData(
            this,
            Constants.PrefUserID,
            0
        )!!

        edtOldPassword = findViewById(R.id.edtOldPassword)
        edtNewpPassword = findViewById(R.id.edtNewpPassword)
        edtConformPsw = findViewById(R.id.edtConformPsw)
        btnSubmitPassword = findViewById(R.id.btnSubmitPassword)

        iv_toolbar_backImage.setOnClickListener {
            onBackPressed()
        }

        btnSubmitPassword.setOnClickListener {

            var changePasswordRequestVo: ChangePasswordRequestVo = ChangePasswordRequestVo()
            changePasswordRequestVo.userId = prefeUserId
            changePasswordRequestVo.deviceId = android_id
            changePasswordRequestVo.oldPassword = edtOldPassword.text.toString().trim()
            changePasswordRequestVo.newPassword = edtNewpPassword.text.toString().trim()
            changePasswordRequestVo.confirmPassword = edtConformPsw.text.toString().trim()
            changePassword(changePasswordRequestVo)

        }
    }

    private fun changePassword(changePasswordRequestVo: ChangePasswordRequestVo) {
        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.changePassword(
                changePasswordRequestVo,
                "bearer " + Utils.readStringFromSharedPref(
                    this,
                    Constants.SHARED_PREF_TOKEN,
                    ""
                )
            )

        try {
            call.enqueue(object : Callback<ChangePasswordResponseVo> {
                override fun onResponse(
                    call: Call<ChangePasswordResponseVo>,
                    response: Response<ChangePasswordResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            "change password sucessfull",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()

                    } else {
                        Toast.makeText(
                            this@ChangePasswordActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ChangePasswordResponseVo>, t: Throwable) {
                    Toast.makeText(this@ChangePasswordActivity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}