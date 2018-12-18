package com.gmediasolutions.anurakti.base

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.alert.NetworkStateReceiver
import com.gmediasolutions.anurakti.model.RegisterModel
import kotlinx.android.synthetic.main.activity_register.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.InputStream


class RegisterActivity : AppCompatActivity(), NetworkStateReceiver.NetworkStateReceiverListener {

    private var networkStateReceiver: NetworkStateReceiver? = null

    private var pDialog: ProgressDialog? = null

    private var edtname: EditText? = null
    private var edtemail: EditText? = null
    private var edtpass: EditText? = null
    private var edtcnfpass: EditText? = null
    //    private var edtnumber: EditText? = null
    private var check: String? = null
    private var name: String? = null
    private var email: String? = null
    private var password: String? = null
    private var mobile: String? = null

    companion object {
        val TAG = "MyTag"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

//        check Internet Connection
        networkStateReceiver = NetworkStateReceiver()
        networkStateReceiver!!.addListener(this)
        this.registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        pDialog = ProgressDialog(this)

        edtname = findViewById(R.id.name)
        edtemail = findViewById(R.id.email)
        edtpass = findViewById(R.id.password)
        edtcnfpass = findViewById(R.id.confirmpassword)
//        edtnumber = findViewById(R.id.number)

        edtname!!.addTextChangedListener(nameWatcher)
        edtemail!!.addTextChangedListener(emailWatcher)
        edtpass!!.addTextChangedListener(passWatcher)
        edtcnfpass!!.addTextChangedListener(cnfpassWatcher)
//        edtnumber!!.addTextChangedListener(numberWatcher)


//        validate user details and register user

        register.setOnClickListener {

            if (validateName() && validateEmail() && validatePass() && validateCnfPass()) {

                name = edtname!!.text.toString()
                email = edtemail!!.text.toString()
                password = edtcnfpass!!.text.toString()
//                mobile = edtnumber!!.text.toString()

                //Validation Success
                val userRegister = RegisterModel("200", name!!, email!!, password!!)
                saveUserDetail(userRegister)
                Toast.makeText(applicationContext, "Registration Successful", Toast.LENGTH_LONG).show()

            }
        }

//        Take already registered user to login page

        login_now.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }

    }

    //TextWatcher for Name -----------------------------------------------------

    internal var nameWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //none
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //none
        }

        override fun afterTextChanged(s: Editable) {

            check = s.toString()
            edtname!!.requestFocus()

            if (check!!.length < 4 || check!!.length > 20) {
                edtname!!.error = "Name Must consist of 4 to 20 characters"
            }
        }

    }

    //TextWatcher for Email -----------------------------------------------------

    internal var emailWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //none
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //none
        }

        override fun afterTextChanged(s: Editable) {

            check = s.toString()
            edtemail!!.requestFocus()

            if (check!!.length < 4 || check!!.length > 40) {
                edtemail!!.error = "Email Must consist of 4 to 20 characters"
            } else if (!check!!.matches("^[A-za-z0-9.@]+".toRegex())) {
                edtemail!!.error = "Only . and @ characters allowed"
            } else if (!check!!.contains("@") || !check!!.contains(".")) {
                edtemail!!.error = "Enter Valid Email"
            }

        }

    }

    //TextWatcher for pass -----------------------------------------------------

    internal var passWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //none
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //none
        }

        override fun afterTextChanged(s: Editable) {

            check = s.toString()
            edtpass!!.requestFocus()

            if (check!!.length < 4 || check!!.length > 20) {
                edtpass!!.error = "Password Must consist of 4 to 20 characters"
            } else if (!check!!.matches("^[A-za-z0-9@]+".toRegex())) {
                edtemail!!.error = "Only @ special character allowed"
            }
        }

    }

    //TextWatcher for repeat Password -----------------------------------------------------

    internal var cnfpassWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //none
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //none
        }

        override fun afterTextChanged(s: Editable) {

            check = s.toString()
            edtcnfpass!!.requestFocus()

            if (check != edtpass!!.text.toString()) {
                edtcnfpass!!.error = "Both the passwords do not match"
            }
        }

    }


    //TextWatcher for Mobile -----------------------------------------------------

/*
    internal var numberWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //none
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //none
        }

        override fun afterTextChanged(s: Editable) {

            check = s.toString()
            edtnumber!!.requestFocus()

            if (check!!.length > 10) {
                edtnumber!!.error = "Number cannot be grated than 10 digits"
            } else if (check!!.length < 10) {
                edtnumber!!.error = "Number should be 10 digits"
            }
        }

    }
*/


