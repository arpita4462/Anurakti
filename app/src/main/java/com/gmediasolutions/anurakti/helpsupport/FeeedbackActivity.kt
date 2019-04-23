package com.gmediasolutions.anurakti.helpsupport

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.CardView
import android.view.View
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.base.MainActivity
import com.gmediasolutions.anurakti.R
import kotlinx.android.synthetic.main.custom_toolbar.*

class FeeedbackActivity : BaseActivity() {
    var listState: Parcelable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        setupToolbar()
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


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable("LIST_STATE_KEY ", listState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        listState = savedInstanceState!!.getParcelable("LIST_STATE_KEY")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@FeeedbackActivity, HelpSupportActivity::class.java))
    }
}