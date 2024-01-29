package com.everymomentholy.ui.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.GetUserProfileRequestVo
import com.everymomentholy.api.request.GetUserProfileUpdateRequestVo
import com.everymomentholy.api.request.NotificationAlertRequestVo
import com.everymomentholy.api.response.BaseResponseVo
import com.everymomentholy.api.response.GetUserProfileUpdateResponseVo
import com.everymomentholy.api.response.GetUserProfileVo
import com.everymomentholy.api.response.NotificationAlertResponseVo
import com.everymomentholy.ui.activity.*
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
    private lateinit var countryCodePicker: com.hbb20.CountryCodePicker
    private var selectedCode: String = ""
    private lateinit var shadowView: TextView
    private lateinit var ivOrderHistory: ImageView
    private lateinit var btnDeleteAccount: Button

    private var profile_upload_ImageUri: Uri? = null
    private lateinit var file: File

    var subscriptionStart = ""
    var subscriptionEnd = ""
    var subscriptionStatus = ""
    var subscriptionType = ""
    var subscriptionID = ""

    var isImageSelect = false

    // var profile_upload_ImageUri: Uri? = "null"
    var prefeUserId: Int = 0
    lateinit var userProfileMultipart: MultipartBody.Part
    //visible
    lateinit var progressCardView: CardView

    lateinit var notification_switch: SwitchCompat

    var isCheck: Boolean = true
    private var status: String = "On"
    private lateinit var notificationStatus: String

    lateinit var txtMySubscription: TextView

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myprofile, container, false)

        freeText = view.findViewById(R.id.textFree)
        txtUseName = view.findViewById(R.id.txtUseName)
        (activity as MainActivity).iv_toolbar_search.visibility = View.GONE

        ivChangePassword = view.findViewById(R.id.ivChangePassword)

        txtMySubscription = view.findViewById(R.id.txtMySubscription)

        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile)

        //visible
        progressCardView = view.findViewById(R.id.progressCardView)

        edtUserFirstName = view.findViewById(R.id.edtUserFirstName)
        edtUserLastName = view.findViewById(R.id.edtUserLastName)
        edtUserEmail = view.findViewById(R.id.edtUserEmail)
        edtUserPhoneNumber = view.findViewById(R.id.edtUserPhoneNumber)
        profile_image = view.findViewById(R.id.profile_image)
        ivOpenGallery = view.findViewById(R.id.ivOpenGallery)
        countryCodePicker = view.findViewById(R.id.country_code_picker)
        shadowView = view.findViewById(R.id.shadowView)
        ivOrderHistory = view.findViewById(R.id.ivOrderHistory)
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount)


        notification_switch = view.findViewById(R.id.notification_switch)

        /*val sharedPreferences: SharedPreferences = requireActivity().getSharedPreferences(
            "save",
            MODE_PRIVATE
        )
        notification_switch.setChecked(sharedPreferences.getBoolean("value", true))*/

        val sharedPreferences =
            requireActivity().getSharedPreferences("savestate", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        //notification_switch.isChecked = sharedPreferences.getBoolean("switch", true)
        notification_switch.setChecked(sharedPreferences.getBoolean("switch", true))

        notification_switch.setOnCheckedChangeListener { buttonView, isChecked ->

            //working code for save state
            if (isChecked) {

                /*val myBoolean = isChecked
                val result: String = BooleanUtils.toStringYesNo(myBoolean)
                Log.e("ischeck", "onCreateView: " + result)*/

                isCheck = isChecked

                status = isCheck.toString()
                status = "On"

                Log.e("ischeck", "onCreateView: " + status)

                editor.putBoolean("switch", true);
                editor.apply();
                notification_switch.setChecked(true)

                notificationAlert()

            } else {
                isCheck = isChecked

                status = isCheck.toString()
                status = "Off"

                Log.e("ischeck", "onCreateView: " + status)
                editor.putBoolean("switch", false);
                editor.apply();
                notification_switch.setChecked(false)

                notificationAlert()
            }

        }

        ivOpenGallery.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .start()
        }

        countryCodePicker.setOnCountryChangeListener() {
            selectedCode = countryCodePicker.selectedCountryCode
        }

        //ccp!!.setDefaultCountryUsingNameCode("IN")
        selectedCode = countryCodePicker!!.selectedCountryCode


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

            countryCodePicker.isClickable = true
            countryCodePicker.isFocusableInTouchMode = true
            shadowView.visibility = View.GONE
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

                    //visible
                    progressCardView.visibility = View.VISIBLE
                    requireActivity().getWindow().setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )

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

        btnDeleteAccount.setOnClickListener {
            promptUserConfirmation()
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
            requireActivity(), Constants.USER_NAME,
            ""
        ).toString()

        //visible
        progressCardView.visibility = View.VISIBLE
        requireActivity().getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        getUserProfile()

        ivChangePassword.setOnClickListener()
        {
            val intent = Intent(activity, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        ivOrderHistory.setOnClickListener()
        {
            val intent = Intent(context, OrderHistoryActivity::class.java)
            startActivity(intent)
        }

        txtMySubscription.setOnClickListener {
            val intent = Intent(activity, MySubscriptionStatus::class.java)
            intent.putExtra("Start", subscriptionStart);
            intent.putExtra("End", subscriptionEnd);
            intent.putExtra("Status", subscriptionStatus);
            intent.putExtra("Type", subscriptionType);
            intent.putExtra("SubscriptionId", subscriptionID);
            startActivity(intent)
        }

        return view
    }


    private fun promptUserConfirmation() {
        val alertDialog = AlertDialog.Builder(requireContext())
        val inflater = activity?.layoutInflater
        val alertView: View = inflater?.inflate(R.layout.dialog_account_delete, null)!!
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val alertButtonCancel = alertView.findViewById<View>(R.id.txtDeleteCancel) as TextView
        val alertButtonYes = alertView.findViewById<View>(R.id.txtDeleteYes) as TextView

        alertButtonYes.setOnClickListener {
            if (Utils.isNetworkAvailable(requireActivity())) {
                //visible
                progressCardView.visibility = View.VISIBLE
                requireActivity().getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                deleteUserAccount()
            } else {
                Toast.makeText(
                    requireActivity(),
                    requireContext().resources.getString(R.string.check_internet),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        alertButtonCancel.setOnClickListener {
            show.dismiss()
        }
        show.setCanceledOnTouchOutside(false)

    }

    private fun deleteUserAccount() {
        val request = APIService.buildService(APIInterface::class.java)
        val call = request.deleteUserAccount(
            Utils.readIntFromSharedPref(
                requireContext(),
                Constants.PrefUserID,
                -1
            ), "bearer " + Utils.readStringFromSharedPref(
                requireContext(),
                Constants.SHARED_PREF_TOKEN,
                ""
            )
        )

        try {
            call.enqueue(object : Callback<BaseResponseVo> {
                override fun onResponse(
                    call: Call<BaseResponseVo>,
                    response: Response<BaseResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        Toast.makeText(
                            requireContext(),
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        Utils.writeUserIdBooleanFromSharedPref(requireContext(), false);
                        Utils.clearAllPreference(requireContext())
                        val intent = Intent(requireContext(), SelectOptionActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        activity?.finish()
                    } else {
                        Toast.makeText(
                            requireActivity(),
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<BaseResponseVo>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
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
                        //visible
                        progressCardView.visibility = View.GONE
                        requireActivity().getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
                        //  var upCominStartDate = response.body()!!.response.userSubscriptionData.started_at

                        Utils.writeStringToSharedPref(
                            requireActivity(), Constants.UpcomingPlan,
                            response.body()!!.response.userSubscriptionData.upcomingPlan
                        )


                        Utils.writeStringToSharedPref(
                            requireActivity(), Constants.UPCOMING_STARTED_AT,
                            response.body()!!.response.userSubscriptionData.upcomingPlanData.started_at
                        )

                        Utils.writeStringToSharedPref(
                            requireActivity(), Constants.UPCOMING_EXPIRED_AT,
                            response.body()!!.response.userSubscriptionData.upcomingPlanData.expire_at
                        )

                        Utils.writeStringToSharedPref(
                            requireActivity(), Constants.UPCOMING_SUB_TYPE,
                            response.body()!!.response.userSubscriptionData.upcomingPlanData.subscription_type
                        )

                        Utils.writeStringToSharedPref(
                            requireActivity(), Constants.UPCOMING_SUB_STATUS,
                            response.body()!!.response.userSubscriptionData.upcomingPlanData.subscription_status
                        )

                        subscriptionStart =
                            response.body()!!.response.userSubscriptionData.started_at
                        subscriptionEnd =
                            response.body()!!.response.userSubscriptionData.expire_at
                        subscriptionStatus =
                            response.body()!!.response.userSubscriptionData.subscription_status
                        subscriptionType =
                            response.body()!!.response.userSubscriptionData.subscription_type
                        subscriptionID =
                            response.body()!!.response.userSubscriptionData.subscription_id


                        fun String.toEditable(): Editable =
                            Editable.Factory.getInstance().newEditable(this)
                        edtUserFirstName.text =
                            Editable.Factory.getInstance().newEditable(firstname)
                        edtUserLastName.text = Editable.Factory.getInstance().newEditable(lastname)
                        edtUserEmail.text = Editable.Factory.getInstance().newEditable(email)
                        edtUserPhoneNumber.text =
                            Editable.Factory.getInstance().newEditable(phoneNo)
                        countryCodePicker.setCountryForPhoneCode(response.body()!!.response.countryCode.toInt())
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

    private fun notificationAlert() {

        /*var notificationAlertStatus = Utils.readStringFromSharedPref(
            requireActivity(), Constants.IS_NOTIFICATION_ON,
            status
        ).toString()

        Log.e("TAG", "notificationAlert: " + notificationAlertStatus)*/

        var notificationAlertRequestVo: NotificationAlertRequestVo = NotificationAlertRequestVo()
        notificationAlertRequestVo.userId = prefeUserId
        notificationAlertRequestVo.deviceId = android_id
        notificationAlertRequestVo.notification_status = status
        notificationAlertRequestVo.firstName = edtUserFirstName.text.toString().trim()
        notificationAlertRequestVo.lastName = edtUserLastName.text.toString().trim()
        notificationAlertRequestVo.email = edtUserEmail.text.toString().trim()
        notificationAlertRequestVo.mobileNo = edtUserPhoneNumber.text.toString().trim()
        notificationAlertRequestVo.countryCode = selectedCode.toString()
        "bearer " + Utils.readStringFromSharedPref(
            requireActivity(),
            Constants.SHARED_PREF_TOKEN,
            ""
        )

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.notificationAlert(
                notificationAlertRequestVo.userId,
                notificationAlertRequestVo.deviceId,
                notificationAlertRequestVo.notification_status,
                notificationAlertRequestVo.firstName,
                notificationAlertRequestVo.lastName,
                notificationAlertRequestVo.email,
                notificationAlertRequestVo.countryCode,
                notificationAlertRequestVo.mobileNo,
                "bearer " + Utils.readStringFromSharedPref(
                    requireActivity(),
                    Constants.SHARED_PREF_TOKEN,
                    ""
                )
            )


        try {
            call.enqueue(object : Callback<NotificationAlertResponseVo> {
                @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
                override fun onResponse(
                    call: Call<NotificationAlertResponseVo>,
                    response: Response<NotificationAlertResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        /* Toast.makeText(
                             requireActivity(),
                             response.body()!!.message,
                             Toast.LENGTH_LONG
                         ).show()*/

                    } else {
                        /* Log.e("Log", "onResponse: " + response.body()!!.message)
                         Toast.makeText(
                             requireActivity(),
                             response.body()!!.message,
                             Toast.LENGTH_LONG
                         ).show()*/

                    }
                }

                override fun onFailure(call: Call<NotificationAlertResponseVo>, t: Throwable) {
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
        getUserProfileUpdateRequestVo.notification_status = status
        getUserProfileUpdateRequestVo.firstName = edtUserFirstName.text.toString().trim()
        getUserProfileUpdateRequestVo.lastName = edtUserLastName.text.toString().trim()
        getUserProfileUpdateRequestVo.email = edtUserEmail.text.toString().trim()
        getUserProfileUpdateRequestVo.mobileNo = edtUserPhoneNumber.text.toString().trim()
        getUserProfileUpdateRequestVo.countryCode = selectedCode.toString()

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
                getUserProfileUpdateRequestVo.notification_status,
                getUserProfileUpdateRequestVo.firstName,
                getUserProfileUpdateRequestVo.lastName,
                getUserProfileUpdateRequestVo.email,
                getUserProfileUpdateRequestVo.countryCode,
                getUserProfileUpdateRequestVo.mobileNo,
                "bearer " + Utils.readStringFromSharedPref(
                    requireActivity(),
                    Constants.SHARED_PREF_TOKEN,
                    ""
                ), userProfileMultipart
            )


        try {
            call.enqueue(object : Callback<GetUserProfileUpdateResponseVo> {
                @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
                override fun onResponse(
                    call: Call<GetUserProfileUpdateResponseVo>,
                    response: Response<GetUserProfileUpdateResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        Utils.writeStringToSharedPref(
                            requireActivity(), Constants.USER_NAME,
                            getUserProfileUpdateRequestVo.firstName + " " + getUserProfileUpdateRequestVo.lastName
                        )

                        txtUseName.text = Utils.readStringFromSharedPref(
                            requireContext(), Constants.USER_NAME,
                            ""
                        ).toString()
                        /* Utils.writeStringToSharedPref(
                             requireActivity(), Constants.NAME,
                             getUserProfileUpdateRequestVo.firstName + getUserProfileUpdateRequestVo.lastName
                         )*/

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

                        val bundle = Bundle()
                        bundle.putString("name", getUserProfileUpdateRequestVo.firstName)

                        btnEditProfile.visibility = View.VISIBLE
                        edtUserFirstName.isEnabled = false
                        edtUserLastName.isEnabled = false
                        edtUserEmail.isEnabled = false
                        edtUserPhoneNumber.isEnabled = false
                        txtUseName.text =
                            getUserProfileUpdateRequestVo.firstName + " " + getUserProfileUpdateRequestVo.lastName

                        shadowView.visibility = View.VISIBLE

                        showAlert()

                    } else {
                        //visible
                        progressCardView.visibility = View.GONE
                        requireActivity().getWindow()
                            .clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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

        if (data!!.data != null) {
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
        //visible
        progressCardView.visibility = View.GONE
        requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        ivOpenGallery.visibility = View.GONE
        btnUpdateProfile.visibility = View.GONE
        alertButton.setOnClickListener {
            show.dismiss()
        }
        show.setCanceledOnTouchOutside(false);

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
            edtUserFirstName.error = resources.getString(R.string.firstname_error)
            edtUserFirstName.requestFocus()
            isValid = false
        }

        if (lastName.isEmpty()) {
            edtUserLastName.error = resources.getString(R.string.lastname_error)
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