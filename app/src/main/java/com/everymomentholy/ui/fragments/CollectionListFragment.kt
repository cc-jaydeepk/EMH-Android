package com.everymomentholy.ui.fragments

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.CollectionRequestVo
import com.everymomentholy.api.request.PurchaseRequestVo
import com.everymomentholy.api.response.*
import com.everymomentholy.interfaces.OnInAppPurchaseListener
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.adapter.BottomSliderCollectionAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.ProductTypes
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CollectionListFragment : Fragment(), OnInAppPurchaseListener {

    lateinit var rvCollectionList: RecyclerView
    var prefeUserId: Int = 0
    var android_id: String = ""
    var liturgies: GetLiturgiesDataVo = GetLiturgiesDataVo()

    //lateinit var bottomSliderAdapter: CollectionAdapter
    lateinit var bottomSliderAdapter: BottomSliderCollectionAdapter
   // lateinit var ivToolbarNotification: ImageView
   // lateinit var txtToolbarName: TextView
   // lateinit var ivToolbarDrawer: ImageView
   // lateinit var ivToolbarBack: ImageView
    var isPurchaseSuccess: Boolean = false
    var volumePurchaseCode: String = ""
    lateinit var progressCardView: CardView
    var isFrom: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_collection_list, container, false)

        (activity as MainActivity).toolbar.visibility = View.VISIBLE
        (activity as MainActivity).iv_toolbar_backImage.visibility = View.VISIBLE
        (activity as MainActivity).iv_toolbar_search.visibility = View.GONE
        (activity as MainActivity).iv_toolbar_drawer.visibility = View.GONE
        (activity as MainActivity).txt_toolbar_name.text = "Collection"

        rvCollectionList = view.findViewById(R.id.rvCollectionList)

        progressCardView = view.findViewById(R.id.progressCardView)


        (activity as MainActivity).iv_toolbar_backImage.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        prefeUserId = Utils.readIntData(
            requireActivity(),
            Constants.PrefUserID,
            0
        )!!

        android_id = Settings.Secure.getString(
            requireActivity().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        //liturgies = intent.getSerializableExtra("liturgies") as GetLiturgiesDataVo
        //liturgies = requireArguments().getSerializable("liturgies") as GetLiturgiesDataVo
        if (arguments != null) {
            liturgies = requireArguments().getSerializable("liturgies") as GetLiturgiesDataVo
        }

        if (Utils.isNetworkAvailable(requireActivity())) {
            if (liturgies.isVolume == "Yes")
                progressCardView.visibility = View.VISIBLE
            requireActivity().getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            getAboutVolume(liturgies.volumeId)

            getCollectionList(liturgies.volumeId)

        } else {
            Toast.makeText(
                requireActivity(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }

        return view
    }

    fun getCollectionList(volumeId: Int) {
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
                @RequiresApi(Build.VERSION_CODES.CUPCAKE)
                override fun onResponse(
                    call: Call<CollectionListResponseVo>,
                    response: Response<CollectionListResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {


                        var totalPriceCollection: Double = 0.0

                        /*for (i in response.body()!!.response.data) {
                            totalPriceCollection += i.bookAmount
                        }*/

                        var wholeCollection = CollectionDataVo()
                        if (liturgies.isVolume == "Yes" && !liturgies.isPurchased.equals(
                                "Yes",
                                false
                            )
                        ) {
                            wholeCollection.bookCoverPageImage = liturgies.volumeCoverPageImage
                            wholeCollection.bookTitle = liturgies.volumeTitle
                            wholeCollection.bookAmount = liturgies.volumeAmount
                            wholeCollection.discountAmount = liturgies.discountAmount
                            wholeCollection.bookPurchaseCode = volumePurchaseCode
                            wholeCollection.volumeId = liturgies.volumeId
                        } else {
                            wholeCollection.bookCoverPageImage = liturgies.bookCoverPageImage
                            wholeCollection.bookTitle = liturgies.bookTitle
                            wholeCollection.bookAmount =
                                liturgies.bookAmount
                            wholeCollection.bookPurchaseCode = liturgies.bookPurchaseCode
                        }

                        var arrCollectionList: ArrayList<CollectionDataVo> = ArrayList()
                        if (liturgies.isPurchased != "Yes") {
                            arrCollectionList.add(wholeCollection)
                        }
                        arrCollectionList.addAll(response.body()!!.response.data)
                        progressCardView.visibility = View.GONE
                        requireActivity().getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        bottomSliderAdapter = BottomSliderCollectionAdapter(
                            requireActivity(),
                            arrCollectionList
                        )

                        val layoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(requireActivity())
                        rvCollectionList.layoutManager = layoutManager
                        rvCollectionList.adapter = bottomSliderAdapter

                        // showBottomSheetDialog()

                    } else {
                        progressCardView.visibility = View.GONE
                        requireActivity().getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(
                            requireActivity(),
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<CollectionListResponseVo>, t: Throwable) {
                    Toast.makeText(
                        requireActivity(),
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
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
                        progressCardView.visibility = View.GONE
                        requireActivity().getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        volumePurchaseCode = response.body()!!.response.volumePurchaseCode
                    } else {
                        progressCardView.visibility = View.GONE
                        requireActivity().getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(
                            requireActivity(),
                            response.body()!!.status.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AboutVolumeResponseVo>, t: Throwable) {
                    Toast.makeText(
                        requireActivity(),
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun onPurchaseComplete(purchaseRequestVo: PurchaseRequestVo) {

        /* runOnUiThread {
             Toast.makeText(this, "In app purchase complete", Toast.LENGTH_LONG).show()
         }*/

        val request = APIService.buildService(APIInterface::class.java)
        lateinit var call: Call<PrivateShareResponseVo>

        when (purchaseRequestVo.productType) {
            ProductTypes.LITURGY -> {
                call = request.purchaseLiturgyAcknowledge(
                    purchaseRequestVo,
                    "bearer " + Utils.readStringFromSharedPref(
                        requireActivity(),
                        Constants.SHARED_PREF_TOKEN,
                        ""
                    )
                )
            }
            ProductTypes.BOOK -> {
                call = request.purchaseBookAcknowledge(
                    purchaseRequestVo,
                    "bearer " + Utils.readStringFromSharedPref(
                        requireActivity(),
                        Constants.SHARED_PREF_TOKEN,
                        ""
                    )
                )
                if (liturgies.isVolume != "Yes") liturgies.isPurchased = "Yes"
            }
            ProductTypes.VOLUME -> {
                call = request.purchaseVolumeAcknowledge(
                    purchaseRequestVo,
                    "bearer " + Utils.readStringFromSharedPref(
                        requireActivity(),
                        Constants.SHARED_PREF_TOKEN,
                        ""
                    )
                )
                liturgies.isPurchased = "Yes"
            }
        }

        try {
            call.enqueue(object : Callback<PrivateShareResponseVo> {
                override fun onResponse(
                    call: Call<PrivateShareResponseVo>,
                    response: Response<PrivateShareResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        isPurchaseSuccess = true
                        if (Utils.isNetworkAvailable(requireActivity())) {
                            isPurchaseSuccess = false
                            if (liturgies.isVolume == "Yes")
                                getAboutVolume(liturgies.volumeId)
                            getCollectionList(liturgies.volumeId)
                        } else {
                            Toast.makeText(
                                requireActivity(),
                                resources.getString(R.string.check_internet),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    } else {
                        Toast.makeText(
                            requireActivity(),
                            response.body()!!.response,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PrivateShareResponseVo>, t: Throwable) {
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
        /*if (Utils.isNetworkAvailable(requireActivity())) {
            if (liturgies.isVolume == "Yes") {
                getAboutVolume(liturgies.volumeId)
                getCollectionList(liturgies.volumeId)
            }
        } else {
            Toast.makeText(
                requireActivity(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }*/
    }

    /*fun replaceFragment(fragment: Fragment, txtToolbarTitle: String, arguments: Bundle? = null) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        transaction.replace(R.id.mainFrame, fragment)
        transaction.disallowAddToBackStack()
        if (arguments != null) {
            fragment.arguments = arguments
        }
        transaction.commit()
    }*/


}