package com.everymomentholy.ui.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.loader.content.CursorLoader
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.GetUserProfileRequestVo
import com.everymomentholy.api.request.GetUserProfileUpdateRequestVo
import com.everymomentholy.api.response.GetUserProfileUpdateResponseVo
import com.everymomentholy.api.response.GetUserProfileVo
import com.everymomentholy.ui.activity.ChangePasswordActivity
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.github.drjacky.imagepicker.ImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MyProfileFragment : Fragment() {

    lateinit var ivChangePassword: ImageView
    private lateinit var edtUserFirstName: EditText
    private lateinit var edtUserLastName: EditText
    private lateinit var edtUserEmail: EditText
    private lateinit var edtUserPhoneNumber: EditText
    private lateinit var btnEditProfile: Button
    private lateinit var btnUpdateProfile: Button
    private lateinit var profile_image: ImageView
    private lateinit var ivOpenGallery: ImageView
    private lateinit var txtUseName: TextView
    private lateinit var freeText: TextView
    private lateinit var android_id: String

    private var profile_upload_ImageUri: Uri? = null
    private lateinit var file: File

    var isImageSelect = false

    // var profile_upload_ImageUri: Uri? = "null"
    var prefeUserId: Int = 0
    lateinit var userProfileMultipart: MultipartBody.Part
    lateinit var progressCardView: CardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myprofile, container, false)

        freeText = view.findViewById(R.id.textFree)
        txtUseName = view.findViewById(R.id.txtUseName)

        ivChangePassword = view.findViewById(R.id.ivChangePassword)

        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile)

        progressCardView = view.findViewById(R.id.progressCardView)

        edtUserFirstName = view.findViewById(R.id.edtUserFirstName)
        edtUserLastName = view.findViewById(R.id.edtUserLastName)
        edtUserEmail = view.findViewById(R.id.edtUserEmail)
        edtUserPhoneNumber = view.findViewById(R.id.edtUserPhoneNumber)
        profile_image = view.findViewById(R.id.profile_image)
        ivOpenGallery = view.findViewById(R.id.ivOpenGallery)



        ivOpenGallery.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .start()
        }


