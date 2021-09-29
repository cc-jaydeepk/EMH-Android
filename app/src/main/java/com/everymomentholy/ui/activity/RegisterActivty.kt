package com.everymomentholy.ui.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.RegisterRequestVo
import com.everymomentholy.api.response.RegisterResponseVo
import com.hbb20.CountryCodePicker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class RegisterActivty : AppCompatActivity(), CountryCodePicker.OnCountryChangeListener {

    private var ccp: CountryCodePicker? = null
    private var countryCode: String? = null
    private var countryName: String? = null
    private lateinit var btnRedister: Button
    private lateinit var edtFirstName: EditText
    private lateinit var edtLastName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtConfirmPsw: EditText
    private lateinit var edtPhoneNumber: EditText
    private lateinit var country_code: EditText
    private lateinit var android_id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        android_id = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        Log.e("device id", android_id)


        ccp = findViewById(R.id.country_code_picker)
        ccp!!.setOnCountryChangeListener(this)

        ccp!!.setDefaultCountryUsingNameCode("IN")

        edtFirstName = findViewById(R.id.edtFirstName)
        edtLastName = findViewById(R.id.edtLastName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirmPsw = findViewById(R.id.edtConfirmPsw)
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber)


        btnRedister = findViewById(R.id.btnRedister)
        btnRedister.setOnClickListener {
            // showDialog()


            if (checkValidation()) {
                var registrationRequestVo: RegisterRequestVo = RegisterRequestVo()

                registrationRequestVo.firstName = edtFirstName.text.toString().trim()
                registrationRequestVo.lastName = edtLastName.text.toString().trim()
                registrationRequestVo.email = edtEmail.text.toString().trim()
                registrationRequestVo.countryCode = "+44"
                registrationRequestVo.password = edtPassword.text.toString().trim()
                registrationRequestVo.deviceType = "1"
                registrationRequestVo.deviceId = android_id
                registrationRequestVo.phoneNo = edtPhoneNumber.text.toString().trim()
                registration(registrationRequestVo)
            }

        }


    }

    private fun registration(registrationRequestVo: RegisterRequestVo) {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.userRegistration(registrationRequestVo)

        try {
            call.enqueue(object : Callback<RegisterResponseVo> {
                override fun onResponse(
                    call: Call<RegisterResponseVo>,
                    response: Response<RegisterResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        Log.e("Successfull", response.body()!!.message)

                        val intent = Intent(this@RegisterActivty, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                        Toast.makeText(
                            this@RegisterActivty,
                            "Registration successfully",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        Log.e("Fail", response.body()!!.message.toString())
                        Toast.makeText(
                            this@RegisterActivty,
                            "Registration Fail",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponseVo>, t: Throwable) {
                    Toast.makeText(this@RegisterActivty, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

    }

    private fun showDialog() {
        val dialog = Dialog(this)

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        //val dialogLayout = inflater.inflate(R.layout.register_dialog, null)
        val dialogLayout = inflater.inflate(R.layout.login_dialog, null)

        builder.setView(dialogLayout)
        builder.show()

    }

    override fun onCountrySelected() {
        countryCode = ccp!!.selectedCountryCode
        countryName = ccp!!.selectedCountryName

        Toast.makeText(this, "Country Code " + countryCode, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "Country Name " + countryName, Toast.LENGTH_SHORT).show()
    }

    private fun checkValidation(): Boolean {

        val firstNm = edtFirstName.text.toString().trim()
        val lastNm = edtLastName.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()
        var isValid = true

        if (firstNm.isEmpty()) {
            edtFirstName.error = resources.getString(R.string.firstname_error)
            edtFirstName.requestFocus()
            isValid = false
        }

        if (lastNm.isEmpty()) {
            edtLastName.error = resources.getString(R.string.lastname_error)
            edtLastName.requestFocus()
            isValid = false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtFirstName.error = resources.getString(R.string.valid_email_error)
            edtFirstName.requestFocus()
            isValid = false
        }

        if (password.isEmpty()) {
            edtPassword.error = resources.getString(R.string.password_error)
            edtPassword.requestFocus()
            isValid = false
        }

        return isValid
    }

}