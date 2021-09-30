package com.everymomentholy.ui.activity.fragments

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
import com.everymomentholy.api.response.HomeDailyLiturgyResponseVo
import com.everymomentholy.api.response.ResponseVo
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class HomeFragment : Fragment() {

    private lateinit var txtTitle: TextView
    //lateinit var responsevo: ResponseVo()
    // lateinit var responseVo: ResponseVo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        txtTitle = view.findViewById(R.id.txtTitle)
        dailyLiturgyQuote()
        return view
    }

    private fun dailyLiturgyQuote() {

        val request = APIService.buildService(APIInterface::class.java)
        val call = request.dailyLiturgyQuote()

        try {
            call.enqueue(object : Callback<HomeDailyLiturgyResponseVo> {
                override fun onResponse(
                    call: Call<HomeDailyLiturgyResponseVo>,
                    response: Response<HomeDailyLiturgyResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        var responseVo: ResponseVo = ResponseVo()
                        txtTitle.text = responseVo.parentLiturgy


                    } else {

                    }
                }

                override fun onFailure(call: Call<HomeDailyLiturgyResponseVo>, t: Throwable) {
                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

    }

}