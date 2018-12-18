package com.gmediasolutions.anurakti.vendor

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
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Window
import com.gmediasolutions.anurakti.adapter.VendorRVAdapter
import com.gmediasolutions.anurakti.custom.CustomVendorDialog
import com.willowtreeapps.spruce.Spruce
import com.willowtreeapps.spruce.animation.DefaultAnimations
import com.willowtreeapps.spruce.sort.DefaultSort


class VendorsActivity : BaseActivity(), VendorRVAdapter.ItemClickListener {

    var listState: Parcelable? = null
    var adapter: VendorRVAdapter? = null
    var recyclerView: RecyclerView? = null
    private var spruceAnimator: Animator? = null
    private var customVendorDialog: CustomVendorDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendor)

//        set up the RecyclerView
        recyclerView = findViewById(R.id.vendors_recycleView)

        setupToolbar()
        setupRecycleView()

    }


    private fun setupRecycleView() {
        // data to populate the RecyclerView with
        val data = arrayOf(
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17","18","19", "20", "21", "22", "23", "24","25",
            "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38","39", "40", "41", "42", "43", "44", "45"
        )

        val numberOfColumns = 2
//        recyclerView!!.setLayoutManager(GridLayoutManager(this, numberOfColumns))
        val linearLayoutManager = object : GridLayoutManager(this, numberOfColumns) {
            override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
                super.onLayoutChildren(recycler, state)
                initSpruce()
            }
        }
        recyclerView!!.layoutManager = linearLayoutManager
        adapter = VendorRVAdapter(this, data)
        adapter!!.setClickListener(this)
        recyclerView!!.setAdapter(adapter)


    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun initSpruce() {
        spruceAnimator = Spruce.SpruceBuilder(recyclerView)
            .sortWith(DefaultSort(100))
            .animateWith(
                DefaultAnimations.shrinkAnimator(recyclerView, 800),
                ObjectAnimator.ofFloat(
                    recyclerView,
                    "translationX",
                    -recyclerView!!.getWidth().toFloat(),
                    0f
                ).setDuration(800)
            )
            .start()
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
        customVendorDialog = CustomVendorDialog(this)
        customVendorDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customVendorDialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimation
        customVendorDialog!!.setCanceledOnTouchOutside(false)
        customVendorDialog!!.show()
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
        startActivity(Intent(this@VendorsActivity, MainActivity::class.java))
    }
}