//    save instance for screen rotation

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putString("KeyEmail", edtemail!!.text.toString())
        outState.putString("KeyName", edtname!!.text.toString())
//        outState.putString("KeyPhone", edtnumber!!.text.toString())
        outState.putString("KeyPass", edtpass!!.text.toString())
        outState.putString("KeyConPass", edtcnfpass!!.text.toString())

        super.onSaveInstanceState(outState)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        email = savedInstanceState!!.getString("KeyEmail")
        name = savedInstanceState.getString("KeyName")
//        mobile = savedInstanceState.getString("KeyPhone")
        password = savedInstanceState.getString("KeyPass")
//        edtcnfpass = savedInstanceState.getString("KeyConPass")

    }

    /*saving the data in Database*/

    private fun saveUserDetail(users: RegisterModel) {

        pDialog!!.show()
        val requestBody = HashMap<String, RegisterModel>()
//        requestBody.put("data", users)
        val retrofituser = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(getString(R.string.api_base_url))
            .build()
        val apiServiceuser = retrofituser.create(ApiInterface::class.java)
        val postUser = apiServiceuser.register(requestBody)

        postUser.enqueue(object : Callback<RegisterModel> {

            override fun onFailure(call: Call<RegisterModel>, t: Throwable) {
                pDialog!!.dismiss()
                Log.i("response_fail", t.localizedMessage)
                Toast.makeText(applicationContext, "Registration Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<RegisterModel>, response: Response<RegisterModel>) {
                Log.i("response_succes", response.message())
                Log.i("response_succes_code", response.code().toString())
                Log.i("response_succes_body", response.body().toString())
                if (response.isSuccessful) {
                    pDialog!!.dismiss()

//                    val singupintent = Intent(applicationContext, LoginActivity::class.java)
//                    singupintent.putExtra("user_email", email)
//                    singupintent.putExtra("user_password", password)
//                    startActivity(singupintent)
//                    finish()
                    Toast.makeText(applicationContext, "Verify your Email Address", Toast.LENGTH_LONG).show()
                } else {
                    if (response.code() == 422) {
                        pDialog!!.dismiss()
                        Toast.makeText(applicationContext, "Email-Id already exist", Toast.LENGTH_LONG).show()
                    } else {
                        pDialog!!.dismiss()
                        Toast.makeText(applicationContext, "Registration Error", Toast.LENGTH_LONG).show()
                    }
                }

            }

        })
    }

    /*Validation of Fields*/
//    private fun validateNumber(): Boolean {
//        edtnumber!!.requestFocus()
//
//        check = edtnumber!!.text.toString()
//        Log.e("inside number", check!!.length.toString() + " ")
//        if (check!!.length > 10) {
//            return false
//        } else if (check!!.length < 10) {
//            return false
//        }
//        return true
//    }

    private fun validateCnfPass(): Boolean {
        edtcnfpass!!.requestFocus()

        check = edtcnfpass!!.text.toString()

        return check == edtpass!!.text.toString()
    }

    private fun validatePass(): Boolean {

        edtpass!!.requestFocus()

        check = edtpass!!.text.toString()

        if (check!!.length < 4 || check!!.length > 20) {
            return false
        } else if (!check!!.matches("^[A-za-z0-9@]+".toRegex())) {
            return false
        }
        return true
    }

    private fun validateEmail(): Boolean {
        edtemail!!.requestFocus()

        check = edtemail!!.text.toString()

        if (check!!.length < 4 || check!!.length > 40) {
            return false
        } else if (!check!!.matches("^[A-za-z0-9.@]+".toRegex())) {
            return false
        } else if (!check!!.contains("@") || !check!!.contains(".")) {
            return false
        }

        return true
    }

    private fun validateName(): Boolean {
        edtname!!.requestFocus()

        check = edtname!!.text.toString()
        return !(check!!.length < 4 || check!!.length > 20)

    }


    override fun onResume() {
        super.onResume()
        networkStateReceiver = NetworkStateReceiver()
        networkStateReceiver!!.addListener(this)
        this.registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onStop() {
        super.onStop()
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
        showSnack("true")
    }

    override fun networkUnavailable() {
        showSnack("false")
    }

    override fun onDestroy() {
        super.onDestroy()
        networkStateReceiver!!.removeListener(this)
        this.unregisterReceiver(networkStateReceiver)
    }
}


