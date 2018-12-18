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
import android.support.v7.widget.RecyclerView
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.adapter.ShareNameListAdapter
import com.gmediasolutions.anurakti.model.UserSocialModel.GetFriendModel
import com.gmediasolutions.anurakti.model.UserSocialModel.GetFriendModelData
import com.gmediasolutions.anurakti.model.UserSocialModel.ShareRequest
import kotlinx.android.synthetic.main.custom_likename.*


/**
 * Created by Arpita Patel on 07-10-2018.
 */
class CustomShareNameDialog(
    var mycontext: Context,
    var user_token: String,
    var userID: String,
    private var timelineIds: String?,
    val frndUserID: String
) : Dialog(mycontext), ShareNameListAdapter.SharelistAdapterListener {

    private var frindlistRecycleView: RecyclerView? = null
    private lateinit var frindlistFilterSerch: ShareNameListAdapter
    private var frindlist: MutableList<GetFriendModelData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_likename)

        tv_likepeople.text = mycontext.getString(R.string.share_with)

        frindlistRecycleView = findViewById(R.id.list_like)


        frindlist = ArrayList()
        frindlistFilterSerch = ShareNameListAdapter(
            frindlist,
            R.layout.listname_rv_view,
            context!!,
            this,
            userID,
            user_token,
            timelineIds
        )
        frindlistRecycleView!!.layoutManager = LinearLayoutManager(mycontext, LinearLayout.VERTICAL, false)
        frindlistRecycleView!!.setHasFixedSize(true)
        frindlistRecycleView!!.adapter = frindlistFilterSerch
        frindlistFilterSerch.notifyDataSetChanged()

        showfrndlist(userID)
        share_i.setOnClickListener {
//            val sharecmmt = ShareRequest(userId = userID)
//            sharePost(sharecmmt,timelineIds,user_token)
        }
    }

/*
    fun sharePost(sharecmmt: ShareRequest, timelineId: String?, user_token: String) {
        val requestBody = HashMap<String, ShareRequest>()
        requestBody.clear()
        requestBody.put("data", sharecmmt)
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                return chain.proceed(newRequest)
            }
        }).build()
        val retrofitobjectf = Retrofit.Builder().client(client).baseUrl(mycontext.getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build()
        val apiServicef = retrofitobjectf.create(ApiInterface::class.java)
        val call = apiServicef.shareTimeline(timelineId!!,requestBody)
        call.enqueue(object : Callback<ApiReturn> {
            override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                Toast.makeText(mycontext, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                if (response.isSuccessful) {
                    val clickintent = Intent(mycontext, UserSocialActivity::class.java)
                    mycontext.startActivity(clickintent)
                    Toast.makeText(mycontext, "Post Share", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mycontext, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
*/

    private fun showfrndlist(user_id: String) {

        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest = chain.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
            chain.proceed(newRequest)
        }.build()
        val retrofitobjectf = Retrofit.Builder().client(client).baseUrl(mycontext.getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create()).build()
        val apiServicef = retrofitobjectf.create(ApiInterface::class.java)
        val call = apiServicef.getFriends(user_id)
        call.enqueue(object : Callback<GetFriendModel> {
            override fun onFailure(call: Call<GetFriendModel>, t: Throwable) {
                Toast.makeText(mycontext, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<GetFriendModel>, response: Response<GetFriendModel>) {

                val frndlists = response.body()
                if (frndlists != null) {
                    frindlist!!.clear()
                    var dataToRemove: GetFriendModelData? = null
                    for (frnd in frndlists.data) {
                        if (frnd.friendId.equals(frndUserID)) {
                            dataToRemove = frnd
                            frindlistFilterSerch.notifyDataSetChanged()
                            break
                        }
                    }
                    frindlist!!.addAll(frndlists.data)
                    if (dataToRemove != null) {
                        frindlist!!.remove(dataToRemove)
                        frindlistFilterSerch.notifyDataSetChanged()
                    }
                    frindlistFilterSerch.notifyDataSetChanged()
                } else {
                }
            }

        })
    }

    override fun onShareSelected(share: GetFriendModelData) {
    }


}
