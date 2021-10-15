package com.everymomentholy.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.HomeDailyLiturgyResponseVo
import com.everymomentholy.api.response.HomegetSettingResponseVo
import com.folioreader.FolioReader
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    private lateinit var txtTitle: TextView
    private lateinit var txtQuote: TextView
    private lateinit var txtDailyQuote: TextView
    private lateinit var txtDate: TextView
    private lateinit var imgHomeClock: ImageView
    private lateinit var ivHomeShare: ImageView

    lateinit var quotesText: String
    lateinit var cotedText: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        txtTitle = view.findViewById(R.id.txtTitle)
        txtQuote = view.findViewById(R.id.txtQuote)
        txtDailyQuote = view.findViewById(R.id.txtDailyQuote)
        txtDate = view.findViewById(R.id.txtDate)
        imgHomeClock = view.findViewById(R.id.imgHomeClock)
        ivHomeShare = view.findViewById(R.id.ivHomeShare)

        ivHomeShare.setOnClickListener {
           // shareText()
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, quotesText)
            shareIntent.type = "text/plain"
            startActivity(Intent.createChooser(shareIntent, "send to"))
        }

        // dailyLiturgyQuote()
        //getSettings()

        //  val folioReader = FolioReader.get()
        //folioReader.openBook(R.raw.before_shopping)
        return view
    }


    override fun onResume() {
        super.onResume()
        dailyLiturgyQuote()
        getSettings()
    }

    private fun getSettings() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getSettings()

        try {
            call.enqueue(object : Callback<HomegetSettingResponseVo> {
                override fun onResponse(
                    call: Call<HomegetSettingResponseVo>,
                    response: Response<HomegetSettingResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        // txtQuote.text = response.body()!!.response.parentLiturgy
                        Glide
                            .with(context!!)
                            .load(response.body()!!.response.home_page_liturgy_image)
                            .centerCrop()
                            .into(imgHomeClock)


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

                        txtQuote.text = response.body()!!.response.parentLiturgy
                        txtDailyQuote.text = response.body()!!.response.quote
                        txtDate.text = response.body()!!.response.date

                        quotesText = response.body()!!.response.quote
                        cotedText = response.body()!!.response.parentLiturgy
                        Log.e("text", quotesText)

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

    private fun shareText() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_TEXT, quotesText)
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, "send to"))
    }
}