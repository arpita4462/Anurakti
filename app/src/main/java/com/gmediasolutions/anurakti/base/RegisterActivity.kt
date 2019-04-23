package com.gmediasolutions.anurakti.base

import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.alert.NetworkStateReceiver
import com.gmediasolutions.anurakti.model.ApiReturn
import com.gmediasolutions.anurakti.model.RegisterModel
import com.gmediasolutions.anurakti.model.SignUpModel
import kotlinx.android.synthetic.main.activity_register.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class RegisterActivity : AppCompatActivity(), NetworkStateReceiver.NetworkStateReceiverListener {

    private var networkStateReceiver: NetworkStateReceiver? = null

    private var pDialog: ProgressDialog? = null

    private var edtfname: EditText? = null
    private var edtlname: EditText? = null
    private var edtgender: EditText? = null
    private var edtcountry: EditText? = null
    private var edtstate: EditText? = null
    private var edtemail: EditText? = null
    private var edtmobile: EditText? = null
    private var edtpass: EditText? = null
    private var edtcnfpass: EditText? = null
    //    private var edtnumber: EditText? = null
    private var check: String? = null
    private var fname: String? = null
    private var lname: String? = null
    private var gender: String? = null
    private var country: String? = null
    private var state: String? = null
    private var email: String? = null
    private var password: String? = null
    private var confpassword: String? = null
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

        edtfname = findViewById(R.id.fname)
        edtlname = findViewById(R.id.lname)
        edtcountry = findViewById(R.id.country)
        edtstate = findViewById(R.id.state)
        edtgender = findViewById(R.id.gender)
        edtemail = findViewById(R.id.email)
        edtmobile = findViewById(R.id.mobile)
        edtpass = findViewById(R.id.password)
        edtcnfpass = findViewById(R.id.confirmpassword)
//        edtnumber = findViewById(R.id.number)

        edtfname!!.addTextChangedListener(fnameWatcher)
        edtlname!!.addTextChangedListener(lnameWatcher)
        edtstate!!.addTextChangedListener(stateWatcher)
        edtcountry!!.addTextChangedListener(countryWatcher)
        edtemail!!.addTextChangedListener(emailWatcher)
        edtpass!!.addTextChangedListener(passWatcher)
        edtcnfpass!!.addTextChangedListener(cnfpassWatcher)
        edtmobile!!.addTextChangedListener(numberWatcher)


//        validate user details and register user

        register.setOnClickListener {

            if (validateName() && validateEmail() && validatePass() && validateCnfPass()) {

                fname = edtfname!!.text.toString()
                lname = edtlname!!.text.toString()
                gender = edtgender!!.text.toString()
                country = edtcountry!!.text.toString()
                state = edtstate!!.text.toString()
                mobile = edtmobile!!.text.toString()
                email = edtemail!!.text.toString()
                password = edtpass!!.text.toString()
                confpassword = edtcnfpass!!.text.toString()

                //Validation Success
                val userRegister = SignUpModel(fname, lname!!, email!!, password!!,confpassword,true,
                    gender,country,state,mobile)
                saveUserDetail(userRegister)
//                Toast.makeText(applicationContext, "Registration Successful", Toast.LENGTH_LONG).show()

            }
        }

//        Take already registered user to login page

        login_now.setOnClickListener {

//            checkconnectionapi()
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            finish()
        }

    }

    //TextWatcher for Name -----------------------------------------------------

    internal var fnameWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //none
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //none
        }

        override fun afterTextChanged(s: Editable) {

            check = s.toString()
            edtfname!!.requestFocus()

            if (check!!.length < 4 || check!!.length > 20) {
                edtfname!!.error = "Name Must consist of 4 to 20 characters"
            }
        }

    }
    internal var lnameWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //none
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //none
        }

        override fun afterTextChanged(s: Editable) {

            check = s.toString()
            edtlname!!.requestFocus()

            if (check!!.length < 4 || check!!.length > 20) {
                edtlname!!.error = "Name Must consist of 4 to 20 characters"
            }
        }

    }
    internal var countryWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //none
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //none
        }

        override fun afterTextChanged(s: Editable) {

            check = s.toString()
            edtcountry!!.requestFocus()

            if (check!!.length < 4 || check!!.length > 20) {
                edtcountry!!.error = "Name Must consist of 4 to 20 characters"
            }
        }

    }
    internal var stateWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //none
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //none
        }

        override fun afterTextChanged(s: Editable) {

            check = s.toString()
            edtstate!!.requestFocus()

            if (check!!.length < 4 || check!!.length > 20) {
                edtstate!!.error = "Name Must consist of 4 to 20 characters"
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


    internal var numberWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //none
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //none
        }

        override fun afterTextChanged(s: Editable) {

            check = s.toString()
            edtmobile!!.requestFocus()

            if (check!!.length > 10) {
                edtmobile!!.error = "Number cannot be grated than 10 digits"
            } else if (check!!.length < 10) {
                edtmobile!!.error = "Number should be 10 digits"
            }
        }

    }



//    save instance for screen rotation

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putString("KeyEmail", edtemail!!.text.toString())
        outState.putString("KeyName", edtfname!!.text.toString())
        outState.putString( "KeyPhone", edtmobile!!.text.toString())
        outState.putString("KeyPass", edtpass!!.text.toString())
        outState.putString("KeyConPass", edtcnfpass!!.text.toString())

        super.onSaveInstanceState(outState)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        email = savedInstanceState!!.getString("KeyEmail")
        fname = savedInstanceState.getString("KeyName")
        mobile = savedInstanceState.getString("KeyPhone")
        password = savedInstanceState.getString("KeyPass")
//        edtcnfpass = savedInstanceState.getString("KeyConPass")

    }

    /*saving the data in Database*/

    private fun saveUserDetail(users: SignUpModel) {
        Log.i("response_user", users.toString())
        pDialog!!.show()
        val requestBody = HashMap<String, SignUpModel>()
        requestBody.put("data", users)
        val retrofituser = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(getString(R.string.base_url))
            .build()
        val apiServiceuser = retrofituser.create(ApiInterface::class.java)
        val postUser = apiServiceuser.registration(requestBody)

        postUser.enqueue(object : Callback<ApiReturn> {

            override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                pDialog!!.dismiss()
                Log.i("response_fail", t.localizedMessage)
                Toast.makeText(applicationContext, "Registration Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                Log.i("response_succes_msg", response.message().toString())
                Log.i("response_succes_code", response.code().toString())
                Log.i("response_succes_body", response.body().toString())
                if (response.isSuccessful) {
                    pDialog!!.dismiss()

                    val singupintent = Intent(applicationContext, LoginActivity::class.java)
                    singupintent.putExtra("user_email", email)
                    singupintent.putExtra("user_password", password)
                    startActivity(singupintent)
                    finish()
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
    private fun validateNumber(): Boolean {
        edtmobile!!.requestFocus()

        check = edtmobile!!.text.toString()
        Log.e("inside number", check!!.length.toString() + " ")
        if (check!!.length > 10) {
            return false
        } else if (check!!.length < 10) {
            return false
        }
        return true
    }

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
        edtfname!!.requestFocus()

        check = edtfname!!.text.toString()
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


