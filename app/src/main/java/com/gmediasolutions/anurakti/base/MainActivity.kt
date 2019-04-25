package com.gmediasolutions.anurakti.base

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.b2b.B2BActivity
import com.gmediasolutions.anurakti.blogger.BlogsActivity
import com.gmediasolutions.anurakti.fragment.MainFragment
import com.gmediasolutions.anurakti.helpsupport.HelpSupportActivity
import com.gmediasolutions.anurakti.newsevent.NewsActivity
import com.gmediasolutions.anurakti.socialmedia.UserSocialActivity
import com.gmediasolutions.anurakti.vendor.GetAllVendorsActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_toolbar.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setuptoolbar()
        setupFragment()

        nav_view.setNavigationItemSelectedListener(this)


    }

    private fun setuptoolbar() {
         setSupportActionBar(my_toolbar)

        my_toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_material)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, my_toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
    }

    fun setupFragment(){
        val fragmentManager = supportFragmentManager
        val fragment = MainFragment()
        fragmentManager.beginTransaction().replace(R.id.first_container, fragment).commit()

    }
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
            finish()
            moveTaskToBack(true)
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_profile -> {
                startActivity(Intent(this@MainActivity, UserSocialActivity::class.java))
            }
            R.id.nav_blog -> {
                startActivity(Intent(this@MainActivity, BlogsActivity::class.java))
            }
            R.id.nav_news -> {
                startActivity(Intent(this@MainActivity, NewsActivity::class.java))
            }
            R.id.nav_vendor -> {
                startActivity(Intent(this@MainActivity, GetAllVendorsActivity::class.java))
            }
            R.id.nav_b2b -> {
                startActivity(Intent(this@MainActivity, B2BActivity::class.java))
            }
            R.id.nav_Help -> {
                startActivity(Intent(this@MainActivity, HelpSupportActivity::class.java))
            }

        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
