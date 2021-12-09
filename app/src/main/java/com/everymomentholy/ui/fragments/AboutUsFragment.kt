package com.everymomentholy.ui.fragments

import android.R.attr.button
import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.AboutUsResponseVO
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AboutUsFragment : Fragment() {
    private lateinit var txtAbout: TextView
    lateinit var spanned: Spanned
    lateinit var webView: WebView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_aboutus, container, false)
        (activity as MainActivity).iv_toolbar_search.visibility = View.GONE
        // txtAbout = view.findViewById(R.id.txtAbout)
        webView = view.findViewById(R.id.webView)
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.setSupportZoom(true)
       
        if (Utils.isNetworkAvailable(requireContext())) {
            aboutUs()
        } else {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }
        return view
    }

    private fun aboutUs() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.aboutUs()

        try {
            call.enqueue(object : Callback<AboutUsResponseVO> {
                override fun onResponse(
                    call: Call<AboutUsResponseVO>,
                    response: Response<AboutUsResponseVO>
                ) {
                    if (response.body()?.statusCode == 1) {

                        webView.loadData(
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

                override fun onFailure(call: Call<AboutUsResponseVO>, t: Throwable) {
                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}