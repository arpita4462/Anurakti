package com.gmediasolutions.anurakti.blogger

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v7.widget.CardView
import android.util.Base64
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.base.MainActivity
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.ApiReturn
import com.gmediasolutions.anurakti.model.BlogModel.AddBlogRequest
import com.gmediasolutions.anurakti.model.BlogModel.AddBlogResponse
import com.gmediasolutions.anurakti.model.BlogModel.AddBlogResponseData
import com.gmediasolutions.anurakti.model.UserSocialModel.TimelineReturn
import kotlinx.android.synthetic.main.activity_add_post.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File


class AddBlogActivity : BaseActivity() {

    private var postTitile: String? = null
    private var postBody: String? = null
    private var postImage: String? = null
    private var postImageUri: Uri? = null
//    private var posttext: String? = null

    private var base64img: String? = null
    private val SELECT_GALLERY = 200

    private var current_user_id: String? = null

    @SuppressLint("MissingPermission")
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
        post_image.setOnClickListener {
            val updloadimg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(updloadimg, SELECT_GALLERY)
        }

        post_submit.setOnClickListener {

//            val connMgr = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//            val networkInfo = connMgr.activeNetworkInfo
//            if (networkInfo == null) {
//                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
//            } else {

                postBody = et_post.text.toString()
                postTitile = et_postTitle.text.toString()

//                    bitmapimage = (post_iv.drawable as BitmapDrawable).bitmap

                if (postImage != null) {
                    val bm = BitmapFactory.decodeFile(postImage)
                    val bao = ByteArrayOutputStream()
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bao)
                    val byte = bao.toByteArray()
                    base64img = Base64.encodeToString(byte, Base64.NO_WRAP)

                    val addblog = AddBlogRequest(base64img!!, postTitile!!, postBody!!)
                    addblogtoDB(addblog)

                } else {
                    if (postTitile.equals("")) {
                        Toast.makeText(this, "Fill the Details", Toast.LENGTH_LONG).show()
                    } else {
                        val addblog = AddBlogRequest(base64img!!, postTitile!!, postBody!!)
                        addblogtoDB(addblog)
                    }
//                }
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


    private fun addblogtoDB(savepost: AddBlogRequest) {
        spotDialog!!.show()
//        val requestBody = HashMap<String, AddBlogRequest>()
//        requestBody.clear()
//        requestBody.put("data", savepost)

        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                return chain.proceed(newRequest)
            }

        }).build()
        val retrofituser = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(getString(R.string.base_url))
            .build()
        val apiServiceuser = retrofituser.create(ApiInterface::class.java)
        val file = File(postImage)
        val fileReqBody = RequestBody.create(MediaType.parse("image/*"), file)
        val part = MultipartBody.Part.createFormData("pic", file.getName(), fileReqBody)
        val postTitile = RequestBody.create(MediaType.parse("text/plain"), postTitile)
        val postBody = RequestBody.create(MediaType.parse("text/plain"), postBody)

        val postUser = apiServiceuser.addBlog(part!!, postTitile!!, postBody!!)

        postUser.enqueue(object : Callback<AddBlogResponse> {

            override fun onFailure(call: Call<AddBlogResponse>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(this@AddBlogActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<AddBlogResponse>, response: Response<AddBlogResponse>) {
                if (response.code() == 401) {
                    spotDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@AddBlogActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@AddBlogActivity, LoginActivity::class.java))
                    finish()
                } else {
                    if (response.isSuccessful) {
                        spotDialog!!.dismiss()
                        startActivity(Intent(this@AddBlogActivity, BlogsActivity::class.java))
                        Toast.makeText(this@AddBlogActivity, "Successfully Upload,Wait for admin approve", Toast.LENGTH_SHORT).show()
                    } else {
                        spotDialog!!.dismiss()
                        Toast.makeText(this@AddBlogActivity, "Uploading Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

    }


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
