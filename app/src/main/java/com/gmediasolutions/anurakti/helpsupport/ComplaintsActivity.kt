package com.gmediasolutions.anurakti.helpsupport

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.CardView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.base.MainActivity
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.model.ApiReturn
import com.gmediasolutions.anurakti.model.SupportModel.ComplaintModel
import kotlinx.android.synthetic.main.activity_complian.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ComplaintsActivity : BaseActivity() {
    var listState: Parcelable? = null
    private var subject: String? = null
    private var name: String? = null
    private var email: String? = null
    private var mobile: String? = null
    private var feedback: String? = null
    private var feedbackType: String? = null
    private var feedbackIntent: String? = null
    private var check: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complian)

        feedbackIntent = intent.getStringExtra("feedback_type")


        if (feedbackIntent.equals("assistance")){
            type_tv.text="Assistance"
            feedbackType="assistance"
        }else if (feedbackIntent.equals("complaints")){
            type_tv.text="Complaints"
            feedbackType="complaints"
        }

        setupToolbar()

        et_feedback!!.addTextChangedListener(feedbackWatcher)
        et_subject!!.addTextChangedListener(subjectWatcher)

        submit.setOnClickListener {
            subject = et_subject.getText().toString()
            feedback = et_feedback.getText().toString()
            name = et_name.getText().toString()
            mobile = et_mobile.getText().toString()
            email = et_email.getText().toString()


            if (validateFeedback() && validateSubject()) {
                val detail = ComplaintModel(subject!!,feedback!!,name!!,mobile!!,email!!,feedbackType!!)
                saveDetail(detail)
            } else {

            }
        }

    }


    private fun saveDetail(users: ComplaintModel) {
        Log.i("response_user", users.toString())
        val requestBody = HashMap<String, ComplaintModel>()
        requestBody.put("data", users)
        val retrofituser = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(getString(R.string.base_url))
            .build()
        val apiServiceuser = retrofituser.create(ApiInterface::class.java)
        val postUser = apiServiceuser.addcomplaints(requestBody!!)

        postUser.enqueue(object : Callback<ApiReturn> {

            override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                Log.i("response_fail", t.localizedMessage)
                Toast.makeText(this@ComplaintsActivity, "Registration Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                Log.i("response_succes_msg", response.message().toString())
                Log.i("response_succes_code", response.code().toString())
                Log.i("response_succes_body", response.body().toString())
                if (response.isSuccessful) {

                    Toast.makeText(this@ComplaintsActivity, "Thank you!", Toast.LENGTH_LONG).show()

                    val singupintent = Intent(this@ComplaintsActivity, HelpSupportActivity::class.java)
                    startActivity(singupintent)
                    finish()
                } else {
                        Toast.makeText(this@ComplaintsActivity, "Something went wrong.", Toast.LENGTH_LONG).show()

                }

            }

        })
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

    internal var subjectWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //none
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //none
        }

        override fun afterTextChanged(s: Editable) {

            check = s.toString()
            et_subject!!.requestFocus()

            if (check!!.length < 4 || check!!.length > 20) {
                et_subject!!.error = "Field Must consist of 4 to 20 characters"
            }
        }

    }
    internal var feedbackWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //none
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //none
        }

        override fun afterTextChanged(s: Editable) {

            check = s.toString()
            et_feedback!!.requestFocus()

            if (check!!.length < 4 ) {
                et_feedback!!.error = "Field Must consist of some characters"
            }
        }

    }
    private fun validateSubject(): Boolean {
        et_subject!!.requestFocus()

        check = et_subject!!.text.toString()
        return !(check!!.length < 4 || check!!.length > 20)

    }
    private fun validateFeedback(): Boolean {
        et_feedback!!.requestFocus()

        check = et_feedback!!.text.toString()
        return !(check!!.length < 4 )

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable("LIST_STATE_KEY ", listState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        listState = savedInstanceState!!.getParcelable("LIST_STATE_KEY")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@ComplaintsActivity, HelpSupportActivity::class.java))
    }
}