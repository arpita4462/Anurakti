package com.gmediasolutions.anurakti.alert


import android.view.WindowManager
import android.view.Gravity
import android.R.attr.gravity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.widget.TextView
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.view.Window.FEATURE_NO_TITLE
import android.os.Bundle
import android.view.Window
import com.gmediasolutions.anurakti.R
import kotlinx.android.synthetic.main.progress_alert.*


class AVLoadingDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.progress_alert)
        this.setCancelable(false)
        avi.show()
    }
}