package com.gmediasolutions.anurakti.custom

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.gmediasolutions.anurakti.R
import com.wang.avi.AVLoadingIndicatorView


class CustomVendorDialog(internal var mycontext: Context, var couponCode: String) : Dialog(mycontext) {
    internal var btnApply: Button? = null
    internal var codeTV: TextView? = null

    private var progressBar: AVLoadingIndicatorView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setContentView();
        setContentView(R.layout.custom_vendor_dialog)

        btnApply = findViewById(R.id.btn_apply) as Button
        codeTV = findViewById(R.id.tv_code)

        codeTV!!.text=couponCode

        progressBar = findViewById(R.id.progressBar) as AVLoadingIndicatorView

        btnApply!!.setOnClickListener(View.OnClickListener {
            progressBar!!.visibility = View.VISIBLE
        })

    }
}