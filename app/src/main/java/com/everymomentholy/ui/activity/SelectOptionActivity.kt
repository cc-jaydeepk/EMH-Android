package com.everymomentholy.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.work.*
import com.everymomentholy.R
import com.everymomentholy.services.MyLiturgiesDataWorker
import com.everymomentholy.ui.fragments.*
import com.everymomentholy.utils.Constants
import com.folioreader.emh.EMHUtils.Companion.showLoginDialog
import java.util.concurrent.TimeUnit

class SelectOptionActivity : AppCompatActivity() {

    lateinit var btnRegister: Button
    lateinit var btnLogin: Button
    lateinit var txtSkip: TextView
    lateinit var mainFramelayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activty_select_option)

        supportActionBar?.hide()

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



}