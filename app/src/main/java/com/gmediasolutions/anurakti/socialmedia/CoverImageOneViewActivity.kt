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
import com.gmediasolutions.anurakti.adapter.CoverImageOneViewAdapter
import com.gmediasolutions.anurakti.alert.SessionManagment
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.ApiReturn
import com.gmediasolutions.anurakti.model.UserSocialModel.CoverPicCommentModel
import com.gmediasolutions.anurakti.model.UserSocialModel.CoverPicCommentModelData
import com.gmediasolutions.anurakti.model.UserSocialModel.CoverPicCommentRequest
import kotlinx.android.synthetic.main.activity_timeline_single_image.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class CoverImageOneViewActivity : BaseActivity() {


    private var editComment: String? = null
    private var commentRecycleView: RecyclerView? = null
    private var commentList: MutableList<CoverPicCommentModelData>? = null


    private var commentadapter: CoverImageOneViewAdapter?=null
    private var getpicId: String? = null
    private var getpicImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline_single_image)


        // get user data from session
        val loginuser: HashMap<String, String> = session!!.userDetails
        user_id = loginuser.get(SessionManagment.USER_ID)
        user_token = loginuser.get(SessionManagment.USER_TOKEN)

        commentRecycleView = findViewById(R.id.comments_recycleView)

        commentList=ArrayList()


        getpicId = intent.getStringExtra("pic_id")
        getpicImage = intent.getStringExtra("pic_comment")
        commentadapter = CoverImageOneViewAdapter(commentList!!, R.layout.comment_rv_view, applicationContext,user_id!!,user_token!!,getpicId!!,getpicImage!!)

        commentRecycleView!!.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        commentRecycleView!!.setHasFixedSize(true)
        commentRecycleView!!.adapter = commentadapter

        getpiccomment(getpicId)

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
                }else{
                    val savecomment = CoverPicCommentRequest( getpicId!!,user_id!!, editComment!!)
                    saveindatabase(savecomment)
                    et_post_one_view.setText("")
                    commentadapter!!.notifyDataSetChanged()
                    getpiccomment(getpicId)
                }


            }
        }


    }

    private fun getpiccomment(getpicId: String?) {
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                return chain.proceed(newRequest)
            }

        }).build()
        val retrofitobject = Retrofit.Builder().client(client).baseUrl(getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build()
        val apiService = retrofitobject.create(ApiInterface::class.java)
        val call = apiService.getCoverPicComment(getpicId!!)
        call.enqueue(object : Callback<CoverPicCommentModel> {
            override fun onFailure(call: Call<CoverPicCommentModel>, t: Throwable) {
                Toast.makeText(this@CoverImageOneViewActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<CoverPicCommentModel>, response: Response<CoverPicCommentModel>) {
                if (response.code() == 401) {
                    session!!.logoutUser()
                    Toast.makeText(this@CoverImageOneViewActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@CoverImageOneViewActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val picdata = response.body()
                    if (picdata != null) {
                        if (true) {
                            timeline_ProgressBar.visibility = View.GONE
                            Glide.with(this@CoverImageOneViewActivity)
                                    .load(getpicImage)
                                .placeholder(R.drawable.noimage)
                                .thumbnail(0.5f)
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(image_one_iv)

                            if (commentRecycleView != null) {
                                commentRecycleView!!.adapter = CoverImageOneViewAdapter(picdata.data, R.layout.comment_rv_view, this@CoverImageOneViewActivity, user_id!!, user_token!!, getpicId, getpicImage!!)
                            } else {
                                timeline_ProgressBar.visibility = View.GONE
                                post_error_text1.visibility = View.VISIBLE
                                post_error_text1.text = getString(R.string.no_comment)
                            }
                        } else {
                            Toast.makeText(this@CoverImageOneViewActivity, "Loading Error", Toast.LENGTH_SHORT).show()

                        }
                    }
                }
            }
        })
    }

    private fun saveindatabase(savecomment: CoverPicCommentRequest) {
        val requestBody = HashMap<String, CoverPicCommentRequest>()
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
        val postUser = apiServiceuser.coverPicComment(requestBody)
        postUser.enqueue(object : Callback<ApiReturn> {

            override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                Toast.makeText(this@CoverImageOneViewActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                if (response.code() == 401) {
                    session!!.logoutUser()
                    Toast.makeText(this@CoverImageOneViewActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@CoverImageOneViewActivity, LoginActivity::class.java))
                    finish()
                } else {
                    if (response.isSuccessful) {
                } else {
                    Toast.makeText(this@CoverImageOneViewActivity, "Uploading Error", Toast.LENGTH_SHORT).show()
                }

            }
        }
        })

    }




    /*Checking Internet Connection and Showing Message*/
    private fun showSnack(isConnected: String) {
        val message: String
        val color: Int
        if (isConnected.equals("true")) {

        } else {
            message = getString(R.string.sorry_nointernet)
            color = Color.RED
            val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
            val sbView = snackbar.view
            val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
            textView.setTextColor(color)
            snackbar.show()
        }


    }

    override fun networkAvailable() {
        getpiccomment(getpicId)
        showSnack("true")
    }

    override fun networkUnavailable() {
        showSnack("false")
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putString("KeyUserId", user_id)
        outState.putString("KeyUserToken", user_token)
        outState.putString("KeyFrndId", getpicId)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        user_id = savedInstanceState!!.getString("KeyUserId")
        user_token = savedInstanceState.getString("KeyUserToken")
        getpicId = savedInstanceState.getString("KeyFrndId")

    }


}

