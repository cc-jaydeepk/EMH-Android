package com.everymomentholy.ui.fragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.MyLiturgiesRequestVo
import com.everymomentholy.api.response.DataVo
import com.everymomentholy.api.response.MyLiturgiesResponseVo
import com.everymomentholy.interfaces.LiturgyLitstClickListner
import com.everymomentholy.ui.adapter.ButtomSLiderAdapter
import com.everymomentholy.ui.adapter.MyLiturgyAdapter
import com.everymomentholy.ui.adapter.NotificationListAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class MyLiturgiesFragment : Fragment(), LiturgyLitstClickListner {

    private lateinit var recycler_liturgy: RecyclerView
    private lateinit var ll_enroute_bottom_sheet: LinearLayout
    private lateinit var txtUserId: TextView

    private lateinit var liturgyAdapter: MyLiturgyAdapter
    private lateinit var android_id: String
    private lateinit var bt: BottomSheetDialog
    var prefeUserId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myliturgies, container, false)

        recycler_liturgy = view.findViewById(R.id.recycler_liturgy)
       // txtUserId = view.findViewById(R.id.txtUserId)
        ll_enroute_bottom_sheet = view.findViewById(R.id.ll_enroute_bottom_sheet)

        android_id = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "MySharedPref",
            MODE_PRIVATE
        )

        prefeUserId = sharedPreferences.getInt("userId", 0)

        getMyLiturgiesList()

        ll_enroute_bottom_sheet.setOnClickListener {
            showBottomSheetDialog()
        }

        return view
    }


    private fun getMyLiturgiesList() {
        var myLiturgiesRequestVo: MyLiturgiesRequestVo = MyLiturgiesRequestVo()
        myLiturgiesRequestVo.appUserId = prefeUserId
        myLiturgiesRequestVo.deviceId = android_id

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.getLiturgies(myLiturgiesRequestVo.appUserId, myLiturgiesRequestVo.deviceId)

        try {
            call.enqueue(object : Callback<MyLiturgiesResponseVo> {
                override fun onResponse(
                    call: Call<MyLiturgiesResponseVo>,
                    response: Response<MyLiturgiesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        liturgyAdapter = MyLiturgyAdapter(
                            context!!,
                            response.body()!!.response.data,
                            this@MyLiturgiesFragment
                        )
                        val layoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(context)
                        recycler_liturgy.layoutManager = layoutManager
                        recycler_liturgy.adapter = liturgyAdapter

                    } else {
                        Toast.makeText(
                            requireActivity(),
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
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

    private fun showBottomSheetDialog() {
        val dialog = context?.let { BottomSheetDialog(it) }
        val view = layoutInflater.inflate(R.layout.activity_buttom_slider, null)

        val buttomRcv = view.findViewById<RecyclerView>(R.id.buttomRecyclerView)
        buttomRcv.layoutManager = LinearLayoutManager(activity)
        buttomRcv.adapter = ButtomSLiderAdapter()
        // adapter = ButtomSLiderAdapter()

        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()
    }

    override fun onMyLiturgiesListClick(pos: Int, dataVo: DataVo) {
        TODO("Not yet implemented")
    }
}