package com.everymomentholy.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.ContactUsRequestVo
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactus)

        edtYourName = findViewById(R.id.edtYourName)
        edtEmailAddress = findViewById(R.id.edtEmailAddress)
        edtMessage = findViewById(R.id.edtMessage)
        btnContactusSubmit = findViewById(R.id.btnContactusSubmit)

        btnContactusSubmit.setOnClickListener {

            var contactUsRequestVo: ContactUsRequestVo = ContactUsRequestVo()
            contactUsRequestVo.userName = edtYourName.text.toString().trim()
            contactUsRequestVo.email = edtEmailAddress.text.toString().trim()
            contactUsRequestVo.message = edtMessage.text.toString().trim()

            contactUs(contactUsRequestVo)
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
                    if (response.body()?.statusCode == 1) {

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
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}