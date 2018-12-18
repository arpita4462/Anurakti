package com.gmediasolutions.anurakti.careerandtalent

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.base.MainActivity
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.adapter.CandTTabPageAdapter
import com.gmediasolutions.anurakti.alert.AVLoadingDialog
import kotlinx.android.synthetic.main.activity_candt.*
import kotlinx.android.synthetic.main.custom_toolbar.*


class CandTActivity : BaseActivity() {

    private var spotdialog: AVLoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candt)

        setupToolbar()
        setupTabLayout()
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

    // setup tab layout
    fun setupTabLayout() {
        candt_tabs.addTab(candt_tabs.newTab().setText("Student"))
        candt_tabs.addTab(candt_tabs.newTab().setText("Company"))
        candt_tabs.setTabGravity(TabLayout.GRAVITY_FILL)

        val adapter = CandTTabPageAdapter(supportFragmentManager, candt_tabs.getTabCount())
        candt_viewpager.setAdapter(adapter)
        candt_tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                candt_viewpager.setCurrentItem(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        candt_tabs.setupWithViewPager(this.candt_viewpager)


        candt_reg.setOnClickListener {
            startActivity(Intent(this@CandTActivity, CandTRegistrationActivity::class.java))

        }
    }

}