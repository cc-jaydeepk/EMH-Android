package com.everymomentholy.ui.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.GetFavoriteListRequestVo
import com.everymomentholy.api.request.GetLiturgiesRequestVo
import com.everymomentholy.api.response.GetFavoritesResponseVo
import com.everymomentholy.api.response.GetLiturgiesResponseVo
import com.everymomentholy.ui.adapter.FavoriteAdapter
import com.everymomentholy.ui.adapter.GetLiturgiesAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesFragment : Fragment() {

    private lateinit var favRecyclerView: RecyclerView
    private var adapter: RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>? = null

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        favRecyclerView = view.findViewById(R.id.favRecyclerView)
        favRecyclerView.layoutManager = LinearLayoutManager(activity)
        favRecyclerView.adapter = FavoriteAdapter()
        adapter = FavoriteAdapter()
        getFavoriteLiturgiesList()
        return view
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    @SuppressLint("HardwareIds")
    private fun getFavoriteLiturgiesList() {
        var getFavoriteListRequestVo: GetFavoriteListRequestVo = GetFavoriteListRequestVo()
        getFavoriteListRequestVo.userId =
            Utils.readIntFromSharedPref(requireContext(), Constants.PrefUserID, -1)
        getFavoriteListRequestVo.deviceId = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getFavoriteList(
            Utils.readIntFromSharedPref(requireContext(), Constants.PrefUserID, -1),
            "bearer " + Utils.readStringFromSharedPref(requireContext(), Constants.SHARED_PREF_TOKEN, "")
        )

        try {
            call.enqueue(object : Callback<GetFavoritesResponseVo> {
                override fun onResponse(
                    call: Call<GetFavoritesResponseVo>,
                    response: Response<GetFavoritesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                    } else {
                        Toast.makeText(
                            requireActivity(),
                            response.body()!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GetFavoritesResponseVo>, t: Throwable) {
                    Toast.makeText(requireActivity(), "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}