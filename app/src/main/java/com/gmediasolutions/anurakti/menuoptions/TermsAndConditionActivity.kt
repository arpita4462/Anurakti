package com.gmediasolutions.anurakti.menuoptions

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.R

class TermsAndConditionActivity : BaseActivity() {


    var info: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_condition)
        info = findViewById(R.id.tems_cond_web)

        spotDialog!!.show()

        try {
            spotDialog!!.dismiss()
            info!!.loadUrl("file:///android_asset/termsCondition.html")

        } catch (e: Exception) {
            e.printStackTrace()

        }
    }

    override fun onStart() {
        super.onStart()
        try {
            spotDialog!!.dismiss()
            info!!.loadUrl("file:///android_asset/termsCondition.html")

        } catch (e: Exception) {
            e.printStackTrace()

        }
    }
}
