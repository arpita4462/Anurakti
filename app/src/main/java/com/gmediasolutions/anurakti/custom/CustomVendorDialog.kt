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
import com.gmediasolutions.anurakti.alert.AVLoadingDialog
import com.wang.avi.AVLoadingIndicatorView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class CustomVendorDialog(internal var mycontext: Context) : Dialog(mycontext) {
    internal var btnApply: Button? = null

    private var progressBar: AVLoadingIndicatorView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setContentView();
        setContentView(R.layout.custom_vendor_dialog)

        btnApply = findViewById(R.id.btn_apply) as Button
        progressBar = findViewById(R.id.progressBar) as AVLoadingIndicatorView

        btnApply!!.setOnClickListener(View.OnClickListener {
            progressBar!!.visibility = View.VISIBLE
        })

    }
}