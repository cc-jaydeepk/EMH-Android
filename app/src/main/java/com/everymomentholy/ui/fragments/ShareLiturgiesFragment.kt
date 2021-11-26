package com.everymomentholy.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.ShareLiturgiesResponseVo
import com.everymomentholy.ui.activity.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ShareLiturgiesFragment : Fragment() {
    private lateinit var textSahreLiturgy: TextView
    private lateinit var howToshareWebview: WebView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_share_how, container, false)
        (activity as MainActivity).iv_toolbar_search.visibility = View.GONE
        howToshareWebview = view.findViewById(R.id.how_to_share)
        val webSettings: WebSettings = howToshareWebview.settings
        webSettings.javaScriptEnabled = true
        shareLiturgies()
        return view
    }

    private fun shareLiturgies() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.shareLiturgies()

        try {
            call.enqueue(object : Callback<ShareLiturgiesResponseVo> {
                override fun onResponse(
                    call: Call<ShareLiturgiesResponseVo>,
                    response: Response<ShareLiturgiesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {


                        howToshareWebview.loadData(
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

                override fun onFailure(call: Call<ShareLiturgiesResponseVo>, t: Throwable) {
                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}