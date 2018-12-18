package com.gmediasolutions.anurakti.blogger

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.base.MainActivity
import com.gmediasolutions.anurakti.R
import kotlinx.android.synthetic.main.custom_toolbar.*
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.gmediasolutions.anurakti.adapter.BloggerRVAdapter
import com.gmediasolutions.anurakti.adapter.VendorRVAdapter
import com.willowtreeapps.spruce.Spruce
import com.willowtreeapps.spruce.animation.DefaultAnimations
import com.willowtreeapps.spruce.sort.DefaultSort
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.CardView
import android.widget.TextView
import kotlinx.android.synthetic.main.blog_rv_view.*
import android.support.v4.util.Pair
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_blog.*
import android.view.animation.AnimationUtils.loadLayoutAnimation
import android.view.animation.LayoutAnimationController
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.activity_news_event.*


class BloggerActivity : BaseActivity(), BloggerRVAdapter.ItemClickListener {


    var listState: Parcelable? = null
    var adapter: BloggerRVAdapter? = null
    var recyclerView: RecyclerView? = null
    private var spruceAnimator: Animator? = null
    val data = arrayOf(
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
        "19",
        "20",
        "21",
        "22",
        "23",
        "27",
        "28",
        "29",
        "30",
        "32",
        "33",
        "34",
        "35",
        "37",
        "39",
        "40",
        "41",
        "42",
        "43",
        "44",
        "45",
        "46",
        "47",
        "48"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog)

        // set up the RecyclerView
        recyclerView = findViewById(R.id.blog_recycleView)

        setupToolbar()
//        setupRecycleView()
        setupAnimatedRecycleView()

        blog_add.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@BloggerActivity, AddBlogActivity::class.java))
        })

    }


    private fun setupAnimatedRecycleView() {
        val resId = R.anim.layout_animation_from_right
        val animation = AnimationUtils.loadLayoutAnimation(applicationContext, resId)
        recyclerView!!.setLayoutAnimation(animation)

        recyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        recyclerView!!.setHasFixedSize(true)
        adapter = BloggerRVAdapter(this, data)
        recyclerView!!.adapter = adapter
//        adapter!!.notifyDataSetChanged()
    }

    //    function to call again  recyclerview animation(if user wants)
    private fun runLayoutAnimation(recyclerView: RecyclerView) {
        val context = recyclerView.context
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom)

        recyclerView.layoutAnimation = controller
        recyclerView.adapter!!.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
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

    override fun onItemClick(view: View, position: Int) {
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
//        ActivityCompat.finishAfterTransition(this)
        startActivity(Intent(this@BloggerActivity, MainActivity::class.java))
    }
}