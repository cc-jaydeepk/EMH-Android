package com.everymomentholy.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.everymomentholy.R
import com.hbb20.CountryCodePicker
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.*
import com.everymomentholy.api.response.LoginResponseVo
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
    private lateinit var btnRegister: Button
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

    private lateinit var imgCheckbox: CheckBox
    private lateinit var registerProgressBar: ProgressBar
    var isAcceptTerms = false
    lateinit var progressCardView: CardView
    lateinit var iv_toolbar_drawer: ImageView
    lateinit var iv_toolbar_backImage: ImageView
    lateinit var iv_toolbar_notification: ImageView
    lateinit var txt_toolbar_name: TextView
    lateinit var ivRegiBack: ImageView
    lateinit var txt_privacy_policy: TextView


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

        //ccp!!.setDefaultCountryUsingNameCode("IN")
        countryCode = ccp!!.selectedCountryCode

        progressCardView = findViewById(R.id.progressCardView)
        registerProgressBar = findViewById(R.id.registerProgressBar)
        edtFirstName = findViewById(R.id.edtFirstName)
        edtLastName = findViewById(R.id.edtLastName)
        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)
        edtConfirmPsw = findViewById(R.id.edtConfirmPsw)
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber)
        imgCheckbox = findViewById(R.id.imgCheckbox)
        txt_privacy_policy = findViewById(R.id.tvPrivacyPolicy)
        /*  imgCheckbox.setOnClickListener {
              isAcceptTerms = true
              imgCheckbox.setImageResource(R.drawable.ic_check_box);
          }*/

        imgCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->

            isAcceptTerms = isChecked
        }

        ivRegiBack = findViewById(R.id.ivRegiBack)
        btnRegister = findViewById(R.id.btnRedister)

        ivRegiBack.setOnClickListener() {
            onBackPressed()
        }

        txt_privacy_policy.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }
        btnRegister.setOnClickListener {


            if (checkValidation()) {

                if (Utils.isNetworkAvailable(this)) {

                    if (isAcceptTerms) {
                        progressCardView.visibility = View.VISIBLE
                        getWindow().setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        )
                        btnRegister.isEnabled = false
                        var registrationRequestVo: RegisterRequestVo = RegisterRequestVo()
                        registrationRequestVo.firstName = edtFirstName.text.toString().trim()
                        registrationRequestVo.lastName = edtLastName.text.toString().trim()
                        registrationRequestVo.email = edtEmail.text.toString().trim()
                        registrationRequestVo.countryCode = countryCode.toString()
                        registrationRequestVo.password = edtPassword.text.toString().trim()
                        registrationRequestVo.deviceType = "1"
                        registrationRequestVo.deviceId = android_id
                        registrationRequestVo.phoneNo = edtPhoneNumber.text.toString().trim()
                        registration(registrationRequestVo)
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
                            registrationRequestVo.firstName + registrationRequestVo.lastName
                        )

                        Utils.writeStringToSharedPref(
                            this@RegisterActivity, Constants.USER_EMAIL,
                            registrationRequestVo.email
                        )

                        progressCardView.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        showDialog()

                    } else {
                        progressCardView.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Log.e("Fail", response.body()!!.message.toString())
                        btnRegister.isEnabled = true
                        Toast.makeText(
                            this@RegisterActivity,
                            response.body()!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

                override fun onFailure(call: Call<RegisterResponseVo>, t: Throwable) {
                    progressCardView.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    btnRegister.isEnabled = true
                    Toast.makeText(this@RegisterActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            btnRegister.isEnabled = true
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
                            this@RegisterActivity, Constants.USER_NAME,
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
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        showDialog()


                    } else {
                        progressCardView.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
        txtOk.isEnabled = true
        txtOk.setOnClickListener {
            //txtOk

            /*val intent =
                Intent(this@RegisterActivity, SelectSubscriptionPlan::class.java)
            intent.putExtra("boolean", true)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()*/

            if (Utils.isNetworkAvailable(this)) {
                var loginRequestVo: LoginRequestVo = LoginRequestVo()
                loginRequestVo.deviceId = android_id
                loginRequestVo.email = edtEmail.text.toString().trim()
                loginRequestVo.password = edtPassword.text.toString().trim()
                loginRequestVo.deviceType = Constants.DEVICE_TYPE
                txtOk.isEnabled = false
                progressCardView.visibility = View.VISIBLE
                getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                login(loginRequestVo)
            } else {
                Toast.makeText(
                    this@RegisterActivity,
                    resources.getString(R.string.check_internet),
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        builder.setCancelable(false)
        builder.setView(dialogLayout)
        builder.show()

    }

    override fun onCountrySelected() {
        countryCode = ccp!!.selectedCountryCode
        countryName = ccp!!.selectedCountryName
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
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.error = resources.getString(R.string.valid_email_error)
            edtEmail.requestFocus()
            isValid = false
        }



        if (password.isEmpty()) {
            edtPassword.error = resources.getString(R.string.password_error)
            edtPassword.requestFocus()
            isValid = false
        } else if (password.length < 6) {
            edtPassword.error = resources.getString(R.string.password_char_limit_error)
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

                        Constants.USER_LOGIN_STATUS = Constants.LOGIN

                        Utils.writeIntToSharedPref(
                            this@RegisterActivity, Constants.PrefUserID,
                            response.body()!!.response.userId
                        )

                        Utils.writeStringToSharedPref(
                            this@RegisterActivity, Constants.USER_NAME,
                            response.body()!!.response.firstName + " " + response.body()!!.response.lastName
                        )

                        Utils.writeStringToSharedPref(
                            this@RegisterActivity, Constants.USER_SUBSCRIPTIONSTATUS,
                            response.body()!!.response.subscription
                        )

                        Utils.writeStringToSharedPref(
                            this@RegisterActivity, Constants.USER_EMAIL,
                            response.body()!!.response.email
                        )

                        Utils.writeStringToSharedPref(
                            this@RegisterActivity, Constants.PROFILE_PIC,
                            response.body()!!.response.userProfilePic
                        )

                        Utils.writeStringToSharedPref(
                            this@RegisterActivity, Constants.SHARED_PREF_TOKEN,
                            response.body()!!.response.token
                        )

                        Utils.writeUserIdBooleanFromSharedPref(getApplicationContext(), true)

                        progressCardView.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        /*val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                        intent.putExtra("boolean", true)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()*/

                        val intent =
                            Intent(this@RegisterActivity, SelectSubscriptionPlan::class.java)
                        intent.putExtra("boolean", true)
//                        intent.flags =
//                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                    } else {
                        progressCardView.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Toast.makeText(
                            this@RegisterActivity,
                            response.body()!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponseVo>, t: Throwable) {
                    progressCardView.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    Toast.makeText(this@RegisterActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            progressCardView.visibility = View.GONE
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            exception.printStackTrace()
        }
    }
}