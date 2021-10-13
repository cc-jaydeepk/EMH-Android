package com.everymomentholy.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.GetUserProfileUpdateRequestVo
import com.everymomentholy.api.response.GetUserProfileUpdateResponseVo
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileActivity : AppCompatActivity() {

    private lateinit var edtUpdateFirstName: EditText
    private lateinit var edtUpdateLastName: EditText
    private lateinit var edtUpdateEmail: EditText
    private lateinit var edtUpdatePhoneNuber: EditText
    private lateinit var btnUpdateProfile: Button
    private lateinit var ivOpenGallaery: ImageView
    private lateinit var profile_image: CircleImageView
    private lateinit var bitmap: Bitmap

    private lateinit var android_id: String
    var prefeUserId: Int = 0
    private val IMG_REQUEST = 21

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        edtUpdateFirstName = findViewById(R.id.edtUpdateFirstName)
        edtUpdateLastName = findViewById(R.id.edtUpdateLastName)
        edtUpdateEmail = findViewById(R.id.edtUpdateEmail)
        edtUpdatePhoneNuber = findViewById(R.id.edtUpdatePhoneNuber)
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile)
        ivOpenGallaery = findViewById(R.id.ivOpenGallaery)
        profile_image = findViewById(R.id.profile_image)

        ivOpenGallaery.setOnClickListener {
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(intent, IMG_REQUEST)

//            ImagePicker.with(this)
//                .crop()
//                .start()
        }

        android_id = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )

        prefeUserId = Utils.readIntData(
            this,
            Constants.PrefUserID,
            0
        )!!

        btnUpdateProfile.setOnClickListener {

            getUserProfileUpdate()

        }
    }


    private fun getUserProfileUpdate() {
        var getUserProfileUpdateRequestVo: GetUserProfileUpdateRequestVo =
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
                    this,
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

                        Toast.makeText(
                            this@EditProfileActivity,
                            "Profile update successfully",
                            Toast.LENGTH_LONG
                        ).show()

                    } else {
                        /* Toast.makeText(
                             this@EditProfileActivity,
                             response.body()!!.message,
                             Toast.LENGTH_LONG
                         ).show()*/
                    }
                }

                override fun onFailure(call: Call<GetUserProfileUpdateResponseVo>, t: Throwable) {
                    Toast.makeText(this@EditProfileActivity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            /*val path = data.data
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, path)
                profile_image.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }*/

            val uri = data.data
            profile_image.setImageURI(uri)
        }
    }

}