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
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.gmediasolutions.anurakti.adapter.VendorRVAdapter
import com.gmediasolutions.anurakti.b2b.B2BActivity
import com.gmediasolutions.anurakti.blogger.BloggerActivity
import com.gmediasolutions.anurakti.careerandtalent.CandTActivity
import com.gmediasolutions.anurakti.newsevent.NewsEventActivity
import com.gmediasolutions.anurakti.socialmedia.UserSocialActivity
import com.gmediasolutions.anurakti.vendor.VendorsActivity
import com.willowtreeapps.spruce.Spruce
import com.willowtreeapps.spruce.animation.DefaultAnimations
import com.willowtreeapps.spruce.sort.DefaultSort


class HelpSupportActivity : BaseActivity() {
    var listState: Parcelable? = null
    private var item1Layout: CardView? = null
    private var item2Layout: CardView? = null
    private var item3Layout: CardView? = null
    private var item4Layout: CardView? = null
    private var item5Layout: CardView? = null
    private var item6Layout: CardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_support)

//        initialize the layout ids
        item1Layout = findViewById(R.id.item1)
        item2Layout = findViewById(R.id.item2)
        item3Layout = findViewById(R.id.item3)
        item4Layout = findViewById(R.id.item4)
        item5Layout = findViewById(R.id.item5)
        item6Layout = findViewById(R.id.item6)

        setupToolbar()
        iconOption()
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

    private fun iconOption() {

        YoYo.with(Techniques.StandUp).duration(1000).playOn(item1Layout)
        YoYo.with(Techniques.StandUp).duration(1000).playOn(item2Layout)
        YoYo.with(Techniques.StandUp).duration(1000).playOn(item3Layout)
        YoYo.with(Techniques.StandUp).duration(1000).playOn(item4Layout)
        YoYo.with(Techniques.StandUp).duration(1000).playOn(item5Layout)
        YoYo.with(Techniques.StandUp).duration(1000).playOn(item6Layout)

        item1Layout!!.setOnClickListener {
            startActivity(Intent(this@HelpSupportActivity, UserSocialActivity::class.java))
        }
        item2Layout!!.setOnClickListener {
            startActivity(Intent(this@HelpSupportActivity, FeeedbackActivity::class.java))
        }

    }


    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putString("KeyUserId", user_id)
        outState.putString("KeyUserToken", user_token)
        outState.putParcelable("LIST_STATE_KEY ", listState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        user_id = savedInstanceState!!.getString("KeyUserId")
        user_token = savedInstanceState.getString("KeyUserToken")
        listState = savedInstanceState.getParcelable("LIST_STATE_KEY")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@HelpSupportActivity, MainActivity::class.java))
    }
}