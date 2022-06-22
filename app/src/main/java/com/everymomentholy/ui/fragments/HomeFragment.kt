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
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.response.*
import com.everymomentholy.interfaces.ShareItem
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.activity.NotificationListActivity
import com.everymomentholy.ui.adapter.QuoteAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.google.gson.Gson
import com.yuyakaido.android.cardstackview.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(), CardStackListener, ShareItem {

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
    private lateinit var shareQuote: ImageView

    lateinit var quotesText: String
    lateinit var cotedText: String
    lateinit var progressbarHomeFragment: ProgressBar
    var notificationCount = 0


    private lateinit var cardStackView: CardStackView

    private lateinit var quoteAdapter: QuoteAdapter
    private val manager by lazy { CardStackLayoutManager(requireActivity(), this) }
    private var quoteList: ArrayList<QuotePreviousquoteVo> = ArrayList()

    lateinit var leftButton: ImageView
    lateinit var rightButton: ImageView

    var data: String = ""


    @RequiresApi(Build.VERSION_CODES.FROYO)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //val view = inflater.inflate(R.layout.fragment_home_new, container, false)
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        quoteAdapter = QuoteAdapter(
            requireActivity(),
            quoteList,
            this
        )

        cardStackView = view.findViewById(R.id.card_stack_view)
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
        //  txtDailyQuote = view.findViewById(R.id.txtDailyQuote)
        txtDate = view.findViewById(R.id.txtDate)
        imgHomeClock = view.findViewById(R.id.imgHomeClock)
        ivHomeShare = view.findViewById(R.id.ivHomeShare)
        //  ivHomeShare.visibility = View.VISIBLE


        rightButton = view.findViewById(R.id.rightArraw)
        leftButton = view.findViewById(R.id.leftArrow)

        shareQuote = view.findViewById(R.id.shareImage)
        shareQuote.setOnClickListener {
            //shareMethod()
            shareQQ()
        }

        quoteLiturgy()
        setupCardStackView()

        leftButton.setOnClickListener {
            val setting = RewindAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(DecelerateInterpolator())
                .build()
            manager.setRewindAnimationSetting(setting)
            cardStackView.rewind()
        }


        rightButton.setOnClickListener {
            leftButton.visibility = View.VISIBLE
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            cardStackView.swipe()

            if (manager.topPosition + 1 == quoteAdapter!!.itemCount) {

                view.setClickable(false);
                view.setEnabled(false);

                rightButton.isClickable = false


                manager.setCanScrollHorizontal(false)
                // paginate()
            }
        }

        // cardStackView = view.findViewById(R.id.card_stack)
        quoteAdapter = QuoteAdapter(
            requireActivity(),
            quoteList,
            this
        )




        ivHomeShare.setOnClickListener {

            if (Utils.isNetworkAvailable(requireContext())) {
                ivHomeShare.visibility = View.INVISIBLE
                iv_toolbar_drawer.visibility = View.GONE
                iv_toolbar_notification.visibility = View.GONE
                ivHomeShare.isEnabled = false

                screenShotCapture()

                //After taking screenshot reset the button and view again
                iv_toolbar_drawer.visibility = View.VISIBLE
                iv_toolbar_notification.visibility = View.VISIBLE
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.feature_requires_internet),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return view
    }


    private fun setupCardStackView() {
        initialize()
    }


    /*private fun initialize() {
        manager.setStackFrom(StackFrom.Top)
        // manager.setVisibleCount(7)
        manager.setVisibleCount(6)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        // manager.setDirections(Direction.HORIZONTAL)
        // manager.setDirections(Direction.Right)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(false)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = quoteAdapter

        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }*/

    private fun initialize() {
        manager.setStackFrom(StackFrom.Top)
        manager.setVisibleCount(5)
        manager.setTranslationInterval(11.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        // manager.setDirections(Direction.HORIZONTAL)
        // manager.setDirections(Direction.Right)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(false)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        cardStackView.layoutManager = manager
        cardStackView.adapter = quoteAdapter

        if (manager.topPosition + 1 == quoteAdapter!!.itemCount) {

            manager.setCanScrollHorizontal(false)
            // paginate()
        }

        cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }


    private fun quoteLiturgy() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.quoteLiturgies()

        try {
            call.enqueue(object : Callback<QuoteResponseVo> {
                override fun onResponse(
                    call: Call<QuoteResponseVo>,
                    response: Response<QuoteResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {


                        var arrquoteList: ArrayList<QuotePreviousquoteVo> = ArrayList()
                        arrquoteList.addAll(response.body()!!.response.previousquotes)
                        quoteAdapter = QuoteAdapter(
                            requireActivity(),
                            arrquoteList,
                            this@HomeFragment
                        )

                        val second = QuotePreviousquoteVo(
                            response.body()!!.response.date,
                            response.body()!!.response.parentLiturgy,
                            response.body()!!.response.quote
                        )
                        arrquoteList.add(second)

                        /*for (n in arrquoteList.indices){
                          //  println("myArray[$n]: ${myArray[n]}")
                            Log.e("position", "onResponse: " + n)
                        }*/

                        /*val firstName: String = arrquoteList.get(5).toString()
                        Log.e("position", "onResponse: " + firstName)*/

                        for ((index, value) in arrquoteList.withIndex()) {
                            // println("Value at Index $index is: $value")
                            Log.e("po", "Value at Index " + value)
                        }

                        cardStackView.layoutManager = manager
                        cardStackView.adapter = quoteAdapter


                    }
                }

                override fun onFailure(call: Call<QuoteResponseVo>, t: Throwable) {
                    Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
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
            BuildConfig.APPLICATION_ID + ".provider",
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
            //  ivHomeShare.visibility = View.VISIBLE
            if (Utils.isNetworkAvailable(requireContext())) {
                progressbarHomeFragment.visibility = View.VISIBLE
                rootLayout.visibility = View.GONE
                dailyLiturgyQuote()
                //quoteLiturgy()
                getSettings()
            } else {
                /* Toast.makeText(
                     requireContext(),
                     requireContext().resources.getString(R.string.check_internet),
                     Toast.LENGTH_LONG
                 ).show()*/
                showOfflineUI()
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

                            Utils.storeJsonInFile(
                                context!!,
                                response.body()!!.response.home_page_liturgy_image,
                                Constants.HOME_IMAGE_FILE_NAME
                            )
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
                    }
                }

                override fun onFailure(call: Call<HomegetSettingResponseVo>, t: Throwable) {
                    //Toast.makeText(context, "${t.message}", Toast.LENGTH_SHORT).show()
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
                        // txtDailyQuote.text = response.body()!!.response.quote

                        Utils.storeJsonInFile(
                            context!!,
                            Gson().toJson(response.body()!!.response),
                            Constants.DAILY_QUOTE_FILE_NAME
                        )

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

    private fun showOfflineUI() {

        progressbarHomeFragment.visibility = View.GONE
        rootLayout.visibility = View.VISIBLE

        try {
            Glide
                .with(requireContext())
                .load(Utils.readJsonFromFile(requireContext(), Constants.HOME_IMAGE_FILE_NAME))
                .centerCrop()
                .into(imgHomeClock)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        val jsonString =
            Utils.readJsonFromFile(requireContext(), Constants.DAILY_QUOTE_FILE_NAME)

        if (!jsonString.isNullOrEmpty()) {

            val response: DailyLiturgiesResponseVo =
                Gson().fromJson(jsonString, DailyLiturgiesResponseVo::class.java)

            txtQuote.text = response.parentLiturgy
            txtDailyQuote.text = response.quote

            cotedText = response.parentLiturgy

            var format = SimpleDateFormat("d")
            val date: String = format.format(Date())

            val current = Calendar.getInstance().time

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
        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {

    }

    override fun onCardSwiped(direction: Direction?) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        leftButton.setColorFilter(
            requireActivity().getResources().getColor(R.color.loginbg)
        )
        if (manager.topPosition + 1 == quoteAdapter!!.itemCount) {

            manager.setCanScrollHorizontal(false)
            // paginate()
        }

        /*if (manager.topPosition + 7 == quoteAdapter!!.itemCount) {
            leftButton.setColorFilter(
                requireActivity().getResources().getColor(R.color.right_light_gray)
            )

        }*/
    }

    override fun onCardRewound() {
        rightButton.isClickable = true
        manager.setCanScrollHorizontal(true)
        rightButton.setColorFilter(
            requireActivity().getResources().getColor(R.color.loginbg)
        )
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View?, position: Int) {

        if (manager.topPosition + 1 == quoteAdapter!!.itemCount) {
            rightButton.isClickable = false
            rightButton.setColorFilter(
                requireActivity().getResources().getColor(R.color.right_light_gray)
            )
            manager.setCanScrollHorizontal(false)

        }

        if (quoteList.size == manager.topPosition) {
            leftButton.setColorFilter(
                requireActivity().getResources().getColor(R.color.right_light_gray)
            )

        }

    }

    override fun onCardDisappeared(view: View?, position: Int) {
    }

    override fun shareKiturgy(pos: Int, quote: QuotePreviousquoteVo) {

        data = quote.quote
        //Log.e("DATA", "shareKiturgy: " + data)
    }

    fun shareQQ() {
        var datashare = data
        Log.e("DATA", "shareQQ: " + datashare)

        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, datashare)
        startActivity(Intent.createChooser(intent, "Share With"))

    }
}