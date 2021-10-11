package com.everymomentholy.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.everymomentholy.R
import com.everymomentholy.ui.fragments.*
import com.everymomentholy.utils.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var iv_toolbar_back: ImageView
    private lateinit var iv_toolbar_notification: ImageView
    private lateinit var iv_toolbar_backImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        iv_toolbar_backImage = findViewById(R.id.iv_toolbar_backImage)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        val navBottomView: BottomNavigationView = findViewById(R.id.bottom_navigation_view)

        var fragment1: Fragment? = null
        fragment1 = HomeFragment()
        addFragment(fragment1)

        iv_toolbar_notification = findViewById(R.id.iv_toolbar_notification)
        iv_toolbar_notification.setOnClickListener {
            /*val intent = Intent(this@MainActivity, NotificationListActivity::class.java)
            startActivity(intent)*/

            var fragment: Fragment
            fragment = NotificationListFragment()
            replaceFragment(fragment)
        }

        iv_toolbar_back = findViewById(R.id.iv_toolbar_back)
        iv_toolbar_back.setOnClickListener {
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
            }

            /** Called when a drawer has settled in a completely open state.  */
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)

            }
        }

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            when (it.itemId) {
                R.id.nav_homeFragment -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_myLiturgiesFragment -> {
                    replaceFragment(MyLiturgiesFragment())
                    true
                }
                R.id.nav_favoritesFragment -> {
                    replaceFragment(FavoritesFragment())
                    true
                }
                R.id.nav_getLiturgiesFragment -> {
                    replaceFragment(GetLiturgiesFragment())
                    true
                }
                R.id.nav_featuredFragment -> {
                    replaceFragment(FeaturedFragment())
                    true
                }
                R.id.nav_orderBookFragment -> {
                    replaceFragment(OrderBookFragment())
                    true
                }
                R.id.nav_searchFragment -> {
                    replaceFragment(SearchFragment())
                    true
                }
                R.id.nav_shareLiturgiesFragment -> {
                    replaceFragment(ShareLiturgiesFragment())
                    true
                }
                R.id.nav_aboutUsFragment -> {
                    replaceFragment(AboutUsFragment())
                    true
                }
                R.id.nav_FAQFragment -> {
                    replaceFragment(FAQFragment())
                    true
                }
                R.id.nav_conditionFragment -> {
                    replaceFragment(ConditionFragment())
                    true
                }
                R.id.nav_myProfileFragment -> {
                    replaceFragment(MyProfileFragment())
                    true
                }
                R.id.nav_logoutFragment -> {
                    // replaceFragment(ExploreTrailFragment())
                    Utils.writeUserIdBooleanFromSharedPref(getApplicationContext(), false);
                    val intent = Intent(applicationContext, SelectOptionActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }


       // loadFragment(HomeFragment())

        navBottomView.setOnNavigationItemSelectedListener {
            val fragment: Fragment
            when (it.itemId) {
                R.id.nav_homeFragment -> {
                    fragment = HomeFragment()
                    loadFragment(fragment)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_myLiturgiesFragment -> {
                    title = resources.getString(R.string.my_liturgy)
                    fragment = MyLiturgiesFragment()
                    loadFragment(fragment)
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.nav_favoritesFragment -> {
                    fragment = FavoritesFragment()
                    loadFragment(fragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_getLiturgiesFragment -> {
                    fragment = GetLiturgiesFragment()
                    loadFragment(fragment)
                    return@setOnNavigationItemSelectedListener true
                }

            }
            false

        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        //transaction.addToBackStack(null)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }

    fun AppCompatActivity.replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.disallowAddToBackStack()
        // transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun addFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.nav_host_fragment, fragment)
        //transaction.replace(R.id.nav_host_fragment, fragment)
        transaction.commit()
        drawerLayout.closeDrawers()
    }

}