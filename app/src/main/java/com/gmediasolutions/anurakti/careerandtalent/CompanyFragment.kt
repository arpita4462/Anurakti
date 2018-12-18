package com.gmediasolutions.anurakti.careerandtalent

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.support.v7.widget.RecyclerView
import android.widget.SearchView
import android.widget.Toast
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.adapter.CandCompanyAdapter
import com.gmediasolutions.anurakti.adapter.CandJobAdapter
import com.gmediasolutions.anurakti.alert.AVLoadingDialog
import com.gmediasolutions.anurakti.alert.NetworkStateReceiver
import com.gmediasolutions.anurakti.alert.SessionManagment
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.CandTModel.CompanyModel
import com.gmediasolutions.anurakti.model.CandTModel.CompanyModelData
import com.willowtreeapps.spruce.Spruce
import com.willowtreeapps.spruce.animation.DefaultAnimations
import com.willowtreeapps.spruce.sort.DefaultSort
import okhttp3.Interceptor
import okhttp3.OkHttpClient


class CompanyFragment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener, SearchView.OnQueryTextListener,
    CandCompanyAdapter.CompanyAdapterListener {
    private var spotdialog: AVLoadingDialog? = null
    private var companyRecycleView: RecyclerView? = null
    private var searchcompany: SearchView? = null
    private lateinit var companyFilterSerch: CandCompanyAdapter
    private var companyList: MutableList<CompanyModelData>? = null
    private var networkStateReceiver: NetworkStateReceiver? = null
    private var session: SessionManagment? = null
    private var user_token: String? = null
    private var user_id: String? = null
    private var spruceAnimator: Animator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myView = inflater.inflate(R.layout.fragment_company, container, false)

        //        initialize the layout view
        companyRecycleView = myView.findViewById(R.id.company_recycleView)
        searchcompany = myView.findViewById(R.id.serach_view_company)

        basicFunction()
        setupRecyclerViewAndSearchView()

        return myView

    }

    private fun basicFunction() {
//        internet connection check
        networkStateReceiver = NetworkStateReceiver()
        networkStateReceiver!!.addListener(this)
        context!!.registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

//        loading view
        spotdialog = AVLoadingDialog(context!!)

//        call session managment
        session = SessionManagment(context!!)
        session!!.checkLogin()

//        get user data from session
        val loginuser: HashMap<String, String> = session!!.userDetails
        user_id = loginuser.get(SessionManagment.USER_ID)
        user_token = loginuser.get(SessionManagment.USER_TOKEN)
    }

    fun setupRecyclerViewAndSearchView() {

        companyList = ArrayList()

        searchcompany!!.setOnQueryTextListener(this)
        companyFilterSerch = CandCompanyAdapter(companyList, R.layout.candt_rv_view, context!!, this)


        val linearLayoutManager = object : LinearLayoutManager(context) {
            override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
                super.onLayoutChildren(recycler, state)
                initSpruce()
            }
        }
        companyRecycleView!!.layoutManager = linearLayoutManager
        companyRecycleView!!.setHasFixedSize(true)
        companyRecycleView!!.adapter = companyFilterSerch
        companyFilterSerch.notifyDataSetChanged()
        showdata()

    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun initSpruce() {
        spruceAnimator = Spruce.SpruceBuilder(companyRecycleView)
            .sortWith(DefaultSort(100))
            .animateWith(
                DefaultAnimations.shrinkAnimator(companyRecycleView, 800),
                ObjectAnimator.ofFloat(
                    companyRecycleView,
                    "translationX",
                    -companyRecycleView!!.getWidth().toFloat(),
                    0f
                ).setDuration(800)
            )
            .start()
    }


    private fun showdata() {
        spotdialog!!.show()
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                return chain.proceed(newRequest)
            }

        }).build()
        val retrofitobjectf = Retrofit.Builder().client(client).baseUrl(context!!.getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create()).build()
        val apiServicef = retrofitobjectf.create(ApiInterface::class.java)
        val call = apiServicef.getCompany()
        call.enqueue(object : Callback<CompanyModel> {
            override fun onFailure(call: Call<CompanyModel>, t: Throwable) {
                spotdialog!!.dismiss()
                if (context != null) {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call<CompanyModel>, response: Response<CompanyModel>) {
                if (response.code() == 401) {
                    spotdialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context!!, LoginActivity::class.java))
                    activity!!.finish()
                } else {
                    spotdialog!!.dismiss()
                    val companylists = response.body()
                    if (companylists != null) {
                        companyList!!.clear()
                        companyList!!.addAll(companylists.data)
                        companyFilterSerch.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        companyFilterSerch.filter.filter(query)
        companyFilterSerch.notifyDataSetChanged()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        companyFilterSerch.filter.filter(newText)
        companyFilterSerch.notifyDataSetChanged()
        return false
    }

    override fun onCompanySelected(company: CompanyModelData) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchcompany!!.setQuery("", false)
        searchcompany!!.setIconified(true)
    }

    /*Checking Internet Connection and Showing Message*/
    private fun showSnack(isConnected: String) {
        val message: String
        if (isConnected.equals("true")) {
        } else {
            message = context!!.getString(R.string.sorry_nointernet)
            if (context != null) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (spruceAnimator != null) {
            spruceAnimator!!.start()
        }
        showdata()
    }

    override fun networkAvailable() {
        showdata()
        showSnack("true")
    }

    override fun networkUnavailable() {
        showSnack("false")
    }

    override fun onDestroy() {
        super.onDestroy()
        networkStateReceiver!!.removeListener(this)
        context!!.unregisterReceiver(networkStateReceiver)
    }
}
