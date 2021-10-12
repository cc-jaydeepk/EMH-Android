package com.everymomentholy.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.GetLiturgiesRequestVo
import com.everymomentholy.api.response.GetLiturgiesResponseVo
import com.everymomentholy.interfaces.GetLiturgiesClickListner
import com.everymomentholy.ui.adapter.GetLiturgiesAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GetLiturgiesFragment : Fragment(), GetLiturgiesClickListner {

    lateinit var viewPager: ViewPager
    private lateinit var adapter: GetLiturgiesAdapter
    private lateinit var android_id: String
    var prefeUserId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_getliturgies, container, false)

        viewPager = view.findViewById(R.id.viewPager)

        android_id = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "MySharedPref",
            Context.MODE_PRIVATE
        )

        prefeUserId = sharedPreferences.getInt("userId", 0)
        getBooks()

        return view
    }

    private fun getBooks() {
        var getLiturgiesRequestVo: GetLiturgiesRequestVo = GetLiturgiesRequestVo()
        getLiturgiesRequestVo.appUserId = prefeUserId
        getLiturgiesRequestVo.deviceId = android_id

        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getBooks(getLiturgiesRequestVo.appUserId, getLiturgiesRequestVo.deviceId)

        try {
            call.enqueue(object : Callback<GetLiturgiesResponseVo> {
                override fun onResponse(
                    call: Call<GetLiturgiesResponseVo>,
                    response: Response<GetLiturgiesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        adapter = GetLiturgiesAdapter(
                            context!!,
                            response.body()!!.response.data
                        )
                        /* val horizontalLayoutManagaer = LinearLayoutManager(
                             context,
                             LinearLayoutManager.HORIZONTAL,
                             false
                         )*/
                        viewPager.setPadding(100, 0, 100, 0)
                        viewPager.setAdapter(adapter);


                    } else {
                        Toast.makeText(
                            requireActivity(),
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GetLiturgiesResponseVo>, t: Throwable) {
                    Toast.makeText(requireActivity(), "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

}