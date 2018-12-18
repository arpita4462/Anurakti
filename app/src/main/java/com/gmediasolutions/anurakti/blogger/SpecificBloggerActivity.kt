package com.gmediasolutions.anurakti.blogger

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.base.MainActivity
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.NewsModel.SpecificNewsModel
import kotlinx.android.synthetic.main.activity_specific_news_event.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.support.v4.app.ActivityCompat


class SpecificBloggerActivity : BaseActivity() {

    private var getnewsId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specific_blogger)

//        ne_delete.visibility=View.GONE
//        ne_imgone.visibility=View.GONE
//        getnewsId= intent.getStringExtra("news_id")

        setupToolbar()
//        getspecificnews(getnewsId)

//        ne_delete.setOnClickListener{
////            deletePost()
//        }

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
        outState!!.putString("KeySearch", getnewsId!!)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        getnewsId = savedInstanceState!!.getString("KeySearch")

    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCompat.finishAfterTransition(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }
}

