package com.everymomentholy.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.LoginRequestVo
import com.everymomentholy.api.response.LoginResponseVo
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var btn_Login: Button
    private lateinit var edtLoginEmail: EditText
    private lateinit var edtLoginPassword: EditText
    private lateinit var android_id: String
    private lateinit var txtForgotPsw: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        android_id = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        Log.e("device id", android_id)

        btn_Login = findViewById(R.id.btn_Login)
        edtLoginEmail = findViewById(R.id.edtLoginEmail)
        edtLoginPassword = findViewById(R.id.edtLoginPassword)
        txtForgotPsw = findViewById(R.id.txtForgotPsw)

        txtForgotPsw.setOnClickListener {
            var intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        /* if (Utils.getLoggedStatus(applicationContext)) {
             val intent = Intent(applicationContext, MainActivity::class.java)
             startActivity(intent)
         } else {
            // val intent = Intent(applicationContext, LoginActivity::class.java)
             startActivity(intent)
         }*/

        btn_Login.setOnClickListener {

            if (checkValidation()) {

                if (Utils.isNetworkAvailable(this)) {

                    var loginRequestVo: LoginRequestVo = LoginRequestVo()
                    loginRequestVo.deviceId = android_id
                    loginRequestVo.email = edtLoginEmail.text.toString().trim()
                    loginRequestVo.password = edtLoginPassword.text.toString().trim()
                    //loginRequestVo.deviceType = "1"
                    loginRequestVo.deviceType = Constants.DEVICE_TYPE
                    login(loginRequestVo)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        resources.getString(R.string.check_internet),
                        Toast.LENGTH_LONG
                    ).show()
                }


            }


        }
    }

    private fun login(loginRequestVo: LoginRequestVo) {

        val request = APIService.buildService(APIInterface::class.java)
        val call = request.userLogin(loginRequestVo)

        try {
            call.enqueue(object : Callback<LoginResponseVo> {
                override fun onResponse(
                    call: Call<LoginResponseVo>,
                    response: Response<LoginResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        Utils.writeIntToSharedPref(
                            this@LoginActivity, Constants.PrefUserID,
                            response.body()!!.response.userId
                        )

                        Utils.readStringFromSharedPref(
                            this@LoginActivity, Constants.SHARED_PREF_TOKEN,
                            response.body()!!.response.token
                        )

                        Utils.writeUserIdBooleanFromSharedPref(getApplicationContext(), true);

                        /*val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                        val myEdit = sharedPreferences.edit()
                        myEdit.putInt("userId", response.body()!!.response.userId)
                        myEdit.apply()*/

                        // Log.e("loginresponse", appOpenCount)

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)


                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            response.body()!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponseVo>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun checkValidation(): Boolean {

        val email = edtLoginEmail.text.toString().trim()
        val password = edtLoginPassword.text.toString().trim()
        var isValid = true

        if (email.isEmpty()) {
            edtLoginEmail.error = resources.getString(R.string.email_error)
            edtLoginEmail.requestFocus()
            isValid = false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtLoginEmail.error = resources.getString(R.string.valid_email_error)
            edtLoginEmail.requestFocus()
            isValid = false
        }

        if (password.isEmpty()) {
            edtLoginPassword.error = resources.getString(R.string.password_error)
            edtLoginPassword.requestFocus()
            isValid = false
        }

        return isValid
    }
}