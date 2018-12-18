package com.gmediasolutions.anurakti.base

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.support.design.widget.Snackbar
import android.support.v4.content.res.ResourcesCompat
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.alert.AVLoadingDialog
import com.gmediasolutions.anurakti.alert.CheckPermission
import com.gmediasolutions.anurakti.alert.NetworkStateReceiver
import com.gmediasolutions.anurakti.alert.SessionManagment
import com.gmediasolutions.anurakti.custom.ForgetPswdDialog
import com.gmediasolutions.anurakti.model.LoginModel.LoginRequest
import com.gmediasolutions.anurakti.model.LoginModel.LoginReturn
import com.gmediasolutions.anurakti.model.LoginModel.VerifyEmailModel
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity(), NetworkStateReceiver.NetworkStateReceiverListener {

    private var session: SessionManagment? = null
    var user_token: String? = null
    var user_id: String? = null

    private var networkStateReceiver: NetworkStateReceiver? = null

    private var pDialog: AVLoadingDialog? = null

    //    var user: FirebaseUser? = null
    private var firebase_token: String? = null
    private var email: String? = null
    private var pass: String? = null

    private var intentEmail: String? = null
    private var intentPassword: String? = null

    private var customRestpwd: ForgetPswdDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        pDialog = AVLoadingDialog(this)

//        check runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (CheckPermission().ischeckandrequestPermission(this@LoginActivity)) {
            }
        }

//        check Internet Connection
        networkStateReceiver = NetworkStateReceiver()
        networkStateReceiver!!.addListener(this)
        this.registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

//        shared prefenced
        session = SessionManagment(applicationContext)
        val loginuser: HashMap<String, String> = session!!.userDetails
        user_id = loginuser.get(SessionManagment.USER_ID)
        user_token = loginuser.get(SessionManagment.USER_TOKEN)

        Log.i("user11", loginuser.toString())


//        Intent from register class
        intentEmail = intent.getStringExtra("user_email")
        intentPassword = intent.getStringExtra("user_password")

        if (intentEmail != null && intentPassword != null) {
            email_et.setText(intentEmail)
            password_et.setText(intentPassword!!)
        }
//        check user is loggedin from session
        if (session!!.UserisLoggedIn) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
        }

        register_now.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            finish()
        }

        forgot_pass.setOnClickListener {
            customRestpwd = ForgetPswdDialog(this)
            customRestpwd!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            customRestpwd!!.window!!.attributes.windowAnimations = R.style.DialogAnimation
            customRestpwd!!.setCanceledOnTouchOutside(false)
            customRestpwd!!.show()
        }


        login_button.setOnClickListener {
            email = email_et.getText().toString()
            pass = password_et.getText().toString()

//        pDialog!!.show()

            if (validateUsername(email!!) && validatePassword(pass!!)) {
                val userlogindetail = LoginRequest(email!!, pass!!)
                attemptIntentLogin(userlogindetail)
//                attempdemologin(email!!,pass!!)
            } else {

            }
        }


    }

