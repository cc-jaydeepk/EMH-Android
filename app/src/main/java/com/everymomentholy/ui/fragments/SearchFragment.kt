package com.everymomentholy.ui.fragments

import android.annotation.SuppressLint
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
import com.everymomentholy.api.request.SearchLiturgiesRequestVo
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.everymomentholy.api.response.MyLiturgiesResponseVo
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.adapter.SearchAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

@SuppressLint("NewApi")
class SearchFragment : Fragment() {

    private lateinit var recyclerviewSearch: RecyclerView
    private lateinit var edtSearch: EditText
    private lateinit var icSearch: ImageView
    private lateinit var txtSearchItemCount: TextView
    private var adapter: RecyclerView.Adapter<SearchAdapter.MyViewHolder>? = null
    private var arrSearchedData: ArrayList<MyLiturgiesDataVo> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerviewSearch = view.findViewById(R.id.recyclerviewSearch)
        edtSearch = view.findViewById(R.id.edtSearch)
        icSearch = view.findViewById(R.id.ic_search)
        txtSearchItemCount = view.findViewById(R.id.txt_search_item_count)
        //ivToolbarDrawer = view.findViewById(R.id.iv_toolbar_drawer)

        icSearch.setOnClickListener() {
            if (Utils.isNetworkAvailable(requireContext())) {
                getSearchLiturgies()
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.check_internet),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        //ivToolbarDrawer.setImageDrawable(resources.getDrawable(R.drawable.ic_back))

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.length >= 3) {
                    if (Utils.isNetworkAvailable(requireContext())) {
                        getSearchLiturgies()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.check_internet),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    if (arrSearchedData.isNotEmpty()) {
                        arrSearchedData.clear()
                        recyclerviewSearch.layoutManager = LinearLayoutManager(activity)
                        adapter = SearchAdapter(
                            requireContext(),
                            arrSearchedData
                        )
                        recyclerviewSearch.adapter = adapter
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        return view
    }

    @SuppressLint("HardwareIds")
    fun getSearchLiturgies() {
        var searchLiturgiesRequestVo: SearchLiturgiesRequestVo = SearchLiturgiesRequestVo()
        searchLiturgiesRequestVo.searchText = edtSearch.text.toString()
        searchLiturgiesRequestVo.deviceId = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        var userID = -1

        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
            userID = Constants.SKIP_LOGIN_USER_ID
        } else {
            userID = Utils.readIntFromSharedPref(requireContext(), Constants.PrefUserID, -1)
        }

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.searchLiturgies(
                searchLiturgiesRequestVo.deviceId,
                searchLiturgiesRequestVo.searchText,
                userID
            )

        try {
            call.enqueue(object : Callback<MyLiturgiesResponseVo> {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onResponse(
                    call: Call<MyLiturgiesResponseVo>,
                    response: Response<MyLiturgiesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        txtSearchItemCount.text =
                            "Show " + response.body()!!.response.data.size.toString() + " matches"

                        arrSearchedData = response.body()!!.response.data

                        recyclerviewSearch.layoutManager = LinearLayoutManager(activity)
                        adapter = SearchAdapter(
                            requireContext(),
                            arrSearchedData
                        )
                        recyclerviewSearch.adapter = adapter
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

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).ivToolbarDrawer.visibility = View.VISIBLE
        (activity as MainActivity).iv_toolbar_backImage.visibility = View.GONE
    }
}