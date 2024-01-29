package com.everymomentholy.ui.fragments

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.MyLiturgiesRequestVo
import com.everymomentholy.api.request.PurchaseRequestVo
import com.everymomentholy.api.response.*
import com.everymomentholy.interfaces.OnInAppPurchaseListener
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.adapter.GetLiturgiesFromBookIDAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.ProductTypes
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LiturgiesListFragment : Fragment(), OnInAppPurchaseListener {

    lateinit var rvLiturgiesList: RecyclerView
    private lateinit var android_id: String
    var prefeUserId: Int = 0
    lateinit var getLiturgiesFromBookIDAdapter: GetLiturgiesFromBookIDAdapter
    lateinit var ivToolbarDrawer: ImageView
    lateinit var txtToolbarName: TextView
    lateinit var ivToolbarNotification: ImageView

    lateinit var collectionData: CollectionDataVo

    // var collectionData: CollectionDataVo? = null
    lateinit var collectionFavData: GetFavoritesDataVo
    var bookId = 0
    var bookfavId = 0
    var isPurchaseSuccess: Boolean = false
    lateinit var ivToolbarBack: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_liturgies_list, container, false)

        (activity as MainActivity).toolbar.visibility = View.GONE

        rvLiturgiesList = view.findViewById(R.id.rvLiturgiesList)
        ivToolbarDrawer = view.findViewById(R.id.iv_toolbar_drawer)
        ivToolbarNotification = view.findViewById(R.id.iv_toolbar_notification)
        txtToolbarName = view.findViewById(R.id.txt_toolbar_name)
        ivToolbarBack = view.findViewById(R.id.iv_toolbar_backImage)

        ivToolbarNotification.visibility = View.GONE
        //ivToolbarDrawer.setImageDrawable(resources.getDrawable(R.drawable.ic_back))
        ivToolbarDrawer.visibility = View.GONE
        ivToolbarBack.visibility = View.VISIBLE
        txtToolbarName.text = "Liturgies"

        ivToolbarBack.setOnClickListener() {
            requireActivity().onBackPressed()
        }

        android_id = Settings.Secure.getString(
            requireActivity().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        prefeUserId = Utils.readIntData(
            requireActivity(),
            Constants.PrefUserID,
            0
        )!!

//        bookId = intent.getIntExtra("bookID", 0)
//        collectionData = intent.getSerializableExtra("collection") as CollectionDataVo

        if (arguments != null) {
            //liturgies = requireArguments().getSerializable("liturgies") as GetLiturgiesDataVo
           // bookId = intent.getIntExtra("bookID", 0)
            bookId = requireArguments().getInt("bookID", 0)
            collectionData = requireArguments().getSerializable("collection") as CollectionDataVo
        }

        if (Utils.isNetworkAvailable(requireActivity())) {
            getMyLiturgiesList(bookId)
            //getMyFavLiturgiesList(bookfavId)
        } else {
            Toast.makeText(
                requireActivity(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }

        return view
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
                @RequiresApi(Build.VERSION_CODES.CUPCAKE)
                override fun onResponse(
                    call: Call<MyLiturgiesResponseVo>,
                    response: Response<MyLiturgiesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        var liturgiesList: ArrayList<MyLiturgiesDataVo> = ArrayList()


                        var liturgie: MyLiturgiesDataVo = MyLiturgiesDataVo()
                        liturgie.bookId = collectionData.bookId
                        liturgie.chapterPageImage = collectionData.bookCoverPageImage
                        liturgie.price = collectionData.bookAmount
                        liturgie.chapterTitle = collectionData.bookTitle
                        liturgie.liturgyPurchaseCode = collectionData.bookPurchaseCode

                        if (collectionData.isPurchased == "Yes" || collectionData.bookAmount == "0.00" || collectionData.bookAmount == "0.0") {

                        } else {
                            liturgiesList.add(liturgie)
                        }
                        liturgiesList.addAll(response.body()?.response?.data!!)

                        rvLiturgiesList.layoutManager =
                            LinearLayoutManager(requireActivity())
                        getLiturgiesFromBookIDAdapter = GetLiturgiesFromBookIDAdapter(
                            requireActivity(),
                            liturgiesList
                        )
                        rvLiturgiesList.adapter = getLiturgiesFromBookIDAdapter

                    } else {
                        Toast.makeText(
                            requireActivity(),
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e("litu", response.body()!!.response.message)
                    }
                }

                override fun onFailure(call: Call<MyLiturgiesResponseVo>, t: Throwable) {
                    Toast.makeText(requireActivity(), "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }



    /**
     * This callback will acknowledge the successful in-app purchase to the backend server.
     */
    override fun onPurchaseComplete(purchaseRequestVo: PurchaseRequestVo) {

        /*runOnUiThread {
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
                collectionData.isPurchased = "Yes"
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
                            getMyLiturgiesList(bookId)
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

        if (Utils.isNetworkAvailable(requireActivity())) {
            getMyLiturgiesList(bookId)
        } else {
            Toast.makeText(
                requireActivity(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }
    }


}