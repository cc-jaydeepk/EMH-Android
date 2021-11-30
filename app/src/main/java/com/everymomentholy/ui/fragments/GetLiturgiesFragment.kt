package com.everymomentholy.ui.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.GetLiturgiesRequestVo
import com.everymomentholy.api.response.GetLiturgiesDataVo
import com.everymomentholy.api.response.GetLiturgiesResponseVo
import com.everymomentholy.interfaces.GetLiturgiesClickListner
import com.everymomentholy.ui.activity.CollectionListActivity
import com.everymomentholy.ui.activity.LiturgiesListDialogActivity
import com.everymomentholy.ui.activity.MainActivity
import com.everymomentholy.ui.adapter.GetLiturgiesAdapter
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GetLiturgiesFragment : Fragment(), GetLiturgiesClickListner {

    lateinit var viewPager: ViewPager
    private lateinit var adapter: GetLiturgiesAdapter
    private lateinit var android_id: String
    var prefeUserId: Int = 0
    lateinit var txtToolbarName: TextView
    lateinit var txtGetLiturgiesAbout: TextView
    private lateinit var txtLiturgyTitle: TextView
    private lateinit var txtLiturgyPrice: TextView
    var liturgiesTitle: String = ""
    private lateinit var btnGetLiturgiesReadNow: Button
    private lateinit var txtUnlock: TextView
    private lateinit var txtDollar: TextView

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_getliturgies, container, false)


        viewPager = view.findViewById(R.id.viewPager)
        txtLiturgyTitle = view.findViewById(R.id.txtLiturgyTitle)
        txtLiturgyPrice = view.findViewById(R.id.txtLiturgyPrice)
        // btnGetLiturgiesReadNow = view.findViewById(R.id.btnGetLiturgiesReadNow)
        txtGetLiturgiesAbout = view.findViewById(R.id.txtGetLiturgiesAbout)
        txtUnlock = view.findViewById(R.id.txtUnlock)

        txtDollar = view.findViewById(R.id.txtDollar)

        android_id = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )
        prefeUserId = Utils.readIntData(
            requireActivity(),
            Constants.PrefUserID,
            0
        )!!

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                var liturgyData = adapter.getLiturgiesData()?.get(position)

                setLiturgiesAndVolumeData(liturgyData)
            }

            override fun onPageSelected(position: Int) {
                txtLiturgyTitle.text = adapter.getLiturgiesData()?.get(position).volumeTitle
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })

        if (Utils.isNetworkAvailable(requireContext())) {
            getBooks()
        } else {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.check_internet),
                Toast.LENGTH_LONG
            ).show()
        }

        return view
    }

    private fun getBooks() {
        var getLiturgiesRequestVo: GetLiturgiesRequestVo = GetLiturgiesRequestVo()
        getLiturgiesRequestVo.deviceId = android_id
        var token = ""
        if (Constants.USER_LOGIN_STATUS == Constants.LOGIN) {
            token = "bearer " + Utils.readStringFromSharedPref(
                requireContext(),
                Constants.SHARED_PREF_TOKEN,
                ""
            )
            getLiturgiesRequestVo.appUserId = prefeUserId
        } else {
            getLiturgiesRequestVo.appUserId = Constants.SKIP_LOGIN_USER_ID
        }
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.getBooks(
            getLiturgiesRequestVo.appUserId,
            getLiturgiesRequestVo.deviceId,
            token
        )

        try {
            call.enqueue(object : Callback<GetLiturgiesResponseVo> {
                override fun onResponse(
                    call: Call<GetLiturgiesResponseVo>,
                    response: Response<GetLiturgiesResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        if (context != null) {
                            adapter = GetLiturgiesAdapter(
                                context!!,
                                response.body()!!.response.data
                            )
                            viewPager.setPadding(100, 0, 100, 0)
                            viewPager.adapter = adapter;
                        }

                    } else {
                        Toast.makeText(
                            requireActivity(),
                            response.body()!!.response.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GetLiturgiesResponseVo>, t: Throwable) {
                    Toast.makeText(requireActivity(), "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun setLiturgiesAndVolumeData(liturgyData: GetLiturgiesDataVo) {
        txtGetLiturgiesAbout.setOnClickListener() {
            /*    val intent = Intent(context, AboutBookLiturgiesActivity::class.java)
                intent.putExtra("liturgies", liturgyData)
                context?.startActivity(intent)
    */
            val bundle = Bundle()
            bundle.putSerializable("liturgies", liturgyData)
            var fragment: Fragment = AboutBookLiturgiesFragment()
            (activity as MainActivity).replaceFragment(fragment, "Get Liturgies", bundle)

        }

        txtUnlock.setOnClickListener() {
            if (liturgyData.isVolume == "Yes") {
                val intent = Intent(context, CollectionListActivity::class.java)
                intent.putExtra("liturgies", liturgyData)
                context?.startActivity(intent)
            } else {
                if (txtUnlock.text == "Read Now") {
                    val intent = Intent(context, LiturgiesListDialogActivity::class.java)
                    intent.putExtra("liturgies", liturgyData)
                    context?.startActivity(intent)
                } else {
                    AlertDialog.Builder(requireContext())
                        .setMessage("This part is under Development.")
                        .setPositiveButton(android.R.string.yes) { dialog, which ->
                        }.show()
                }
            }
        }

        if (liturgyData.isVolume == "Yes") {

            txtLiturgyTitle.text = liturgyData.volumeTitle

            if (liturgyData.bookAmount == "0.0" || liturgyData.bookAmount == "0.00" || liturgyData.isPurchased == "Yes") {
                txtUnlock.text = "Read Now"
                var sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    txtUnlock.setBackground(context?.resources?.getDrawable(R.drawable.bg_read_now));
                    txtUnlock.setTextColor(context?.resources?.getColor(R.color.loginbg)!!)
                } else {
                    txtUnlock.setBackground(context?.resources?.getDrawable(R.drawable.bg_read_now));
                    txtUnlock.setTextColor(context?.resources?.getColor(R.color.loginbg)!!)
                }
                if (liturgyData.isPurchased == "Yes") {
                    txtLiturgyPrice.text = "Purchased"
                } else {
                    txtLiturgyPrice.text = "Free"
                }
                txtDollar.text = ""
            } else {
                txtLiturgyPrice.text = "$ " + liturgyData.volumeAmount
                txtUnlock.setBackground(context?.resources?.getDrawable(R.drawable.bg_unlock));
                txtUnlock.setTextColor(context?.resources?.getColor(R.color.white)!!)
                txtUnlock.text = "Unlock"
            }

        } else {

            if (liturgyData.bookAmount == "0.0" || liturgyData.bookAmount == "0.00" || liturgyData.isPurchased == "Yes") {
                txtUnlock.text = "Read Now"
                var sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    txtUnlock.setBackground(context?.resources?.getDrawable(R.drawable.bg_read_now));
                    txtUnlock.setTextColor(context?.resources?.getColor(R.color.loginbg)!!)
                } else {
                    txtUnlock.setBackground(context?.resources?.getDrawable(R.drawable.bg_read_now));
                    txtUnlock.setTextColor(context?.resources?.getColor(R.color.loginbg)!!)
                }
                if (liturgyData.isPurchased == "Yes") {
                    txtLiturgyPrice.text = "Purchased"
                } else {
                    txtLiturgyPrice.text = "Free"
                }
                txtDollar.text = ""
            } else {
                txtLiturgyPrice.text = "$ " + liturgyData.bookAmount
                txtUnlock.setBackground(context?.resources?.getDrawable(R.drawable.bg_unlock));
                txtUnlock.setTextColor(context?.resources?.getColor(R.color.white)!!)
                txtUnlock.text = "Unlock"
            }
            txtLiturgyTitle.text = liturgyData.bookTitle

        }
    }

    override fun onResume() {
        super.onResume()
        // (activity as MainActivity).toolbar.visibility = View.VISIBLE
    }


}