package com.everymomentholy.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.AboutUsResponseVO
import com.everymomentholy.api.response.TermsConditionResponseVo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConditionFragment : Fragment() {

    private lateinit var txtTermsCondition: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_condition, container, false)
        txtTermsCondition = view.findViewById(R.id.txtTermsCondition)
        termsCondition()
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

                        //txtTermsCondition.text = response.body()!!.response.termsDescription

                    } else {
                        /*Toast.makeText(
                            context,
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()*/
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