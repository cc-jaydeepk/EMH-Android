package com.everymomentholy.services

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.MyLiturgiesRequestVo
import com.everymomentholy.api.response.MyLiturgiesDataVo
import com.everymomentholy.api.response.MyLiturgiesResponseVo
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
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

    val context = appContext

    override fun doWork(): Result {

        val isSuccess = getLiturgiesList()

        return if (isSuccess)
            Result.success()
        else
            Result.failure()
    }

    @SuppressLint("HardwareIds")
    private fun getLiturgiesList(): Boolean {

        var isSuccess = false

        val prefUserId = Utils.readIntData(
            context,
            Constants.PrefUserID,
            0
        )!!
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        var myLiturgiesRequestVo: MyLiturgiesRequestVo = MyLiturgiesRequestVo()
        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
            myLiturgiesRequestVo.appUserId = Constants.SKIP_LOGIN_USER_ID
        } else {
            myLiturgiesRequestVo.appUserId = prefUserId
        }
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

                    Utils.storeJsonInFile(context, response.toString())

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
}