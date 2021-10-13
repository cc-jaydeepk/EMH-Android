package com.everymomentholy.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.GetUserProfileRequestVo
import com.everymomentholy.api.response.GetUserProfileVo
import com.everymomentholy.ui.activity.ChangePasswordActivity
import com.everymomentholy.ui.activity.EditProfileActivity
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyProfileFragment : Fragment() {

    lateinit var imgChangePsw: ImageView
    private lateinit var edtUserFirstName: EditText
    private lateinit var edtUserLastName: EditText
    private lateinit var edtUserEmail: EditText
    private lateinit var edtUserPhoneNuber: EditText
    private lateinit var btnEditProfile: Button
    private lateinit var profile_image: ImageView
    private lateinit var android_id: String
    var prefeUserId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myprofile, container, false)

        imgChangePsw = view.findViewById(R.id.imgChangePsw)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        edtUserFirstName = view.findViewById(R.id.edtUserFirstName)
        edtUserLastName = view.findViewById(R.id.edtUserLastName)
        edtUserEmail = view.findViewById(R.id.edtUserEmail)
        edtUserPhoneNuber = view.findViewById(R.id.edtUserPhoneNuber)
        profile_image = view.findViewById(R.id.profile_image)


        btnEditProfile.setOnClickListener {
            val intent = Intent(requireActivity(), EditProfileActivity::class.java)
            startActivity(intent)
        }

        android_id = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        prefeUserId = Utils.readIntData(
            requireActivity(),
            Constants.PrefUserID,
            0
        )!!

        getUserProfile()

        imgChangePsw.setOnClickListener {
            val intent = Intent(activity, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
        return view
    }

    private fun getUserProfile() {
        var getUserProfileRequestVo: GetUserProfileRequestVo = GetUserProfileRequestVo()
        getUserProfileRequestVo.deviceId = android_id
        getUserProfileRequestVo.userId = prefeUserId

        Log.e(
            "token", Utils.readStringFromSharedPref(
                requireContext(),
                Constants.SHARED_PREF_TOKEN,
                ""
            ).toString()
        )

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.getUserProfile(
                getUserProfileRequestVo.userId, getUserProfileRequestVo.deviceId,
                "bearer " + Utils.readStringFromSharedPref(
                    requireContext(),
                    Constants.SHARED_PREF_TOKEN,
                    ""
                )
            )


        try {
            call.enqueue(object : Callback<GetUserProfileVo> {
                override fun onResponse(
                    call: Call<GetUserProfileVo>,
                    response: Response<GetUserProfileVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        var fiestname = response.body()!!.response.firstName
                        var lastname = response.body()!!.response.lastName
                        var email = response.body()!!.response.email
                        var phoneNo = response.body()!!.response.mobile
                        fun String.toEditable(): Editable =
                            Editable.Factory.getInstance().newEditable(this)
                        edtUserFirstName.text =
                            Editable.Factory.getInstance().newEditable(fiestname)
                        edtUserLastName.text = Editable.Factory.getInstance().newEditable(lastname)
                        edtUserEmail.text = Editable.Factory.getInstance().newEditable(email)
                        edtUserPhoneNuber.text = Editable.Factory.getInstance().newEditable(phoneNo)

                        Glide.with(requireActivity())
                            .load(response.body()!!.response.userProfilePic)
                            .into(profile_image)

                    } else {
                        /* Toast.makeText(
                             requireActivity(),
                             response.body()!!.response.message,
                             Toast.LENGTH_LONG
                         ).show()*/
                    }
                }

                override fun onFailure(call: Call<GetUserProfileVo>, t: Throwable) {
                    Toast.makeText(requireActivity(), "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}