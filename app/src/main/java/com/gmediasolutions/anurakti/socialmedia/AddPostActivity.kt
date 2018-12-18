package com.gmediasolutions.anurakti.socialmedia

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.base.MainActivity
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream

class AddPostActivity : BaseActivity() {

    private var postImage: String? = null
    private var postImageUri: Uri? = null
    private var posttext: String? = null

    private var base64img: String? = null
    private val SELECT_GALLERY = 200

    private var current_user_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        setupToolbar()

//        get intent value
        current_user_id = intent.getStringExtra("post_user")

        post_iv.setOnClickListener {
            val updloadimg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(updloadimg, SELECT_GALLERY)
        }

        post_submit.setOnClickListener {

            val connMgr = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            if (networkInfo == null) {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
            } else {

                posttext = et_post.text.toString()

//                    bitmapimage = (post_iv.drawable as BitmapDrawable).bitmap

                if (postImage != null) {
                    val bm = BitmapFactory.decodeFile(postImage)
                    val bao = ByteArrayOutputStream()
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bao)
                    val byte = bao.toByteArray()
                    base64img = Base64.encodeToString(byte, Base64.NO_WRAP)

//                    val update = TimelineRequest(user_id!!, posttext!!, base64img!!)
//                    updatepost(update)

                } else {
                    if (posttext.equals("")) {
                        Toast.makeText(this, "Fill the Details", Toast.LENGTH_LONG).show()
                    } else {
//                        val update = TimelineRequest(user_id!!, posttext!!, "null")
//                        updatepost(update)
                    }
                }
            }
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

//    private fun updatepost(savepost: TimelineRequest) {
//        spotDialog!!.show()
//        val requestBody = HashMap<String, TimelineRequest>()
//        requestBody.clear()
//        requestBody.put("data", savepost)
//
//        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
//            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
//                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
//                return chain.proceed(newRequest)
//            }
//
//        }).build()
//        val retrofituser = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(client)
//            .baseUrl(getString(R.string.base_url))
//            .build()
//        val apiServiceuser = retrofituser.create(ApiInterface::class.java)
//        val postUser = apiServiceuser.addTimeline(requestBody)
//
//        postUser.enqueue(object : Callback<TimelineReturn> {
//
//            override fun onFailure(call: Call<TimelineReturn>, t: Throwable) {
//                spotDialog!!.dismiss()
//                Toast.makeText(this@CreatePost, "No Internet Connection", Toast.LENGTH_SHORT).show()
//
//            }
//
//            override fun onResponse(call: Call<TimelineReturn>, response: Response<TimelineReturn>) {
//                if (response.code() == 401) {
//                    spotDialog!!.dismiss()
//                    session!!.logoutUser()
//                    Toast.makeText(this@CreatePost, "Session Out", Toast.LENGTH_LONG).show()
//                    startActivity(Intent(this@CreatePost, LoginActivity::class.java))
//                    finish()
//                } else {
//                    if (response.isSuccessful) {
//                        spotDialog!!.dismiss()
//                        startActivity(Intent(this@CreatePost, MyProfileActivity::class.java))
//                        Toast.makeText(this@CreatePost, "Successfully Upload", Toast.LENGTH_SHORT).show()
//                    } else {
//                        spotDialog!!.dismiss()
//                        Toast.makeText(this@CreatePost, "Uploading Error", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            }
//        })
//
//    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            postImageUri = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = getContentResolver().query(postImageUri, filePathColumn, null, null, null)
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            postImage = cursor.getString(columnIndex)
            cursor.close()

            val photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), postImageUri)
            post_iv!!.setImageBitmap(photoBitmap)


        }
    }


    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putString("KeyFrndId", current_user_id)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        current_user_id = savedInstanceState!!.getString("KeyFrndId")

    }

}
