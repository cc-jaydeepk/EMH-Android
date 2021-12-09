package com.everymomentholy.ui.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
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
import com.everymomentholy.api.response.GetFavoritesDataVo
import com.everymomentholy.api.response.GetFavoritesResponseVo
import com.everymomentholy.ui.adapter.FavoriteAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.folioreader.emh.EMHUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FavoritesFragment : Fragment() {

    private lateinit var favRecyclerView: RecyclerView
    private lateinit var favoriteAdapter: FavoriteAdapter

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        favRecyclerView = view.findViewById(R.id.favRecyclerView)
        favRecyclerView.layoutManager = LinearLayoutManager(activity)

        if (Utils.isNetworkAvailable(requireContext())) {
            getFavoriteLiturgiesList()
        } else {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    @SuppressLint("HardwareIds")
    private fun getFavoriteLiturgiesList() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getFavoriteList(
            Utils.readIntFromSharedPref(requireContext(), Constants.PrefUserID, -1),
            "bearer " + Utils.readStringFromSharedPref(
                requireContext(),
                Constants.SHARED_PREF_TOKEN,
                ""
            )
        )

        try {
            call.enqueue(object : Callback<GetFavoritesResponseVo> {
                override fun onResponse(
                    call: Call<GetFavoritesResponseVo>,
                    response: Response<GetFavoritesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        if(context != null){
                            setAdapter(response.body()!!.response.data)
                        }
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

    fun setAdapter(favLiturgiesData: ArrayList<GetFavoritesDataVo>) {
        favoriteAdapter = FavoriteAdapter(requireContext(), favLiturgiesData)
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context)
        favRecyclerView.layoutManager = layoutManager
        favRecyclerView.adapter = favoriteAdapter
    }

    override fun onResume() {
        super.onResume()
        if (EMHUtils.favoriteFlagChange) {
            try {
                if (favoriteAdapter.bookOpenPosition != -1) {
                    favoriteAdapter.updateFavoriteStatusFromBookRead()
                    favoriteAdapter.notifyDataSetChanged()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }
}