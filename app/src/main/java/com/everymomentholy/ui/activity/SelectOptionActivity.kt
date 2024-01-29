package com.everymomentholy.ui.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.work.*
import com.everymomentholy.R
import com.everymomentholy.api.request.LoginRequestVo
import com.everymomentholy.ui.fragments.*
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability


class SelectOptionActivity : AppCompatActivity() {

    lateinit var btnRegister: Button
    lateinit var btnLogin: Button
    lateinit var txtSkip: TextView
    lateinit var mainFramelayout: FrameLayout
    private var appUpdateManager: AppUpdateManager? = null


    private val MY_REQUEST_CODE = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_select_option)

        supportActionBar?.hide()

        appUpdateManager =
            AppUpdateManagerFactory.create(this);// Returns an intent object that you use to check for an update.

        checkForUpdates()

        //checkUpdate()

        mainFramelayout = findViewById(R.id.mainFramelayout)

        btnRegister = findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val intent = Intent(this@SelectOptionActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnLogin = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {
            Constants.USER_LOGIN_STATUS = Constants.LOGIN
            val intent = Intent(this@SelectOptionActivity, LoginActivity::class.java)
            startActivity(intent)

            /*val intent = Intent(this@SelectOptionActivity, SelectSubscriptionPlan::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()*/
        }

        txtSkip = findViewById(R.id.txtSkip)
        txtSkip.setOnClickListener {

            Constants.USER_LOGIN_STATUS = Constants.SKIP_LOGIN
            val bundle = Bundle()
            bundle.putBoolean("onPress", false);
            /*replaceFragment(
                SubscriptionPlanListFragment(),
                "Subscription",
                bundle
            )*/
            //replaceFragment(SubscriptionPlanListFragment(), "Subscription")


            val intent = Intent(this@SelectOptionActivity, SelectSubscriptionPlan::class.java)
//            intent.flags =
//                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFramelayout, fragment)
        transaction.commit()
    }

    fun replaceFragment(fragment: Fragment, txtToolbarTitle: String, arguments: Bundle? = null) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        transaction.replace(R.id.mainFramelayout, fragment)
        transaction.disallowAddToBackStack()
        if (arguments != null) {
            fragment.arguments = arguments
        }
        transaction.commit()
    }

    fun checkForUpdates() {
        appUpdateManager?.getAppUpdateInfo()?.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() === UpdateAvailability.UPDATE_AVAILABLE) // This example applies an immediate update. To apply a flexible update
            {
                // isNewVersion = true
                showPop()
            } else {
                /*Toast.makeText(
                    this@SelectOptionActivity,
                    "Finish",
                    Toast.LENGTH_LONG
                ).show()*/
                // isNewVersion = false
            }
        }
    }

    private fun showPop() {

        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.register_dialog, null)
        // val dialogLayout = inflater.inflate(R.layout.login_dialog, null)
        val txtDialogSucces = dialogLayout.findViewById<TextView>(R.id.txtDialogSucces)
        val txtTitle = dialogLayout.findViewById<TextView>(R.id.txtTitle)
        txtTitle.text = "Update Alert"
        val txtOk = dialogLayout.findViewById<TextView>(R.id.txtOk)
        txtDialogSucces.text = "A new version of the app is available. Please update to continue."
        txtOk.setOnClickListener {
            if (Utils.isNetworkAvailable(this)) {
                playStore()
            } else {
                Toast.makeText(
                    this@SelectOptionActivity,
                    resources.getString(R.string.check_internet),
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        builder.setCancelable(false)
        builder.setView(dialogLayout)
        builder.show()

    }

    fun playStore() {
        val appPackageName = packageName
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (anfe: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }


}