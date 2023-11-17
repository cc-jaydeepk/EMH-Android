package com.everymomentholy.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
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

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val uri = data!!.data
        profile_image.setImageURI(uri)
    }

}