//    private fun attempdemologin(email: String, pass: String) {
//        input_layout_email.setErrorEnabled(false)
//        input_layout_pass.setErrorEnabled(false)
//
//        if (email=="admin@domain.com" && pass=="admin"){
//            session!!.createLoginSession(email,pass)
//            //login for testing ,no validation
//            val loginSuccess = Intent(this@LoginActivity, UserSocialActivity::class.java)
//            startActivity(loginSuccess)
//            finish()
//        }else{
//            Toast.makeText(this,"Authentication Fail",Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun attemptIntentLogin(userslogin: LoginRequest) {
        pDialog!!.show()
        val requestBody = java.util.HashMap<String, LoginRequest>()
        requestBody.put("data", userslogin)


        val retrofituser = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(getString(R.string.base_url))
            .build()
        val apiServiceuser = retrofituser.create(ApiInterface::class.java)
        val postUser = apiServiceuser.login(requestBody)

        postUser.enqueue(object : Callback<LoginReturn> {

            override fun onFailure(call: Call<LoginReturn>, t: Throwable) {
                pDialog!!.dismiss()
                Log.i("user_login_failure", t.localizedMessage.toString())
                Toast.makeText(applicationContext, "Authentication Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<LoginReturn>, response: Response<LoginReturn>) {
                if (response.code() == 401) {
                    pDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@LoginActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@LoginActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val loginresponse = response.body()
                    if (loginresponse!!.result != null) {
                        Log.i("user_login_success", response.body().toString())

                        val user_responseid = loginresponse.result.userData.userId
                        val user_responsetoken = loginresponse.result.token
                        if (response.isSuccessful) {
                            pDialog!!.dismiss()
//                            subscribeToPushService(user_responseid)
                            if (loginresponse.response.equals("error")) {
                                pDialog!!.dismiss()
                                Toast.makeText(applicationContext, "Authentication Error", Toast.LENGTH_LONG).show()
                            } else {
                                pDialog!!.dismiss()
                                checkIfEmailVerified(user_responseid, user_responsetoken)
                            }
                        } else {
                            pDialog!!.dismiss()
                            Toast.makeText(applicationContext, "Authentication Error", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        pDialog!!.dismiss()
                        Toast.makeText(applicationContext, "Authentication Error", Toast.LENGTH_LONG).show()

                    }
                }
            }
        })

    }

//  If email of user is verified then only intent will work i.e. go to main activity
    private fun checkIfEmailVerified(user_responseid: String, user_responsetoken: String) {
        pDialog!!.show()
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                return chain.proceed(newRequest)
            }

        }).build()
        val retrofitobjectf = Retrofit.Builder().client(client).baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create()).build()
        val apiServicef = retrofitobjectf.create(ApiInterface::class.java)
        val call = apiServicef.verify(user_responseid)
        call.enqueue(object : Callback<VerifyEmailModel> {
            override fun onFailure(call: Call<VerifyEmailModel>, t: Throwable) {
                pDialog!!.dismiss()
                Toast.makeText(this@LoginActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<VerifyEmailModel>, response: Response<VerifyEmailModel>) {
                if (response.code() == 401) {
                    pDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@LoginActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@LoginActivity, LoginActivity::class.java))
                    finish()
                } else {
                    pDialog!!.dismiss()
                    val verifymail = response.body()
                    if (verifymail != null) {
                        session!!.createLoginSession(user_responseid, user_responsetoken)
                        val singupintent = Intent(applicationContext, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(singupintent)
                        finish()
                    } else {
                        pDialog!!.dismiss()
                        Toast.makeText(applicationContext, "Verify Your Email-Id", Toast.LENGTH_LONG).show()
                    }
                }
            }

        })
    }

    //subscribe for notification
//  private fun subscribeToPushService(user_id: String) {
//      FirebaseMessaging.getInstance().subscribeToTopic(user_id)
//      firebase_token = FirebaseInstanceId.getInstance().getToken()
//  }


    //saved instance for screen rotation
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putString("KeyEmail", email_et.text.toString())
        outState.putString("KeyPass", password_et.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        email = savedInstanceState!!.getString("KeyEmail")
        pass = savedInstanceState.getString("KeyPass")
    }


    private fun validatePassword(pass: String): Boolean {
        password_et.requestFocus()
        if (pass.length < 4 || pass.length > 20) {
            password_et.setError("Password Must consist of 4 to 20 characters")
//          email_et.setErrorEnabled(false)
            password_et.requestFocus()
            return false
        }
        return true
    }

    private fun validateUsername(email: String): Boolean {
        if (email.length < 4 || email.length > 30) {
            email_et.setError("Email Must consist of 4 to 30 characters")
            email_et.requestFocus()
            return false
        } else if (!email.matches("^[A-za-z0-9.@]+".toRegex())) {
            email_et.setError("Only . and @ characters allowed")
            email_et.requestFocus()
            return false
        } else if (!email.contains("@") || !email.contains(".")) {
            email_et.setError("Email must contain @ and .")
            email_et.requestFocus()
            return false
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
        finish()

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

    //runtime permission ask and allow true or false
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CheckPermission().permissionCode -> {
                if (grantResults.last() > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }
            }
        }
    }
}
