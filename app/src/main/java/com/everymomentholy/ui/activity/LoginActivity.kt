package com.everymomentholy.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.LoginRequestVo
import com.everymomentholy.api.response.LoginResponseVo
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var btn_Login: Button
    private lateinit var edtLoginEmail: EditText
    private lateinit var edtLoginPassword: EditText
    private lateinit var android_id: String
    private lateinit var txtForgotPsw: TextView
    lateinit var progressCardView: CardView
    var prefeUserId: Int = 0
    var isUserLogin: Boolean = false
    private lateinit var ivLoginBack: ImageView
    lateinit var remmberCheckBox: CheckBox
    private lateinit var sharedPreferences: SharedPreferences
    private var deafultValue: Boolean = true

    /*companion object {
        var bOne: Boolean? = true
        var userLogin: Boolean? = false
    }*/

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FirebaseApp.initializeApp(this)

        ivLoginBack = findViewById(R.id.ivRegiBack)

        ivLoginBack.setOnClickListener() {
            onBackPressed()
        }

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

        remmberCheckBox = findViewById(R.id.myCheckBox)
        //remmberCheckBox.isChecked = false
        btn_Login = findViewById(R.id.btn_Login)
        edtLoginEmail = findViewById(R.id.edtLoginEmail)
        edtLoginPassword = findViewById(R.id.edtLoginPassword)
        txtForgotPsw = findViewById(R.id.txtForgotPsw)
        progressCardView = findViewById(R.id.progressCardView)


        // getUserProfile()



        sharedPreferences =
            getSharedPreferences("savestate", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
//        remmberCheckBox.setChecked(sharedPreferences.getBoolean("switch", false))

//        if (sharedPreferences.getBoolean("switch", true)) {
        if(!TextUtils.isEmpty(sharedPreferences.getString("email",null))){
            edtLoginEmail.setText(sharedPreferences.getString("email", ""))
            edtLoginPassword.setText(sharedPreferences.getString("password", ""))
            remmberCheckBox.isChecked = true
        }
        if(!TextUtils.isEmpty(sharedPreferences.getString("email",null)))
        {
            remmberCheckBox.setChecked(true)
        }
        else
        {
            remmberCheckBox.setChecked(false)
        }

        remmberCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                editor.putBoolean("switch", true);
                editor.apply();
                remmberCheckBox.setChecked(true)
                val email = edtLoginEmail.text.toString()
                val password = edtLoginPassword.text.toString()
                saveCredentials(email, password, true)
            } else {
                editor.putBoolean("switch", false);
                editor.apply();
                remmberCheckBox.setChecked(false)

                clearCredentials()
            }
        }


        txtForgotPsw.setOnClickListener {
            var intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        btn_Login.setOnClickListener {

            if (checkValidation()) {

                if (Utils.isNetworkAvailable(this)) {

                    var loginRequestVo: LoginRequestVo = LoginRequestVo()
                    loginRequestVo.deviceId = android_id
                    loginRequestVo.email = edtLoginEmail.text.toString().trim()
                    loginRequestVo.password = edtLoginPassword.text.toString().trim()
                    //loginRequestVo.deviceType = "1"
                    loginRequestVo.deviceType = Constants.DEVICE_TYPE
                    // view.setElevation(8f);
                    progressCardView.visibility = View.VISIBLE
                    //To disable the user interaction
                    getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                    getFirebaseToken(loginRequestVo)
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

    private fun saveCredentials(email: String, password: String, remember: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.putBoolean("remember", remember)
        editor.apply()
    }

    private fun clearCredentials() {
        val editor = sharedPreferences.edit()
        editor.remove("email")
        editor.remove("password")
        editor.remove("remember")
        editor.apply()
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
                            this@LoginActivity, Constants.PrefUserID,
                            response.body()!!.response.userId
                        )

                        Log.e("userID", "onResponse: " + response.body()!!.response.userId)

                        Utils.writeStringToSharedPref(
                            this@LoginActivity, Constants.USER_NAME,
                            response.body()!!.response.firstName + " " + response.body()!!.response.lastName
                        )


                        Utils.writeStringToSharedPref(
                            this@LoginActivity, Constants.USER_EMAIL,
                            response.body()!!.response.email
                        )

                        Utils.writeStringToSharedPref(
                            this@LoginActivity, Constants.USER_SUBSCRIPTIONSTATUS,
                            response.body()!!.response.subscription
                        )

                        Utils.writeStringToSharedPref(
                            this@LoginActivity, Constants.PROFILE_PIC,
                            response.body()!!.response.userProfilePic
                        )

                        Utils.writeStringToSharedPref(
                            this@LoginActivity, Constants.SHARED_PREF_TOKEN,
                            response.body()!!.response.token
                        )

                        Utils.writeUserIdBooleanFromSharedPref(getApplicationContext(), true)

                        //view.setElevation(0f);
                        progressCardView.visibility = View.GONE
                        //To get user interaction back
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                        isUserLogin = true
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("boolean", isUserLogin)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                        /*val intent =
                            Intent(this@LoginActivity, SelectSubscriptionPlan::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()*/

                    } else {
                        progressCardView.visibility = View.GONE
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Toast.makeText(
                            this@LoginActivity,
                            response.body()!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponseVo>, t: Throwable) {
                    progressCardView.visibility = View.GONE
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            progressCardView.visibility = View.GONE
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            exception.printStackTrace()
        }
    }

    @SuppressLint("NewApi")
    private fun checkValidation(): Boolean {

        val email = edtLoginEmail.text.toString().trim()
        val password = edtLoginPassword.text.toString().trim()
        var isValid = true

        /*if (email.isEmpty()) {
            edtLoginEmail.error = resources.getString(R.string.email_error)
            edtLoginEmail.requestFocus()
            isValid = false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtLoginEmail.error = resources.getString(R.string.valid_email_error)
            edtLoginEmail.requestFocus()
            isValid = false
        }*/

        if (email.isEmpty()) {
            edtLoginEmail.error = resources.getString(R.string.email_error)
            edtLoginEmail.requestFocus()
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

    fun getFirebaseToken(loginRequestVo: LoginRequestVo) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(
                        "token exception",
                        "Fetching FCM registration token failed",
                        task.exception
                    )
                    login(loginRequestVo)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result!!
                Utils.writeStringToSharedPref(
                    this@LoginActivity,
                    Constants.SHARED_PREF_FIREBASE_INSTANCE_ID,
                    token
                )
                loginRequestVo.firebase_token = token
                login(loginRequestVo)
//                        Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
                // Log.e("token", token.toString())
                Log.e("TOKEN", "getFirebaseToken: " + token)
            })
    }
}