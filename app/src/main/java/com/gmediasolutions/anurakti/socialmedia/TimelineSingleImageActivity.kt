package com.gmediasolutions.anurakti.socialmedia

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.*
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.adapter.TimelineSingleImageAdapter
import com.gmediasolutions.anurakti.alert.AVLoadingDialog
import com.gmediasolutions.anurakti.alert.NetworkStateReceiver
import com.gmediasolutions.anurakti.alert.SessionManagment
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.base.MainActivity
import com.gmediasolutions.anurakti.model.ApiReturn
import com.gmediasolutions.anurakti.model.UserSocialModel.TimelineCommentModel
import com.gmediasolutions.anurakti.model.UserSocialModel.TimelineCommentModelData
import com.gmediasolutions.anurakti.model.UserSocialModel.TimelineCommentRequest
import kotlinx.android.synthetic.main.activity_timeline_single_image.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class TimelineSingleImageActivity : BaseActivity() {

    private var editComment: String? = null
    private var commentRecycleView: RecyclerView? = null
    private var commentList: MutableList<TimelineCommentModelData>? = null

    private var commentadapter: TimelineSingleImageAdapter? = null
    private var gettimelineId: String? = null
    private var gettimelinetext: String? = null
    private var gettimelineImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_single_image)

//        initialize the layout view
        commentRecycleView = findViewById(R.id.comments_recycleView)

//        get intent value
        gettimelineId = intent.getStringExtra("timeline_id")
//        gettimelineId = intent.getStringExtra("pic_id")
        gettimelinetext = intent.getStringExtra("timeline_text")
        gettimelineImage = intent.getStringExtra("timeline_img")

        if (gettimelinetext != null) {
            gettimelinetext = ""
        }

        setupToolbar()
        setupRecyclerView()

        send_comment.setOnClickListener {

            val connMgr = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            if (networkInfo == null) {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
            } else {
                editComment = et_post_one_view.text.toString().trim()

                if (TextUtils.isEmpty(editComment)) {
                    et_post_one_view.error = getString(R.string.ErrorField)
                    et_post_one_view.requestFocus()
                } else {
                    val savecomment = TimelineCommentRequest(gettimelineId!!, user_id!!, editComment!!)
                    saveindatabase(savecomment)
                    et_post_one_view.setText("")
                    commentadapter!!.notifyDataSetChanged()
                    getpiccomment(gettimelineId)
                }


            }
        }


    }

    private fun setupRecyclerView() {
        commentList = ArrayList()

        commentadapter = TimelineSingleImageAdapter(
            commentList!!,
            R.layout.comment_rv_view,
            this@TimelineSingleImageActivity,
            user_id!!,
            user_token!!,
            gettimelineId!!,
            gettimelinetext!!,
            gettimelineImage!!
        )
        commentRecycleView!!.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        commentRecycleView!!.setHasFixedSize(true)
        commentRecycleView!!.adapter = commentadapter

        getpiccomment(gettimelineId)
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


    private fun getpiccomment(gettimelineId: String?) {
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
        val call = apiService.getTimelineComment(gettimelineId!!)
        call.enqueue(object : Callback<TimelineCommentModel> {
            override fun onFailure(call: Call<TimelineCommentModel>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(this@TimelineSingleImageActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<TimelineCommentModel>, response: Response<TimelineCommentModel>) {
                if (response.code() == 401) {
                    spotDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@TimelineSingleImageActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@TimelineSingleImageActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val picdata = response.body()
                    if (picdata != null) {
                        spotDialog!!.dismiss()
                        if (true) {
                            timeline_ProgressBar.visibility = View.GONE
                            post_error_text1.visibility = View.GONE
                            tv_text_one_view.text = gettimelinetext
                            Glide.with(this@TimelineSingleImageActivity)
                                .load(gettimelineImage)
                                .centerCrop()
                                .into(image_one_iv)

                            if (commentRecycleView != null) {
                                commentRecycleView!!.adapter = TimelineSingleImageAdapter(
                                    picdata.data,
                                    R.layout.comment_rv_view,
                                    this@TimelineSingleImageActivity,
                                    user_id!!,
                                    user_token!!,
                                    gettimelineId,
                                    gettimelinetext!!,
                                    gettimelineImage!!
                                )
                            } else {
                                timeline_ProgressBar.visibility = View.GONE
                                post_error_text1.visibility = View.VISIBLE
                                post_error_text1.text = getString(R.string.no_comment)
                            }
                        } else {
                            Toast.makeText(this@TimelineSingleImageActivity, "Loading Error", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }
        })
    }


    private fun saveindatabase(savecomment: TimelineCommentRequest) {
        spotDialog!!.show()
        val requestBody = HashMap<String, TimelineCommentRequest>()
        requestBody.clear()
        requestBody.put("data", savecomment)
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                return chain.proceed(newRequest)
            }

        }).build()
        val retrofituser = Retrofit.Builder().client(client).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.base_url))
                .build()
        val apiServiceuser = retrofituser.create(ApiInterface::class.java)
        val postUser = apiServiceuser.timelineComment(requestBody)
        postUser.enqueue(object : Callback<ApiReturn> {

            override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(this@TimelineSingleImageActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                if (response.code() == 401) {
                    spotDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@TimelineSingleImageActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@TimelineSingleImageActivity, LoginActivity::class.java))
                    finish()
                } else {
                    if (response.isSuccessful) {
                    spotDialog!!.dismiss()
                } else {
                    spotDialog!!.dismiss()
                }

            }}

        })

    }



    override fun onSaveInstanceState(outState: Bundle?) {

        outState!!.putString("KeyFrndId", gettimelineId)
        outState.putString("KeyFrndText", gettimelinetext)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        gettimelineId = savedInstanceState!!.getString("KeyFrndId")
        gettimelinetext = savedInstanceState.getString("KeyFrndText")

    }
}

