package com.gmediasolutions.anurakti.b2b

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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.base.MainActivity
import com.gmediasolutions.anurakti.R
import kotlinx.android.synthetic.main.activity_b2b_registration.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import java.io.ByteArrayOutputStream

class B2BRegistrationActivity : BaseActivity() {

    private var b2bProductName: String? = null
    private var b2bName: String? = null
    private var areaDescrip: String? = null
    private var b2bemail_id: String? = null
    private var b2bContent: String? = null
    private var b2bContact: String? = null
    private var b2bType: String? = null
    private var b2baddress: String? = null
    private var b2bquantity: String? = null
    private var b2bImage: String? = null
    private var b2bImageUri: Uri? = null
    private var base64img: String? = null
    var listState: Parcelable? = null
    private val SELECT_GALLERY = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b2b_registration)

        setupToolbar()


//        spinner setup
        val adpter_b2b = ArrayAdapter.createFromResource(this, R.array.b2b_arrays, android.R.layout.simple_spinner_item)

        adpter_b2b.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_b2b.adapter = adpter_b2b

        spinner_b2b.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
                    b2bType = adapterView.getItemAtPosition(i).toString()
                    if (b2bType.equals("product")) {
                        b2b_productname.hint = "Product Name"
                        b2b_quantity.hint = "Quantity"
                        b2b_detail.hint = "Product Description"
                        b2b_areaDesc.visibility = View.GONE
                    }
                    if (b2bType.equals("property")) {
                        b2b_productname.hint = "Property Name"
                        b2b_quantity.hint = "Area"
                        b2b_detail.hint = "Property Description"
                        b2b_areaDesc.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
            }
        }

//        upload image btn
        b2b_uploadimg.setOnClickListener {
            val updloadimg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(updloadimg, SELECT_GALLERY)
        }
//         submit button
        b2b_submit.setOnClickListener {

            b2bProductName = b2b_productname.text.toString()
            b2bName = b2b_fullname.text.toString()
            b2bContact = b2b_contacts.text.toString()
            b2bemail_id = b2b_emailid.text.toString()
            b2baddress = b2b_add.text.toString()
            b2bquantity = b2b_quantity.text.toString()
            b2bContent = b2b_detail.text.toString()
            areaDescrip = b2b_areaDesc.text.toString()

            if (isValidate()) {
                if (b2bImage != null) {
                    val bm = BitmapFactory.decodeFile(b2bImage)
                    val bao = ByteArrayOutputStream()
                    bm.compress(Bitmap.CompressFormat.JPEG, 90, bao)
                    val byte = bao.toByteArray()
                    base64img = Base64.encodeToString(byte, Base64.NO_WRAP)

//                    val saveb2bdata = B2BRequest(user_id!!,b2bProductName!!,b2bName!!, b2bContact!!, b2bemail_id!!, base64img!!, b2bContent!!,areaDescrip!!, b2baddress!!, b2bType!!, b2bquantity!!)
//                    saveindatabase(saveb2bdata)

                } else {
                    Toast.makeText(applicationContext, "Upload Image", Toast.LENGTH_SHORT).show()

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
            b2bImageUri = data.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = getContentResolver().query(b2bImageUri!!, filePathColumn, null, null, null)
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            b2bImage = cursor.getString(columnIndex)
            cursor.close()

            val photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), b2bImageUri)
            b2b_displayiv!!.setImageBitmap(photoBitmap)
        }
    }

    /*
        private fun saveindatabase(saveb2b: B2BRequest) {
            spotdialog!!.show()
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
            val postUser = apiServiceuser.addProduct(requestBody)
            postUser.enqueue(object : Callback<ApiReturn> {

                override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                    spotdialog!!.dismiss()
                    Toast.makeText(this@B2BRegistrationActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                    if (response.code() == 401) {
                        spotdialog!!.dismiss()
                        session!!.logoutUser()
                        Toast.makeText(this@B2BRegistrationActivity, "Session Out", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@B2BRegistrationActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        if (response.isSuccessful) {
                            spotdialog!!.dismiss()
                            cleartext()
                            val intenttem = Intent(this@B2BRegistrationActivity, B2BActivity::class.java)
                            startActivity(intenttem)
                            Toast.makeText(this@B2BRegistrationActivity, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                        } else {
                            spotdialog!!.dismiss()
                            Toast.makeText(this@B2BRegistrationActivity, "Uploading Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            })

        }
    */
    private fun isValidate(): Boolean {
        if (b2b_fullname.getText().toString().trim().length < 1) {
            b2b_fullname.error = getString(R.string.ErrorField)
            b2b_fullname.requestFocus()
            return false

        } else if (b2b_contacts.getText().toString().trim().length < 1 || b2b_contacts.getText().toString().trim().length > 12 || b2b_contacts.getText().toString().trim().length < 10) {
            b2b_contacts.error = getString(R.string.ErrorField)
            b2b_contacts.requestFocus()
            return false

        } else if (b2bType.equals("Select Category")) {
            Toast.makeText(this@B2BRegistrationActivity, "Select Category", Toast.LENGTH_LONG).show()
            return false

        } else if (b2b_productname.getText().toString().trim().length < 1) {
            b2b_productname.error = getString(R.string.ErrorField)
            b2b_productname.requestFocus()
            return false

        } else if (b2b_emailid.getText().toString().trim().length < 1 || isEmailValid(b2b_emailid.text.toString()) == false) {
            b2b_emailid.error = getString(R.string.ErrorField)
            b2b_emailid.requestFocus()
            return false
        } else if (b2b_add.getText().toString().trim().length < 1) {
            b2b_add.error = getString(R.string.ErrorField)
            b2b_add.requestFocus()
            return false

        } else if (b2b_quantity.getText().toString().trim().length < 1) {
            b2b_quantity.error = getString(R.string.ErrorField)
            b2b_quantity.requestFocus()
            return false

        } else if (b2b_detail.getText().toString().trim().length < 1) {
            b2b_detail.error = getString(R.string.ErrorField)
            b2b_detail.requestFocus()
            return false

        } else
            return true

    }

    fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun cleartext() {
        b2b_productname.setText("")
        b2b_fullname.setText("")
        b2b_contacts.setText("")
        b2b_emailid.setText("")
        b2b_add.setText("")
        b2b_quantity.setText("")
        b2b_detail.setText("")
        b2b_areaDesc.setText("")
        spinner_b2b.setSelection(0)
        b2b_displayiv.setImageBitmap(null)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putInt("KeyState", spinner_b2b!!.selectedItemPosition)
        outState.putParcelable("LIST_STATE_KEY ", listState)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        spinner_b2b!!.setSelection(savedInstanceState!!.getInt("KeyState", 0))
        listState = savedInstanceState!!.getParcelable("LIST_STATE_KEY")
    }


}
