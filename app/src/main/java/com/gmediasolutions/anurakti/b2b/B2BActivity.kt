package com.gmediasolutions.anurakti.b2b

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import android.content.Intent
import android.support.v4.content.res.ResourcesCompat
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.base.MainActivity
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.adapter.B2BTabPageAdapter
import kotlinx.android.synthetic.main.activity_b2b.*
import kotlinx.android.synthetic.main.custom_toolbar.*


class B2BActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b2b)

        setupToolbar()
        setupTabLayout()

    }

    fun setupTabLayout() {
        b2b_tabs.addTab(b2b_tabs.newTab().setText("Products"))
        b2b_tabs.addTab(b2b_tabs.newTab().setText("Property"))
        b2b_tabs.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = B2BTabPageAdapter(supportFragmentManager, b2b_tabs.tabCount)
        b2b_viewpager.adapter = adapter
        b2b_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                b2b_viewpager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        b2b_tabs.setupWithViewPager(this.b2b_viewpager)

        b2b_reg.setOnClickListener {
            startActivity(Intent(this@B2BActivity, B2BRegistrationActivity::class.java))
        }
    }

    fun setupToolbar() {


//      setup toolbar
        if (my_toolbar != null) {
            setSupportActionBar(my_toolbar)

            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            my_toolbar.setNavigationOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            })
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@B2BActivity, MainActivity::class.java))
    }
}
