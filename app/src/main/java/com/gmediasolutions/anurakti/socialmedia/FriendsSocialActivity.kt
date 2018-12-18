package com.gmediasolutions.anurakti.socialmedia

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.base.MainActivity
import com.gmediasolutions.anurakti.menuoptions.AboutUsActivity
import com.gmediasolutions.anurakti.menuoptions.ContactUsActivity
import com.gmediasolutions.anurakti.menuoptions.TermsAndConditionActivity
import com.gmediasolutions.anurakti.model.LoginModel.VerifyEmailModel
import kotlinx.android.synthetic.main.custom_toolbar.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FriendsSocialActivity : BaseActivity() {
    private var getfrndId: String? = null
    private var getfrndName: String? = null

    private var fragmentManagerfrnds: FragmentManager? = null
    private var bottomNavigationfrnds: BottomNavigationView? = null

    private var timelineFagmentfrnds: FriendsTimelineFagment? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_frndsocial)

//        initialize
        bottomNavigationfrnds = findViewById(R.id.navigationView_frnds)
        fragmentManagerfrnds = supportFragmentManager

//        get intent value
        getfrndId = intent.getStringExtra("frnd_id")

        setupToolbar()
        setupbottomnavigation()
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

    @SuppressLint("RestrictedApi")
    private fun setupbottomnavigation() {
        if (bottomNavigationfrnds != null) {

            // Select first menu item by default and show Fragment accordingly.
            val menu = bottomNavigationfrnds!!.getMenu()
            selectFragmentfrnds(menu.getItem(0))
            val menuView = bottomNavigationfrnds!!.getChildAt(0) as BottomNavigationMenuView
            for (i in 0 until menuView.childCount) {
                val itemView = menuView.getChildAt(i) as BottomNavigationItemView
//                itemView.setShiftingMode(false)
                itemView.setChecked(false)
            }
            bottomNavigationfrnds!!.setOnNavigationItemSelectedListener(object :
                BottomNavigationView.OnNavigationItemSelectedListener {
                override fun onNavigationItemSelected(item: MenuItem): Boolean {

                    selectFragmentfrnds(item)
                    return false
                }
            })

        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.my_profile -> {
                startActivity(Intent(this@FriendsSocialActivity, UserSocialActivity::class.java))
            }
            R.id.terms_con -> {
                startActivity(Intent(this@FriendsSocialActivity, TermsAndConditionActivity::class.java))
            }
            R.id.about_us -> {
                startActivity(Intent(this@FriendsSocialActivity, AboutUsActivity::class.java))
            }
            R.id.contact_us -> {
                startActivity(Intent(this@FriendsSocialActivity, ContactUsActivity::class.java))
            }
            R.id.sign_out -> {
                logoutUser()
            }
            else -> {
            }
        }
        return true
    }

    private fun logoutUser() {
        spotDialog!!.show()
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                return chain.proceed(newRequest)
            }

        }).build()
        val retrofitobjectf = Retrofit.Builder().client(client).baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create()).build()
        val apiServicef = retrofitobjectf.create(ApiInterface::class.java)
        val call = apiServicef.logout()
        call.enqueue(object : Callback<VerifyEmailModel> {
            override fun onFailure(call: Call<VerifyEmailModel>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(this@FriendsSocialActivity, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<VerifyEmailModel>, response: Response<VerifyEmailModel>) {
                if (response.code() == 401) {
                    spotDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@FriendsSocialActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@FriendsSocialActivity, LoginActivity::class.java))
                    finish()
                } else {
                    spotDialog!!.dismiss()
                    val logoutuser = response.body()
                    if (logoutuser != null) {
                        session!!.logoutUser()
                        startActivity(Intent(this@FriendsSocialActivity, LoginActivity::class.java))
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        finish()
                    }
                }
            }
        })
    }

    private fun selectFragmentfrnds(item: MenuItem?) {
        item!!.setChecked(true)
        when (item.getItemId()) {
            R.id.navigation_timeline -> {
                timelineFagmentfrnds = FriendsTimelineFagment.newInstance()
                openFragment(timelineFagmentfrnds!!)
                return
            }
            R.id.navigation_profile -> {
                val aboutfragment = FriendsProfileFragment.newInstance()
                openFragment(aboutfragment)
                return
            }
            R.id.navigation_photos -> {
                val photofragment = FriendsPhotosFragment.newInstance()
                openFragment(photofragment)
                return
            }
            R.id.navigation_frnd -> {
                val frindsfragment = FriendFriendsFragment.newInstance()
                openFragment(frindsfragment)
                return
            }
        }


    }

    private fun openFragment(fragment: Fragment) {

        if (fragment == null)
            return
        if (fragmentManagerfrnds != null) {
            val ft = fragmentManagerfrnds!!.beginTransaction()
            if (ft != null) {
                ft.replace(R.id.fragmentView_frnds, fragment)
                val frnd_id = Bundle()
                frnd_id.putString("frnd_id", getfrndId)
                frnd_id.putString("frnd_name", getfrndName)
                fragment.setArguments(frnd_id)
                ft.commit()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@FriendsSocialActivity, UserSocialActivity::class.java))
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putString("KeyUserId", user_id)
        outState.putString("KeyUserToken", user_token)
        outState.putString("KeyFrndId", getfrndId)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        user_id = savedInstanceState!!.getString("KeyUserId")
        user_token = savedInstanceState.getString("KeyUserToken")
        getfrndId = savedInstanceState.getString("KeyFrndId")

    }

}
