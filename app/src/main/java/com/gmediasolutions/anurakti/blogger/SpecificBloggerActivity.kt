package com.gmediasolutions.anurakti.blogger

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import kotlinx.android.synthetic.main.custom_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.support.v4.app.ActivityCompat
import android.util.Base64
import com.gmediasolutions.anurakti.model.ApiReturn
import com.gmediasolutions.anurakti.model.BlogModel.SpecificBlogModel
import kotlinx.android.synthetic.main.activity_specific_blogger.*
import okhttp3.*
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.ByteArrayOutputStream
import java.io.File


class SpecificBloggerActivity : BaseActivity() {

    private var getblogId: Int? = null
    private var current_user_id: String? = null
    private val SELECT_GALLERY = 300
    private var blogImage: String? = null
    private var blogImageUri: Uri? = null
    private var base64img: String? = null
    private var bitmapimage: Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specific_blogger)

        blog_delete.visibility=View.GONE
        blog_edits.visibility=View.GONE
        blog_img.visibility=View.GONE
        getblogId= intent.getIntExtra("blog_id",0)

        fieldsenable(false)

        setupToolbar()
        getspecificblog(getblogId)


        blog_uploadimgs.setOnClickListener {
            val updloadimg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(updloadimg, SELECT_GALLERY)
        }


        blog_edits.setOnClickListener {
            fieldsenable(true)
            blog_uploadimgs.visibility = View.VISIBLE
            blog_submits.visibility = View.VISIBLE

        }

        blog_delete.setOnClickListener{
            deletePost()
        }

        blog_submits.setOnClickListener {

            val connMgr = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            if (networkInfo == null) {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
            } else {

                    if (bitmapimage != null) {
                        val bao = ByteArrayOutputStream()
                        bitmapimage!!.compress(Bitmap.CompressFormat.JPEG, 90, bao)
                        val byte = bao.toByteArray()
                        base64img = Base64.encodeToString(byte, Base64.NO_WRAP)

                        updateblogtoDB()
                    } else {
                        Toast.makeText(this, "Upload Image", Toast.LENGTH_SHORT).show()

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

    private fun getspecificblog(getblogId: Int?) {
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
        val call = apiService.getBlogbyId(getblogId!!)
        call.enqueue(object : Callback<SpecificBlogModel> {
            override fun onFailure(call: Call<SpecificBlogModel>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<SpecificBlogModel>, response: Response<SpecificBlogModel>) {
                if (response.code() == 401) {
                    spotDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@SpecificBloggerActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@SpecificBloggerActivity, LoginActivity::class.java))
                    finish()
                } else {
                    spotDialog!!.dismiss()
                    val blog = response.body()
                    current_user_id = blog!!.data.blog.user_id.toString()
                    if (user_id.equals(current_user_id)) {
                        blog_edits.visibility = View.VISIBLE
                        blog_delete.visibility = View.VISIBLE
                    } else {
                        blog_edits.visibility = View.GONE
                        blog_delete.visibility = View.GONE
                    }
                    tv_blogUser.text =blog!!.data.blog.user!!.first_name + " " + blog!!.data.blog.user!!.last_name
                    tv_blogTime.text =blog!!.data.blog.updated_at
                    blog_headline_tv.setText(blog!!.data.blog.subject.toString())
                    blog_context1.setText(blog!!.data.blog.body)
                    Glide.with(this@SpecificBloggerActivity).load(blog.data.blog.user!!.user_pic_id)
                        .centerCrop()
                        .into(user_pic)

                    if (blog!!.data.blog.pic.equals("")) {
                        blog_img.visibility = View.GONE
                    } else {
                        blog_img.visibility = View.VISIBLE
                        Glide.with(this@SpecificBloggerActivity).load(blog.data.blog.pic)
                            .centerCrop()
                            .into(blog_img)
                    }

                }
            }
        })
    }

    private fun updateblogtoDB() {
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
        val file = File(blogImage)
        val fileReqBody = RequestBody.create(MediaType.parse("image/*"), file)
        val part = MultipartBody.Part.createFormData("pic", file.getName(), fileReqBody)
        val postTitile = RequestBody.create(MediaType.parse("text/plain"), blog_headline_tv.text.toString())
        val postBody = RequestBody.create(MediaType.parse("text/plain"), blog_context1.text.toString())

        val postUser = apiServiceuser.updateBlog(user_id!!,part!!, postTitile!!, postBody!!)

        postUser.enqueue(object : Callback<ApiReturn> {

            override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(this@SpecificBloggerActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                if (response.code() == 401) {
                    spotDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@SpecificBloggerActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@SpecificBloggerActivity, LoginActivity::class.java))
                    finish()
                } else {
                    if (response.isSuccessful) {
                        spotDialog!!.dismiss()
                        startActivity(Intent(this@SpecificBloggerActivity, BlogsActivity::class.java))
                        Toast.makeText(this@SpecificBloggerActivity, "Successfully Upload", Toast.LENGTH_SHORT).show()
                    } else {
                        spotDialog!!.dismiss()
                        Toast.makeText(this@SpecificBloggerActivity, "Uploading Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            blogImageUri = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = getContentResolver().query(blogImageUri!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            blogImage = cursor.getString(columnIndex)
            cursor.close()

            val photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), blogImageUri)
            blog_img!!.setImageBitmap(photoBitmap)

            bitmapimage = BitmapFactory.decodeFile(blogImage)
        }
    }

    private fun deletePost() {
        spotDialog!!.show()
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
        val postUser = apiServiceuser.deleteBlogbyId()
        postUser.enqueue(object : Callback<ApiReturn> {

            override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(this@SpecificBloggerActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                if (response.code() == 401) {
                    spotDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@SpecificBloggerActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@SpecificBloggerActivity, LoginActivity::class.java))
                    finish()
                } else {
                    if (response.isSuccessful) {
                        spotDialog!!.dismiss()
                        startActivity(Intent(this@SpecificBloggerActivity, BlogsActivity::class.java))
                        Toast.makeText(this@SpecificBloggerActivity, "Successfully Deleted", Toast.LENGTH_SHORT).show()
                    } else {
                        spotDialog!!.dismiss()
                        Toast.makeText(this@SpecificBloggerActivity, "Deleting Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })

    }

    private fun fieldsenable(isEnabled: Boolean) {
        blog_headline_tv.isEnabled = isEnabled
        blog_context1.isEnabled = isEnabled
        blog_img.isEnabled = isEnabled
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putInt("KeySearch", getblogId!!)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        getblogId = savedInstanceState!!.getInt("KeySearch")

    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityCompat.finishAfterTransition(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }
}

