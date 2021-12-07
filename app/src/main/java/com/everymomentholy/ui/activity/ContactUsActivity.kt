package com.everymomentholy.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.util.Util
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.ContactUsRequestVo
import com.everymomentholy.api.request.ResetPasswordRequestVo
import com.everymomentholy.api.response.ContectUsResponseVo
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactUsActivity : AppCompatActivity() {

    private lateinit var edtYourName: EditText
    private lateinit var edtEmailAddress: EditText
    private lateinit var edtMessage: EditText
    private lateinit var btnContactusSubmit: Button
    private lateinit var iv_toolbar_drawer: ImageView
    private lateinit var txt_toolbar_name: TextView
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactus)

        edtYourName = findViewById(R.id.edtYourName)
        edtEmailAddress = findViewById(R.id.edtEmailAddress)
        edtMessage = findViewById(R.id.edtMessage)
        btnContactusSubmit = findViewById(R.id.btnContactusSubmit)
        txt_toolbar_name = findViewById(R.id.txt_toolbar_name)
        txt_toolbar_name.text = "Contact Us"


        iv_toolbar_drawer = findViewById(R.id.iv_toolbar_drawer)
        iv_toolbar_drawer.setImageResource(R.drawable.ic_back)
        iv_toolbar_drawer.setOnClickListener {
            onBackPressed()
        }

        btnContactusSubmit.setOnClickListener {


            if (checkValidation()) {

                if (Utils.isNetworkAvailable(this)) {

                    var contactUsRequestVo: ContactUsRequestVo = ContactUsRequestVo()
                    contactUsRequestVo.userName = edtYourName.text.toString().trim()
                    contactUsRequestVo.email = edtEmailAddress.text.toString().trim()
                    contactUsRequestVo.message = edtMessage.text.toString().trim()

                    progressDialog = Utils.showProgressDialog(this@ContactUsActivity)!!
                    progressDialog.show()
                    contactUs(contactUsRequestVo)

                } else {
                    Toast.makeText(
                        this@ContactUsActivity,
                        resources.getString(R.string.check_internet),
                        Toast.LENGTH_LONG
                    ).show()
                }


            }
        }
    }

    private fun contactUs(contactUsRequestVo: ContactUsRequestVo) {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.contactUs(contactUsRequestVo)

        try {
            call.enqueue(object : Callback<ContectUsResponseVo> {
                override fun onResponse(
                    call: Call<ContectUsResponseVo>,
                    response: Response<ContectUsResponseVo>
                ) {
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()
                    }
                    if (response.body()?.statusCode == 1) {
                        edtEmailAddress.setText("")
                        edtMessage.setText("")
                        edtYourName.setText("")

                        Toast.makeText(
                            this@ContactUsActivity,
                            response.body()!!.response.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        /*Toast.makeText(
                            this@ContactUsActivity,
                            response.body()!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()*/
                    }
                }

                override fun onFailure(call: Call<ContectUsResponseVo>, t: Throwable) {
                    Toast.makeText(this@ContactUsActivity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()
                    }
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.FROYO)
    private fun checkValidation(): Boolean {

        val yourName = edtYourName.text.toString().trim()
        val yourEmail = edtEmailAddress.text.toString().trim()
        val yourMessage = edtMessage.text.toString().trim()
        var isValid = true

        if (yourName.isEmpty()) {
            edtYourName.error = resources.getString(R.string.contactus_name_error)
            edtYourName.requestFocus()
            isValid = false
        }

        if (yourEmail.isEmpty()) {
            edtEmailAddress.error = resources.getString(R.string.contactus_email_error)
            edtEmailAddress.requestFocus()
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(yourEmail).matches()) {
            edtEmailAddress.error = resources.getString(R.string.valid_email_error)
            edtEmailAddress.requestFocus()
            isValid = false
        }

        if (yourMessage.isEmpty()) {
            edtMessage.error = resources.getString(R.string.contactus_message_error)
            edtMessage.requestFocus()
            isValid = false
        }

        return isValid
    }
}