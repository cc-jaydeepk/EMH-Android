package com.everymomentholy.ui.fragments

import android.R.attr.bitmap
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.GetUserProfileUpdateRequestVo
import com.everymomentholy.api.response.GetUserProfileUpdateResponseVo
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.github.drjacky.imagepicker.ImagePicker
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UpdateProfileFragment : Fragment() {

    private lateinit var edtUpdateFirstName: EditText
    private lateinit var edtUpdateLastName: EditText
    private lateinit var edtUpdateEmail: EditText
    private lateinit var edtUpdatePhoneNuber: EditText
    private lateinit var btnUpdateProfile: Button
    private lateinit var ivOpenGallaery: ImageView
    private lateinit var profile_image: CircleImageView
    lateinit var progressCardView: CardView

//    lateinit var bitmapImage: Bitmap

    private lateinit var android_id: String
    var prefeUserId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_profile, container, false)

        edtUpdateFirstName = view.findViewById(R.id.edtUpdateFirstName)
        edtUpdateLastName = view.findViewById(R.id.edtUpdateLastName)
        edtUpdateEmail = view.findViewById(R.id.edtUpdateEmail)
        edtUpdatePhoneNuber = view.findViewById(R.id.edtUpdatePhoneNuber)
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile)
        ivOpenGallaery = view.findViewById(R.id.ivOpenGallaery)
        profile_image = view.findViewById(R.id.profile_image)
        progressCardView = view.findViewById(R.id.progressCardView)

        ivOpenGallaery.setOnClickListener {

            ImagePicker.with(this)
                .crop()
                .start()
        }

        android_id = Settings.Secure.getString(
            requireActivity().contentResolver,
            Settings.Secure.ANDROID_ID
        )

        prefeUserId = Utils.readIntData(
            requireContext(),
            Constants.PrefUserID,
            0
        )!!

        btnUpdateProfile.setOnClickListener {

            progressCardView.visibility = View.VISIBLE
            getUserProfileUpdate()

        }

        return view
    }

    private fun getUserProfileUpdate() {
        /*var getUserProfileUpdateRequestVo: GetUserProfileUpdateRequestVo =
            GetUserProfileUpdateRequestVo()
        getUserProfileUpdateRequestVo.deviceId = android_id
        getUserProfileUpdateRequestVo.userId = prefeUserId
        getUserProfileUpdateRequestVo.firstName = edtUpdateFirstName.text.toString().trim()
        getUserProfileUpdateRequestVo.lastName = edtUpdateLastName.text.toString().trim()
        getUserProfileUpdateRequestVo.email = edtUpdateEmail.text.toString().trim()
        getUserProfileUpdateRequestVo.countryCode = "+44"

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.getUserProfileUpdate(
                getUserProfileUpdateRequestVo.userId,
                getUserProfileUpdateRequestVo.deviceId,
                getUserProfileUpdateRequestVo.firstName,
                getUserProfileUpdateRequestVo.lastName,
                getUserProfileUpdateRequestVo.email,
                getUserProfileUpdateRequestVo.countryCode,
                "bearer " + Utils.readStringFromSharedPref(
                    requireActivity(),
                    Constants.SHARED_PREF_TOKEN,
                    ""
                )
            )


        try {
            call.enqueue(object : Callback<GetUserProfileUpdateResponseVo> {
                override fun onResponse(
                    call: Call<GetUserProfileUpdateResponseVo>,
                    response: Response<GetUserProfileUpdateResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        progressCardView.visibility = View.GONE

                        val bundle = Bundle()
                        bundle.putString("name", getUserProfileUpdateRequestVo.firstName)
                        //bundle.putParcelable("BitmapImage", bitmapImage);


                        val myProfileFragment = MyProfileFragment()
                        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
                        transaction.replace(R.id.nav_host_fragment, myProfileFragment)
                            .addToBackStack(null)
                        transaction.commit()

                        myProfileFragment.setArguments(bundle)

                        // showSuccesDialog()

                    } else {
                        Toast.makeText(
                            requireActivity(),
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GetUserProfileUpdateResponseVo>, t: Throwable) {
                    Toast.makeText(requireActivity(), "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }*/
    }

    private fun showSuccesDialog() {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.register_dialog, null)
        // val dialogLayout = inflater.inflate(R.layout.login_dialog, null)
        val txtOk = dialogLayout.findViewById<TextView>(R.id.txtOk)
        val txtDialogSucces = dialogLayout.findViewById<TextView>(R.id.txtDialogSucces)
        txtDialogSucces.text = "User profile updated successfully"
        txtOk.setOnClickListener {
            // onBackPressed()

        }

        builder.setView(dialogLayout)
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val uri = data!!.data
        var bitmapImage = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        profile_image.setImageBitmap(bitmapImage)

        //profile_image.setImageURI(uri)
    }
}