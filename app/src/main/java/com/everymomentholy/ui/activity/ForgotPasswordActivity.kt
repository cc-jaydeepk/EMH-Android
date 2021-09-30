package com.everymomentholy.ui.activity

import android.content.Intent
import android.os.Bundle
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
import com.everymomentholy.api.request.ForgotPasswordRequestVo
import com.everymomentholy.api.response.ForgotPasswordResponseVo
import com.everymomentholy.api.response.LoginResponseVo
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var edtForgotEmail: EditText
    private lateinit var btnForgotPswSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgotpassword)

        edtForgotEmail = findViewById(R.id.edtForgotEmail)
        btnForgotPswSubmit = findViewById(R.id.btnForgotSubmit)


        btnForgotPswSubmit.setOnClickListener {

            if (checkValidation()) {
                if (Utils.isNetworkAvailable(this)) {


                    forgotPassword()


                } else {
                    Toast.makeText(
                        this@ForgotPasswordActivity,
                        resources.getString(R.string.check_internet),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }
    }

    private fun forgotPassword() {

        var forgotPasswordRequestVo: ForgotPasswordRequestVo = ForgotPasswordRequestVo()
        forgotPasswordRequestVo.email = edtForgotEmail.text.toString().trim()
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.forgotPassword(forgotPasswordRequestVo.email)

        try {
            call.enqueue(object : Callback<ForgotPasswordResponseVo> {
                override fun onResponse(
                    call: Call<ForgotPasswordResponseVo>,
                    response: Response<ForgotPasswordResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            "A verification code has been sent to your registered email address to reset the password",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent =
                            Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                    } else {
                        Toast.makeText(
                            this@ForgotPasswordActivity,
                            response.body()!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ForgotPasswordResponseVo>, t: Throwable) {
                    Toast.makeText(this@ForgotPasswordActivity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun checkValidation(): Boolean {

        val email = edtForgotEmail.text.toString().trim()
        var isValid = true

        if (email.isEmpty()) {
            edtForgotEmail.error = resources.getString(R.string.email_error)
            edtForgotEmail.requestFocus()
            isValid = false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtForgotEmail.error = resources.getString(R.string.valid_email_error)
            edtForgotEmail.requestFocus()
            isValid = false
        }

        return isValid
    }
}