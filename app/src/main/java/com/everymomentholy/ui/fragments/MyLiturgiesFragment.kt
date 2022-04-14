package com.everymomentholy.ui.fragments

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.GetLiturgiesRequestVo
import com.everymomentholy.api.request.MyLiturgiesRequestVo
import com.everymomentholy.api.response.*
import com.everymomentholy.interfaces.LiturgyLitstClickListner
import com.everymomentholy.ui.adapter.BottomSliderAdapter
import com.everymomentholy.ui.adapter.MyLiturgyAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.folioreader.emh.EMHUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
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

    //lateinit var freeLiturgies: ArrayList<LiturgiesDataVo>
    private var freeLiturgies: ArrayList<MyLiturgiesDataVo> = arrayListOf()
    private lateinit var bottomSliderAdapter: BottomSliderAdapter
    private var freePurchasedLiturgies = ArrayList<GetLiturgiesDataVo>()
    lateinit var progressCardView: CardView

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myliturgies, container, false)

        recycler_liturgy = view.findViewById(R.id.recycler_liturgy)
        // txtUserId = view.findViewById(R.id.txtUserId)
        ll_enroute_bottom_sheet = view.findViewById(R.id.ll_enroute_bottom_sheet)
        progressCardView = view.findViewById(R.id.progressCardView)
        android_id = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        prefeUserId = Utils.readIntData(
            requireActivity(),
            Constants.PrefUserID,
            0
        )!!

        Log.e("log", prefeUserId.toString())

        //getMyLiturgiesList()
        if (Utils.isNetworkAvailable(requireContext())) {
            getBooks()
        } else {
            setUpOfflineView()
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
            loadFromTheCache()
        }

        ll_enroute_bottom_sheet.setOnClickListener {
            if (!freeLiturgies.isNullOrEmpty())
                showBottomSheetDialog(freeLiturgies, false)
        }

        return view
    }

    private fun loadFromTheCache() {

    }

    private fun getMyLiturgiesList(bookID: Int, isAuto: Boolean = false) {
        var myLiturgiesRequestVo: MyLiturgiesRequestVo = MyLiturgiesRequestVo()
        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
            myLiturgiesRequestVo.appUserId = Constants.SKIP_LOGIN_USER_ID
        } else {
            myLiturgiesRequestVo.appUserId = prefeUserId
        }
        myLiturgiesRequestVo.deviceId = android_id

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
                        progressCardView.visibility = View.GONE
                        var byBookID =
                            response.body()!!.response.data.filter { it.bookId == bookID } as ArrayList<MyLiturgiesDataVo>
                        //freeLiturgies = response.body()!!.response.data.filter { it.isFree == "Yes" } as ArrayList<LiturgiesDataVo>
                        freeLiturgies =
                            byBookID.filter { it.isFree == "Yes" || it.isPurchased == "Yes" } as ArrayList<MyLiturgiesDataVo>

                        Log.e("free liturgies", freeLiturgies.size.toString())

                        if (freeLiturgies.size > 0) {
                            showBottomSheetDialog(freeLiturgies, isAuto)
                        } else {
                            progressCardView.visibility = View.GONE
                            AlertDialog.Builder(context!!)
                                .setMessage("No liturgies available.")
                                .setPositiveButton(android.R.string.yes) { dialog, which ->
                                }.show()
                        }
                    } else {
                        progressCardView.visibility = View.GONE
                        if (context != null) {
                            Toast.makeText(
                                requireContext(),
                                response.body()!!.response.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        Log.e("litu", response.body()!!.response.message)
                    }
                }

                override fun onFailure(call: Call<MyLiturgiesResponseVo>, t: Throwable) {
                    progressCardView.visibility = View.GONE
                    if (context != null) {
                        Toast.makeText(
                            requireContext(), "${t.message}", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        } catch (exception: Exception) {
            progressCardView.visibility = View.GONE
            exception.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showBottomSheetDialog(
        filteredDataVo: ArrayList<MyLiturgiesDataVo>,
        isAuto: Boolean
    ) {

        val buttomRcv = view?.findViewById<RecyclerView>(R.id.buttomRecyclerView)

        val topCurveAnchor = view?.findViewById<ImageView>(R.id.topCurveAnchor)
        lateinit var bottomSheet: RelativeLayout
        try {
            bottomSheet = view?.findViewById<RelativeLayout>(R.id.bottom_sheet) as RelativeLayout


            val ivSlideUp = view?.findViewById<ImageView>(R.id.ivSlideUp)

            val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
            if (!isAuto) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }


            bottomSheet.setZ(10.0F)
            // bottomSheetBehavior.peekHeight = 80
            bottomSheetBehavior.setPeekHeight(
                requireActivity().getResources().getDimension(R.dimen.bottom_sheet_hight)
                    .toInt()
            )

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

            android_id = Settings.Secure.getString(
                requireContext().contentResolver,
                Settings.Secure.ANDROID_ID
            )

            bottomSliderAdapter = BottomSliderAdapter(
                requireContext(),
                filteredDataVo,
            )
            val layoutManager: RecyclerView.LayoutManager =
                LinearLayoutManager(context)
            if (buttomRcv != null) {
                buttomRcv.layoutManager = layoutManager
                buttomRcv.adapter = bottomSliderAdapter
            }

            ivSlideUp?.setOnClickListener() {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                } else {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onMyLiturgiesListClick(pos: Int, bookID: Int, isAuto: Boolean) {
        freePurchasedLiturgies.forEach { f -> f.isClicked = false }
        freePurchasedLiturgies[pos].isClicked = true
        liturgyAdapter.notifyDataSetChanged()
        progressCardView.visibility = View.VISIBLE
        if (Utils.isNetworkAvailable(requireContext())) {
            getMyLiturgiesList(bookID, isAuto)
        } else {
            showLiturgiesOffline()
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun getBooks() {
        var getLiturgiesRequestVo: GetLiturgiesRequestVo = GetLiturgiesRequestVo()
        var token = ""
        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
            getLiturgiesRequestVo.appUserId = Constants.SKIP_LOGIN_USER_ID
            getLiturgiesRequestVo.deviceId = android_id
        } else {
            getLiturgiesRequestVo.appUserId = prefeUserId
            getLiturgiesRequestVo.deviceId = android_id
            token = "bearer " + Utils.readStringFromSharedPref(
                requireContext(),
                Constants.SHARED_PREF_TOKEN,
                ""
            )
        }
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
                    if (response.body()?.statusCode == 1 && context != null) {

                        var noVolume =
                            response.body()!!.response.data.filter { it.isVolume == "No" } as ArrayList<GetLiturgiesDataVo>

                        /*       if (!freePurchasedLiturgies.isNullOrEmpty())
                                   freePurchasedLiturgies.clear()

                               freePurchasedLiturgies =
                                   noVolume.filter { it.isFreeLiturgyAvailable == "Yes" || it.isPurchased == "Yes" } as ArrayList<GetLiturgiesDataVo>
       */
                        var freeAvailableLiturgies =
                            noVolume.filter { it.isFreeLiturgyAvailable == "Yes" || it.isPurchased == "Yes" } as ArrayList<GetLiturgiesDataVo>

                        if (!freePurchasedLiturgies.isNullOrEmpty())
                            freePurchasedLiturgies.clear()

                        freePurchasedLiturgies = freeAvailableLiturgies

                        Log.e("free", freePurchasedLiturgies.size.toString())
                        try {

                            if (freePurchasedLiturgies.isNotEmpty()) {
                                freePurchasedLiturgies[0].isClicked = true
                                liturgyAdapter = MyLiturgyAdapter(
                                    context!!,
                                    freePurchasedLiturgies,
                                    this@MyLiturgiesFragment
                                )
                                val layoutManager: RecyclerView.LayoutManager =
                                    LinearLayoutManager(context)
                                recycler_liturgy.layoutManager = layoutManager
                                recycler_liturgy.adapter = liturgyAdapter
                                liturgyAdapter.setLiturgiesClick(this@MyLiturgiesFragment)
                                getMyLiturgiesList(freePurchasedLiturgies[0].bookId, true)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        if (context != null) {
                            Toast.makeText(
                                requireContext(),
                                response.body()!!.response.message.toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<GetLiturgiesResponseVo>, t: Throwable) {
                    if (context != null) {
                        Toast.makeText(
                            requireContext(), "${t.message}", Toast.LENGTH_SHORT
                        ).show()
                    }
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
                if (bottomSliderAdapter.bookOpenPosition != -1) {
                    bottomSliderAdapter.updateFavoriteStatusFromBookRead()
                    bottomSliderAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun setUpOfflineView() {
        val booksJsonString = Utils.readJsonFromFile(requireContext(), Constants.BOOKS_FILE_NAME)
        val liturgiesJsonString =
            Utils.readJsonFromFile(requireContext(), Constants.LITURGIES_FILE_NAME)

        val response: LiturgiesResponseVo =
            Gson().fromJson(booksJsonString, LiturgiesResponseVo::class.java)

        var noVolume =
            response.data.filter { it.isVolume == "No" } as ArrayList<GetLiturgiesDataVo>


        var freeAvailableLiturgies =
            noVolume.filter { it.isFreeLiturgyAvailable == "Yes" || it.isPurchased == "Yes" } as ArrayList<GetLiturgiesDataVo>

        if (!freePurchasedLiturgies.isNullOrEmpty())
            freePurchasedLiturgies.clear()

        freePurchasedLiturgies = freeAvailableLiturgies

        Log.e("free", freePurchasedLiturgies.size.toString())
        try {

            if (freePurchasedLiturgies.isNotEmpty()) {
                freePurchasedLiturgies[0].isClicked = true
                liturgyAdapter = MyLiturgyAdapter(
                    requireContext(),
                    freePurchasedLiturgies,
                    this@MyLiturgiesFragment
                )
                val layoutManager: RecyclerView.LayoutManager =
                    LinearLayoutManager(context)
                recycler_liturgy.layoutManager = layoutManager
                recycler_liturgy.adapter = liturgyAdapter
                liturgyAdapter.setLiturgiesClick(this@MyLiturgiesFragment)
                getMyLiturgiesList(freePurchasedLiturgies[0].bookId, true)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //
    private fun showLiturgiesOffline()
    {

    }
}