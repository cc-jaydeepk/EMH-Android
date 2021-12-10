package com.everymomentholy.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.everymomentholy.BuildConfig
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.HomeDailyLiturgyResponseVo
import com.everymomentholy.api.response.HomegetSettingResponseVo
import com.everymomentholy.api.response.NotificationDataVo
import com.everymomentholy.api.response.NotificationResponseVo
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.activity.NotificationListActivity
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var txtTitle: TextView
    private lateinit var txtQuote: TextView
    private lateinit var txtDailyQuote: TextView
    private lateinit var txtDate: TextView
    private lateinit var txt_toolbar: TextView
    private lateinit var imgHomeClock: ImageView
    private lateinit var ivHomeShare: ImageView
    private lateinit var rootLayout: RelativeLayout
    private lateinit var txtToolbar: RelativeLayout
    private lateinit var iv_toolbar_drawer: ImageView
    private lateinit var iv_toolbar_notification: ImageView
    private lateinit var txtToolbarNotificationCount: TextView

    lateinit var quotesText: String
    lateinit var cotedText: String
    lateinit var progressDialog: android.app.ProgressDialog
    lateinit var progressbarHomeFragment: ProgressBar
    var notificationCount = 0

    @RequiresApi(Build.VERSION_CODES.FROYO)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        rootLayout = view.findViewById(R.id.rootLayout)
        txt_toolbar = view.findViewById(R.id.txt_toolbar)
        //  (activity as MainActivity?)!!.initToolBar("Every Moment Holy")
        iv_toolbar_drawer = view.findViewById(R.id.iv_toolbar_drawer)
        iv_toolbar_notification = view.findViewById(R.id.iv_toolbar_notification)
        txtToolbarNotificationCount = view.findViewById(R.id.txt_toolbar_notification_count)
        progressbarHomeFragment = view.findViewById(R.id.progressbar_home_fragment)

        iv_toolbar_notification.setOnClickListener {
            val intent = Intent(requireActivity(), NotificationListActivity::class.java)
            startActivity(intent)
        }

        iv_toolbar_drawer.setOnClickListener {
            (activity as MainActivity?)?.openDrawer()
        }

        txtTitle = view.findViewById(R.id.txtTitle)
        txtQuote = view.findViewById(R.id.txtQuote)
        txtDailyQuote = view.findViewById(R.id.txtDailyQuote)
        txtDate = view.findViewById(R.id.txtDate)
        imgHomeClock = view.findViewById(R.id.imgHomeClock)
        ivHomeShare = view.findViewById(R.id.ivHomeShare)
        ivHomeShare.visibility = View.VISIBLE

        ivHomeShare.setOnClickListener {

            ivHomeShare.visibility = View.INVISIBLE
            iv_toolbar_drawer.visibility = View.GONE
            iv_toolbar_notification.visibility = View.GONE
            ivHomeShare.isEnabled = false

            screenShotCapture()

            //After taking screenshot reset the button and view again
            iv_toolbar_drawer.visibility = View.VISIBLE
            iv_toolbar_notification.visibility = View.VISIBLE
        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.FROYO)
    private fun screenShotCapture() {
        var b: Bitmap = getBitmapFromView(rootLayout, Color.WHITE)

        if (b != null) {

            val saveFile: File =
                getMainDirectoryName(requireActivity()) //get the path to save screenshot
            val file: File = store(
                b,
                "screenshot.jpg",
                saveFile
            ) //save the screenshot to selected path
            shareScreenshot(file) //finally share screenshot
        } else  //If bitmap is null show toast message
            Toast.makeText(requireActivity(), "Failed to take screenshot!!", Toast.LENGTH_SHORT)
                .show()
    }

    open fun getBitmapFromView(view: View, defaultColor: Int): Bitmap {
        var bitmap =
            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        canvas.drawColor(defaultColor)
        view.draw(canvas)
        return bitmap
    }

    @RequiresApi(Build.VERSION_CODES.FROYO)
    fun getMainDirectoryName(context: Context): File {

        val mainDir = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Demo"
        )

        //If File is not present create directory
        if (!mainDir.exists()) {
            if (mainDir.mkdir()) Log.e(
                "Create Directory",
                "Main Directory Created : $mainDir"
            )
        }
        return mainDir
    }

    fun store(bm: Bitmap, fileName: String?, saveFilePath: File): File {

        val dir = File(saveFilePath.absolutePath)
        if (!dir.exists()) dir.mkdirs()
        val file = File(saveFilePath.absolutePath, fileName)
        try {
            val fOut = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut)
            fOut.flush()
            fOut.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return file
    }

    private fun shareScreenshot(file: File) {

        val uri = FileProvider.getUriForFile(
            requireActivity(),
            BuildConfig.APPLICATION_ID + "." + requireActivity().getLocalClassName() + ".provider",
            file
        )
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_SUBJECT, "")
        intent.putExtra(Intent.EXTRA_STREAM, uri) //pass uri here
        startActivity(Intent.createChooser(intent, "Share With"))

        ivHomeShare.isEnabled = true
       // ivHomeShare.visibility = View.VISIBLE
        progressbarHomeFragment.visibility = View.GONE
        rootLayout.visibility = View.VISIBLE
    }


    override fun onResume() {
        super.onResume()
        if (context != null) {
            ivHomeShare.visibility = View.VISIBLE
            if (Utils.isNetworkAvailable(requireContext())) {
                progressbarHomeFragment.visibility = View.VISIBLE
                rootLayout.visibility = View.GONE
                dailyLiturgyQuote()
                getSettings()
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().resources.getString(R.string.check_internet),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

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
                    progressbarHomeFragment.visibility = View.GONE
                    rootLayout.visibility = View.VISIBLE
                    if (response.body()?.statusCode == 1) {

                        // txtQuote.text = response.body()!!.response.parentLiturgy
                        try {
                            Glide
                                .with(context!!)
                                .load(response.body()!!.response.home_page_liturgy_image)
                                .centerCrop()
                                .into(imgHomeClock)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                            if (context != null)
                                getNotificationListWithoutLogin()
                        } else {
                            if (context != null)
                                getNotificationList()
                        }
                    } else {

                    }
                }

                override fun onFailure(call: Call<HomegetSettingResponseVo>, t: Throwable) {
                    //Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                    if (progressDialog.isShowing) {
                        progressDialog.dismiss()
                    }
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun dailyLiturgyQuote() {

        val request = APIService.buildService(APIInterface::class.java)
        val call = request.dailyLiturgyQuote(
            "bearer " + Utils.readStringFromSharedPref(
                requireContext(),
                Constants.SHARED_PREF_TOKEN,
                ""
            )
        )

        try {
            call.enqueue(object : Callback<HomeDailyLiturgyResponseVo> {
                @SuppressLint("SimpleDateFormat")
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(
                    call: Call<HomeDailyLiturgyResponseVo>,
                    response: Response<HomeDailyLiturgyResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        txtQuote.text = response.body()!!.response.parentLiturgy
                        txtDailyQuote.text = response.body()!!.response.quote


                        //  quotesText = response.body()!!.response.quote
                        cotedText = response.body()!!.response.parentLiturgy
                        // Log.e("text", quotesText)
                        var format = SimpleDateFormat("d")
                        val date: String = format.format(Date())

                        //val validUrl = response.body()!!.response.date.split("-").first()
                        //  val current = LocalDateTime.now()
                        val current = Calendar.getInstance().getTime()

                        if (date.endsWith("1") && !date.endsWith("11"))
                            format = SimpleDateFormat("d'st' MMM yyyy");
                        else if (date.endsWith("2") && !date.endsWith("12"))
                            format = SimpleDateFormat("d'nd' MMM yyyy");
                        else if (date.endsWith("3") && !date.endsWith("13"))
                            format = SimpleDateFormat("d'rd' MMM yyyy");
                        else
                            format = SimpleDateFormat("d'th' MMM yyyy");
                        val yourDate = format.format(Date())
                        val formatter = SimpleDateFormat("dd mm yyyy")
                        var answer: String = formatter.format(current)
                        Log.d("answer", answer)
                        val validUrl = Date()
                        Log.e("text", yourDate)
                        txtDate.text = yourDate

                    } else {

                    }
                }

                override fun onFailure(call: Call<HomeDailyLiturgyResponseVo>, t: Throwable) {
                    //Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }

    }

    private fun getNotificationListWithoutLogin() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.notificationListWithoutLogin()

        try {
            call.enqueue(object : Callback<NotificationResponseVo> {
                override fun onResponse(
                    call: Call<NotificationResponseVo>,
                    response: Response<NotificationResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        if (response.body()?.response!!.data.size > 0) {
                            notificationCount = response.body()?.response!!.data.size
                            txtToolbarNotificationCount.visibility = View.VISIBLE
                            txtToolbarNotificationCount.text = notificationCount.toString()
                        } else {
                            txtToolbarNotificationCount.visibility = View.GONE
                        }

                    } else {

                    }
                }

                override fun onFailure(call: Call<NotificationResponseVo>, t: Throwable) {

                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun getNotificationList() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getNotification(
            Utils.readIntFromSharedPref(
                requireContext(),
                Constants.PrefUserID,
                -1
            ), "bearer " + Utils.readStringFromSharedPref(
                requireContext(),
                Constants.SHARED_PREF_TOKEN,
                ""
            )
        )

        try {
            call.enqueue(object : Callback<NotificationResponseVo> {
                override fun onResponse(
                    call: Call<NotificationResponseVo>,
                    response: Response<NotificationResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        if (response.body()!!.response.data.size > 0) {
                            var notificationUnreadData =
                                response.body()!!.response.data.filter { it.mode == "Unread" } as ArrayList<NotificationDataVo>

                            if (notificationUnreadData.size > 0) {
                                notificationCount = notificationUnreadData.size
                                txtToolbarNotificationCount.visibility = View.VISIBLE
                                txtToolbarNotificationCount.text = notificationCount.toString()
                            }
                        } else {
                            txtToolbarNotificationCount.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<NotificationResponseVo>, t: Throwable) {

                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

}