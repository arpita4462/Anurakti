package com.gmediasolutions.anurakti.newsevent

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
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
import com.gmediasolutions.anurakti.model.ApiReturn
import com.gmediasolutions.anurakti.model.NewsModel.SpecificNewsModel
import kotlinx.android.synthetic.main.activity_specific_news_event.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class SpecificNewsEventActivity : BaseActivity() {

    private var getnewsId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specific_news_event)

//        ne_delete.visibility = View.GONE
        ne_imgone.visibility = View.GONE
        getnewsId = intent.getStringExtra("news_id")

        setupToolbar()
        getspecificnews(getnewsId)

//        ne_delete.setOnClickListener {
//                        deletePost()
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

    private fun getspecificnews(getnewsId: String?) {
        spotDialog!!.show()
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                return chain.proceed(newRequest)
            }

        }).build()
        val retrofitobject = Retrofit.Builder().client(client).baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create()).build()
        val apiService = retrofitobject.create(ApiInterface::class.java)
        val call = apiService.getNewsbyId(getnewsId!!)
        call.enqueue(object : Callback<SpecificNewsModel> {
            override fun onFailure(call: Call<SpecificNewsModel>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<SpecificNewsModel>, response: Response<SpecificNewsModel>) {
                if (response.code() == 401) {
                    spotDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@SpecificNewsEventActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@SpecificNewsEventActivity, LoginActivity::class.java))
                    finish()
                } else {
                    spotDialog!!.dismiss()
                    val mnews = response.body()

                    ne_catone.text = mnews!!.data.category
                    ne_headone.text = mnews.data.heading
                    if (mnews.data.photos.equals("") || mnews.data.photos==null) {
                        ne_imgone.visibility = View.GONE
                    } else {
                        ne_imgone.visibility = View.VISIBLE
                        Glide.with(this@SpecificNewsEventActivity).load(mnews.data.photos)
                            .centerCrop()
                            .into(ne_imgone)
                    }
                    ne_context1.text = mnews.data.content1
                    ne_context2.text = mnews.data.content2

                }
            }
        })
    }


//    private fun deletePost() {
//        spotDialog!!.show()
//        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
//            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
//                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
//                return chain.proceed(newRequest)
//            }
//
//        }).build()
//        val retrofituser = Retrofit.Builder().client(client).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())
//                .baseUrl(getString(R.string.base_url))
//                .build()
//        val apiServiceuser = retrofituser.create(ApiInterface::class.java)
//        val postUser = apiServiceuser.deleteNews(getnewsId!!)
//        postUser.enqueue(object : Callback<ApiReturn> {
//
//            override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
//                spotDialog!!.dismiss()
//                Toast.makeText(this@SpecificNewsEventActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
//                if (response.code() == 401) {
//                    spotDialog!!.dismiss()
//                    session!!.logoutUser()
//                    Toast.makeText(this@SpecificNewsEventActivity, "Session Out", Toast.LENGTH_LONG).show()
//                    startActivity(Intent(this@SpecificNewsEventActivity, LoginActivity::class.java))
//                    finish()
//                } else {
//                    if (response.isSuccessful) {
//                        spotDialog!!.dismiss()
//                        startActivity(Intent(this@SpecificNewsEventActivity, NewsEventActivity::class.java))
//                        Toast.makeText(this@SpecificNewsEventActivity, "Successfully Deleted", Toast.LENGTH_SHORT).show()
//                    } else {
//                        spotDialog!!.dismiss()
//                        Toast.makeText(this@SpecificNewsEventActivity, "Deleting Error", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//
//        })
//
//    }


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

}

