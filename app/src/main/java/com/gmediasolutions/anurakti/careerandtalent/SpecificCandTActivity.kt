package com.gmediasolutions.anurakti.careerandtalent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
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
import com.gmediasolutions.anurakti.model.CandTModel.SpecificCandTModel
import kotlinx.android.synthetic.main.activity_specific_candt.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream

class SpecificCandTActivity : BaseActivity() {

    private var candtProfilefor: String? = null
    private var candtfullname: String? = null
    private var candtfathern: String? = null
    private var candtemail: String? = null
    private var candContact: String? = null
    private var candIndustryType: String? = null
    private var candContactper: String? = null
    private var candtaddress: String? = null
    private var candtapplyfor: String? = null
    private var candthiringfor: String? = null
    private var candtkeyskill: String? = null
    private var candtjobexp: String? = null
    private var candtprevicomany: String? = null
    private var candtImage: String? = null
    private var candtImageUri: Uri? = null
    private var candtImagef: String? = null
    private var candtFileUri: Uri? = null
    private var base64img: String? = null
    private var base64file: String? = null
    private var btnclick: String? = null
    private var candt_hiringfors: TextView? = null

    private var bitmapimage: Bitmap? = null
    private var bitmapimagef: Bitmap? = null

    private val SELECT_GALLERY = 300
    private val SELECT_FILE = 301

    private var spinner_profilefor: Spinner? = null
    var listState: Parcelable? = null

    private var getcandtId: String? = null

    private var current_user_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specific_candt)

        setupToolbar()

        btnclick = "no"

        spinner_profilefor = findViewById(R.id.spinner_candts)
        candt_hiringfors = findViewById(R.id.candt_hiringfors)

        getcandtId = intent.getStringExtra("candt_id")

        candt_submits.visibility = View.GONE
        val profilefor_spinner =
            ArrayAdapter.createFromResource(this, R.array.candt_arrays, android.R.layout.simple_spinner_item)


        profilefor_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner_profilefor!!.adapter = profilefor_spinner
        spinner_profilefor!!.isEnabled = false

        spinner_profilefor!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                    candtProfilefor = adapterView.getItemAtPosition(i).toString()
                    if (btnclick.equals("yes")) {
                        if (candtProfilefor.equals("student")) {
                            candt_hiringfors!!.visibility = View.GONE
                            candthiringfor = "null"
                            candt_precomps.visibility = View.VISIBLE
                            cnadt_fnames.visibility = View.VISIBLE
                            candt_applyingfors.visibility = View.VISIBLE
                            candt_uploadfiles.visibility = View.VISIBLE
                            candt_filenames.visibility = View.VISIBLE
                        } else if (candtProfilefor.equals("company")) {
                            candt_hiringfors!!.visibility = View.VISIBLE
                            candtfathern = "null"
                            candtprevicomany = "null"
                            candtapplyfor = "null"
                            candt_precomps.visibility = View.GONE
                            cnadt_fnames.visibility = View.GONE
                            candt_applyingfors.visibility = View.GONE
                            candt_uploadfiles.visibility = View.GONE
                            candt_filenames.visibility = View.GONE
                        }
                    }
                } else {

                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
            }

        }

        fieldsenable(false)
