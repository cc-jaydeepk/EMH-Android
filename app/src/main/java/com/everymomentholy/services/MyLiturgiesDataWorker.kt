package com.everymomentholy.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.GetLiturgiesRequestVo
import com.everymomentholy.api.request.MyLiturgiesRequestVo
import com.everymomentholy.api.response.*
import com.everymomentholy.ui.adapter.MyLiturgyAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

/**
 * This worker is invoked on the App startup.
 *
 * This worker fetches the List of Liturgies available for the user from the server and saves the response
 * json to a local txt file while also downloading all the liturgies .epub files.
 *
 */
class MyLiturgiesDataWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    val LOG_TAG = "liturgy worker"
    /*  val LITURGIES_FILE_NAME = "liturgies.json"
      val BOOKS_FILE_NAME = "books.json"*/

    val context = appContext

    val prefUserId = Utils.readIntData(
        context,
        Constants.PrefUserID,
        5
    )!!
    val androidId = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )

    override fun doWork(): Result {

        val isSuccess = getLiturgiesList()
        getBooks()

        //   if (Constants.USER_LOGIN_STATUS == Constants.LOGIN) {
        getFavoriteLiturgiesList()
        // }

        return if (isSuccess)
            Result.success()
        else
            Result.failure()
    }

    @SuppressLint("HardwareIds")
    private fun getLiturgiesList(): Boolean {

        var isSuccess = false

        var myLiturgiesRequestVo: MyLiturgiesRequestVo = MyLiturgiesRequestVo()
        /*if (Constants.USER_LOGIN_STATUS != Constants.LOGIN) {
            myLiturgiesRequestVo.appUserId = Constants.SKIP_LOGIN_USER_ID
        } else {*/
        myLiturgiesRequestVo.appUserId = prefUserId
        //}
        myLiturgiesRequestVo.deviceId = androidId

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.getLiturgies(myLiturgiesRequestVo.appUserId, myLiturgiesRequestVo.deviceId)

        try {
            call.enqueue(object : Callback<MyLiturgiesResponseVo> {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun onResponse(
                    call: Call<MyLiturgiesResponseVo>,
                    response: Response<MyLiturgiesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        Log.e(LOG_TAG, response.body()!!.response.message)

                    } else {
                        Log.e(LOG_TAG, response.body()!!.response.message)
                    }

                    Utils.storeJsonInFile(
                        context,
                        Gson().toJson(response.body()!!.response),
                        Constants.LITURGIES_FILE_NAME
                    )

                    //   GlobalScope.launch {
                    downloadLiturgies(response.body()!!.response.data)
                    //  }

                    isSuccess = true
                }

                override fun onFailure(call: Call<MyLiturgiesResponseVo>, t: Throwable) {
                    Log.e(LOG_TAG, t.localizedMessage)
                    isSuccess = false
                }
            })
        } catch (exception: Exception) {
            Log.e(LOG_TAG, exception.localizedMessage)
            exception.printStackTrace()
            isSuccess = false
        }

        return isSuccess
    }


    /**
     * download all available liturgies
     */
    private fun downloadLiturgies(data: ArrayList<MyLiturgiesDataVo>) {
        data.forEach {

            val cw = ContextWrapper(context)
            val directory = cw.getDir("files", AppCompatActivity.MODE_PRIVATE)
            if (!directory.exists()) {
                directory.mkdir()
            }
            var path = context?.filesDir?.absolutePath
            val bookFile = File(path + "/test_" + it.chapterId + ".epub")
            if (bookFile.exists() && bookFile.length() > 0) {
                Log.e(LOG_TAG, "it.chapterId.epub book exists")
            } else {
                val downloadId =
                    PRDownloader.download(
                        it.chapterUrl,
                        path,
                        "test_" + it.chapterId + ".epub"
                    )
                        .build()
                        .setOnStartOrResumeListener { }
                        .setOnPauseListener { }
                        .setOnCancelListener { }
                        .setOnProgressListener { }
                        .start(object : OnDownloadListener {
                            override fun onDownloadComplete() {
                                Log.e(LOG_TAG, "single download complete")
                            }

                            override fun onError(error: com.downloader.Error?) {
                                Log.e(LOG_TAG, "single download error")
                            }
                        })
                Log.e(LOG_TAG, "download id $downloadId")
            }
        }
    }

    private fun getBooks() {

        var getLiturgiesRequestVo = GetLiturgiesRequestVo()
        var token = ""
        /* if (Constants.USER_LOGIN_STATUS != Constants.LOGIN) {
             getLiturgiesRequestVo.appUserId = Constants.SKIP_LOGIN_USER_ID
             getLiturgiesRequestVo.deviceId = androidId
         } else {*/
        getLiturgiesRequestVo.appUserId = prefUserId
        getLiturgiesRequestVo.deviceId = androidId
        token = "bearer " + Utils.readStringFromSharedPref(
            context,
            Constants.SHARED_PREF_TOKEN,
            ""
        )
        //}
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getBooks(
            getLiturgiesRequestVo.appUserId,
            getLiturgiesRequestVo.deviceId, token
        )

        try {
            call.enqueue(object : Callback<GetLiturgiesResponseVo> {
                override fun onResponse(
                    call: Call<GetLiturgiesResponseVo>,
                    response: Response<GetLiturgiesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        if (response.body()?.statusCode == 1) {
                            Log.e(LOG_TAG, response.body()!!.response.message)

                        } else {
                            Log.e(LOG_TAG, response.body()!!.response.message)
                        }
                        Utils.storeJsonInFile(
                            context,
                            Gson().toJson(response.body()!!.response),
                            Constants.BOOKS_FILE_NAME
                        )
                    }
                }

                override fun onFailure(call: Call<GetLiturgiesResponseVo>, t: Throwable) {
                    Log.e(LOG_TAG, t.message!!)
                }
            })
        } catch (exception: java.lang.Exception) {
            exception.printStackTrace()
        }
    }


    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    @SuppressLint("HardwareIds")
    private fun getFavoriteLiturgiesList() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getFavoriteList(
            Utils.readIntFromSharedPref(context, Constants.PrefUserID, -1),
            "bearer " + Utils.readStringFromSharedPref(
                context,
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
                        Log.e(LOG_TAG, response.body()!!.message)

                    } else {
                        if (response.body() != null)
                            Log.e(LOG_TAG, response.body()!!.message)
                    }
                    Utils.storeJsonInFile(
                        context,
                        Gson().toJson(response.body()!!.response),
                        Constants.FAVORITES_FILE_NAME
                    )
                }

                override fun onFailure(call: Call<GetFavoritesResponseVo>, t: Throwable) {

                    if (context != null) {
                        Log.e(LOG_TAG, t.message!!)
                    }
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}