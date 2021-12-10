package com.everymomentholy.ui.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.CollectionRequestVo
import com.everymomentholy.api.request.MyLiturgiesRequestVo
import com.everymomentholy.api.response.*
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.adapter.*
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AboutBookLiturgiesFragment : Fragment() {

    var liturgies: GetLiturgiesDataVo = GetLiturgiesDataVo()
    lateinit var txtAboutDescription: TextView
    lateinit var txtTitle: TextView
    lateinit var ivAboutImage: ImageView
    lateinit var llAboutBookBottomSheet: LinearLayout
    lateinit var bottomSliderAdapter: BottomSliderCollectionAdapter
    lateinit var bottomSliderLiturgiesAdapter: BottomSliderLiturgiesAdapter
    var prefeUserId: Int = 0
    var android_id: String = ""
    lateinit var ivBack: ImageView

    @SuppressLint("HardwareIds")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.activity_about_book_liturgies, container, false)

        (activity as MainActivity).toolbar.visibility = View.GONE

        txtAboutDescription = view.findViewById(R.id.txtAboutDescription)
        txtTitle = view.findViewById(R.id.txtTitle)
        ivAboutImage = view.findViewById(R.id.ivAboutImage)
        llAboutBookBottomSheet = view.findViewById(R.id.ll_about_book_bottom_sheet)
        ivBack = view.findViewById(R.id.iv_back)

        if (arguments != null) {
            liturgies = requireArguments().getSerializable("liturgies") as GetLiturgiesDataVo
        }

        ivBack.setOnClickListener() {
            (activity as MainActivity).toolbar.visibility = View.VISIBLE
            (activity as MainActivity).replaceFragment(GetLiturgiesFragment(), "Get Liturgies")
        }

        if (Utils.isNetworkAvailable(requireContext())) {
            if (liturgies.isVolume == "Yes")
                getAboutVolume(liturgies.volumeId)
            else
                getAboutBookLiturgies(liturgies.bookId)
        } else {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }

        txtTitle.text = liturgies.volumeTitle

        llAboutBookBottomSheet.setOnClickListener {
            if (Utils.isNetworkAvailable(requireContext())) {
                if (liturgies.isVolume == "Yes")
                    getCollectionList(liturgies.volumeId)
                else
                    getMyLiturgiesList(liturgies.bookId)
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.check_internet),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        prefeUserId = Utils.readIntData(
            requireContext(),
            Constants.PrefUserID,
            0
        )!!

        android_id = Settings.Secure.getString(
            requireActivity().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        /*if (Utils.isNetworkAvailable(requireContext())) {
            if (liturgies.isVolume == "Yes")
                getCollectionList(liturgies.volumeId)
            else
                getMyLiturgiesList(liturgies.bookId)
        } else {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }*/

        return view
    }

    private fun getAboutBookLiturgies(bookId: Int) {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.aboutBook(bookId)

        try {
            call.enqueue(object : Callback<AboutBookResponseVo> {
                override fun onResponse(
                    call: Call<AboutBookResponseVo>,
                    response: Response<AboutBookResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        Glide.with(this@AboutBookLiturgiesFragment)
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
                            context!!,
                            response.body()!!.status.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AboutBookResponseVo>, t: Throwable) {
                    Toast.makeText(
                        context!!,
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun getAboutVolume(volumeID: Int) {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getAboutVolume(volumeID)

        try {
            call.enqueue(object : Callback<AboutVolumeResponseVo> {
                override fun onResponse(
                    call: Call<AboutVolumeResponseVo>,
                    response: Response<AboutVolumeResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        Glide.with(this@AboutBookLiturgiesFragment)
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
                            context!!,
                            response.body()!!.status.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AboutVolumeResponseVo>, t: Throwable) {
                    Toast.makeText(
                        context!!,
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showBottomSheetDialog() {
        val rvBootmSheet = view?.findViewById<RecyclerView>(R.id.buttomRecyclerView)

        val topCurveAnchor = view?.findViewById<ImageView>(R.id.topCurveAnchor)
        var bottomSheet = view?.findViewById<RelativeLayout>(R.id.bottom_sheet) as RelativeLayout
        val ivSlideUp = view?.findViewById<ImageView>(R.id.ivSlideUp)

        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        bottomSheet.setZ(10.0F)
        //  bottomSheetBehavior.peekHeight = 340
        // bottomSheetBehavior.peekHeight = 160
        bottomSheetBehavior.setPeekHeight(
            requireActivity().getResources().getDimension(R.dimen.bottom_sheet_hight)
                .toInt()
        );

        bottomSheetBehavior.isHideable = false

        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    //update my bottomsheet state.
                    ivSlideUp?.setImageDrawable(requireContext().resources.getDrawable(R.drawable.ic_down_arrow))

                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    ivSlideUp?.setImageDrawable(requireContext().resources.getDrawable(R.drawable.slideup_arrow))
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        ivSlideUp?.setOnClickListener() {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

        }

        android_id = Settings.Secure.getString(
            requireActivity().contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context)
        rvBootmSheet!!.layoutManager = layoutManager
        rvBootmSheet.adapter = bottomSliderAdapter
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showBottomSheetForLiturgiesDialog() {

        val buttomRcv = view?.findViewById<RecyclerView>(R.id.buttomRecyclerView)

        val topCurveAnchor = view?.findViewById<ImageView>(R.id.topCurveAnchor)
        var bottomSheet = view?.findViewById<RelativeLayout>(R.id.bottom_sheet) as RelativeLayout
        val ivSlideUp = view?.findViewById<ImageView>(R.id.ivSlideUp)

        val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        bottomSheet.setZ(10.0F)
        //  bottomSheetBehavior.peekHeight = 340
        bottomSheetBehavior.peekHeight = 160

        bottomSheetBehavior.isHideable = false

        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    //update my bottomsheet state.
                    ivSlideUp?.setImageResource(R.drawable.ic_down_arrow)

                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    ivSlideUp?.setImageResource(R.drawable.slideup_arrow)
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

        topCurveAnchor?.setOnClickListener() {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        android_id = Settings.Secure.getString(
            requireActivity().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        ivSlideUp?.setOnClickListener() {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

        }

        val layoutManager: RecyclerView.LayoutManager =
            LinearLayoutManager(context)
        buttomRcv!!.layoutManager = layoutManager
        buttomRcv.adapter = bottomSliderLiturgiesAdapter
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getCollectionList(volumeId: Int) {
        var collectionRequestVo: CollectionRequestVo = CollectionRequestVo()
        collectionRequestVo.volumeId = volumeId
        collectionRequestVo.deviceId = android_id

        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
            prefeUserId = Constants.SKIP_LOGIN_USER_ID
        }

        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getCollectionList(
            collectionRequestVo.deviceId,
            collectionRequestVo.volumeId,
            prefeUserId
        )

        try {
            call.enqueue(object : Callback<CollectionListResponseVo> {

                override fun onResponse(
                    call: Call<CollectionListResponseVo>,
                    response: Response<CollectionListResponseVo>
                ) {
                    if (response.body()?.statusCode == 1 && context != null) {

                        var totalPriceCollection: Double = 0.0

                        /*for (i in response.body()!!.response.data) {
                            totalPriceCollection += i.bookAmount
                        }*/

                        var wholeCollection: CollectionDataVo = CollectionDataVo()
                        var arrCollectionList: ArrayList<CollectionDataVo> = ArrayList()
                        arrCollectionList.addAll(response.body()!!.response.data)

                        var unPurchasedItems =
                            arrCollectionList.filter { cl -> cl.isPurchased != "Yes" }
                        if (unPurchasedItems.isEmpty()) liturgies.isPurchased = "Yes"

                        if (liturgies.isVolume == "Yes") {
                            if (liturgies.isPurchased == "Yes") {

                            } else {
                                wholeCollection.bookCoverPageImage = liturgies.volumeCoverPageImage
                                wholeCollection.bookTitle = liturgies.volumeTitle
                                wholeCollection.bookAmount = liturgies.volumeAmount
                                wholeCollection.volumeId = liturgies.volumeId
                                arrCollectionList.add(0, wholeCollection)
                            }
                        } else {
                            /*wholeCollection.bookCoverPageImage = liturgies.bookCoverPageImage
                            wholeCollection.bookTitle = liturgies.bookTitle
                            wholeCollection.bookAmount =
                                liturgies.bookAmount*/
                        }

                        bottomSliderAdapter = BottomSliderCollectionAdapter(
                            context!!,
                            arrCollectionList
                        )
                        showBottomSheetDialog()

                    } else {
                        if (context != null) {
                            Toast.makeText(
                                context!!,
                                response.body()!!.response.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<CollectionListResponseVo>, t: Throwable) {
                    if (context != null) {
                        Toast.makeText(
                            context!!,
                            "${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private fun getMyLiturgiesList(bookID: Int) {
        var myLiturgiesRequestVo: MyLiturgiesRequestVo = MyLiturgiesRequestVo()
        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
            myLiturgiesRequestVo.appUserId = Constants.SKIP_LOGIN_USER_ID
        } else {
            myLiturgiesRequestVo.appUserId = prefeUserId
        }
        myLiturgiesRequestVo.deviceId = android_id

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.getLiturgiesFromBookId(
                myLiturgiesRequestVo.appUserId,
                myLiturgiesRequestVo.deviceId,
                bookID
            )

        try {
            call.enqueue(object : Callback<MyLiturgiesResponseVo> {
                @SuppressLint("NewApi")
                @RequiresApi(Build.VERSION_CODES.CUPCAKE)
                override fun onResponse(
                    call: Call<MyLiturgiesResponseVo>,
                    response: Response<MyLiturgiesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1 && context != null) {

                        var liturgiesList: ArrayList<MyLiturgiesDataVo> = ArrayList()

                        liturgiesList.addAll(response.body()?.response?.data!!)

                        var unPurchasedItems =
                            liturgiesList.filter { ll -> ll.isPurchased != "Yes" }
                        if (unPurchasedItems.isEmpty()) liturgies.isPurchased = "Yes"

                        if (liturgies.isPurchased == "Yes" || liturgies.bookAmount == "0.00" || liturgies.bookAmount == "0.0") {

                        } else {
                            var liturgie = MyLiturgiesDataVo()
                            liturgie.bookId = liturgies.bookId
                            liturgie.chapterPageImage = liturgies.bookCoverPageImage
                            liturgie.price = liturgies.bookAmount
                            liturgie.chapterTitle = liturgies.bookTitle
                            liturgiesList.add(0, liturgie)
                        }
                        // liturgiesList.add(liturgie)


                        /*rvLiturgiesList.layoutManager =
                            LinearLayoutManager(this@LiturgiesListActivity)
                        getLiturgiesFromBookIDAdapter = GetLiturgiesFromBookIDAdapter(
                            this@LiturgiesListActivity,
                            liturgiesList
                        )
                        rvLiturgiesList.adapter = getLiturgiesFromBookIDAdapter*/
                        bottomSliderLiturgiesAdapter = BottomSliderLiturgiesAdapter(
                            context!!,
                            liturgiesList
                        )
                        showBottomSheetForLiturgiesDialog()

                    } else {
                        Toast.makeText(
                            activity,
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("litu", response.body()!!.response.message)
                    }
                }

                override fun onFailure(call: Call<MyLiturgiesResponseVo>, t: Throwable) {
                    Toast.makeText(
                        activity,
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        } catch (exception: java.lang.Exception) {
            exception.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        // Toast.makeText(context, "This is on resume", Toast.LENGTH_LONG).show()
        if (context != null) {
            if (Utils.isNetworkAvailable(requireContext())) {
                if (liturgies.isVolume == "Yes")
                    Handler(Looper.getMainLooper()).postDelayed(
                        Runnable { getCollectionList(liturgies.volumeId) },
                        Constants.AFTER_PURCHASE_REFRESH_DELAY
                    )
                else
                    Handler(Looper.getMainLooper()).postDelayed(
                        Runnable { getMyLiturgiesList(liturgies.bookId) },
                        Constants.AFTER_PURCHASE_REFRESH_DELAY
                    )

            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.check_internet),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}