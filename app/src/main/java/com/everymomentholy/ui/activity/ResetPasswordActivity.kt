package com.everymomentholy.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.LoginRequestVo
import com.everymomentholy.api.request.ResetPasswordRequestVo
import com.everymomentholy.api.response.LoginResponseVo
import com.everymomentholy.api.response.ResetPasswordResponseVo
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var edtResetNewPassword: EditText
    private lateinit var edtResetConfirmPassword: EditText
    private lateinit var edtVerificationCode: EditText
    private lateinit var btnUpdatePassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resetpassword)

        edtResetNewPassword = findViewById(R.id.edtResetNewPassword)
        edtResetConfirmPassword = findViewById(R.id.edtResetConfirmPassword)
        edtVerificationCode = findViewById(R.id.edtVerificationCode)
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword)

        btnUpdatePassword.setOnClickListener {

            if (checkValidation()) {

                if (Utils.isNetworkAvailable(this)) {

                    var resetPasswordRequestVo: ResetPasswordRequestVo = ResetPasswordRequestVo()
                    resetPasswordRequestVo.emailCode = edtVerificationCode.text.toString().trim()
                    resetPasswordRequestVo.password = edtResetNewPassword.text.toString().trim()
                    resetPassword(resetPasswordRequestVo)

                } else {
                    Toast.makeText(
                        this@ResetPasswordActivity,
                        resources.getString(R.string.check_internet),
                        Toast.LENGTH_LONG
                    ).show()
                }


            }
        }
    }

    private fun resetPassword(resetPasswordRequestVo: ResetPasswordRequestVo) {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.resetPassword(resetPasswordRequestVo)

        try {
            call.enqueue(object : Callback<ResetPasswordResponseVo> {
                override fun onResponse(
                    call: Call<ResetPasswordResponseVo>,
                    response: Response<ResetPasswordResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        Toast.makeText(
                            this@ResetPasswordActivity,
                            "Your password updated successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)


                    } else {
                        Toast.makeText(
                            this@ResetPasswordActivity,
                            response.body()!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResetPasswordResponseVo>, t: Throwable) {
                    Toast.makeText(this@ResetPasswordActivity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun checkValidation(): Boolean {

        val newPassword = edtResetNewPassword.text.toString().trim()
        val confirmPassword = edtResetConfirmPassword.text.toString().trim()
        val verificationCode = edtVerificationCode.text.toString().trim()
        var isValid = true

        if (newPassword.isEmpty()) {
            edtResetNewPassword.error = resources.getString(R.string.reset_password_error)
            edtResetNewPassword.requestFocus()
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            edtResetConfirmPassword.error =
                resources.getString(R.string.reset_confirmpassword_error)
            edtResetConfirmPassword.requestFocus()
            isValid = false
        }

        if (verificationCode.isEmpty()) {
            edtVerificationCode.error = resources.getString(R.string.reset_verificationcode_error)
            edtVerificationCode.requestFocus()
            isValid = false
        }

        return isValid
    }
}