//        candt_uploadfiles.visibility = View.GONE
        getspecificcandt(getcandtId)



        candt_uploadfiles.setOnClickListener {
            val updloadimg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(updloadimg, SELECT_FILE)
        }
        candt_delete.setOnClickListener {
            //            deletePost()
        }

        candt_edits.setOnClickListener {
            btnclick = "yes"
            fieldsenable(true)
            candt_uploadimgs.visibility = View.VISIBLE
            candt_submits.visibility = View.VISIBLE
//            candt_uploadfiles.visibility = View.VISIBLE
        }
        candt_uploadimgs.setOnClickListener {
            val updloadimg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(updloadimg, SELECT_GALLERY)
        }
        candt_submits.setOnClickListener {
            btnclick = "no"
            val connMgr = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connMgr.activeNetworkInfo
            if (networkInfo == null) {
                Toast.makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show()
            } else {
                if (isValidate()) {
                    /*  candtfullname = cnadt_fullnames.text.toString()
                    candtfathern = cnadt_fnames.text.toString()
                    candtemail = cnadt_emailids.text.toString()
                    candContact = cnadt_contactss.text.toString()
                    candIndustryType = candt_industytypes.text.toString()
                    candContactper = candt_contactpers.text.toString()
                    candtaddress = candt_addresss.text.toString()
                    candtapplyfor = candt_applyingfors.text.toString()
                    candthiringfor = candt_hiringfors!!.text.toString()
                    candtkeyskill = candt_keywordss.text.toString()
                    candtjobexp = candt_jobexps.text.toString()
                    candtprevicomany = candt_precomps.text.toString()*/
                    if (candtProfilefor.equals("student")) {
                        candt_hiringfors!!.visibility = View.GONE
                        candthiringfor = "null"
                        candt_precomps.visibility = View.VISIBLE
                        cnadt_fnames.visibility = View.VISIBLE
                        candt_applyingfors.visibility = View.VISIBLE
                        candt_uploadfiles.visibility = View.VISIBLE
                        candt_filenames.visibility = View.VISIBLE
                        candtfullname = cnadt_fullnames.text.toString()
                        candtfathern = cnadt_fnames.text.toString()
                        candtemail = cnadt_emailids.text.toString()
                        candContact = cnadt_contactss.text.toString()
                        candIndustryType = candt_industytypes.text.toString()
                        candContactper = candt_contactpers.text.toString()
                        candtaddress = candt_addresss.text.toString()
                        candtapplyfor = candt_applyingfors.text.toString()
                        candtkeyskill = candt_keywordss.text.toString()
                        candtjobexp = candt_jobexps.text.toString()
                        candtprevicomany = candt_precomps.text.toString()
                        bitmapimage = (candt_displayivs.drawable as BitmapDrawable).bitmap
                    } else if (candtProfilefor.equals("company")) {
                        candt_hiringfors!!.visibility = View.VISIBLE
                        candtfathern = "null"
                        candtprevicomany = "null"
                        candtapplyfor = "null"
                        candt_precomps.visibility = View.GONE
                        cnadt_fnames.visibility = View.GONE
                        candt_applyingfors.visibility = View.GONE
                        candt_uploadfiles.visibility = View.GONE
                        candt_filenames.visibility = View.GONE
                        candtfullname = cnadt_fullnames.text.toString()
                        candthiringfor = candt_hiringfors!!.text.toString()
                        candtemail = cnadt_emailids.text.toString()
                        candContact = cnadt_contactss.text.toString()
                        candIndustryType = candt_industytypes.text.toString()
                        candContactper = candt_contactpers.text.toString()
                        candtaddress = candt_addresss.text.toString()
                        candtkeyskill = candt_keywordss.text.toString()
                        candtjobexp = candt_jobexps.text.toString()
                        bitmapimage = (candt_displayivs.drawable as BitmapDrawable).bitmap
                    }
                    if (candtImagef != null) {
                        bitmapimagef = (candt_filenames.drawable as BitmapDrawable).bitmap
                    }
                    if (bitmapimage != null) {
                        val bao = ByteArrayOutputStream()
                        bitmapimage!!.compress(Bitmap.CompressFormat.JPEG, 90, bao)
                        val byte = bao.toByteArray()
                        base64img = Base64.encodeToString(byte, Base64.NO_WRAP)

//                        val savecandtdata = CandTRequest(user_id!!, candtProfilefor!!, candtfullname!!, candtfathern!!, candtemail!!, candContact!!, candIndustryType!!, candContactper!!, candtaddress!!, candthiringfor!!, candtapplyfor!!, candtkeyskill!!, candtjobexp!!, candtprevicomany!!, "Resume", base64img!!)
//                        saveindatabase(savecandtdata,getcandtId!!)
                    } else {
                        if (bitmapimagef != null) {
                            val bao = ByteArrayOutputStream()
                            bitmapimagef!!.compress(Bitmap.CompressFormat.JPEG, 90, bao)
                            val byte = bao.toByteArray()
                            base64file = Base64.encodeToString(byte, Base64.NO_WRAP)

//                            val savecandtdata = CandTRequest(user_id!!, candtProfilefor!!, candtfullname!!, candtfathern!!, candtemail!!, candContact!!, candIndustryType!!, candContactper!!, candtaddress!!, candthiringfor!!, candtapplyfor!!, candtkeyskill!!, candtjobexp!!, candtprevicomany!!, base64file!!, base64img!!)
//                            saveindatabase(savecandtdata, getcandtId!!)
                        } else {
                            Toast.makeText(applicationContext, "Upload Resume", Toast.LENGTH_SHORT).show()
                        }
//                    Toast.makeText(applicationContext, "Upload Image", Toast.LENGTH_SHORT).show()
                    }

                    /* if (bitmapimage != null) {
                         val bao = ByteArrayOutputStream()
                         bitmapimage!!.compress(Bitmap.CompressFormat.JPEG, 90, bao)
                         val byte = bao.toByteArray()
                         base64img = Base64.encodeToString(byte, Base64.NO_WRAP)

                         val savecandtdata = CandTRequest(user_id!!, candtProfilefor!!, candtfullname!!, candtfathern!!, candtemail!!, candContact!!,  candIndustryType!!, candContactper!!, candtaddress!!, candthiringfor!!, candtapplyfor!!, candtkeyskill!!, candtjobexp!!, candtprevicomany!!, "resume",base64img!!)
                         saveindatabase(savecandtdata)

                     }else{
                         if (bitmapimagef != null) {
                             val bao = ByteArrayOutputStream()
                             bitmapimagef!!.compress(Bitmap.CompressFormat.JPEG, 90, bao)
                             val byte = bao.toByteArray()
                             base64file = Base64.encodeToString(byte, Base64.NO_WRAP)

                             val savecandtdata = CandTRequest(user_id!!, candtProfilefor!!, candtfullname!!, candtfathern!!, candtemail!!, candContact!!, candIndustryType!!, candContactper!!, candtaddress!!, candthiringfor!!, candtapplyfor!!, candtkeyskill!!, candtjobexp!!, candtprevicomany!!, base64file!!,base64img!!)
                             saveindatabase(savecandtdata)

                         }
                     }
                     else {
                         Toast.makeText(this, "Upload Image", Toast.LENGTH_SHORT).show()

                     }*/

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


    private fun getspecificcandt(getcandtId: String?) {
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
        val call = apiService.getCareerDetails(getcandtId!!)
        call.enqueue(object : Callback<SpecificCandTModel> {
            override fun onFailure(call: Call<SpecificCandTModel>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(this@SpecificCandTActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()

            }

            override fun onResponse(call: Call<SpecificCandTModel>, response: Response<SpecificCandTModel>) {
                if (response.code() == 401) {
                    spotDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@SpecificCandTActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@SpecificCandTActivity, LoginActivity::class.java))
                    finish()
                } else {
                    spotDialog!!.dismiss()
                    val candtdata = response.body()
                    if (candtdata != null) {
                        spotDialog!!.dismiss()
                        current_user_id = candtdata.data.get(0).userId
                        if (user_id.equals(current_user_id)) {
                            candt_edits.visibility = View.VISIBLE
                            candt_delete.visibility = View.VISIBLE
                        } else {
                            candt_edits.visibility = View.GONE
                            candt_delete.visibility = View.GONE
                        }
                        Glide.with(this@SpecificCandTActivity).load(candtdata.data.get(0).image)
                            .centerCrop().into(candt_displayivs)
                        Glide.with(this@SpecificCandTActivity).load(candtdata.data.get(0).resume)
                            .centerCrop().into(candt_filenames)


                        cnadt_fullnames.setText(candtdata.data.get(0).name)
                        candt_contactpers.setText(candtdata.data.get(0).contactPesron)
                        cnadt_contactss.setText(candtdata.data.get(0).mobileNumber)
                        cnadt_fnames.setText(candtdata.data.get(0).fatherName)
                        cnadt_emailids.setText(candtdata.data.get(0).emailId)
                        candtProfilefor = candtdata.data.get(0).profileFor
                        spinner_profilefor!!.setSelection(
                            (spinner_profilefor!!.adapter as ArrayAdapter<String>).getPosition(
                                candtProfilefor!!
                            )
                        )
                        if (candtProfilefor.equals("student")) {
                            candt_hiringfors!!.visibility = View.GONE
                            candt_precomps.visibility = View.VISIBLE
                            cnadt_fnames.visibility = View.VISIBLE
                            candt_applyingfors.visibility = View.VISIBLE
                            candt_uploadfiles.visibility = View.GONE
                            candt_filenames.visibility = View.VISIBLE
                        }
                        if (candtProfilefor.equals("company")) {
                            candt_hiringfors!!.visibility = View.VISIBLE
                            candt_precomps.visibility = View.GONE
                            cnadt_fnames.visibility = View.GONE
                            candt_applyingfors.visibility = View.GONE
                            candt_uploadfiles.visibility = View.GONE
                            candt_filenames.visibility = View.GONE
                        }
                        candt_industytypes.setText(candtdata.data.get(0).industryType)
                        candt_contactpers.setText(candtdata.data.get(0).contactPesron)
                        candt_addresss.setText(candtdata.data.get(0).address)
                        candt_hiringfors!!.setText(candtdata.data.get(0).hiringFor)
                        candt_applyingfors.setText(candtdata.data.get(0).applyingFor)
                        candt_keywordss.setText(candtdata.data.get(0).keywords)
                        candt_jobexps.setText(candtdata.data.get(0).jobExperience)
                        candt_precomps.setText(candtdata.data.get(0).previousCompany)
                    } else {
                        spotDialog!!.dismiss()
                        Toast.makeText(this@SpecificCandTActivity, "Downloading Data Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

/*
    private fun saveindatabase(savecandtdata: CandTRequest, getcandtId: String) {
        spotDialog!!.show()
        val requestBody = HashMap<String, CandTRequest>()
        requestBody.clear()
        requestBody.put("data", savecandtdata)
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
        val postUser = apiServiceuser.editProfile(getcandtId, requestBody)
        postUser.enqueue(object : Callback<ApiReturn> {

            override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(this@SpecificCandTActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                if (response.code() == 401) {
                    spotDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@SpecificCandTActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@SpecificCandTActivity, LoginActivity::class.java))
                    finish()
                } else {
                    if (response.isSuccessful) {
                        spotDialog!!.dismiss()
                        fieldsenable(false)
                        candt_uploadimgs.visibility = View.GONE
                        candt_submits.visibility = View.GONE
                        Toast.makeText(this@SpecificCandTActivity, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                    } else {
                        spotDialog!!.dismiss()
                        Toast.makeText(this@SpecificCandTActivity, "Uploading Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })

    }
*/


/*
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
        val postUser = apiServiceuser.deleteCarrerAndTalent(getcandtId!!)
        postUser.enqueue(object : Callback<ApiReturn> {

            override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(this@SpecificCandTActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                if (response.code() == 401) {
                    spotDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@SpecificCandTActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@SpecificCandTActivity, LoginActivity::class.java))
                    finish()
                } else {
                    if (response.isSuccessful) {
                    spotDialog!!.dismiss()
                    startActivity(Intent(this@SpecificCandTActivity, CandTActivity::class.java))
                    Toast.makeText(this@SpecificCandTActivity, "Successfully Deleted", Toast.LENGTH_SHORT).show()
                } else {
                    spotDialog!!.dismiss()
                    Toast.makeText(this@SpecificCandTActivity, "Deleting Error", Toast.LENGTH_SHORT).show()
                }

            }}

        })

    }
*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            candtImageUri = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = getContentResolver().query(candtImageUri, filePathColumn, null, null, null)
            cursor.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            candtImage = cursor.getString(columnIndex)
            cursor.close()

            val photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), candtImageUri)
            candt_displayivs!!.setImageBitmap(photoBitmap)

            bitmapimage = BitmapFactory.decodeFile(candtImage)
        }
        if (requestCode == SELECT_FILE && resultCode == Activity.RESULT_OK && data != null) {
            candtFileUri = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = getContentResolver().query(candtFileUri, filePathColumn, null, null, null)
            cursor.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            candtImagef = cursor.getString(columnIndex)
            cursor.close()

            val photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), candtFileUri)
            candt_filenames!!.setImageBitmap(photoBitmap)

            bitmapimagef = BitmapFactory.decodeFile(candtImagef)
        }
    }


/*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
*/

    override fun onSaveInstanceState(outState: Bundle?) {

        outState!!.putString("KeySearch", getcandtId!!)
        outState.putInt("KeyState", spinner_candts!!.selectedItemPosition)
        outState.putParcelable("LIST_STATE_KEY ", listState)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)


        getcandtId = savedInstanceState!!.getString("KeySearch")
        spinner_candts!!.setSelection(savedInstanceState.getInt("KeyState", 0))
        listState = savedInstanceState.getParcelable("LIST_STATE_KEY")

    }

    private fun isValidate(): Boolean {
        if (candtProfilefor.equals("Select Category")) {
            Toast.makeText(this@SpecificCandTActivity, "Select Category", Toast.LENGTH_LONG).show()
            return false
        } else if (cnadt_fullnames.getText().toString().trim().length < 1) {
            cnadt_fullnames.error = getString(R.string.ErrorField)
            cnadt_fullnames.requestFocus()
            return false

        } else if (cnadt_emailids.text.toString().trim().length < 1 || isEmailValid(cnadt_emailids.text.toString()) == false) {
            cnadt_emailids.error = getString(R.string.invalid_entry)
            cnadt_emailids.requestFocus()
            return false
        } else if (cnadt_contactss.getText().toString().trim().length < 1 || cnadt_contactss.getText().toString().trim().length > 12 || cnadt_contactss.getText().toString().trim().length < 10) {
            cnadt_contactss.error = getString(R.string.ErrorField)
            cnadt_contactss.requestFocus()
            return false

        } else if (candt_industytypes.getText().toString().trim().length < 1) {
            candt_industytypes.error = getString(R.string.ErrorField)
            candt_industytypes.requestFocus()
            return false
        } else if (candt_addresss.getText().toString().trim().length < 1) {
            candt_addresss.error = getString(R.string.ErrorField)
            candt_addresss.requestFocus()
            return false

        } else if (candt_keywordss.getText().toString().trim().length < 1) {
            candt_keywordss.error = getString(R.string.ErrorField)
            candt_keywordss.requestFocus()
            return false

        } else
            return true

    }

    fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    private fun fieldsenable(isEnabled: Boolean) {

        cnadt_fullnames.isEnabled = isEnabled
        cnadt_fnames.isEnabled = isEnabled
        cnadt_contactss.isEnabled = isEnabled
        cnadt_emailids.isEnabled = isEnabled
        candt_industytypes.isEnabled = isEnabled
        candt_contactpers.isEnabled = isEnabled
        candt_addresss.isEnabled = isEnabled
        candt_hiringfors!!.isEnabled = isEnabled
        candt_applyingfors.isEnabled = isEnabled
        candt_keywordss.isEnabled = isEnabled
        candt_jobexps.isEnabled = isEnabled
        candt_precomps.isEnabled = isEnabled
    }

}

