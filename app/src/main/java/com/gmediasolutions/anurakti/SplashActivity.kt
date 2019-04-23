package com.gmediasolutions.anurakti

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.R.id.textView
import android.widget.Toast
import believe.cht.fadeintextview.TextViewListener
import kotlinx.android.synthetic.main.splash_screen.*


/**
 * Created by Arpita Patel on 04-10-2018.
 */
class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

//        logo animation
        YoYo.with(Techniques.ZoomIn)
            .duration(2000)
            .playOn(findViewById(R.id.logo))
//        appname_tv.setTypeface(typeface)

        appname_tv.setListener(object : TextViewListener {
            override fun onTextStart() {
            }

            override fun onTextFinish() {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                finish()
            }
        })

        appname_tv.setLetterDuration(100) // sets letter duration programmatically
        appname_tv.setText("M Y  A N U R A K T I") // sets the text with animation and call it from string resources
        appname_tv.isAnimating()

    }


}
