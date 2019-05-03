package com.gmediasolutions.anurakti.custom

import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.support.design.widget.TextInputLayout
import android.widget.EditText
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.model.ApiReturn
import com.gmediasolutions.anurakti.model.ForgetPassRequest
import com.gmediasolutions.anurakti.model.ResetPassRequest
import com.wang.avi.AVLoadingIndicatorView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class ResetPswdDialog(internal var mycontext: Context) : Dialog(mycontext) {
    internal var btn_reset_password: Button? = null
    internal var et_email: EditText? = null
    internal var et_pass: EditText? = null
    internal var et_confpass: EditText? = null
    internal var et_otp: EditText? = null
    internal var inputLayoutEmail: TextInputLayout? = null
    internal var inputLayoutPass: TextInputLayout? = null
    internal var inputLayoutConPass: TextInputLayout? = null
    internal var inputLayoutOTP: TextInputLayout? = null

    private var progressBar: AVLoadingIndicatorView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setContentView();
        setContentView(R.layout.dialog_resetpswd)

        et_email = findViewById(R.id.femail) as EditText
        et_pass = findViewById(R.id.fpass) as EditText
        et_confpass = findViewById(R.id.fconpass) as EditText
        et_otp = findViewById(R.id.fotp) as EditText
        btn_reset_password = findViewById(R.id.btn_reset_password) as Button
        progressBar = findViewById(R.id.progressBar) as AVLoadingIndicatorView
        inputLayoutEmail = findViewById(R.id.input_layout_femail) as TextInputLayout
        inputLayoutPass = findViewById(R.id.input_layout_fpass) as TextInputLayout
        inputLayoutConPass = findViewById(R.id.input_layout_fconpass) as TextInputLayout
        inputLayoutOTP = findViewById(R.id.input_layout_fotp) as TextInputLayout

        progressBar!!.visibility = View.GONE


        btn_reset_password!!.setOnClickListener(View.OnClickListener {
            val email = et_email!!.text.toString().trim { it <= ' ' }
            val password = et_pass!!.text.toString().trim { it <= ' ' }
            val confpassword = et_confpass!!.text.toString().trim { it <= ' ' }
            val otp = et_otp!!.text.toString().trim { it <= ' ' }

            if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
                inputLayoutEmail!!.error = mycontext.getString(R.string.err_msg_email)
                return@OnClickListener
            }else if (TextUtils.isEmpty(password)) {
                inputLayoutPass!!.error = mycontext.getString(R.string.ErrorField)
                return@OnClickListener
            }else if (TextUtils.isEmpty(confpassword)) {
                inputLayoutConPass!!.error = mycontext.getString(R.string.ErrorField)
                return@OnClickListener
            }else if (password != confpassword) {
                inputLayoutConPass!!.error = mycontext.getString(R.string.con)
                return@OnClickListener
            }else if (TextUtils.isEmpty(otp) ) {
                inputLayoutOTP!!.error = mycontext.getString(R.string.ErrorField)
                return@OnClickListener
            }

            progressBar!!.visibility = View.VISIBLE
            val resetpass = ResetPassRequest(email,password,confpassword,otp)
            val requestBody = HashMap<String, ResetPassRequest>()
            requestBody.clear()
            requestBody.put("data",resetpass)


            val retrofituser = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(mycontext.getString(R.string.base_url))
                .build()
            val apiServiceuser = retrofituser.create(ApiInterface::class.java)
            val postUser = apiServiceuser.resetPassword(requestBody)

            postUser.enqueue(object : Callback<ApiReturn> {

                override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                    progressBar!!.visibility = View.GONE
                    Toast.makeText(mycontext, "Error", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                    val loginresponse=response.body()
                    if(loginresponse!=null) {
                        if (response.isSuccessful) {
                            dismiss()
                            progressBar!!.visibility = View.GONE
                            Toast.makeText(mycontext, "Password Changed Successfully ", Toast.LENGTH_SHORT).show()

                        } else {
                            progressBar!!.visibility = View.GONE
                            Toast.makeText(mycontext, "Please Try Again!!!!!", Toast.LENGTH_SHORT).show()


                        }
                    }else{
                        progressBar!!.visibility = View.GONE
                        Toast.makeText(mycontext, "Please Try Again!!!!!", Toast.LENGTH_SHORT).show()


                    }
                }

            })
        })


    }

    internal fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}