package com.everymomentholy.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
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

    private lateinit var iv_drawer_profile_image: ImageView
    private lateinit var txt_drawer_UserName: TextView
    private lateinit var txt_drawer_email: TextView

    private lateinit var android_id: String
    var prefeUserId: Int = 0

    lateinit var userName: String
    lateinit var profileImage: String


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

        txt_drawer_UserName.text =  Utils.readStringFromSharedPref(
            this@MainActivity, Constants.USER_NAME,
            ""
        ).toString()

        //getUserProfile()


        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val navBottomView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)


        var fragment1: Fragment? = null
        fragment1 = HomeFragment()
        addFragment(fragment1, "Every Moment Holy")




        iv_toolbar_notification.setOnClickListener {
            /*val intent = Intent(this@MainActivity, NotificationListActivity::class.java)
            startActivity(intent)*/

            txt_toolbar_name.text = "Notifications"
            iv_toolbar_notification.visibility = View.GONE
            iv_toolbar_drawer.visibility = View.GONE
            iv_toolbar_backImage.visibility = View.VISIBLE
            var fragment: Fragment
            fragment = NotificationListFragment()
            replaceFragment(fragment, "Notification")
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
                // txt_drawer_UserName.text = userName

            }

            /** Called when a drawer has settled in a completely open state.  */
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                // txt_drawer_UserName.text = userName
                //  lastsynced.setText(lastsynced());

                profileImage = Utils.readStringFromSharedPref(
                    this@MainActivity, Constants.PROFILE_PIC,
                    ""
                ).toString()
                iv_drawer_profile_image.setImageURI(Uri.parse(profileImage))

                profileImage = Utils.readStringFromSharedPref(
                    this@MainActivity, Constants.DEFAULT_PROFILE_PIC,
                    ""
                ).toString()

                iv_drawer_profile_image.setImageURI(Uri.parse(profileImage))

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
                    replaceFragment(HomeFragment(), "Every Moment Holy")
                    true
                }
                R.id.nav_myLiturgiesFragment -> {
                    replaceFragment(MyLiturgiesFragment(), "My Liturgies")
                    true
                }
                R.id.nav_favoritesFragment -> {
                    replaceFragment(FavoritesFragment(), "Favourites")
                    true
                }
                R.id.nav_getLiturgiesFragment -> {
                    replaceFragment(GetLiturgiesFragment(), "Get Liturgies")
                    true
                }
                R.id.nav_featuredFragment -> {
                    replaceFragment(FeaturedFragment(), "Featured")
                    true
                }
                R.id.nav_orderBookFragment -> {
                    replaceFragment(OrderBookFragment(), "Book Ordered")
                    true
                }
                R.id.nav_searchFragment -> {
                    replaceFragment(SearchFragment(), "Search")
                    true
                }
                R.id.nav_shareLiturgiesFragment -> {
                    replaceFragment(ShareLiturgiesFragment(), "Share")
                    true
                }
                R.id.nav_aboutUsFragment -> {
                    replaceFragment(AboutUsFragment(), "About Us")
                    true
                }
                R.id.nav_FAQFragment -> {
                    replaceFragment(FAQFragment(), "FAQ")
                    true
                }
                R.id.nav_conditionFragment -> {
                    replaceFragment(ConditionFragment(), "Terms & Condition")
                    true
                }
                R.id.nav_myProfileFragment -> {
                    txt_toolbar_name.text = "My Profile"
                    iv_toolbar_notification.visibility = View.GONE
                    navBottomView.visibility = View.GONE
                    replaceFragment(MyProfileFragment(), "My Profile")
                    true
                }
                R.id.nav_logoutFragment -> {
                    showLogoutDialog()

                    /* Utils.writeUserIdBooleanFromSharedPref(getApplicationContext(), false);
                     val intent = Intent(applicationContext, SelectOptionActivity::class.java)
                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                     startActivity(intent)*/
                    true
                }
                else -> false
            }
        }


        navBottomView.setOnNavigationItemSelectedListener {
            val fragment: Fragment
            when (it.itemId) {
                R.id.nav_homeFragment -> {

                    iv_toolbar_notification.visibility = View.VISIBLE
                    iv_toolbar_search.visibility = View.GONE
                    iv_toolbar_notification.setImageResource(R.drawable.ic_notification);
                    txt_toolbar_name.text = "Every Moment Holy"
                    fragment = HomeFragment()
                    loadFragment(fragment)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_myLiturgiesFragment -> {
                    iv_toolbar_search.visibility = View.VISIBLE
                    iv_toolbar_notification.visibility = View.GONE
                    // iv_toolbar_notification.setImageResource(R.drawable.ic_searchimg);
                    iv_toolbar_search.setOnClickListener {
                        Toast.makeText(
                            this@MainActivity,
                            "Search",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    txt_toolbar_name.text = "My Liturgies"
                    fragment = MyLiturgiesFragment()
                    replaceFragment(fragment, "My Liturgies")
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_favoritesFragment -> {
                    txt_toolbar_name.text = "Favorites"
                    iv_toolbar_notification.setImageResource(R.drawable.ic_searchimg);
                    iv_toolbar_notification.setOnClickListener {
                        Toast.makeText(
                            this@MainActivity,
                            "Favorites",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    fragment = FavoritesFragment()
                    replaceFragment(fragment, "Favourites")
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_getLiturgiesFragment -> {
                    fragment = GetLiturgiesFragment()
                    replaceFragment(fragment, "Get Liturgies")
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false

        }
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
        show.setCanceledOnTouchOutside(false);
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
                        /*Toast.makeText(
                            this@MainActivity,
                            "Logout sucessfull",
                            Toast.LENGTH_LONG
                        ).show()*/

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

                        Glide.with(this@MainActivity)
                            .load(response.body()!!.response.userProfilePic)
                            .into(iv_drawer_profile_image)

                    } else {
                        /* Toast.makeText(
                             requireActivity(),
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

    private fun loadFragment(fragment: Fragment) {
        txt_toolbar_name.setTextColor(ContextCompat.getColor(this, R.color.black));
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        //transaction.addToBackStack(null)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    fun AppCompatActivity.replaceFragment(fragment: Fragment, txtToolbarTitle: String) {
        txt_toolbar_name.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
        txt_toolbar_name.text = txtToolbarTitle
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.disallowAddToBackStack()
        // transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun addFragment(fragment: Fragment, txtToolbarTitle: String) {
        txt_toolbar_name.setTextColor(ContextCompat.getColor(this, R.color.loginbg));
        txt_toolbar_name.text = txtToolbarTitle
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.nav_host_fragment, fragment)
        //transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commit()
        drawerLayout.closeDrawers()
    }

    /*override fun onResume() {
        super.onResume()
        getUserProfile()
    }*/

    override fun onStart() {
        super.onStart()
        // getUserProfile()
    }

}