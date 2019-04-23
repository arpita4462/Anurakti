/*
package com.gmediasolutions.anurakti.newsevent

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import kotlinx.android.synthetic.main.activity_news_event.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.base.MainActivity
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.NewsModel.NewsModel
import com.gmediasolutions.anurakti.model.NewsModel.UpcomingNE
import kotlinx.android.synthetic.main.custom_toolbar.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient


class NewsEventActivity : BaseActivity(), SearchView.OnQueryTextListener, NEFilterSerch.NewsAdapterListener {

    private var neCategory: String? = null

    private lateinit var neFilterSerch: NEFilterSerch
    private var newsList: MutableList<NewsModel>? = null

    var listState: Parcelable? = null
    var searchNews: SearchView? = null
//    var spinner_news: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_event)

        searchNews = findViewById(R.id.serach_view_ne) as SearchView
//        spinner_news = findViewById(R.id.spinner_ne) as Spinner

        setupToolbar()
        setupRecycleView()
//        setupSpinner()

    }

*/
/*
    private fun setupSpinner() {
        val adpter_ne = ArrayAdapter.createFromResource(this, R.array.b2b_arrays, android.R.layout.simple_spinner_item)

        adpter_ne.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_news!!.adapter = adpter_ne

        spinner_news!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                if (view != null) {
                    if (adapterView.selectedItemPosition == 0) {
                        (adapterView.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.hintcolor
                            )
                        )
                    } else {
                        (adapterView.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.black
                            )
                        )
                    }
                    neCategory = adapterView.getItemAtPosition(i).toString()
                    if (!neCategory.equals("Select Category")) {
                        neFilterSerch.filter.filter(neCategory)
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
            }
        }
    }
*//*


    private fun setupRecycleView() {
        newsList = ArrayList()
        searchNews!!.clearFocus()

        searchNews!!.setOnQueryTextListener(this)

        val resId = R.anim.layout_animation_from_bottom
        val animation = AnimationUtils.loadLayoutAnimation(applicationContext, resId)

        neFilterSerch = NEFilterSerch(newsList, R.layout.news_rv_view, applicationContext, this)

        ne_recycleView!!.setLayoutAnimation(animation)
        ne_recycleView.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        ne_recycleView.setHasFixedSize(true)
        ne_recycleView.adapter = neFilterSerch
        neFilterSerch.notifyDataSetChanged()
        showNews()

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

    private fun showNews() {
        spotDialog!!.show()
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                return chain.proceed(newRequest)
            }

        }).build()
        val retrofitobject = Retrofit.Builder().client(client).baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create()).build()
        val apiService = retrofitobject.create(ApiInterface::class.java)
        val call = apiService.getNews()
        call.enqueue(object : Callback<UpcomingNE> {
            override fun onFailure(call: Call<UpcomingNE>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<UpcomingNE>, response: Response<UpcomingNE>) {
                if (response.code() == 401) {
                    spotDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(this@NewsEventActivity, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(this@NewsEventActivity, LoginActivity::class.java))
                    finish()
                } else {
                    val mnews = response.body()
                    if (mnews != null) {
                        spotDialog!!.dismiss()
                        newsList!!.clear()
                        newsList!!.addAll(mnews.data)
                        neFilterSerch.notifyDataSetChanged()
                    } else {
                        spotDialog!!.dismiss()
                        Toast.makeText(getApplicationContext(), "Downloading News Error", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        neFilterSerch.filter.filter(query)
        neFilterSerch.notifyDataSetChanged()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        neFilterSerch.filter.filter(newText)
        neFilterSerch.notifyDataSetChanged()
        return false
    }

    override fun onNewsSelected(news: NewsModel) {
        Toast.makeText(getApplicationContext(), "Selected: " + news.heading, Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putString("KeyUserId", user_id)
        outState.putString("KeyUserToken", user_token)
//        outState.putInt("KeyState", spinner_news!!.selectedItemPosition)
        outState.putString("KeySearch", searchNews!!.query.toString())
        outState.putParcelable("LIST_STATE_KEY ", listState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        user_id = savedInstanceState!!.getString("KeyUserId")
        user_token = savedInstanceState.getString("KeyUserToken")
//        spinner_news!!.setSelection(savedInstanceState.getInt("KeyState", 0))
        searchNews!!.setQuery(savedInstanceState.getString("KeySearch"), true)
        listState = savedInstanceState.getParcelable("LIST_STATE_KEY")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@NewsEventActivity, MainActivity::class.java))
    }
}*/