//        edtUserFirstName.setEnabled(false);
//        edtUserLastName.setEnabled(false);
//        edtUserEmail.setEnabled(false);
//        edtUserPhoneNumber.setEnabled(false);


        btnEditProfile.setOnClickListener {
            isImageSelect = true

            ivOpenGallery.visibility = View.VISIBLE
            btnUpdateProfile.visibility = View.VISIBLE
            btnEditProfile.visibility = View.GONE

            edtUserFirstName.requestFocus()
            edtUserFirstName.setEnabled(true)

            edtUserLastName.setEnabled(true)
            edtUserLastName.requestFocus()

            edtUserEmail.setEnabled(true)
            edtUserEmail.requestFocus()

            edtUserPhoneNumber.setEnabled(true)
            edtUserPhoneNumber.requestFocus()


            /*val updateProfileFragment = UpdateProfileFragment()
            val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
            transaction.replace(R.id.nav_host_fragment, updateProfileFragment)
                .addToBackStack(null)
            transaction.commit()*/
        }

        btnUpdateProfile.setOnClickListener {
            btnEditProfile.visibility = View.GONE

            if (checkValidation()) {

                if (Utils.isNetworkAvailable(requireActivity())) {
                    //  getUserProfileUpdate()

                    /*var getUserProfileUpdateRequestVo: GetUserProfileUpdateRequestVo =
                        GetUserProfileUpdateRequestVo()
                    getUserProfileUpdateRequestVo.firstName =
                        edtUserFirstName.text.toString().trim()
                    getUserProfileUpdateRequestVo.lastName = edtUserLastName.text.toString().trim()
                    updateProfile(getUserProfileUpdateRequestVo)*/

                    progressCardView.visibility = View.VISIBLE
                    getUserProfileUpdate()

                } else {
                    Toast.makeText(
                        requireActivity(),
                        resources.getString(R.string.check_internet),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }


        val bundle = this.arguments
        if (bundle != null) {
            val userName = bundle["name"].toString()

            //freeText.text = userName.toString()
            txtUseName.text = userName.toString()

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

        txtUseName.text = Utils.readStringFromSharedPref(
            requireActivity(), Constants.NAME,
            ""
        ).toString()

        getUserProfile()

        ivChangePassword.setOnClickListener {
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

                        /*Utils.writeStringToSharedPref(
                            requireActivity(), Constants.USER_NAME,
                            response.body()!!.response.firstName
                        )

                        Log.e("name", response.body()!!.response.firstName)

                        Utils.writeStringToSharedPref(
                            requireActivity(), Constants.USER_EMAIL,
                            response.body()!!.response.email
                        )*/

                        var firstname = response.body()!!.response.firstName
                        var lastname = response.body()!!.response.lastName
                        var email = response.body()!!.response.email
                        var phoneNo = response.body()!!.response.mobile
                        var progileImage = response.body()!!.response.userProfilePic
                        fun String.toEditable(): Editable =
                            Editable.Factory.getInstance().newEditable(this)
                        edtUserFirstName.text =
                            Editable.Factory.getInstance().newEditable(firstname)
                        edtUserLastName.text = Editable.Factory.getInstance().newEditable(lastname)
                        edtUserEmail.text = Editable.Factory.getInstance().newEditable(email)
                        edtUserPhoneNumber.text =
                            Editable.Factory.getInstance().newEditable(phoneNo)

                        //  freeText.text = response.body()!!.response.firstName
                        // txtUseName.text = response.body()!!.response.firstName

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

    private fun getUserProfileUpdate() {


        var getUserProfileUpdateRequestVo: GetUserProfileUpdateRequestVo =
            GetUserProfileUpdateRequestVo()
        getUserProfileUpdateRequestVo.deviceId = android_id
        getUserProfileUpdateRequestVo.userId = prefeUserId
        getUserProfileUpdateRequestVo.firstName = edtUserFirstName.text.toString().trim()
        getUserProfileUpdateRequestVo.lastName = edtUserLastName.text.toString().trim()
        getUserProfileUpdateRequestVo.email = edtUserEmail.text.toString().trim()
        getUserProfileUpdateRequestVo.mobileNo = edtUserPhoneNumber.text.toString().trim()
        getUserProfileUpdateRequestVo.countryCode = "+44"

        if (profile_upload_ImageUri != null) {

            val file: File = generateFile(profile_upload_ImageUri!!) as File
            //file = generateFile(profile_upload_ImageUri!!) as File

            val requestFile =
                RequestBody.create(
                    getMimeType(profile_upload_ImageUri!!)!!.toMediaTypeOrNull(), file
                )
            userProfileMultipart = MultipartBody.Part.createFormData(
                "userProfilePic",
                file!!.getName(),
                requestFile
            )

        } else {

            val drawable = profile_image.drawable
            val bitmap = drawable.toBitmap()
            file = generateFileFromBitmap(bitmap) as File

            Log.e("file", file.toString())

            val requestFile =
                RequestBody.create(
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)!!
                        .toMediaTypeOrNull(), file
                )
            userProfileMultipart = MultipartBody.Part.createFormData(
                "userProfilePic",
                file.getName(),
                requestFile
            )


        }


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
                ), userProfileMultipart
            )


        try {
            call.enqueue(object : Callback<GetUserProfileUpdateResponseVo> {
                override fun onResponse(
                    call: Call<GetUserProfileUpdateResponseVo>,
                    response: Response<GetUserProfileUpdateResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        Utils.writeStringToSharedPref(
                            requireActivity(), Constants.USER_NAME,
                            getUserProfileUpdateRequestVo.firstName
                        )


                        Utils.writeStringToSharedPref(
                            requireActivity(), Constants.USER_EMAIL,
                            getUserProfileUpdateRequestVo.email
                        )

                        if (profile_upload_ImageUri != null) {
                            Utils.writeStringToSharedPref(
                                requireActivity(), Constants.PROFILE_PIC,
                                profile_upload_ImageUri.toString()
                            )
                        } else {
                            Utils.writeStringToSharedPref(
                                requireActivity(), Constants.DEFAULT_PROFILE_PIC,
                                file.toString()
                            )
                        }

                        val prefs =
                            PreferenceManager.getDefaultSharedPreferences(requireActivity())
                        val statusLocked = prefs.edit().putBoolean("myProfile", true).apply()

                        /* val preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity())
                         val editor = preferences.edit()
                         editor.putString("image", java.lang.String.valueOf(profile_upload_ImageUri))
                         editor.commit()*/

                        //  Log.e("image", profile_upload_ImageUri.toString())

                        val bundle = Bundle()
                        bundle.putString("name", getUserProfileUpdateRequestVo.firstName)
                        //bundle.putParcelable("BitmapImage", bitmapImage);


                        btnEditProfile.visibility = View.VISIBLE
                        // progressCardView.visibility = View.GONE
                        // ivOpenGallery.visibility = View.GONE
                        // btnUpdateProfile.visibility = View.GONE

                        edtUserFirstName.setEnabled(false)

                        edtUserLastName.setEnabled(false)

                        edtUserEmail.setEnabled(false)

                        edtUserPhoneNumber.setEnabled(false)

                        txtUseName.text =
                            getUserProfileUpdateRequestVo.firstName + " " + getUserProfileUpdateRequestVo.lastName



                        showAlert()


                        /*val myProfileFragment = MyProfileFragment()
                        val transaction: FragmentTransaction = fragmentManager!!.beginTransaction()
                        transaction.replace(R.id.nav_host_fragment, myProfileFragment)
                            .addToBackStack(null)
                        transaction.commit()

                        myProfileFragment.setArguments(bundle)*/

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
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (isImageSelect) {
            profile_upload_ImageUri = data!!.data!!
            var bitmapImage = MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver,
                profile_upload_ImageUri
            )
            //profile_image.setImageBitmap(bitmapImage)

            profile_image.setImageURI(profile_upload_ImageUri)
        } else {
            Toast.makeText(
                requireActivity(),
                "Image not selected",
                Toast.LENGTH_LONG
            ).show()
        }


        //uploadFile(uri, "My Image");


        //creating a file


    }

    fun showAlert() {
        val alertDialog = AlertDialog.Builder(
            requireActivity()
        )
        val inflater = (context as Activity).layoutInflater
        val alertView: View = inflater.inflate(R.layout.register_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val alertButton = alertView.findViewById<View>(R.id.txtOk) as TextView
        val txtDialogSucces = alertView.findViewById<View>(R.id.txtDialogSucces) as TextView
        txtDialogSucces.text = "User profile updated successfully"
        progressCardView.visibility = View.GONE
        ivOpenGallery.visibility = View.GONE
        btnUpdateProfile.visibility = View.GONE
        alertButton.setOnClickListener {
            show.dismiss()
        }
        show.setCanceledOnTouchOutside(false);

    }


    private fun getRealPathFromURI(contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(requireActivity(), contentUri, proj, null, null, null)
        val cursor: Cursor = loader.loadInBackground()!!
        val column_index: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        val result: String = cursor.getString(column_index)
        cursor.close()
        return result
    }

    private fun checkValidation(): Boolean {

        val email = edtUserEmail.text.toString().trim()
        val firstName = edtUserFirstName.text.toString().trim()
        val lastName = edtUserLastName.text.toString().trim()
        var isValid = true

        if (email.isEmpty()) {
            edtUserEmail.error = resources.getString(R.string.email_error)
            edtUserEmail.requestFocus()
            isValid = false
        }

        if (firstName.isEmpty()) {
            edtUserFirstName.error = resources.getString(R.string.email_error)
            edtUserFirstName.requestFocus()
            isValid = false
        }

        if (lastName.isEmpty()) {
            edtUserLastName.error = resources.getString(R.string.email_error)
            edtUserLastName.requestFocus()
            isValid = false
        }

        return isValid
    }

    private fun generateFile(uri: Uri): File? {
        var file: File? = null
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(
                requireActivity().getContentResolver(),
                uri
            )

            try {
                file = File.createTempFile(
                    System.currentTimeMillis().toString() + "",
                    ".jpg"
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            var out: FileOutputStream? = null
            out = FileOutputStream(file, false)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out)
            out.flush()

            return file
        } catch (ex: RuntimeException) {
            // Assume this is a corrupt video file.
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }


    private fun generateFileFromBitmap(bitmap: Bitmap): File? {
        var file: File? = null

        try {

            try {
                file = File.createTempFile(
                    System.currentTimeMillis().toString() + "",
                    ".jpg"
                )
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            var out: FileOutputStream? = null
            out = FileOutputStream(file, false)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out)
            out.flush()

            return file
        } catch (ex: RuntimeException) {
            // Assume this is a corrupt video file.
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    fun getMimeType(uri: Uri): String? {
        var mimeType: String? = null
        mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr: ContentResolver = requireActivity().getContentResolver()
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                uri
                    .toString()
            )
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                fileExtension.toLowerCase()
            )
        }
        return mimeType
    }
}