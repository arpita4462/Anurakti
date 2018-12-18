package com.gmediasolutions.anurakti.newsevent

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.Base64
import android.view.View
import android.widget.*
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.base.MainActivity
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.model.NewsModel.SpecificNewsModelData
import kotlinx.android.synthetic.main.activity_news_registration.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.io.ByteArrayOutputStream

class NewsRegistrationActivity : BaseActivity() {

    private var newsHeading: String? = null
    private var newsCategory: String? = null
    private var newsContent1: String? = null
    private var newsImage: String? = null
    private var newsImageUri: Uri? = null
    private var base64img: String? = null

    private val SELECT_GALLERY = 200

    private var spinner_news: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_registration)

        setupToolbar()

        spinner_news = findViewById(R.id.spinner_news)

        val adpter_ne = ArrayAdapter.createFromResource(this, R.array.ne_arrays, android.R.layout.simple_spinner_item)

        adpter_ne.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_news!!.adapter = adpter_ne

        spinner_news!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                if (view != null) {
                    if (adapterView.selectedItemPosition == 0) {
                        (adapterView.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.hintcolor
                            )
                        )
                    } else {
                        (adapterView.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.black
                            )
                        )

                    }
                    newsCategory = adapterView.getItemAtPosition(i).toString()
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
            }
        }

        ne_displayiv.setOnClickListener {
            val updloadimg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(updloadimg, SELECT_GALLERY)
        }

        ne_submit.setOnClickListener {


            newsHeading = ne_heading.text.toString()
            newsContent1 = content1.text.toString()
//            newsContent2 = content2.text.toString()

            if (isValidate()) {
                if (newsImage != null) {
                    val bm = BitmapFactory.decodeFile(newsImage)
                    val bao = ByteArrayOutputStream()
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bao)
                    val byte = bao.toByteArray()
                    base64img = Base64.encodeToString(byte, Base64.NO_WRAP)

                    if (base64img != null) {
//                        val savenewsdata =
//                            SpecificNewsModelData(newsCategory!!, newsHeading!!, base64img!!, newsContent1!!, "")
//                        saveindatabase(savenewsdata)
                    } else {
                        Toast.makeText(applicationContext, "Upload Image", Toast.LENGTH_SHORT).show()
                    }
                } else {
//                    val savenewsdata = SpecificNewsModelData(newsCategory!!, newsHeading!!, "", newsContent1!!, "")
//                    saveindatabase(savenewsdata)

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            newsImageUri = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = getContentResolver().query(newsImageUri!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            newsImage = cursor.getString(columnIndex)
            cursor.close()

            val photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), newsImageUri)
            ne_displayiv!!.setImageBitmap(photoBitmap)
        }
    }

/*
    private fun saveindatabase(savenews: SpecificNewsModelData) {
        spotdialog!!.show()
        val requestBody = HashMap<String, SpecificNewsModelData>()
        requestBody.clear()
        requestBody.put("data", savenews)
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
        val postUser = apiServiceuser.addNews(requestBody)

        postUser.enqueue(object : Callback<ApiReturn> {

            override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                spotdialog!!.dismiss()
                Toast.makeText(this@NewsRegistrationActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                if (response.code() == 401) {
                    spotdialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@NewsRegistrationActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@NewsRegistrationActivity, LoginActivity::class.java))
                    finish()
                } else {
                    if (response.isSuccessful) {
                        spotdialog!!.dismiss()
                        cleartext()
                        val intenttem = Intent(this@NewsRegistrationActivity, NewsEventActivity::class.java)
                        startActivity(intenttem)
                        Toast.makeText(this@NewsRegistrationActivity, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                    } else {
                        spotdialog!!.dismiss()
                        Toast.makeText(this@NewsRegistrationActivity, "Uploading Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })

    }
*/

    private fun isValidate(): Boolean {
        if (ne_heading.getText().toString().trim().length < 1) {
            ne_heading.error = getString(R.string.ErrorField)
            ne_heading.requestFocus()
            return false

        } else if (content1.getText().toString().trim().length < 1) {
            content1.error = getString(R.string.ErrorField)
            content1.requestFocus()
            return false

        } else if (spinner_news!!.equals("Select Category")) {
            Toast.makeText(this@NewsRegistrationActivity, "Select Category", Toast.LENGTH_LONG).show()
            return false
        } else
            return true

    }

    fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun cleartext() {

        ne_heading.setText("")
        content1.setText("")
//        content2.setText("")
        ne_displayiv.setImageBitmap(null)
    }

}
