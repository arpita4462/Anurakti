package com.gmediasolutions.anurakti.b2b

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
import android.util.Base64
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.base.MainActivity
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.ApiReturn
import com.gmediasolutions.anurakti.model.B2BModel.B2BRequest
import com.gmediasolutions.anurakti.model.B2BModel.SpecificB2BModel
import kotlinx.android.synthetic.main.activity_specific_b2b.*
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

class SpecificB2BActivity : BaseActivity() {

    private var b2bProductName: String? = null
    private var b2bName: String? = null
    private var b2bemail_id: String? = null
    private var b2bContent: String? = null
    private var b2bContact: String? = null
    private var areaDesc: String? = null
    private var b2bType: String? = null
    private var b2baddress: String? = null
    private var b2bquantity: String? = null
    private var b2bImage: String? = null
    private var b2bImageUri: Uri? = null
    private var base64img: String? = null
    private var bitmapimage: Bitmap? = null
    private val SELECT_GALLERY = 300
    private var getb2bId: String? = null
    private var current_user_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specific_b2b)

        setupToolbar()
//        get intent value
        getb2bId = intent.getStringExtra("product_id")
        b2bType = intent.getStringExtra("product_type")

        b2b_submits.visibility = View.GONE
        b2b_uploadimgs.visibility = View.GONE

        fieldsenable(false)
        getspecificb2b(getb2bId)

        b2b_edits.setOnClickListener {
            fieldsenable(true)
            b2b_uploadimgs.visibility = View.VISIBLE
            b2b_submits.visibility = View.VISIBLE

        }

        b2b_uploadimgs.setOnClickListener {
            val updloadimg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(updloadimg, SELECT_GALLERY)
        }

        b2b_delete.setOnClickListener {
            deletePost()
        }

        b2b_submits.setOnClickListener {

            val connMgr = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            if (networkInfo == null) {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
            } else {
                if (isValidate()) {
                    b2bProductName = b2b_productnames.text.toString().trim()
                    b2bName = b2b_fullnames.text.toString().trim()
                    b2bContact = b2b_contactss.text.toString().trim()
                    b2bemail_id = b2b_emailids.text.toString().trim()
                    b2baddress = b2b_adds.text.toString().trim()
                    b2bquantity = b2b_quantitys.text.toString().trim()
                    b2bContent = b2b_details.text.toString().trim()
                    areaDesc = b2b_areaDescs.text.toString().trim()
                    bitmapimage = (b2b_displayivs.drawable as BitmapDrawable).bitmap

                    if (bitmapimage != null) {
                        val bao = ByteArrayOutputStream()
                        bitmapimage!!.compress(Bitmap.CompressFormat.JPEG, 90, bao)
                        val byte = bao.toByteArray()
                        base64img = Base64.encodeToString(byte, Base64.NO_WRAP)

                        val saveb2bdata = B2BRequest(user_id!!, b2bProductName!!, b2bName!!, b2bContact!!, b2bemail_id!!, base64img!!, b2bContent!!,areaDesc!!, b2baddress!!, b2bType!!, b2bquantity!!)
                        updateindatabase(saveb2bdata)
                    } else {
                        Toast.makeText(this, "Upload Image", Toast.LENGTH_SHORT).show()

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


    private fun getspecificb2b(getb2bId: String?) {
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
        val call = apiService.getSpecificProduct(getb2bId!!)
        call.enqueue(object : Callback<SpecificB2BModel> {
            override fun onFailure(call: Call<SpecificB2BModel>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(this@SpecificB2BActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<SpecificB2BModel>, response: Response<SpecificB2BModel>) {
                if (response.code() == 401) {
                    spotDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@SpecificB2BActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@SpecificB2BActivity, LoginActivity::class.java))
                    finish()
                } else {
                    spotDialog!!.dismiss()
                    val b2bdata = response.body()
                    if (b2bdata != null) {
                        spotDialog!!.dismiss()

                        current_user_id = b2bdata.data.get(0).userId
                        if (user_id.equals(current_user_id)) {
                            b2b_edits.visibility = View.VISIBLE
                            b2b_delete.visibility = View.VISIBLE
                        } else {
                            b2b_edits.visibility = View.GONE
                            b2b_delete.visibility = View.GONE
                        }
                        Glide.with(this@SpecificB2BActivity).load(b2bdata.data.get(0).image)
                            .centerCrop()
                            .into(b2b_displayivs)
                        b2b_productnames.setText(b2bdata.data.get(0).productName)
                        b2b_fullnames.setText(b2bdata.data.get(0).name)
                        b2b_adds.setText(b2bdata.data.get(0).address)
                        b2b_contactss.setText(b2bdata.data.get(0).contactNumber)
                        b2b_emailids.setText(b2bdata.data.get(0).emailId)
                        b2b_details.setText(b2bdata.data.get(0).content)
                        b2b_quantitys.setText(b2bdata.data.get(0).quantity)
                        if (b2bType.equals("product")) {
                            b2b_areaDescs.visibility = View.GONE
                        }
                        if (b2bType.equals("property")) {
                            b2b_areaDescs.visibility = View.VISIBLE
                            b2b_areaDescs.setText(b2bdata.data.get(0).areaDescription)
                        }
                    } else {
                        spotDialog!!.dismiss()
                        Toast.makeText(this@SpecificB2BActivity, "Downloading Data Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }



        private fun updateindatabase(saveb2b: B2BRequest) {
            spotDialog!!.show()
            val requestBody = HashMap<String, B2BRequest>()
            requestBody.clear()
            requestBody.put("data", saveb2b)
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
            val postUser = apiServiceuser.editProduct(getb2bId!!,requestBody)
            postUser.enqueue(object : Callback<ApiReturn> {

                override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                    spotDialog!!.dismiss()
                    Toast.makeText(this@SpecificB2BActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                    if (response.isSuccessful) {
                        spotDialog!!.dismiss()
                        fieldsenable(false)
                        b2b_uploadimgs.visibility = View.GONE
                        b2b_submits.visibility = View.GONE
                        Toast.makeText(this@SpecificB2BActivity, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                    } else {
                        spotDialog!!.dismiss()
                        Toast.makeText(this@SpecificB2BActivity, "Uploading Error", Toast.LENGTH_SHORT).show()
                    }

                }

            })

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
        val postUser = apiServiceuser.deleteProduct(getb2bId!!)
        postUser.enqueue(object : Callback<ApiReturn> {

            override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(this@SpecificB2BActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                if (response.isSuccessful) {
                    spotDialog!!.dismiss()
                    startActivity(Intent(this@SpecificB2BActivity, B2BActivity::class.java))
                    Toast.makeText(this@SpecificB2BActivity, "Successfully Deleted", Toast.LENGTH_SHORT).show()
                } else {
                    spotDialog!!.dismiss()
                    Toast.makeText(this@SpecificB2BActivity, "Deleting Error", Toast.LENGTH_SHORT).show()
                }

            }

        })

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putString("KeyUserId", user_id)
        outState.putString("KeyUserToken", user_token)
        outState.putString("KeySearch", getb2bId!!)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        user_id = savedInstanceState!!.getString("KeyUserId")
        user_token = savedInstanceState.getString("KeyUserToken")
        getb2bId = savedInstanceState.getString("KeySearch")

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            b2bImageUri = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = getContentResolver().query(b2bImageUri!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            b2bImage = cursor.getString(columnIndex)
            cursor.close()

            val photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), b2bImageUri)
            b2b_displayivs!!.setImageBitmap(photoBitmap)

            bitmapimage = BitmapFactory.decodeFile(b2bImage)
        }
    }
/*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
*/

/*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.my_profile -> {
                startActivity(Intent(this@SpecificB2BActivity, MyProfileActivity::class.java))
            }
            R.id.terms_con -> {
                startActivity(Intent(this@SpecificB2BActivity, TermsCondActivity::class.java))
            }
            R.id.about_us -> {
                startActivity(Intent(this@SpecificB2BActivity, AboutUsActivity::class.java))
            }
            R.id.contact_us -> {
                startActivity(Intent(this@SpecificB2BActivity, ContactUsActivity::class.java))
            }
            else -> {
            }
        }
        return true
    }
*/

    private fun isValidate(): Boolean {
        if (b2b_productnames.getText().toString().trim().length < 1) {
            b2b_productnames.error = getString(R.string.ErrorField)
            b2b_productnames.requestFocus()
            return false

        } else if (b2b_fullnames.getText().toString().trim().length < 1) {
            b2b_fullnames.error = getString(R.string.ErrorField)
            b2b_fullnames.requestFocus()
            return false

        } else if (b2b_contactss.getText().toString().trim().length < 1 || b2b_contactss.getText().toString().trim().length > 12 || b2b_contactss.getText().toString().trim().length < 10) {
            b2b_contactss.error = getString(R.string.ErrorField)
            b2b_contactss.requestFocus()
            return false

        } else if (b2bType.equals("Select Category")) {
            Toast.makeText(this@SpecificB2BActivity, "Select Category", Toast.LENGTH_LONG).show()
            return false

        } else if (b2b_emailids.getText().toString().trim().length < 1 || isEmailValid(b2b_emailids.text.toString()) == false) {
            b2b_emailids.error = getString(R.string.ErrorField)
            b2b_emailids.requestFocus()
            return false
        } else if (b2b_adds.getText().toString().trim().length < 1) {
            b2b_adds.error = getString(R.string.ErrorField)
            b2b_adds.requestFocus()
            return false

        } else if (b2b_quantitys.getText().toString().trim().length < 1) {
            b2b_quantitys.error = getString(R.string.ErrorField)
            b2b_quantitys.requestFocus()
            return false

        } else if (b2b_details.getText().toString().trim().length < 1) {
            b2b_details.error = getString(R.string.ErrorField)
            b2b_details.requestFocus()
            return false

        } else
            return true

    }

    fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun fieldsenable(isEnabled: Boolean) {
        b2b_productnames.isEnabled = isEnabled
        b2b_fullnames.isEnabled = isEnabled
        b2b_contactss.isEnabled = isEnabled
        b2b_emailids.isEnabled = isEnabled
        b2b_adds.isEnabled = isEnabled
        b2b_quantitys.isEnabled = isEnabled
        b2b_details.isEnabled = isEnabled

    }


}

