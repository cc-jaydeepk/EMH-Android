package com.everymomentholy.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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

class EditProfileActivity : AppCompatActivity() {

    private lateinit var edtUpdateFirstName: EditText
    private lateinit var edtUpdateLastName: EditText
    private lateinit var edtUpdateEmail: EditText
    private lateinit var edtUpdatePhoneNuber: EditText
    private lateinit var btnUpdateProfile: Button
    private lateinit var ivOpenGallaery: ImageView
    private lateinit var profile_image: CircleImageView
    lateinit var progressCardView: CardView

    private lateinit var android_id: String
    var prefeUserId: Int = 0
    private val IMG_REQUEST = 21

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
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
        progressCardView = findViewById(R.id.progressCardView)

        ivOpenGallaery.setOnClickListener {
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(intent, IMG_REQUEST)

            ImagePicker.with(this)
                .crop()
                .start()
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

            progressCardView.visibility = View.VISIBLE
           // getUserProfileUpdate()

        }
    }


    /*private fun getUserProfileUpdate() {
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

                        progressCardView.visibility = View.GONE
                        showSuccesDialog()

                        *//*Toast.makeText(
                            this@EditProfileActivity,
                            "Profile update successfully",
                            Toast.LENGTH_LONG
                        ).show()*//*

                    } else {
                        Toast.makeText(
                            this@EditProfileActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
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
    }*/

    private fun showSuccesDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.register_dialog, null)
        // val dialogLayout = inflater.inflate(R.layout.login_dialog, null)
        val txtOk = dialogLayout.findViewById<TextView>(R.id.txtOk)
        val txtDialogSucces = dialogLayout.findViewById<TextView>(R.id.txtDialogSucces)
        txtDialogSucces.text = "User profile updated successfully"
        txtOk.setOnClickListener {
            onBackPressed()
        }

        builder.setView(dialogLayout)
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val uri = data!!.data
        profile_image.setImageURI(uri)
    }

}