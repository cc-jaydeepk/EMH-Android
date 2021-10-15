package com.everymomentholy.ui.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.AboutBookResponseVo
import com.everymomentholy.api.response.AboutVolumeResponseVo
import com.everymomentholy.api.response.GetLiturgiesDataVo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AboutBookLiturgiesActivity : AppCompatActivity() {

    var liturgies: GetLiturgiesDataVo = GetLiturgiesDataVo()
    lateinit var txtAboutDescription: TextView
    lateinit var txtTitle: TextView
    lateinit var ivAboutImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_book_liturgies)

        txtAboutDescription = findViewById(R.id.txtAboutDescription)
        txtTitle = findViewById(R.id.txtTitle)
        ivAboutImage = findViewById(R.id.ivAboutImage)

        liturgies = intent.getSerializableExtra("liturgies") as GetLiturgiesDataVo

        if (liturgies.isVolume == "Yes") {
            AboutVolumn(liturgies.volumeId)
        } else {
            AboutBookLiturgies(liturgies.bookId)
        }
        txtTitle.text = liturgies.volumeTitle

    }

    fun AboutBookLiturgies(bookId: Int) {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.aboutBook(bookId)

        try {
            call.enqueue(object : Callback<AboutBookResponseVo> {
                override fun onResponse(
                    call: Call<AboutBookResponseVo>,
                    response: Response<AboutBookResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        Glide.with(this@AboutBookLiturgiesActivity)
                            .load(response.body()!!.response.bookCoverPageImage)
                            .into(ivAboutImage)
                        txtTitle.text = response.body()!!.response.bookTitle
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txtAboutDescription.setText(
                                Html.fromHtml(
                                    response.body()!!.response.bookDescription,
                                    Html.FROM_HTML_MODE_LEGACY
                                )
                            )
                        } else
                            txtAboutDescription.setText(Html.fromHtml(response.body()!!.response.bookDescription))
                        // setAdapter(this@NotificationListActivity, response.body()!!)

                    } else {
                        Toast.makeText(
                            this@AboutBookLiturgiesActivity,
                            response.body()!!.status.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AboutBookResponseVo>, t: Throwable) {
                    Toast.makeText(
                        this@AboutBookLiturgiesActivity,
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun AboutVolumn(volumnID: Int) {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getAboutVolume(volumnID)

        try {
            call.enqueue(object : Callback<AboutVolumeResponseVo> {
                override fun onResponse(
                    call: Call<AboutVolumeResponseVo>,
                    response: Response<AboutVolumeResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        Glide.with(this@AboutBookLiturgiesActivity)
                            .load(response.body()!!.response.volumeCoverPageImage)
                            .into(ivAboutImage)
                        txtTitle.text = response.body()!!.response.volumeTitle
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txtAboutDescription.setText(
                                Html.fromHtml(
                                    response.body()!!.response.volumeDescription,
                                    Html.FROM_HTML_MODE_LEGACY
                                )
                            )
                        } else
                            txtAboutDescription.setText(Html.fromHtml(response.body()!!.response.volumeDescription))
                        // setAdapter(this@NotificationListActivity, response.body()!!)

                    } else {
                        Toast.makeText(
                            this@AboutBookLiturgiesActivity,
                            response.body()!!.status.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AboutVolumeResponseVo>, t: Throwable) {
                    Toast.makeText(
                        this@AboutBookLiturgiesActivity,
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}