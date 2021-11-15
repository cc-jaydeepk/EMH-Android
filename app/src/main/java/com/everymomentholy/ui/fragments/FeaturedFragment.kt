package com.everymomentholy.ui.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.MyLiturgiesRequestVo
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.everymomentholy.api.response.MyLiturgiesResponseVo
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

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_featured, container, false)
        rcvFeatured = view.findViewById(R.id.rcvFeatured)

        getFeaturedList()
        return view
    }

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    private fun getFeaturedList() {
        var myLiturgiesRequestVo: MyLiturgiesRequestVo = MyLiturgiesRequestVo()
        myLiturgiesRequestVo.appUserId =
            Utils.readIntFromSharedPref(requireContext(), Constants.PrefUserID, -1)
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