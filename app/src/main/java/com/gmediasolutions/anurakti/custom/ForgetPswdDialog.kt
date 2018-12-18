package com.gmediasolutions.anurakti.custom

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.support.design.widget.TextInputLayout
import android.widget.ProgressBar
import com.gmediasolutions.anurakti.R.id.email
import android.widget.EditText
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.gmediasolutions.anurakti.R
import com.wang.avi.AVLoadingIndicatorView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class ForgetPswdDialog(internal var mycontext: Context) : Dialog(mycontext) {
    internal var btn_reset_password: Button? = null
    internal var et_email: EditText? = null
    internal var inputLayoutName: TextInputLayout? = null

    private var progressBar: AVLoadingIndicatorView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setContentView();
        setContentView(R.layout.dialog_forgetpswd)

        et_email = findViewById(R.id.email) as EditText
        btn_reset_password = findViewById(R.id.btn_reset_password) as Button
        progressBar = findViewById(R.id.progressBar) as AVLoadingIndicatorView
        inputLayoutName = findViewById(R.id.input_layout_frg_id) as TextInputLayout


/*
        btn_reset_password!!.setOnClickListener(View.OnClickListener {
            val email = et_email!!.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
                inputLayoutName!!.error = mycontext.getString(R.string.err_msg_email)
                return@OnClickListener
            }

            progressBar!!.visibility = View.VISIBLE
            val forgetpwd = ForgetPassRequest(email)
            val requestBody = HashMap<String, ForgetPassRequest>()
            requestBody["data"] = forgetpwd


            val retrofituser = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mycontext.getString(R.string.base_url))
                .build()
            val apiServiceuser = retrofituser.create(ApiInterface::class.java)
            val postUser = apiServiceuser.forgotPassword(requestBody)

            postUser.enqueue(object : Callback<ApiReturn> {

                override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                    progressBar!!.visibility = View.GONE
                    Toast.makeText(mycontext, "Error", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                    val loginresponse=response.body()
                    if(loginresponse!=null) {
                        if (response.isSuccessful) {
                            progressBar!!.visibility = View.GONE
                            Toast.makeText(mycontext, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show()

                        } else {
                            progressBar!!.visibility = View.GONE
                            Toast.makeText(mycontext, "Failed to send reset email!. Try Again!!!!!", Toast.LENGTH_SHORT).show()


                        }
                    }else{
                        progressBar!!.visibility = View.GONE
                        Toast.makeText(mycontext, "Failed to send reset email!. Try Again!!!!!", Toast.LENGTH_SHORT).show()


                    }
                }

            })
        })
*/

    }

    internal fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}