package com.everymomentholy.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.AboutUsResponseVO
import com.everymomentholy.api.response.MyLiturgiesResponseVo
import com.everymomentholy.ui.adapter.MyLiturgyAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AboutUsFragment : Fragment() {
    private lateinit var txtAbout: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_aboutus, container, false)
        txtAbout = view.findViewById(R.id.txtAbout)
        aboutUs()
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

                        //txtAbout.text = response.body()!!.response.description

                    } else {
                        /*Toast.makeText(
                            context,
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()*/
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