package com.gmediasolutions.anurakti.custom

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.os.Bundle
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import kotlinx.android.synthetic.main.custom_delete.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by Arpita Patel on 07-10-2018.
 */
class CustomDelete(var mycontext: Context, var user_token: String, private var timelineIds: String) :
    Dialog(mycontext) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.custom_delete)

        btn_yes.setOnClickListener {

            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
                val newRequest = chain.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                chain.proceed(newRequest)
            }.build()
            val retrofitobjectf = Retrofit.Builder().client(client).baseUrl(mycontext.getString(R.string.base_url))
                .addConverterFactory(GsonConverterFactory.create()).build()
//            val apiServicef = retrofitobjectf.create(ApiInterface::class.java)
/*           val call = apiServicef.deleteTimeline(timelineIds)

           call.enqueue(object : Callback<ApiReturn> {
               override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                   Toast.makeText(mycontext, "No Internet Connection", Toast.LENGTH_SHORT).show()
               }

               override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                       if (response.isSuccessful) {
                       val clickintent = Intent(mycontext, UserSocialActivity::class.java)
                       mycontext.startActivity(clickintent)
                       Toast.makeText(mycontext, "Successfully Delete", Toast.LENGTH_SHORT).show()
                   } else {

                       Toast.makeText(mycontext, "Error", Toast.LENGTH_SHORT).show()
                   }
               }
           })
*/
        }


        btn_no.setOnClickListener {
            dismiss()
        }

    }
}
