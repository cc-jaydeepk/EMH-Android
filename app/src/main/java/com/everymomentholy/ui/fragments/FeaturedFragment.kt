package com.everymomentholy.ui.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.MyLiturgiesRequestVo
import com.everymomentholy.api.response.HomegetSettingResponseVo
import com.everymomentholy.api.response.MyLiturgiesResponseVo
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.adapter.FeaturedAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class FeaturedFragment : Fragment() {

    private lateinit var rcvFeatured: RecyclerView
    private var adapter: RecyclerView.Adapter<FeaturedAdapter.MyViewHolder>? = null
    private lateinit var txtAvaliableLiturgy: TextView

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_featured, container, false)
        rcvFeatured = view.findViewById(R.id.rcvFeatured)
        txtAvaliableLiturgy = view.findViewById(R.id.txtAvaliableLiturgy)
        (activity as MainActivity).iv_toolbar_notification.visibility = View.GONE

        featuredLiturgyMessage()

        getFeaturedList()
        return view
    }

    private fun featuredLiturgyMessage() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getSettings()

        try {
            call.enqueue(object : Callback<HomegetSettingResponseVo> {
                override fun onResponse(
                    call: Call<HomegetSettingResponseVo>,
                    response: Response<HomegetSettingResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        txtAvaliableLiturgy.text =
                            response.body()!!.response.featured_liturgy_message

                    } else {

                    }
                }

                override fun onFailure(call: Call<HomegetSettingResponseVo>, t: Throwable) {
                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    private fun getFeaturedList() {
        var myLiturgiesRequestVo: MyLiturgiesRequestVo = MyLiturgiesRequestVo()

        if (Constants.USER_LOGIN_STATUS == Constants.LOGIN) {
            myLiturgiesRequestVo.appUserId =
                Utils.readIntFromSharedPref(requireContext(), Constants.PrefUserID, -1)
        } else {
            myLiturgiesRequestVo.appUserId = Constants.SKIP_LOGIN_USER_ID
        }
        myLiturgiesRequestVo.deviceId = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.getFeaturedLiturgies(
                myLiturgiesRequestVo.appUserId,
                myLiturgiesRequestVo.deviceId,
                "Yes"
            )

        try {
            call.enqueue(object : Callback<MyLiturgiesResponseVo> {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onResponse(
                    call: Call<MyLiturgiesResponseVo>,
                    response: Response<MyLiturgiesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        rcvFeatured.layoutManager = LinearLayoutManager(activity)
                        adapter = FeaturedAdapter(requireContext(), response.body()!!.response.data)
                        rcvFeatured.adapter = adapter
                    } else {

                    }
                }

                override fun onFailure(call: Call<MyLiturgiesResponseVo>, t: Throwable) {
                    Toast.makeText(requireActivity(), "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}