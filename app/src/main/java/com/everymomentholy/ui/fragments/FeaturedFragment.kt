package com.everymomentholy.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.MyLiturgiesRequestVo
import com.everymomentholy.api.response.HomegetSettingResponseVo
import com.everymomentholy.api.response.MyLiturgiesResponseVo
import com.everymomentholy.interfaces.PlayAudioClickListner
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.activity.PlayAudioActivity
import com.everymomentholy.ui.adapter.FeaturedAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.folioreader.emh.EMHUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class FeaturedFragment : Fragment(), PlayAudioClickListner {

    private lateinit var rcvFeatured: RecyclerView
    private lateinit var featuredAdapter: FeaturedAdapter
    private lateinit var txtAvaliableLiturgy: TextView
    lateinit var progressCardView: CardView

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_featured, container, false)
        rcvFeatured = view.findViewById(R.id.rcvFeatured)
        txtAvaliableLiturgy = view.findViewById(R.id.txtAvaliableLiturgy)
        progressCardView = view.findViewById(R.id.progressCardView)
        (activity as MainActivity).iv_toolbar_notification.visibility = View.GONE

        if (Utils.isNetworkAvailable(requireContext())) {
            progressCardView.visibility = View.VISIBLE
            featuredLiturgyMessage()
            getFeaturedList()
        } else {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }
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

        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
            myLiturgiesRequestVo.appUserId = Constants.SKIP_LOGIN_USER_ID
        } else {
            myLiturgiesRequestVo.appUserId =
                Utils.readIntFromSharedPref(requireContext(), Constants.PrefUserID, -1)
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
                        progressCardView.visibility = View.GONE
                        rcvFeatured.layoutManager = LinearLayoutManager(activity)
                        featuredAdapter = FeaturedAdapter(
                            requireContext(),
                            response.body()!!.response.data,
                            this@FeaturedFragment
                        )
                        rcvFeatured.adapter = featuredAdapter
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

    override fun onResume() {
        super.onResume()
        if (EMHUtils.favoriteFlagChange) {
            try {
                if (featuredAdapter.bookOpenPosition != -1) {
                    featuredAdapter.updateFavoriteStatusFromBookReadFeatured()
                    featuredAdapter.notifyDataSetChanged()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onPlayAudio(title: String, audioUrl: String, isAuto: Boolean) {
       // progressCardView.visibility = View.VISIBLE
        val intent = Intent(context, PlayAudioActivity::class.java)
        intent.putExtra("title", title);
        intent.putExtra("audio", audioUrl);
       // intent.putExtra("isAuto", true);
        context?.startActivity(intent)
      //  progressCardView.visibility = View.GONE
    }
}