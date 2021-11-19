package com.everymomentholy.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.everymomentholy.api.response.GetUserProfileVo
import com.everymomentholy.api.response.LogoutResponseVo
import com.everymomentholy.ui.fragments.*
import com.everymomentholy.utils.Constants
import com.everymomentholy.utils.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var iv_toolbar_drawer: ImageView
    private lateinit var iv_toolbar_notification: ImageView
    private lateinit var iv_toolbar_search: ImageView
    private lateinit var iv_toolbar_backImage: ImageView
    private lateinit var txt_toolbar_name: TextView
    public lateinit var toolbar: Toolbar

    private lateinit var iv_drawer_profile_image: ImageView
    private lateinit var txt_drawer_UserName: TextView
    private lateinit var txt_drawer_email: TextView
    private var currentFragment: String = ""

    private lateinit var android_id: String
    var prefeUserId: Int = 0
    var profileImage: String = ""
    var userImage: String = ""


    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        android_id = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )

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


        val mNavigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = mNavigationView.getHeaderView(0)

        txt_drawer_UserName = headerView.findViewById<TextView>(R.id.txt_drawer_UserName)
        txt_drawer_email = headerView.findViewById<TextView>(R.id.txt_drawer_email)
        iv_drawer_profile_image = headerView.findViewById(R.id.iv_drawer_profile_image)


        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val navBottomView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        navBottomView.visibility = View.VISIBLE

        toolbar.visibility = View.GONE
        var fragment1: Fragment? = null
        fragment1 = HomeFragment()
        addFragment(fragment1, "Every Moment Holy", null)

        getUserProfile()

        /* txt_drawer_UserName.text = Utils.readStringFromSharedPref(
             this@MainActivity, Constants.NAME,
             ""
         ).toString()*/

        txt_drawer_email.text = Utils.readStringFromSharedPref(
            this@MainActivity, Constants.USER_EMAIL,
            ""
        ).toString()

        userImage = Utils.readStringFromSharedPref(
            this@MainActivity, Constants.PROFILE_PIC,
            ""
        ).toString()
        //iv_drawer_profile_image.setImageURI(Uri.parse(userImage))


        iv_toolbar_notification.setOnClickListener {
            /*   val intent = Intent(this@MainActivity, NotificationListActivity::class.java)
               startActivity(intent)
   */
            /*txt_toolbar_name.text = "Notifications"
            iv_toolbar_notification.visibility = View.GONE
            iv_toolbar_drawer.visibility = View.GONE
            iv_toolbar_backImage.visibility = View.VISIBLE
            var fragment: Fragment
            fragment = NotificationListFragment()
            replaceFragment(fragment, "Notification")*/
            showUnderDevDialog()
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



                txt_drawer_email.text = Utils.readStringFromSharedPref(
                    this@MainActivity, Constants.USER_EMAIL,
                    ""
                ).toString()
                invalidateOptionsMenu()
            }
        }

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener {

            drawerLayout.closeDrawer(GravityCompat.START)
            when (it.itemId) {
                R.id.nav_homeFragment -> {
                    toolbar.visibility = View.GONE
                    navBottomView.visibility = View.VISIBLE
                    replaceFragment(HomeFragment(), "Every Moment Holy")
                    navBottomView.selectedItemId = R.id.nav_homeFragment
                    true
                }
                R.id.nav_myLiturgiesFragment -> {
                    toolbar.visibility = View.VISIBLE
                    navBottomView.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.VISIBLE
                    Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_MY_LITURGY
                    replaceFragment(MyLiturgiesFragment(), "My Liturgies")
                    navBottomView.selectedItemId = R.id.nav_myLiturgiesFragment
                    true
                }
                R.id.nav_favoritesFragment -> {
                    toolbar.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.VISIBLE
                    navBottomView.visibility = View.VISIBLE
                    navBottomView.selectedItemId = R.id.nav_favoritesFragment
                    Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_FAVORITES
                    replaceFragment(FavoritesFragment(), "Favourites")
                    //  showUnderDevDialog()
                    // replaceFragment(FavoritesFragment(), "Favourites")
                    true
                }
                R.id.nav_getLiturgiesFragment -> {
                    toolbar.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.VISIBLE
                    navBottomView.visibility = View.VISIBLE
                    Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_GET_LITURGY
                    replaceFragment(GetLiturgiesFragment(), "Get Liturgies")
                    navBottomView.selectedItemId = R.id.nav_getLiturgiesFragment
                    true
                }
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
                    // replaceFragment(OrderBookFragment(), "Book Ordered")
                    /*  toolbar.visibility = View.VISIBLE
                      navBottomView.visibility = View.VISIBLE*/
                    // replaceFragment(OrderBookFragment(), "Book Ordered")
                    showUnderDevDialog()
                    false
                }
                R.id.nav_searchFragment -> {
                    // replaceFragment(SearchFragment(), "Search")
                    toolbar.visibility = View.VISIBLE
                    iv_toolbar_notification.visibility = View.GONE
                    navBottomView.visibility = View.VISIBLE
                    replaceFragment(SearchFragment(), "Search")
                    //showUnderDevDialog()
                    true
                }
                R.id.nav_shareLiturgiesFragment -> {
                    /* toolbar.visibility = View.VISIBLE
                     navBottomView.visibility = View.VISIBLE
                     iv_toolbar_notification.visibility = View.GONE*/
                    //  replaceFragment(ShareLiturgiesFragment(), "How to Share Liturgies")
                    showUnderDevDialog()
                    true
                }
                R.id.nav_aboutUsFragment -> {
                    //  replaceFragment(AboutUsFragment(), "About Us")
                    /* toolbar.visibility = View.VISIBLE
                     navBottomView.visibility = View.VISIBLE
                     iv_toolbar_notification.visibility = View.GONE*/
                    // replaceFragment(AboutUsFragment(), "About Us")
                    showUnderDevDialog()
                    true
                }
                R.id.nav_FAQFragment -> {
                    // replaceFragment(FAQFragment(), "FAQ")
                    /*   toolbar.visibility = View.VISIBLE
                       navBottomView.visibility = View.VISIBLE
                       iv_toolbar_notification.visibility = View.GONE*/
                    // replaceFragment(FAQFragment(), "FAQ")
                    showUnderDevDialog()
                    true
                }
                R.id.nav_conditionFragment -> {
                    //  replaceFragment(ConditionFragment(), "Terms & Condition")
                    /*  toolbar.visibility = View.VISIBLE
                      navBottomView.visibility = View.VISIBLE*/
                    //replaceFragment(ConditionFragment(), "Terms & Condition")
                    showUnderDevDialog()
                    true
                }
                R.id.nav_myProfileFragment -> {
                    toolbar.visibility = View.VISIBLE
                    txt_toolbar_name.text = "My Profile"
                    iv_toolbar_notification.visibility = View.GONE
                    navBottomView.visibility = View.GONE
                    replaceFragment(MyProfileFragment(), "My Profile")
                    true
                }
                R.id.nav_logoutFragment -> {
                    showLogoutDialog()
                    true
                }
                else -> false
            }
        }


        navBottomView.setOnNavigationItemSelectedListener {
            val fragment: Fragment
            when (it.itemId) {
                R.id.nav_homeFragment -> {
                    toolbar.visibility = View.GONE
                    iv_toolbar_notification.visibility = View.VISIBLE
                    // iv_toolbar_search.visibility = View.GONE
                    iv_toolbar_notification.setImageResource(R.drawable.ic_notification);
                    txt_toolbar_name.text = "Every Moment Holy"
                    fragment = HomeFragment()
                    loadFragment(fragment)
                    navView.setCheckedItem(R.id.nav_homeFragment)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_myLiturgiesFragment -> {
                    toolbar.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.VISIBLE
                    iv_toolbar_notification.visibility = View.GONE
                    txt_toolbar_name.text = "My Liturgies"
                    fragment = MyLiturgiesFragment()
                    Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_MY_LITURGY
                    replaceFragment(fragment, "My Liturgies")
                    navView.setCheckedItem(R.id.nav_myLiturgiesFragment)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_favoritesFragment -> {
                    toolbar.visibility = View.VISIBLE
                    txt_toolbar_name.text = "Favorites"
                    iv_toolbar_search.visibility = View.VISIBLE
                    iv_toolbar_notification.visibility = View.GONE
                    fragment = FavoritesFragment()
                    replaceFragment(fragment, "Favourites")
                    Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_FAVORITES
                    navView.setCheckedItem(R.id.nav_favoritesFragment)
                    // showUnderDevDialog()
                    // return@setOnNavigationItemSelectedListener true
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_getLiturgiesFragment -> {
                    toolbar.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.VISIBLE
                    iv_toolbar_notification.visibility = View.GONE
                    fragment = GetLiturgiesFragment()
                    Constants.CURRENT_FRAGMENT = Constants.SEARCH_FROM_GET_LITURGY
                    replaceFragment(fragment, "Get Liturgies")
                    navView.setCheckedItem(R.id.nav_getLiturgiesFragment)
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false

        }

        iv_drawer_profile_image.setOnClickListener() {
            toolbar.visibility = View.VISIBLE
            txt_toolbar_name.text = "My Profile"
            iv_toolbar_notification.visibility = View.GONE
            navBottomView.visibility = View.GONE
            replaceFragment(MyProfileFragment(), "My Profile")
            drawerLayout.close()
        }

        iv_toolbar_search.setOnClickListener {
            toolbar.visibility = View.GONE
            iv_toolbar_search.visibility = View.GONE
            navBottomView.visibility = View.VISIBLE
            replaceFragment(SearchFragment(), "Search")
            //showUnderDevDialog()
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
            logoutUser(logoutRequestVo)
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
                        val intent = Intent(this@MainActivity, SelectOptionActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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

                        Log.e("Name", response.body()!!.response.firstName)

                        Utils.writeStringToSharedPref(
                            this@MainActivity, Constants.USER_EMAIL,
                            response.body()!!.response.email
                        )

                        Utils.writeStringToSharedPref(
                            this@MainActivity, Constants.PROFILE_PIC,
                            response.body()!!.response.userProfilePic
                        )

                        Glide.with(this@MainActivity)
                            .load(response.body()!!.response.userProfilePic)
                            .into(iv_drawer_profile_image)

                    } else {
                        /*Toast.makeText(
                            this@MainActivity,
                            response.body()!!.response.message,
                            Toast.LENGTH_LONG
                        ).show()*/
                    }
                }

                override fun onFailure(call: Call<GetUserProfileVo>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "${t.message}", Toast.LENGTH_SHORT)
                        .show()
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
            txt_toolbar_name.text = txtToolbarTitle
            val fragmentManager = supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            if (fragment is AboutBookLiturgiesFragment)
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
        Glide.with(this@MainActivity)
            .load(userImage)
            .into(iv_drawer_profile_image)

    }

    override fun onBackPressed() {
        if (supportFragmentManager != null) {
            val fragment: Fragment? =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            if (fragment is AboutBookLiturgiesFragment) {
                toolbar.visibility = View.VISIBLE
                replaceFragment(GetLiturgiesFragment(), "Get Liturgies")
            } else if (fragment is SearchFragment) {
                when (Constants.CURRENT_FRAGMENT) {
                    Constants.SEARCH_FROM_MY_LITURGY -> {
                        toolbar.visibility = View.VISIBLE
                        replaceFragment(MyLiturgiesFragment(), "My Liturgies")
                    }
                    Constants.SEARCH_FROM_FAVORITES -> {
                        toolbar.visibility = View.VISIBLE
                        replaceFragment(FavoritesFragment(), "Favourites")
                    }
                    Constants.SEARCH_FROM_GET_LITURGY -> {
                        toolbar.visibility = View.VISIBLE
                        replaceFragment(GetLiturgiesFragment(), "Get Liturgies")
                    }
                    Constants.SEARCH_FROM_FEATURED_LITURGY -> {
                        toolbar.visibility = View.VISIBLE
                        replaceFragment(FeaturedFragment(), "Featured Liturgies")
                    }
                }
            } else {
                super.onBackPressed()
            }
        }
    }

}