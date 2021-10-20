package com.everymomentholy.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.everymomentholy.R
import com.everymomentholy.utils.SavaPreferences
import com.hbb20.CountryCodePicker
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.GetUserProfileRequestVo
import com.everymomentholy.api.request.RegisterRequestVo
import com.everymomentholy.api.request.RegisterWithoutPhoneRequestVo
import com.everymomentholy.api.response.GetUserProfileVo
import com.everymomentholy.api.response.RegisterResponseVo
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity(), CountryCodePicker.OnCountryChangeListener {

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
    var prefeUserId: Int = 0
    private lateinit var txtForgot: TextView

    private lateinit var imgCheckbox: ImageView
    private lateinit var registerProgressBar: ProgressBar
    var isAcceptTerms = false
    lateinit var progressCardView: CardView
    lateinit var iv_toolbar_drawer: ImageView
    lateinit var iv_toolbar_backImage: ImageView
    lateinit var iv_toolbar_notification: ImageView
    lateinit var txt_toolbar_name: TextView


    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        android_id = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        Log.e("device id", android_id)

        prefeUserId = Utils.readIntData(
            this,
            Constants.PrefUserID,
            0
        )!!


        ccp = findViewById(R.id.country_code_picker)
        ccp!!.setOnCountryChangeListener(this)

        ccp!!.setDefaultCountryUsingNameCode("IN")

        progressCardView = findViewById(R.id.progressCardView)
        registerProgressBar = findViewById(R.id.registerProgressBar)
        edtFirstName = findViewById(R.id.edtFirstName)
        edtLastName = findViewById(R.id.edtLastName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirmPsw = findViewById(R.id.edtConfirmPsw)
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber)
        imgCheckbox = findViewById(R.id.imgCheckbox)
        imgCheckbox.setOnClickListener {
            isAcceptTerms = true
            imgCheckbox.setImageResource(R.drawable.ic_check_box);
        }

        //getUserProfile()

        btnRedister = findViewById(R.id.btnRedister)
        btnRedister.setOnClickListener {


            if (checkValidation()) {

                if (Utils.isNetworkAvailable(this)) {

                    if (isAcceptTerms) {
                        progressCardView.visibility = View.VISIBLE
                        /*if (edtPhoneNumber.text.toString().trim() == "") {
                            var registrationRequestVo: RegisterWithoutPhoneRequestVo =
                                RegisterWithoutPhoneRequestVo()
                            registrationRequestVo.firstName = edtFirstName.text.toString().trim()
                            registrationRequestVo.lastName = edtLastName.text.toString().trim()
                            registrationRequestVo.email = edtEmail.text.toString().trim()
                            registrationRequestVo.password = edtPassword.text.toString().trim()
                            registrationRequestVo.deviceType = "1"
                            registrationRequestVo.deviceId = android_id
                            registrationWithoutLogin(registrationRequestVo)
                        } else {*/
                        var registrationRequestVo: RegisterRequestVo = RegisterRequestVo()
                        registrationRequestVo.firstName = edtFirstName.text.toString().trim()
                        registrationRequestVo.lastName = edtLastName.text.toString().trim()
                        registrationRequestVo.email = edtEmail.text.toString().trim()
                        registrationRequestVo.countryCode = "44"
                        registrationRequestVo.password = edtPassword.text.toString().trim()
                        registrationRequestVo.deviceType = "1"
                        registrationRequestVo.deviceId = android_id
                        registrationRequestVo.phoneNo = edtPhoneNumber.text.toString().trim()
                        registration(registrationRequestVo)
                        /*}*/
                    } else {
                        isAcceptTerms = false
                        Toast.makeText(
                            this@RegisterActivity,
                            "Please agree to Privacy Policy",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        resources.getString(R.string.check_internet),
                        Toast.LENGTH_LONG
                    ).show()
                }

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

                        Utils.writeIntToSharedPref(
                            this@RegisterActivity, Constants.PrefUserID,
                            response.body()!!.userId
                        )

                        Utils.writeStringToSharedPref(
                            this@RegisterActivity, Constants.NAME,
                            registrationRequestVo.firstName
                        )

                        Utils.writeStringToSharedPref(
                            this@RegisterActivity, Constants.USER_EMAIL,
                            registrationRequestVo.email
                        )


                        /*val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                        val myEdit = sharedPreferences.edit()
                        myEdit.putInt("userId", response.body()!!.userId)
                        myEdit.apply()*/

                        progressCardView.visibility = View.GONE
                        showDialog()


                    } else {
                        progressCardView.visibility = View.GONE
                        Log.e("Fail", response.body()!!.message.toString())
                        Toast.makeText(
                            this@RegisterActivity,
                            response.body()!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponseVo>, t: Throwable) {
                    progressCardView.visibility = View.GONE
                    Toast.makeText(this@RegisterActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

    }

    private fun registrationWithoutLogin(registrationRequestVo: RegisterWithoutPhoneRequestVo) {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.userRegistrationWithPhone(registrationRequestVo)

        try {
            call.enqueue(object : Callback<RegisterResponseVo> {
                override fun onResponse(
                    call: Call<RegisterResponseVo>,
                    response: Response<RegisterResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        Utils.writeIntToSharedPref(
                            this@RegisterActivity, Constants.PrefUserID,
                            response.body()!!.userId
                        )

                        Utils.writeStringToSharedPref(
                            this@RegisterActivity, Constants.NAME,
                            registrationRequestVo.firstName
                        )

                        Utils.writeStringToSharedPref(
                            this@RegisterActivity, Constants.USER_EMAIL,
                            registrationRequestVo.email
                        )


                        /*val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
                        val myEdit = sharedPreferences.edit()
                        myEdit.putInt("userId", response.body()!!.userId)
                        myEdit.apply()*/

                        progressCardView.visibility = View.GONE
                        showDialog()


                    } else {
                        Log.e("Fail", response.body()!!.message.toString())
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registration Fail",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponseVo>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

    }

    private fun showDialog() {

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.register_dialog, null)
        // val dialogLayout = inflater.inflate(R.layout.login_dialog, null)
        val txtOk = dialogLayout.findViewById<TextView>(R.id.txtOk)
        txtOk.setOnClickListener {
            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        builder.setView(dialogLayout)
        builder.show()

    }

    override fun onCountrySelected() {
        countryCode = ccp!!.selectedCountryCode
        countryName = ccp!!.selectedCountryName

        Toast.makeText(this, "Country Code " + countryCode, Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "Country Name " + countryName, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("NewApi")
    private fun checkValidation(): Boolean {

        val firstNm = edtFirstName.text.toString().trim()
        val lastNm = edtLastName.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val password = edtPassword.text.toString().trim()
        val confirmPsw = edtConfirmPsw.text.toString().trim()
        //val phoneNo = edtPhoneNumber.text.toString().trim()

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

        if (email.isEmpty()) {
            edtEmail.error = resources.getString(R.string.email_error)
            edtEmail.requestFocus()
            isValid = false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.error = resources.getString(R.string.valid_email_error)
            edtEmail.requestFocus()
            isValid = false
        }



        if (password.isEmpty()) {
            edtPassword.error = resources.getString(R.string.password_error)
            edtPassword.requestFocus()
            isValid = false
        }


        if (confirmPsw.isEmpty()) {
            edtConfirmPsw.error = resources.getString(R.string.confirmpassword_error)
            edtConfirmPsw.requestFocus()
            isValid = false

        }

        if (!password.equals(confirmPsw)) {
            edtConfirmPsw.error = resources.getString(R.string.matchpassword_error)
            edtConfirmPsw.requestFocus()
            isValid = false
        }



        return isValid
    }
}