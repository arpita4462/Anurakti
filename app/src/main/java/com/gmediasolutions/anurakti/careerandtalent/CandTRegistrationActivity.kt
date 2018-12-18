package com.gmediasolutions.anurakti.careerandtalent

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.Base64
import android.view.View
import android.widget.*
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.base.MainActivity
import com.gmediasolutions.anurakti.R
import kotlinx.android.synthetic.main.activity_candt_registration.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.io.ByteArrayOutputStream

class CandTRegistrationActivity : BaseActivity() {

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
    private var candtFile: String? = null
    private var candtImageUri: Uri? = null
    private var candtFileUri: Uri? = null
    private var base64img: String? = null
    private var base64file: String? = null
    private val SELECT_GALLERY = 200
    private val SELECT_FILE = 201
    var listState: Parcelable? = null

    private var spinner_candt: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candt_registration)

        setupToolbar()


        spinner_candt = findViewById(R.id.spinner_candt)

        val adpter_candt =
            ArrayAdapter.createFromResource(this, R.array.candt_arrays, android.R.layout.simple_spinner_item)

        adpter_candt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_candt!!.adapter = adpter_candt

        spinner_candt!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                    if (candtProfilefor.equals("student")) {
                        candt_hiringfor.visibility = View.GONE
                        candthiringfor = "null"
                        candt_fname.visibility = View.VISIBLE
                        candt_precomp.visibility = View.VISIBLE
                        candt_applyingfor.visibility = View.VISIBLE
                        candt_uploadfile.visibility = View.VISIBLE
                        candt_filename.visibility = View.VISIBLE
                    }
                    if (candtProfilefor.equals("company")) {
                        candt_hiringfor.visibility = View.VISIBLE
                        candtfathern = "null"
                        candtprevicomany = "null"
                        candtapplyfor = "null"
                        candt_fname.visibility = View.GONE
                        candt_precomp.visibility = View.GONE
                        candt_applyingfor.visibility = View.GONE
                        candt_uploadfile.visibility = View.GONE
                        candt_filename.visibility = View.GONE
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
            }
        }

        candt_displayiv.setOnClickListener {
            val updloadimg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(updloadimg, SELECT_GALLERY)
        }

        candt_uploadfile.setOnClickListener {
            val updloadfile = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            val intent = Intent()
            /*   intent.type = "application/pdf"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Pdf"), SELECT_FILE)*/
//
            startActivityForResult(updloadfile, SELECT_FILE)
        }

        candt_submit.setOnClickListener {


            candtfullname = cnadt_fullname.text.toString()
            candtfathern = candt_fname.text.toString()
            candtemail = cnadt_emailid.text.toString()
            candContact = cnadt_contacts.text.toString()
            candIndustryType = candt_industytype.text.toString()
            candContactper = candt_contactper.text.toString()
            candtaddress = candt_address.text.toString()
            candtapplyfor = candt_applyingfor.text.toString()
            candthiringfor = candt_hiringfor.text.toString()
            candtkeyskill = candt_keywords.text.toString()
            candtjobexp = candt_jobexp.text.toString()
            candtprevicomany = candt_precomp.text.toString()
            if (isValidate()) {
                if (candtImage != null) {
                    val bm = BitmapFactory.decodeFile(candtImage)
                    val bao = ByteArrayOutputStream()
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bao)
                    val byte = bao.toByteArray()
                    base64img = Base64.encodeToString(byte, Base64.NO_WRAP)

                    if (candtFile != null) {
                        val bm1 = BitmapFactory.decodeFile(candtFile)
                        val bao1 = ByteArrayOutputStream()
                        bm1.compress(Bitmap.CompressFormat.JPEG, 90, bao1)
                        val byte1 = bao1.toByteArray()
                        base64file = Base64.encodeToString(byte1, Base64.NO_WRAP)

//                        val savecandtdata = CandTRequest(user_id!!, candtProfilefor!!, candtfullname!!, candtfathern!!, candtemail!!, candContact!!,  candIndustryType!!, candContactper!!, candtaddress!!, candthiringfor!!, candtapplyfor!!, candtkeyskill!!, candtjobexp!!, candtprevicomany!!, base64file!!,base64img!!)
//                        saveindatabase(savecandtdata)
                    } else {
//                        val savecandtdata = CandTRequest(user_id!!, candtProfilefor!!, candtfullname!!, candtfathern!!, candtemail!!, candContact!!,  candIndustryType!!, candContactper!!, candtaddress!!, candthiringfor!!, candtapplyfor!!, candtkeyskill!!, candtjobexp!!, candtprevicomany!!, "Resume",base64img!!)
//                        saveindatabase(savecandtdata)
                    }
                } else {
                    Toast.makeText(applicationContext, "Upload Image", Toast.LENGTH_SHORT).show()
                    //                    Toast.makeText(applicationContext, "Upload Image", Toast.LENGTH_SHORT).show()
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
            candtImageUri = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = getContentResolver().query(candtImageUri!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            candtImage = cursor.getString(columnIndex)
            cursor.close()

            val photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), candtImageUri)
            candt_displayiv!!.setImageBitmap(photoBitmap)
        }
        if (requestCode == SELECT_FILE && resultCode == RESULT_OK && data != null) {

            candtFileUri = data.getData()
            val filePathColumnpdf = arrayOf(MediaStore.Images.Media.DATA)
            val cursorpdf = getContentResolver().query(candtFileUri!!, filePathColumnpdf, null, null, null)
            cursorpdf!!.moveToFirst()
            val columnIndexpdf = cursorpdf.getColumnIndex(filePathColumnpdf[0])
            candtFile = cursorpdf.getString(columnIndexpdf)
            cursorpdf.close()

            val photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), candtFileUri)
            candt_filename!!.setImageBitmap(photoBitmap)

        }
    }

