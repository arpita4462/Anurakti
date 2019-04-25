package com.gmediasolutions.anurakti.vendor

import android.content.Intent
import android.support.v4.os.HandlerCompat.postDelayed
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.widget.ProgressBar
import android.support.v7.widget.RecyclerView
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.View
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.BaseActivity
import com.gmediasolutions.anurakti.PaginationScrollListener
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.adapter.VendorsRVAdapter
import com.gmediasolutions.anurakti.model.Vendors.VendorsModel
import com.gmediasolutions.anurakti.model.Vendors.VendorsModelData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class GetAllVendorsActivity : BaseActivity() {

    var adapter: VendorsRVAdapter?=null
    var linearLayoutManager: LinearLayoutManager?=null

    var rv: RecyclerView?=null
    var progressBar: ProgressBar?=null
//    private var isLoading = false
//    private var isLastPage = false
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
//    private val TOTAL_PAGES = 5
//    private var currentPage = PAGE_START
    private var apiInterface: ApiInterface? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vendor)

        rv = findViewById(R.id.vendors_recycleView) as RecyclerView
        progressBar = findViewById(R.id.main_progress) as ProgressBar

        adapter = VendorsRVAdapter(this)

        linearLayoutManager = GridLayoutManager(this, 2)
        rv!!.layoutManager = linearLayoutManager

        rv!!.itemAnimator = DefaultItemAnimator()

        rv!!.adapter = adapter

//        rv!!.addOnScrollListener(object : PaginationScrollListener(linearLayoutManager!!) {
//
//           override val totalPageCount: Int
//                get() = TOTAL_PAGES
//
//            override val isLastPage: Boolean
//                get() = false
//
//            override var isLoading: Boolean
//                get() = false
//                set(value) {
////                    this.isLoading=isLoading
//                }
//
//            override fun loadMoreItems() {
//                isLoading = true
//                currentPage += 1
//
//                // mocking network delay for API call
//                Handler().postDelayed(Runnable { loadNextPage() }, 1000)
//            }
//        })

        //init service and load data

        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                return chain.proceed(newRequest)
            }

        }).build()
        val retrofituser = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .baseUrl(getString(R.string.base_url))
            .build()
        apiInterface = retrofituser.create(ApiInterface::class.java)

        loadFirstPage()

    }


    private fun loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ")

        callTopRatedMoviesApi().enqueue(object : Callback<VendorsModel> {
            override fun onResponse(call: Call<VendorsModel>, response: Response<VendorsModel>) {
                // Got data. Send it to adapter

                val results = fetchResults(response)
                progressBar!!.visibility = View.GONE
                adapter!!.addAll(results)

//                if (currentPage <= TOTAL_PAGES)
//                    adapter!!.addLoadingFooter()
//                else
//                    isLastPage = true
            }

            override fun onFailure(call: Call<VendorsModel>, t: Throwable) {
                t.printStackTrace()
                // TODO: 08/11/16 handle failure
            }
        })

    }


    private fun fetchResults(response: Response<VendorsModel>): List<VendorsModelData> {
        val topRatedBlogs = response.body()
       var returnvalue:List<VendorsModelData> = ArrayList()
        if(topRatedBlogs!=null){
            returnvalue=topRatedBlogs!!.data
        }
        return returnvalue
    }

//    private fun loadNextPage() {
//        Log.d(TAG, "loadNextPage: $currentPage")
//
//        callTopRatedMoviesApi().enqueue(object : Callback<VendorsModel> {
//            override fun onResponse(call: Call<VendorsModel>, response: Response<VendorsModel>) {
//                adapter!!.removeLoadingFooter()
//                isLoading = false
//
//                val results = fetchResults(response)
//                adapter!!.addAll(results)
//
//                if (currentPage != TOTAL_PAGES)
//                    adapter!!.addLoadingFooter()
//                else
//                    isLastPage = true
//            }
//
//            override fun onFailure(call: Call<VendorsModel>, t: Throwable) {
//                t.printStackTrace()
//                // TODO: 08/11/16 handle failure
//            }
//        })
//    }


    private fun callTopRatedMoviesApi(): Call<VendorsModel> {
        return apiInterface!!.getVendors(
        )
    }

    companion object {

        private val TAG = "MainActivity"

        private val PAGE_START = 1
    }


}
