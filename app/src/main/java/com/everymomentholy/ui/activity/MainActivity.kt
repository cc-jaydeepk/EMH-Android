package com.everymomentholy.ui.activity

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.everymomentholy.R
import com.everymomentholy.api.APIInterface
import com.everymomentholy.api.APIService
import com.everymomentholy.api.request.GetUserProfileRequestVo
import com.everymomentholy.api.request.LogoutRequestVo
import com.everymomentholy.api.request.PurchaseRequestVo
import com.everymomentholy.api.response.GetUserProfileVo
import com.everymomentholy.api.response.LogoutResponseVo
import com.everymomentholy.api.response.PrivateShareResponseVo
import com.everymomentholy.interfaces.OnInAppPurchaseListener
import com.everymomentholy.ui.fragments.*
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.ProductTypes
import com.everymomentholy.utils.Utils
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), OnInAppPurchaseListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    lateinit var iv_toolbar_drawer: ImageView
    lateinit var iv_toolbar_notification: ImageView
    lateinit var iv_toolbar_search: ImageView
    lateinit var iv_toolbar_backImage: ImageView
    lateinit var txt_toolbar_name: TextView
    public lateinit var toolbar: Toolbar
    private lateinit var iv_drawer_profile_image: ImageView
    private lateinit var txt_drawer_UserName: TextView
    private lateinit var txt_drawer_email: TextView

    private var currentFragment: String = ""
    private lateinit var android_id: String
    var prefeUserId: Int = 0
    var profileImage: String = ""
    var userImage: String = ""
    var statusHistory: String = ""
    private val CONTACTS_PERMISSION_REQUEST_CODE = 101
    private val NOTIFICATIONS_PERMISSION_REQUEST_CODE = 102
    lateinit var ivToolbarDrawer: ImageView
    var subscriptionStatus: String = ""
    var isLogin: Boolean = false

    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        android_id = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )


// Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission has already been granted
            // You can now access contacts
            // AccessContacts()
            getAccountDetails(this)
        }

        //Check for the notification permission
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Request the permission


            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATIONS_PERMISSION_REQUEST_CODE
            )

            *//*if(shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS))
            {
                // Show explanation before requesting
                AlertDialog.Builder(this)
                    .setTitle("Notification Permission Needed")
                    .setMessage("This app needs permission to send you important notifications.")
                    .setPositiveButton("Allow") { _, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            NOTIFICATIONS_PERMISSION_REQUEST_CODE
                        )
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            else{
                // Directly request permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATIONS_PERMISSION_REQUEST_CODE
                )
            }*//*

        }*/

        prefeUserId = Utils.readIntData(
            this,
            Constants.PrefUserID,
            0
        )!!


        toolbar = findViewById(R.id.toolbar)
        iv_toolbar_drawer = findViewById(R.id.iv_toolbar_drawer)
        iv_toolbar_backImage = findViewById(R.id.iv_toolbar_backImage)
        iv_toolbar_notification = findViewById(R.id.iv_toolbar_notification)
        iv_toolbar_search = findViewById(R.id.iv_toolbar_search)

        txt_toolbar_name = findViewById(R.id.txt_toolbar_name)
        ivToolbarDrawer = findViewById(R.id.iv_toolbar_drawer)

        val mNavigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = mNavigationView.getHeaderView(0)

        txt_drawer_UserName = headerView.findViewById<TextView>(R.id.txt_drawer_UserName)
        txt_drawer_email = headerView.findViewById<TextView>(R.id.txt_drawer_email)
        iv_drawer_profile_image = headerView.findViewById(R.id.iv_drawer_profile_image)


        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val navBottomView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navBottomView.visibility = View.VISIBLE

        toolbar.visibility = View.VISIBLE
        var fragment1: Fragment? = null
        fragment1 = HomeFragment()
        navView.setCheckedItem(R.id.nav_homeFragment)
        addFragment(fragment1, "Every Moment Holy", null)


        if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
            isLogin = true
            txt_drawer_email.text = ""
            val nav_Menu: Menu = navView.getMenu()
            nav_Menu.findItem(R.id.nav_logoutFragment).setTitle("Login")
        } else {
            if (Utils.isNetworkAvailable(this)) {
                getUserProfile()
            }
        }
        val nav_purchas: Menu = navView.getMenu()
        if (isLogin){
            nav_purchas.findItem(R.id.purchase).setVisible(false)
        }else{
            nav_purchas.findItem(R.id.purchase).setVisible(true)
        }

        //true = visible, false = gone


        var planStatusasd = Utils.readStringFromSharedPref(
            this@MainActivity, Constants.PLAN_STATUS,
            ""
        ).toString()
        Log.e("PLANPLANPLAN", "subscriptionStatusfromProfile: " + planStatusasd)

        iv_toolbar_notification.setOnClickListener {
            val intent = Intent(this@MainActivity, NotificationListActivity::class.java)
            startActivity(intent)
        }

        iv_toolbar_backImage.setOnClickListener {
            onBackPressed()
        }

        iv_toolbar_drawer.setOnClickListener {
            drawerLayout.openDrawer(navView)
        }

        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            /** Called when a drawer has settled in a completely closed state.  */
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                invalidateOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state.  */
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)

                if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                    //todo
                } else {
                    profileImage = Utils.readStringFromSharedPref(
                        this@MainActivity, Constants.PROFILE_PIC,
                        ""
                    ).toString()



                    Glide.with(this@MainActivity)
                        .load(profileImage)
                        .into(iv_drawer_profile_image)

                    txt_drawer_UserName.text = Utils.readStringFromSharedPref(
                        this@MainActivity, Constants.USER_NAME,
                        ""
                    ).toString()

                    /*val subStatus = Utils.readStringFromSharedPref(
                        this@MainActivity, Constants.PROFILE_STATUS,
                        ""
                    ).toString()

                    val nav_purchas: Menu = navView.getMenu()
                    if (subStatus.equals("Yes", true)){
                        nav_purchas.findItem(R.id.purchase).setVisible(false)
                    }else{
                        nav_purchas.findItem(R.id.purchase).setVisible(true)
                    }*/

                    Log.e(
                        "user name", Utils.readStringFromSharedPref(
                            this@MainActivity, Constants.USER_NAME,
                            ""
                        ).toString()
                    )

                    txt_drawer_email.text = Utils.readStringFromSharedPref(
                        this@MainActivity, Constants.USER_EMAIL,
                        ""
                    ).toString()
                }
                invalidateOptionsMenu()
            }
        }

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener {

            drawerLayout.closeDrawer(GravityCompat.START)
            when (it.itemId) {
                R.id.nav_homeFragment -> {
                    toolbar.visibility = View.VISIBLE
                    navBottomView.visibility = View.VISIBLE
                    replaceFragment(HomeFragment(), "Every Moment Holy")
                    navBottomView.selectedItemId = R.id.nav_homeFragment
                    true
                }
                R.id.nav_myLiturgiesFragment -> {
                    toolbar.visibility = View.VISIBLE
                    navBottomView.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.VISIBLE
                    iv_toolbar_notification.visibility = View.GONE
                    Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_MY_LITURGY
                    replaceFragment(MyLiturgiesFragment(), "My Liturgies")
                    navBottomView.selectedItemId = R.id.nav_myLiturgiesFragment
                    true
                }
                R.id.nav_favoritesFragment -> {
                    if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                        showLoginDialog()
                    } else {
                        toolbar.visibility = View.VISIBLE
                        iv_toolbar_search.visibility = View.VISIBLE
                        navBottomView.visibility = View.VISIBLE
                        navBottomView.selectedItemId = R.id.nav_favoritesFragment
                        Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_FAVORITES
                        replaceFragment(FavoritesFragment(), "Favorites")
                    }
                    //  showUnderDevDialog()
                    // replaceFragment(FavoritesFragment(), "Favorites")
                    true
                }
                /*R.id.nav_getLiturgiesFragment -> {
                    toolbar.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.VISIBLE
                    navBottomView.visibility = View.VISIBLE
                    Constants.GET_LITURGIES_VIEW_PAGER_POSITION = 0
                    Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_GET_LITURGY
                    replaceFragment(GetLiturgiesFragment(), "Get Liturgies")
                    navBottomView.selectedItemId = R.id.nav_getLiturgiesFragment
                    true
                }*/
                R.id.nav_featuredFragment -> {
                    toolbar.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.VISIBLE
                    navBottomView.visibility = View.VISIBLE
                    Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_FEATURED_LITURGY
                    replaceFragment(FeaturedFragment(), "Featured Liturgies")
                    navBottomView.selectedItemId = R.id.nav_featuredFragment
                    //showUnderDevDialog()
                    true
                }
                R.id.nav_orderBookFragment -> {
                    navBottomView.visibility = View.VISIBLE
                    replaceFragment(OrderBookFragment(), "Order Books")
                    toolbar.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.GONE
                    iv_toolbar_notification.visibility = View.GONE
                    navBottomView.selectedItemId = R.id.nav_orderBookFragment
                    navView.setCheckedItem(R.id.nav_orderBookFragment)
                    //replaceFragment(OrderBookFragment(), "Book Ordered")
                    //showUnderDevDialog()
                    false
                }
                /*R.id.nav_pastPurchase -> {
                    toolbar.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.GONE
                    iv_toolbar_notification.visibility = View.GONE
                    navBottomView.visibility = View.VISIBLE
                    Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_FEATURED_LITURGY
                    replaceFragment(PreviousPurchaseFragment(), "Previous Purchase")
                    navBottomView.selectedItemId = R.id.nav_pastPurchase
                    //showUnderDevDialog()
                    true
                }*/
                R.id.purchase -> {
                    toolbar.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.GONE
                    iv_toolbar_notification.visibility = View.GONE
                    navBottomView.visibility = View.VISIBLE
                    Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_FEATURED_LITURGY
                    replaceFragment(PreviousPurchaseFragment(), "Past Purchase")
                    navBottomView.selectedItemId = R.id.purchase
                    //showUnderDevDialog()
                    true
                }
                R.id.nav_subscription -> {
                    toolbar.visibility = View.VISIBLE
                    navBottomView.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.GONE
                    iv_toolbar_notification.visibility = View.GONE
                    iv_toolbar_backImage.visibility = View.GONE
                    ivToolbarDrawer.visibility = View.VISIBLE
                    //navBottomView.selectedItemId = R.id.nav_subScriptionPlanListFrag
                    //replaceFragment(SubscriptionPlanFragment(), "Subscription")
                    val bundle = Bundle()
                    bundle.putBoolean("onPress", false);
                    Constants.CURRENT_FRAGMENT = Constants.FROM_HOME_SCREEN
                    //replaceFragment(SubscriptionPlanListFragment(), "Subscription")
                    replaceFragment(
                        SubscriptionPlanListFragment(),
                        "Subscription",
                        bundle
                    )
                    // navBottomView.selectedItemId = R.id.nav_myLiturgiesFragment
                    true

                    /* toolbar.visibility = View.VISIBLE
                     iv_toolbar_search.visibility = View.VISIBLE
                     navBottomView.visibility = View.VISIBLE
                     navBottomView.selectedItemId = R.id.nav_favoritesFragment
                     Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_FAVORITES
                     replaceFragment(FavoritesFragment(), "Favorites")*/
                }
                R.id.nav_searchFragment -> {
                    // replaceFragment(SearchFragment(), "Search")
                    toolbar.visibility = View.VISIBLE
                    iv_toolbar_notification.visibility = View.GONE
                    navBottomView.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.GONE
                    ivToolbarDrawer.visibility = View.VISIBLE
                    iv_toolbar_backImage.visibility = View.GONE
                    replaceFragment(SearchFragment(), "Search")
                    //showUnderDevDialog()
                    true
                }
                R.id.nav_shareLiturgiesFragment -> {
                    toolbar.visibility = View.VISIBLE
                    navBottomView.visibility = View.GONE
                    iv_toolbar_notification.visibility = View.GONE
                    replaceFragment(ShareLiturgiesFragment(), "How to Share Liturgies")
                    // showUnderDevDialog()
                    true
                }
                R.id.nav_aboutUsFragment -> {
                    // replaceFragment(AboutUsFragment(), "About Us")
                    toolbar.visibility = View.VISIBLE
                    navBottomView.visibility = View.GONE
                    iv_toolbar_notification.visibility = View.GONE
                    replaceFragment(AboutUsFragment(), "About Us")
                    // showUnderDevDialog()
                    true
                }
                R.id.nav_FAQFragment -> {
                    // replaceFragment(FAQFragment(), "FAQ")
                    toolbar.visibility = View.VISIBLE
                    navBottomView.visibility = View.GONE
                    iv_toolbar_notification.visibility = View.GONE
                    replaceFragment(FAQFragment(), "FAQ")
                    //showUnderDevDialog()
                    true
                }
                R.id.nav_conditionFragment -> {
                    //  replaceFragment(ConditionFragment(), "Terms & Condition")
                    toolbar.visibility = View.VISIBLE
                    navBottomView.visibility = View.GONE
                    replaceFragment(ConditionFragment(), "Terms & Condition")
                    //showUnderDevDialog()
                    true
                }
                R.id.nav_myProfileFragment -> {
                    if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                        showLoginDialog()
                    } else {
                        toolbar.visibility = View.VISIBLE
                        txt_toolbar_name.text = "My Profile"
                        iv_toolbar_notification.visibility = View.GONE
                        navBottomView.visibility = View.GONE
                        replaceFragment(MyProfileFragment(), "My Profile")
                    }
                    true
                }
                R.id.nav_logoutFragment -> {
                    if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                        val intent = Intent(this@MainActivity, SelectOptionActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        showLogoutDialog()
                    }
                    true
                }
                else -> false
            }
        }


        navBottomView.setOnNavigationItemSelectedListener {
            val fragment: Fragment
            when (it.itemId) {
                R.id.nav_homeFragment -> {
                    toolbar.visibility = View.VISIBLE
                    iv_toolbar_notification.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.GONE
                    iv_toolbar_backImage.visibility = View.GONE
                    // iv_toolbar_search.visibility = View.GONE
                    iv_toolbar_notification.setImageResource(R.drawable.ic_notification);
                    txt_toolbar_name.text = "Every Moment Holy"
                    fragment = HomeFragment()
                    replaceFragment(fragment, "Every Moment Holy")
                    //loadFragment(fragment)
                    navView.setCheckedItem(R.id.nav_homeFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_featuredLiturgiesFragment -> {
                    toolbar.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.VISIBLE
                    iv_toolbar_notification.visibility = View.GONE
                    txt_toolbar_name.text = "Featured Liturgies"
                    fragment = FeaturedFragment()
                    Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_FEATURED_LITURGY
                    replaceFragment(fragment, "Featured Liturgies")
                    navView.setCheckedItem(R.id.nav_myLiturgiesFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_getLiturgiesFragment -> {
                    toolbar.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.VISIBLE
                    iv_toolbar_notification.visibility = View.GONE
                    Constants.GET_LITURGIES_VIEW_PAGER_POSITION = 0
                    fragment = GetLiturgiesFragment()
                    Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_GET_LITURGY
                    replaceFragment(fragment, "Get Liturgies")
                    navView.setCheckedItem(R.id.nav_getLiturgiesFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_favoritesFragment -> {
                    if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                        showLoginDialog()
                    } else {
                        toolbar.visibility = View.VISIBLE
                        txt_toolbar_name.text = "Favorites"
                        iv_toolbar_search.visibility = View.VISIBLE
                        iv_toolbar_notification.visibility = View.GONE
                        fragment = FavoritesFragment()
                        replaceFragment(fragment, "Favorites")
                        Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_FAVORITES
                        navView.setCheckedItem(R.id.nav_favoritesFragment)
                    }
                    // showUnderDevDialog()
                    // return@setOnNavigationItemSelectedListener true
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_searchFragment -> {

                    toolbar.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.GONE
                    iv_toolbar_notification.visibility = View.GONE
                    Constants.GET_LITURGIES_VIEW_PAGER_POSITION = 0
                    fragment = SearchFragment()
                    Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_GET_LITURGY
                    replaceFragment(fragment, "Search")
                    navView.setCheckedItem(R.id.nav_getLiturgiesFragment)

//                    toolbar.visibility = View.VISIBLE
//                    iv_toolbar_notification.visibility = View.GONE
//                    navBottomView.visibility = View.GONE
//                    iv_toolbar_search.visibility = View.GONE
//                    ivToolbarDrawer.visibility = View.VISIBLE
//                    iv_toolbar_backImage.visibility = View.GONE
//                    replaceFragment(SearchFragment(), "Search")
                    return@setOnNavigationItemSelectedListener true
                }
                // FeaturedFragment

            }
            false

        }

        iv_drawer_profile_image.setOnClickListener() {
            if (Constants.USER_LOGIN_STATUS == Constants.SKIP_LOGIN) {
                showLoginDialog()
            } else {
                toolbar.visibility = View.VISIBLE
                txt_toolbar_name.text = "My Profile"
                iv_toolbar_notification.visibility = View.GONE
                navBottomView.visibility = View.GONE
                replaceFragment(MyProfileFragment(), "My Profile")
                drawerLayout.close()
            }
        }

        iv_toolbar_search.setOnClickListener {
            toolbar.visibility = View.VISIBLE
            iv_toolbar_search.visibility = View.GONE
            navBottomView.visibility = View.VISIBLE
            ivToolbarDrawer.visibility = View.GONE
            iv_toolbar_backImage.visibility = View.VISIBLE
            replaceFragment(SearchFragment(), "Search")
            //showUnderDevDialog()
        }
    }

    fun getAccountDetails(context: Context)
    {
        val account = getActiveGoogleAccount(context)
        if (account != null) {
            val email = account.name
            // Do something with the email

            val activeEmail = Utils.writeStringToSharedPref(
                context, Constants.ACTIVE_PLAYSTORE_EMAIL,
                email
            ).toString()
            Log.d("ActiveAccount", "Active Google play account: $activeEmail")
        } else {
            Log.d("ActiveAccount", "No active Google account found")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted
                // Request the permission


                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATIONS_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    // Handle the permission request response
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CONTACTS_PERMISSION_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                    // You can now access contacts
                    // AccessContacts()
                    /*val account = getActiveGoogleAccount(this)
                    if (account != null) {
                        val email = account.name
                        // Do something with the email
                        Log.d("ActiveAccount", "Active Google account: $email")
                    } else {
                        Log.d("ActiveAccount", "No active Google account found")
                    }*/
                    getAccountDetails(this)
                } else {
                    Toast.makeText(
                        this,
                        "The app was not allowed to read your contact",
                        Toast.LENGTH_LONG
                    ).show();
                    // Permission denied
                    // You may want to handle this case gracefully
                    // You can display a message to the user indicating why you need the permission and how to enable it
                }
                return
            }
            // Handle other permissions if needed
            NOTIFICATIONS_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission granted
                    // You can now send notifications

                } else {
                    // Permission denied
                    //show the dialog for denied
                    showPermissionDeniedDialog()
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Notification Permission Denied")
            .setMessage(
                "You have denied notification permission. To enable notifications, go to:\n\n" +
                        "Settings > Apps > ${getString(R.string.app_name)} > Notifications"
            )
            .setPositiveButton("Open Settings", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                    startActivity(intent)
                }
            })
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun getActiveGoogleAccount(context: Context): Account? {
        val accounts =
            AccountManager.get(context).getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)
        return if (accounts.isNotEmpty()) {
            accounts[0] // Return the first Google account
        } else {
            null
        }
    }

    fun showUnderDevDialog() {
        AlertDialog.Builder(this)
            .setMessage("This part is under Development.")
            .setPositiveButton(android.R.string.yes) { dialog, which ->
            }.show()
    }

    private fun showLogoutDialog() {
        val alertDialog = AlertDialog.Builder(
            this
        )
        val inflater = (this as Activity).layoutInflater
        val alertView: View = inflater.inflate(R.layout.logout_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val alertButtonCancel = alertView.findViewById<View>(R.id.txtLougotCancel) as TextView
        val alertButtonYes = alertView.findViewById<View>(R.id.txtLogoutYes) as TextView


        alertButtonYes.setOnClickListener {
            //show.dismiss()
            var logoutRequestVo: LogoutRequestVo = LogoutRequestVo()
            logoutRequestVo.userId = prefeUserId
            if (Utils.isNetworkAvailable(this)) {
                logoutUser(logoutRequestVo)
                show.dismiss()
            } else {
                Toast.makeText(
                    this@MainActivity,
                    resources.getString(R.string.check_internet),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        alertButtonCancel.setOnClickListener {
            show.dismiss()
        }
        show.setCanceledOnTouchOutside(false)
    }

    private fun logoutUser(logoutRequestVo: LogoutRequestVo) {
        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.logoutUser(
                logoutRequestVo,
                "bearer " + Utils.readStringFromSharedPref(
                    this,
                    Constants.SHARED_PREF_TOKEN,
                    ""
                )
            )

        try {
            call.enqueue(object : Callback<LogoutResponseVo> {
                override fun onResponse(
                    call: Call<LogoutResponseVo>,
                    response: Response<LogoutResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {

                        Utils.writeUserIdBooleanFromSharedPref(getApplicationContext(), false);
                        Utils.clearAllPreference(this@MainActivity)
                        //dialog.dismiss()
                       // val intent = Intent(this@MainActivity, SelectOptionActivity::class.java)
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LogoutResponseVo>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
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
                this,
                Constants.SHARED_PREF_TOKEN,
                ""
            ).toString()
        )

        val request = APIService.buildService(APIInterface::class.java)
        val call =
            request.getUserProfile(
                getUserProfileRequestVo.userId, getUserProfileRequestVo.deviceId,
                "bearer " + Utils.readStringFromSharedPref(
                    this,
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

                        // isuserLogin = true

                        Utils.writeStringToSharedPref(
                            this@MainActivity, Constants.NAME,
                            response.body()!!.response.firstName
                        )

                        Utils.writeStringToSharedPref(
                            this@MainActivity, Constants.PLAN_STATUS,
                            response.body()!!.response.userSubscriptionData.subscription
                        )

                        Utils.writeStringToSharedPref(
                            this@MainActivity, Constants.SUBSCRIPTION_TYPE,
                            response.body()!!.response.userSubscriptionData.subscription_type
                        )


                        Log.e("Name", response.body()!!.response.firstName)

                        Utils.writeStringToSharedPref(
                            this@MainActivity, Constants.USER_EMAIL,
                            response.body()!!.response.email
                        )

                        Utils.writeStringToSharedPref(
                            this@MainActivity, Constants.PROFILE_PIC,
                            response.body()!!.response.userProfilePic
                        )

                        /*Utils.writeStringToSharedPref(
                            this@MainActivity, Constants.UPCOMING_SUB_TYPE,
                            response.body()!!.response.userSubscriptionData.upcomingPlanData.subscription
                        )*/

                        txt_drawer_UserName.text = Utils.readStringFromSharedPref(
                            this@MainActivity, Constants.USER_NAME,
                            ""
                        ).toString()

                        Utils.writeStringToSharedPref(
                            this@MainActivity, Constants.PROFILE_STATUS,
                            response.body()!!.response.userSubscriptionData.subscription
                        )

                        Utils.writeStringToSharedPref(
                            this@MainActivity, Constants.ISSUBSCRIBE,
                            response.body()!!.response.userSubscriptionData.subscription
                        )


                        Glide.with(this@MainActivity)
                            .load(response.body()!!.response.userProfilePic)
                            .into(iv_drawer_profile_image)

                        txt_drawer_email.text = response.body()!!.response.email

                        userImage = response.body()!!.response.userProfilePic

                    }
                }

                override fun onFailure(call: Call<GetUserProfileVo>, t: Throwable) {

                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun openDrawer() {
        //  val drawer = findViewById<View>(R.id.drawerLayout) as DrawerLayout
        drawerLayout.openDrawer(navView)
    }

    private fun loadFragment(fragment: Fragment) {
        // txt_toolbar_name.setTextColor(ContextCompat.getColor(this, R.color.black));
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        //transaction.addToBackStack(null)
        transaction.disallowAddToBackStack()
        transaction.commit()
        currentFragment = fragment.javaClass.name
    }

    fun replaceFragment(fragment: Fragment, txtToolbarTitle: String, arguments: Bundle? = null) {
        if (currentFragment != fragment.javaClass.name) {

            try {
                val view = this.currentFocus
                if (view != null) {
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            txt_toolbar_name.text = txtToolbarTitle
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            if (fragment is AboutBookLiturgiesFragment)
                transaction.replace(R.id.nav_host_fragment, fragment)
            else if (fragment is CollectionListFragment) {
                //transaction.addToBackStack(CollectionListFragment::class.java.name)
                transaction.replace(R.id.nav_host_fragment, fragment)
            } else if (fragment is LiturgiesListFragment) {
                // transaction.addToBackStack(LiturgiesListFragment::class.java.name)
                transaction.replace(R.id.nav_host_fragment, fragment)
            } else if (fragment is SubscriptionPlanListFragment)
                transaction.replace(R.id.nav_host_fragment, fragment, "subscriptionFrag")
            else if (fragment is PlayAudioFragment)
                transaction.replace(R.id.nav_host_fragment, fragment)
            else if (fragment is SearchFragment)
                transaction.replace(R.id.nav_host_fragment, fragment, "searchFrag")
            else
                transaction.replace(R.id.nav_host_fragment, fragment)
            transaction.disallowAddToBackStack()
            if (arguments != null) {
                fragment.arguments = arguments
            }
            transaction.commit()
            currentFragment = fragment.javaClass.name
        }
    }

    fun addFragment(fragment: Fragment, txtToolbarTitle: String, arguments: Bundle?) {
        //  txt_toolbar_name.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
        txt_toolbar_name.text = txtToolbarTitle
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.nav_host_fragment, fragment)
        if (fragment is AboutBookLiturgiesFragment) {
            transaction.addToBackStack("yes")
        }
        if (fragment is CollectionListFragment) {
            transaction.addToBackStack(CollectionListFragment::class.java.name)
        }
        if (fragment is LiturgiesListFragment) {
            transaction.addToBackStack(LiturgiesListFragment::class.java.name)
        }
        if (fragment is SubscriptionPlanListFragment) {
            transaction.addToBackStack(SubscriptionPlanListFragment::class.java.name)
        }
        if (arguments != null) {
            fragment.arguments = arguments
        }
        transaction.commit()
        drawerLayout.closeDrawers()
        currentFragment = fragment.javaClass.name
    }

    override fun onResume() {
        super.onResume()
        // getUserProfile()
        if (Constants.USER_LOGIN_STATUS == Constants.LOGIN) {
            Glide.with(this@MainActivity)
                .load(userImage)
                .into(iv_drawer_profile_image)
        }

    }

    override fun onBackPressed() {
        if (supportFragmentManager != null) {
            val fragment: Fragment? =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            if (fragment is AboutBookLiturgiesFragment) {
                toolbar.visibility = View.VISIBLE
                replaceFragment(GetLiturgiesFragment(), "Get Liturgies")
            } else if (fragment is CollectionListFragment) {
                toolbar.visibility = View.VISIBLE
                txt_toolbar_name.text = "Get Liturgies"
                iv_toolbar_search.visibility = View.VISIBLE
                iv_toolbar_drawer.visibility = View.VISIBLE
                iv_toolbar_backImage.visibility = View.GONE
                super.onBackPressed()
                // replaceFragment(GetLiturgiesFragment(), "Get Liturgies")
            } else if (fragment is LiturgiesListFragment) {
                toolbar.visibility = View.VISIBLE
                txt_toolbar_name.text = "Collection"
                super.onBackPressed()
                //replaceFragment(CollectionListFragment(), "Collection")
            } /*else if (fragment is SubscriptionPlanListFragment) {
                toolbar.visibility = View.VISIBLE
                txt_toolbar_name.text = "Collection"
                super.onBackPressed()
                //replaceFragment(GetLiturgiesFragment(), "Subscription")
            }*/ else if (fragment is SubscriptionPlanListFragment) {
                when (Constants.CURRENT_FRAGMENT) {
                    Constants.FROM_COLLECTION_LIST -> {
                        toolbar.visibility = View.VISIBLE
                        iv_toolbar_search.visibility = View.GONE
                        iv_toolbar_notification.visibility = View.GONE
                        txt_toolbar_name.text = "Collection"
                        super.onBackPressed()
                        //replaceFragment(CollectionListFragment(), "Collection")
                    }
                    Constants.FROM_LITURGY_LIST -> {
                        toolbar.visibility = View.VISIBLE
                        iv_toolbar_search.visibility = View.GONE
                        iv_toolbar_notification.visibility = View.GONE
                        txt_toolbar_name.text = "Liturgies"
                        super.onBackPressed()
                        //replaceFragment(LiturgiesListFragment(), "Liturgies")
                    }
                    Constants.FROM_FAVOURITE_LIST -> {
                        toolbar.visibility = View.VISIBLE
                        iv_toolbar_search.visibility = View.VISIBLE
                        iv_toolbar_notification.visibility = View.GONE
                        txt_toolbar_name.text = "Favorites"
                        super.onBackPressed()
                        //replaceFragment(LiturgiesListFragment(), "Liturgies")
                    }
                    Constants.FROM_SEARCH -> {
                        toolbar.visibility = View.VISIBLE
                        ivToolbarDrawer.visibility = View.VISIBLE
                        iv_toolbar_backImage.visibility = View.GONE
                        //iv_toolbar_search.visibility = View.VISIBLE
                        //iv_toolbar_notification.visibility = View.GONE
                        txt_toolbar_name.text = "Search"
                        super.onBackPressed()
                        //replaceFragment(LiturgiesListFragment(), "Liturgies")
                    }

                    Constants.FROM_HOME_SCREEN -> {
                        toolbar.visibility = View.VISIBLE
                        iv_toolbar_search.visibility = View.GONE
                        iv_toolbar_notification.visibility = View.VISIBLE
                        //replaceFragment(HomeFragment(), "Every Moment Holy")
                        txt_toolbar_name.text = "Every Moment Holy"
                        super.onBackPressed()
                        //replaceFragment(CollectionListFragment(), "Collection")
                    }
                }
            } else if (fragment is SearchFragment) {
                when (Constants.CURRENT_FRAGMENT) {
                    Constants.SEARCH_FROM_MY_LITURGY -> {
                        toolbar.visibility = View.VISIBLE
                        iv_toolbar_search.visibility = View.VISIBLE
                        iv_toolbar_notification.visibility = View.GONE
                        replaceFragment(MyLiturgiesFragment(), "My Liturgies")
                    }
                    Constants.SEARCH_FROM_FAVORITES -> {
                        toolbar.visibility = View.VISIBLE
                        iv_toolbar_search.visibility = View.VISIBLE
                        iv_toolbar_notification.visibility = View.GONE
                        replaceFragment(FavoritesFragment(), "Favorites")
                    }
                    Constants.SEARCH_FROM_GET_LITURGY -> {
                        toolbar.visibility = View.VISIBLE
                        iv_toolbar_search.visibility = View.VISIBLE
                        iv_toolbar_notification.visibility = View.GONE
                        replaceFragment(GetLiturgiesFragment(), "Get Liturgies")
                    }
                    Constants.SEARCH_FROM_FEATURED_LITURGY -> {
                        toolbar.visibility = View.VISIBLE
                        iv_toolbar_search.visibility = View.VISIBLE
                        iv_toolbar_notification.visibility = View.GONE
                        replaceFragment(FeaturedFragment(), "Featured Liturgies")
                    }
                }
            } else {
                super.onBackPressed()
            }
        }
    }

    fun showLoginDialog() {
        val alertDialog = AlertDialog.Builder(
            this
        )
        val inflater = (this as Activity).layoutInflater
        val alertView: View = inflater.inflate(R.layout.login_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val alertCancel = alertView.findViewById<View>(R.id.txtLoginCancel) as TextView
        val alertOk = alertView.findViewById<View>(R.id.txtLoginOk) as TextView
        val messageShow = alertView.findViewById<View>(R.id.txtMeaasge) as TextView

        messageShow.text = "You need to login first."
        alertOk.setOnClickListener {
            show.dismiss()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        alertCancel.setOnClickListener {
            show.dismiss()
        }
        show.setCanceledOnTouchOutside(false)
    }

    fun showLiturgyDialog() {
        val alertDialog = AlertDialog.Builder(
            this
        )
        val inflater = layoutInflater
        val alertView: View = inflater.inflate(R.layout.login_dialog, null)
        alertDialog.setView(alertView)
        val show = alertDialog.show()
        val alertCancel = alertView.findViewById<View>(R.id.txtLoginCancel) as TextView
        alertCancel.visibility = View.GONE
        val alertOk = alertView.findViewById<View>(R.id.txtLoginOk) as TextView


        alertOk.setOnClickListener {
            show.dismiss()
//            val intent = Intent(this@MainActivity, LoginActivity::class.java)
//            startActivity(intent)
        }

        alertCancel.setOnClickListener {
            show.dismiss()
        }
        show.setCanceledOnTouchOutside(false)
    }

    fun showDialogForUnlockWithoutLogin() {
        AlertDialog.Builder(this)
            .setMessage("This part is under Development.")
            .setPositiveButton(android.R.string.yes) { dialog, which ->
            }.setNegativeButton(android.R.string.no) { dialog, which ->
            }.setNeutralButton(android.R.string.ok) { dialog, which -> }.show()
    }

    /**
     * This callback will acknowledge the successful in-app purchase to the backend server.
     */
    override fun onPurchaseComplete(purchaseRequestVo: PurchaseRequestVo) {

        /*  runOnUiThread {
              Toast.makeText(this, "In app purchase complete", Toast.LENGTH_LONG).show()
          }
  */
        val request = APIService.buildService(APIInterface::class.java)
        lateinit var call: Call<PrivateShareResponseVo>

        when (purchaseRequestVo.productType) {
            ProductTypes.LITURGY -> {
                call = request.purchaseLiturgyAcknowledge(
                    purchaseRequestVo,
                    "bearer " + Utils.readStringFromSharedPref(
                        this,
                        Constants.SHARED_PREF_TOKEN,
                        ""
                    )
                )
            }
            ProductTypes.BOOK -> {
                call = request.purchaseBookAcknowledge(
                    purchaseRequestVo,
                    "bearer " + Utils.readStringFromSharedPref(
                        this,
                        Constants.SHARED_PREF_TOKEN,
                        ""
                    )
                )
            }
            ProductTypes.VOLUME -> {
                call = request.purchaseVolumeAcknowledge(
                    purchaseRequestVo,
                    "bearer " + Utils.readStringFromSharedPref(
                        this,
                        Constants.SHARED_PREF_TOKEN,
                        ""
                    )
                )
            }
        }

        try {
            call.enqueue(object : Callback<PrivateShareResponseVo> {
                override fun onResponse(
                    call: Call<PrivateShareResponseVo>,
                    response: Response<PrivateShareResponseVo>
                ) {
                    if (response.body()?.statusCode == 1) {
                        /*Toast.makeText(
                            this@MainActivity,
                            "success",
                            Toast.LENGTH_LONG
                        ).show()*/

                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            response.body()!!.response,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PrivateShareResponseVo>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }
}