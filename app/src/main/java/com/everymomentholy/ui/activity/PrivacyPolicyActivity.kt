package com.everymomentholy.ui.activity

import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.PrivacyPolicyResponseVo
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var policyWebView: WebView
    private lateinit var ivToolbarBackImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_policy)

        policyWebView = findViewById(R.id.policyWebView)
        ivToolbarBackImage = findViewById(R.id.iv_toolbar_backImage)

        ivToolbarBackImage.setOnClickListener() {
            onBackPressed()
        }

        if (Utils.isNetworkAvailable(this)) {
            loadPolicy()
        } else {
            Toast.makeText(
                this@PrivacyPolicyActivity,
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun loadPolicy() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.privacyPolicy()

        try {
            call.enqueue(object : Callback<PrivacyPolicyResponseVo> {
                override fun onResponse(
                    call: Call<PrivacyPolicyResponseVo>,
                    response: Response<PrivacyPolicyResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        policyWebView.getSettings().setJavaScriptEnabled(true);
                        policyWebView.loadData(
                            response.body()?.response!!.description,
                            "text/html; charset=utf-8",
                            "UTF-8"
                        );

                    } else {
                        Toast.makeText(
                            this@PrivacyPolicyActivity,
                            response.body()!!.response.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PrivacyPolicyResponseVo>, t: Throwable) {
                    Toast.makeText(this@PrivacyPolicyActivity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}