package com.everymomentholy.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.FaqResponseVo
import com.everymomentholy.ui.activity.ContactUsActivity
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FAQFragment : Fragment() {

    private lateinit var btnContactus: Button
    private lateinit var textFaq: TextView
    private lateinit var faqWebview: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_faq, container, false)
        (activity as MainActivity).iv_toolbar_search.visibility = View.GONE
        textFaq = view.findViewById(R.id.textFaq)
        faqWebview = view.findViewById(R.id.faqWebview)
        val webSettings: WebSettings = faqWebview.getSettings()
        webSettings.javaScriptEnabled = true
        if (Utils.isNetworkAvailable(requireContext())) {
            frequentlyAsked()
        } else {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }
        val content = SpannableString("Frequently Asked Questions")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        textFaq.setText(content)
        btnContactus = view.findViewById(R.id.btnContactus)
        btnContactus.setOnClickListener {

            val intent = Intent(activity, ContactUsActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    private fun frequentlyAsked() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.frequentlyAsked()

        try {
            call.enqueue(object : Callback<FaqResponseVo> {
                override fun onResponse(
                    call: Call<FaqResponseVo>,
                    response: Response<FaqResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        faqWebview.loadData(
                            response.body()!!.response.description,
                            "text/html",
                            "UTF-8"
                        )

                    } else {
                        Toast.makeText(
                            context,
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<FaqResponseVo>, t: Throwable) {
                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}