/*
    private fun saveindatabase(savecandtdata: CandTRequest) {
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
        val postUser = apiServiceuser.addProfile(requestBody)
        postUser.enqueue(object : Callback<ApiReturn> {

            override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(this@CandTRegistrationActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                if (response.code() == 401) {
                    spotDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@CandTRegistrationActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@CandTRegistrationActivity, LoginActivity::class.java))
                    finish()
                } else {
                    if (response.isSuccessful) {
                        spotDialog!!.dismiss()
                        cleartext()
                        startActivity(Intent(this@CandTRegistrationActivity, CandTActivity::class.java))
                        Toast.makeText(this@CandTRegistrationActivity, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                    } else {
                        spotDialog!!.dismiss()
                        Toast.makeText(this@CandTRegistrationActivity, "Uploading Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })

    }
*/

    override fun onSaveInstanceState(outState: Bundle?) {

        outState!!.putInt("KeyState", spinner_candt!!.selectedItemPosition)
        outState.putParcelable("LIST_STATE_KEY ", listState)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)


        spinner_candt!!.setSelection(savedInstanceState!!.getInt("KeyState", 0))
        listState = savedInstanceState!!.getParcelable("LIST_STATE_KEY")
    }

    private fun isValidate(): Boolean {
        if (candtProfilefor.equals("Select Profile For")) {
            Toast.makeText(this@CandTRegistrationActivity, "Select Profile For", Toast.LENGTH_LONG).show()
            return false
        } else if (cnadt_fullname.getText().toString().trim().length < 1) {
            cnadt_fullname.error = getString(R.string.ErrorField)
            cnadt_fullname.requestFocus()
            return false

        } else if (cnadt_emailid.text.toString().trim().length < 1 || isEmailValid(cnadt_emailid.text.toString()) == false) {
            cnadt_emailid.error = getString(R.string.invalid_entry)
            cnadt_emailid.requestFocus()
            return false
        } else if (cnadt_contacts.getText().toString().trim().length < 1 || cnadt_contacts.getText().toString().trim().length > 12 || cnadt_contacts.getText().toString().trim().length < 10) {
            cnadt_contacts.error = getString(R.string.ErrorField)
            cnadt_contacts.requestFocus()
            return false

        } else if (candt_industytype.getText().toString().trim().length < 1) {
            candt_industytype.error = getString(R.string.ErrorField)
            candt_industytype.requestFocus()
            return false
        } else if (candt_address.getText().toString().trim().length < 1) {
            candt_address.error = getString(R.string.ErrorField)
            candt_address.requestFocus()
            return false

        } else if (candt_keywords.getText().toString().trim().length < 1) {
            candt_keywords.error = getString(R.string.ErrorField)
            candt_keywords.requestFocus()
            return false

        } else if (candt_jobexp.getText().toString().trim().length < 1) {
            candt_jobexp.error = getString(R.string.ErrorField)
            candt_jobexp.requestFocus()
            return false

        } else
            return true

    }

    fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun cleartext() {

        cnadt_fullname.setText("")
        candt_fname.setText("")
        cnadt_emailid.setText("")
        cnadt_contacts.setText("")
        candt_industytype.setText("")
        candt_contactper.setText("")
        candt_address.setText("")
        candt_hiringfor.setText("")
        candt_applyingfor.setText("")
        candt_keywords.setText("")
        candt_jobexp.setText("")
        candt_precomp.setText("")
        candt_displayiv.setImageBitmap(null)
        candt_filename.setImageBitmap(null)
        spinner_candt!!.setSelection(0)
    }

}
