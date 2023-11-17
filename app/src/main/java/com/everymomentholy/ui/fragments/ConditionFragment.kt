package com.everymomentholy.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.TermsConditionResponseVo
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ConditionFragment : Fragment() {

    private lateinit var txtTermsCondition: TextView
    private lateinit var termsConditionWebView: WebView
    private lateinit var progressCardView: CardView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_condition, container, false)
        (activity as MainActivity).iv_toolbar_search.visibility = View.GONE

        txtTermsCondition = view.findViewById(R.id.txtTermsCondition)
        termsConditionWebView = view.findViewById(R.id.termsConditionWebView)
        progressCardView = view.findViewById(R.id.progressCardView)

        val webSettings: WebSettings = termsConditionWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        termsConditionWebView.settings.setSupportZoom(true)

        /*termsConditionWebView.setHorizontalScrollBarEnabled(false);
        val webSettings: WebSettings = termsConditionWebView.getSettings()
        webSettings.javaScriptEnabled = true*/

        termsConditionWebView.setHorizontalScrollBarEnabled(false)
        termsConditionWebView.setOnTouchListener(object : OnTouchListener {
            var m_downX = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.pointerCount > 1) {
                    //Multi touch detected
                    return true
                }
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        m_downX = event.x
                    }
                    MotionEvent.ACTION_MOVE, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {

                        // set x so that it doesn't move
                        event.setLocation(m_downX, event.y)
                    }
                }
                return false
            }
        })
        if (Utils.isNetworkAvailable(requireContext())) {
            progressCardView.visibility = View.VISIBLE
            termsCondition()
        } else {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }
        return view
    }

    private fun termsCondition() {

        val request = APIService.buildService(APIInterface::class.java)
        val call = request.termsCondition()

        try {
            call.enqueue(object : Callback<TermsConditionResponseVo> {
                override fun onResponse(
                    call: Call<TermsConditionResponseVo>,
                    response: Response<TermsConditionResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        progressCardView.visibility = View.GONE
                        var description = response.body()!!.response.description

                        // txtTermsCondition.text = Html.fromHtml(description)

                        /*termsConditionWebView.loadData(
                            response.body()!!.response.description,
                            "text/html",
                            "UTF-8"
                        )*/

                        termsConditionWebView.loadDataWithBaseURL(
                            "",
                            description,
                            "text/html",
                            "UTF-8", ""
                        )

                    } else {
                        Toast.makeText(
                            context,
                            response.body()!!.response.messsge,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<TermsConditionResponseVo>, t: Throwable) {
                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

}