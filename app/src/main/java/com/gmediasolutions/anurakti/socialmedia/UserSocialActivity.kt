package com.gmediasolutions.anurakti.socialmedia

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.MenuItem
import com.gmediasolutions.anurakti.*
import com.gmediasolutions.anurakti.base.MainActivity
import kotlinx.android.synthetic.main.custom_toolbar.*


class UserSocialActivity : BaseActivity() {

    private var fragmentManager: FragmentManager? = null
    private var bottomNavigation: BottomNavigationView? = null
    private var timelineFagment: UserTimelineFragment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usersocial)

//        initialize
        fragmentManager = supportFragmentManager

        setupToolbar()
        setupbottomnavigation()

    }

    @SuppressLint("RestrictedApi")
    fun setupbottomnavigation() {
//        setup bottomNavigation
        bottomNavigation = findViewById(R.id.bottom_navigationview)


        if (bottomNavigation != null) {

//         select first menu item by default and show Fragment accordingly.
            val menu = bottomNavigation!!.menu

            selectFragment(menu.getItem(0))
            val menuView = bottomNavigation!!.getChildAt(0) as BottomNavigationMenuView
            for (i in 0 until menuView.childCount) {
                val itemView = menuView.getChildAt(i) as BottomNavigationItemView
                itemView.setChecked(false)
            }

            bottomNavigation!!.setOnNavigationItemSelectedListener { item ->
                selectFragment(item)
                false
            }

        }

    }

    fun setupToolbar() {

//      setup toolbar
        if (my_toolbar != null) {
            setSupportActionBar(my_toolbar)

            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            my_toolbar.setNavigationOnClickListener {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun selectFragment(item: MenuItem?) {
        item!!.isChecked = true

        when (item.itemId) {
            R.id.navigation_timeline -> {
                timelineFagment = UserTimelineFragment.newInstance()
                openFragment(timelineFagment!!)
                return
            }
            R.id.navigation_profile -> {
                val aboutfragment = UserProfileFragment.newInstance()
                openFragment(aboutfragment)
                return
            }
            R.id.navigation_photos -> {
                val photofragment = UserPhotosFragment.newInstance()
                openFragment(photofragment)
                return
            }
            R.id.navigation_frnd -> {
                val frindsfragment = UserFriendsFragment.newInstance()
                openFragment(frindsfragment)
                return
            }
            R.id.navigation_noti -> {
                val notifragment = UserNotificationFragment.newInstance()
                openFragment(notifragment)
                return
            }
        }


    }

    private fun openFragment(fragment: Fragment) {

        if (fragment == null)
            return
        if (fragmentManager != null) {
            val ft = fragmentManager!!.beginTransaction()
            if (ft != null) {
                ft.replace(R.id.fragmentView, fragment)
                ft.commit()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@UserSocialActivity, MainActivity::class.java))
    }

}
