package com.gmediasolutions.anurakti.custom

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.custom_likename.*
import android.support.v7.widget.RecyclerView
import android.view.View
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.adapter.LikeNameListAdapter
import com.gmediasolutions.anurakti.model.UserSocialModel.LikeNameModel
import com.gmediasolutions.anurakti.model.UserSocialModel.LikeNameModelData


/**
 * Created by Arpita Patel on 07-10-2018.
 */
class CustomLikedNameDialog(
    var mycontext: Context,
    var user_token: String,
    var userID: String,
    private var timelineIds: String?
) : Dialog(mycontext), LikeNameListAdapter.LikeAdapterListener {
    override fun onLikeSelected(like: LikeNameModelData) {
    }

    private var likeList: MutableList<LikeNameModelData>? = null
    private lateinit var likeListAdap: LikeNameListAdapter
    private lateinit var listRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_likename)
        share_i.visibility = View.GONE
        listRecyclerView = findViewById(R.id.list_like)

        likeList = ArrayList()
        likeListAdap = LikeNameListAdapter(likeList, R.layout.listname_rv_view, mycontext, this)

        listRecyclerView.layoutManager = LinearLayoutManager(mycontext, LinearLayout.VERTICAL, false)
        listRecyclerView.setHasFixedSize(true)
        listRecyclerView.adapter = likeListAdap
        likeListAdap.notifyDataSetChanged()

        showlikelist()
    }

    private fun showlikelist() {
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest = chain.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
            chain.proceed(newRequest)
        }.build()
        val retrofitobject = Retrofit.Builder().client(client).baseUrl(mycontext.getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create()).build()
        val apiService = retrofitobject.create(ApiInterface::class.java)
        val call = apiService.getNameOfTimelinePic(timelineIds!!)
        call.enqueue(object : Callback<LikeNameModel> {
            override fun onFailure(call: Call<LikeNameModel>, t: Throwable) {
                Toast.makeText(mycontext, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<LikeNameModel>, response: Response<LikeNameModel>) {
                val likelists = response.body()
                if (likelists != null) {
                    likeList!!.addAll(likelists.data)
                    likeListAdap.notifyDataSetChanged()
                    if (likeList!!.size == 0) {
                        tv_likepeople.text = mycontext.getString(R.string.no_one_liked)
                    } else {
                        likeListAdap.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(mycontext, "Downloading Error", Toast.LENGTH_SHORT).show()

                }
            }
        })
    }
}
