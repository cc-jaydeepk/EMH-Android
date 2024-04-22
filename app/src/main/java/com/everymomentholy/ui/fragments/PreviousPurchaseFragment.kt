package com.everymomentholy.ui.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.MyLiturgiesRequestVo
import com.everymomentholy.api.request.PurchaseHistoryReqVo
import com.everymomentholy.api.response.*
import com.everymomentholy.interfaces.LiturgyLitstClickListner
import com.everymomentholy.interfaces.PlayAudioClickListner
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.activity.PlayAudioActivity
import com.everymomentholy.ui.adapter.BottomSliderAdapter
import com.everymomentholy.ui.adapter.GetLiturgiesAdapter
import com.everymomentholy.ui.adapter.PastPurchaseAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PreviousPurchaseFragment : Fragment(), LiturgyLitstClickListner, PlayAudioClickListner {

    private lateinit var rvPurchaseHistory: RecyclerView
    lateinit var relativeNoData: RelativeLayout
    lateinit var txtNoData: TextView
    lateinit var linearLayout: LinearLayout
    lateinit var progressCardView: CardView
    private lateinit var purchaseAdapter: PastPurchaseAdapter

    private var freePurchasedLiturgies = ArrayList<PurchaseDataVo>()
    private var freeLiturgies: ArrayList<MyLiturgiesDataVo> = arrayListOf()
    private lateinit var bottomSliderAdapter: BottomSliderAdapter
    lateinit var relativeBottomSheet: RelativeLayout

    private lateinit var android_id: String
    var prefeUserId: Int = 0
    private lateinit var adapterNew: GetLiturgiesAdapter
    lateinit var includedLayout: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_past_purchase, container, false)


        rvPurchaseHistory = view.findViewById(R.id.rcvHistory)
        progressCardView = view.findViewById(R.id.progressCardView)
        linearLayout = view.findViewById(R.id.linearLayout)
        relativeNoData = view.findViewById(R.id.relativeNoData)
        txtNoData = view.findViewById(R.id.txtNoData)
        relativeBottomSheet = view.findViewById(R.id.bottom_sheet)

        relativeBottomSheet.visibility = View.GONE

        (activity as MainActivity).iv_toolbar_search.visibility = View.GONE
        (activity as MainActivity).iv_toolbar_notification.visibility = View.GONE

        android_id = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )
        prefeUserId = Utils.readIntData(
            requireActivity(),
            Constants.PrefUserID,
            0
        )!!

        if (Utils.isNetworkAvailable(requireActivity())) {
            progressCardView.visibility = View.VISIBLE
            requireActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            getPurchasedBooks()
            //getOrderAndSubscriptionHistory()
        } else {
            Toast.makeText(
                requireActivity(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }

        return view
    }

    private fun getPurchasedBooks() {
        var getPurchaseHistoryReqVo: PurchaseHistoryReqVo = PurchaseHistoryReqVo()

        getPurchaseHistoryReqVo.deviceId = android_id
        var token = ""
        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
            getPurchaseHistoryReqVo.appUserId = Constants.SKIP_LOGIN_USER_ID
        } else {
            token = "bearer " + Utils.readStringFromSharedPref(
                requireContext(),
                Constants.SHARED_PREF_TOKEN,
                ""
            )
            getPurchaseHistoryReqVo.appUserId = prefeUserId
        }
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getPurchasedBooks(
            getPurchaseHistoryReqVo.appUserId,
            getPurchaseHistoryReqVo.deviceId,
            token
        )

        try {
            call.enqueue(object : Callback<PastPurchaseHistoryResponseVo> {
                override fun onResponse(
                    call: Call<PastPurchaseHistoryResponseVo>,
                    response: Response<PastPurchaseHistoryResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        progressCardView.visibility = View.GONE
                        requireActivity().getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        // includedLayout.visibility = View.VISIBLE
                        linearLayout.visibility = View.VISIBLE
                        var noVolume =
                            response.body()!!.response.data.filter { it.isVolume == "No" } as ArrayList<PurchaseDataVo>

                        if (!freePurchasedLiturgies.isNullOrEmpty())
                            freePurchasedLiturgies.clear()

                        freePurchasedLiturgies = noVolume

                        purchaseAdapter = PastPurchaseAdapter(
                            context!!,
                            noVolume,
                            this@PreviousPurchaseFragment
                        )
                        val layoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(context)
                        rvPurchaseHistory.layoutManager = layoutManager
                        rvPurchaseHistory.adapter = purchaseAdapter

                    } else {
                        progressCardView.visibility = View.GONE
                        requireActivity().getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        //  includedLayout.visibility = View.GONE
                        relativeNoData.visibility = View.VISIBLE
                        txtNoData.text = response.body()!!.message
                        /*Toast.makeText(
                            requireActivity(),
                            response.body()!!.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()*/
                    }
                }

                override fun onFailure(call: Call<PastPurchaseHistoryResponseVo>, t: Throwable) {
                    Toast.makeText(requireActivity(), "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun onMyLiturgiesListClick(pos: Int, bookID: Int, isAuto: Boolean) {
        /*freePurchasedLiturgies.forEach { f -> f.isClicked = false }
        freePurchasedLiturgies[pos].isClicked = true
        purchaseAdapter.notifyDataSetChanged()*/

        if (Utils.isNetworkAvailable(requireContext())) {
            progressCardView.visibility = View.VISIBLE
            getMyLiturgiesList(bookID, isAuto)
        } else {
            //showLiturgiesOffline(bookID, isAuto)
            /*  Toast.makeText(
                  requireContext(),
                  resources.getString(R.string.check_internet),
                  Toast.LENGTH_LONG
              ).show()*/
        }
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
                        // freeLiturgies = byBookID.filter { it.isFree == "No" || it.isPurchased == "Yes" } as ArrayList<MyLiturgiesDataVo>
                        freeLiturgies =
                            byBookID.filter { it.isFree == "No" } as ArrayList<MyLiturgiesDataVo>

                        var purchased =
                            freeLiturgies.filter { it.isPurchased == "Yes" } as ArrayList<MyLiturgiesDataVo>

                        Log.e("PURCHASED", "PURCHASED" + purchased.size.toString())

                        if (purchased.size > 0) {
                            showBottomSheetDialog(purchased, isAuto)
                        } else {
                            progressCardView.visibility = View.GONE
                            AlertDialog.Builder(context!!)
                                .setMessage("No liturgies available.")
                                .setPositiveButton(android.R.string.yes) { dialog, which ->
                                }.show()
                        }

                        /*if (freeLiturgies.size > 0) {
                            showBottomSheetDialog(freeLiturgies, isAuto)
                        } else {
                            progressCardView.visibility = View.GONE
                            AlertDialog.Builder(context!!)
                                .setMessage("No liturgies available.")
                                .setPositiveButton(android.R.string.yes) { dialog, which ->
                                }.show()
                        }*/
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
                        /*   Toast.makeText(
                               requireContext(), "${t.message}", Toast.LENGTH_SHORT
                           ).show()*/
                    }
                }
            })
        } catch (exception: java.lang.Exception) {
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
        var bottomSheet: RelativeLayout?
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
                this@PreviousPurchaseFragment
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
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun onPlayAudio(title: String, audioUrl: String, isAuto: Boolean) {
        val intent = Intent(context, PlayAudioActivity::class.java)
        intent.putExtra("title", title);
        intent.putExtra("audio", audioUrl);
        context?.startActivity(intent)
    }

}