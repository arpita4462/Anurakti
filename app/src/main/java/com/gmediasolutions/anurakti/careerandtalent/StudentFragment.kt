package com.gmediasolutions.anurakti.careerandtalent

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
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
import com.gmediasolutions.anurakti.adapter.B2BProductAdapter
import com.gmediasolutions.anurakti.adapter.CandJobAdapter
import com.gmediasolutions.anurakti.alert.AVLoadingDialog
import com.gmediasolutions.anurakti.alert.NetworkStateReceiver
import com.gmediasolutions.anurakti.alert.SessionManagment
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.CandTModel.JobModel
import com.gmediasolutions.anurakti.model.CandTModel.JobModelData
import com.willowtreeapps.spruce.Spruce
import com.willowtreeapps.spruce.animation.DefaultAnimations
import com.willowtreeapps.spruce.sort.DefaultSort
import okhttp3.Interceptor
import okhttp3.OkHttpClient


class StudentFragment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener, SearchView.OnQueryTextListener,
    CandJobAdapter.JobAdapterListener {
    private var spotdialog: AVLoadingDialog? = null
    private var jobRecycleView: RecyclerView? = null
    private var searchjob: SearchView? = null
    private lateinit var jobFilterSerch: CandJobAdapter
    private var jobList: MutableList<JobModelData>? = null
    private var networkStateReceiver: NetworkStateReceiver? = null
    private var session: SessionManagment? = null
    private var user_token: String? = null
    private var user_id: String? = null
    private var spruceAnimator: Animator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val myView = inflater.inflate(R.layout.fragment_job, container, false)

//      initialize the layout view
        jobRecycleView = myView.findViewById(R.id.job_recycleView)
        searchjob = myView.findViewById(R.id.serach_view_job)

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

        jobList = ArrayList()

        searchjob!!.setOnQueryTextListener(this)
        jobFilterSerch = CandJobAdapter(jobList, R.layout.candt_rv_view, context!!, this)

        val linearLayoutManager = object : LinearLayoutManager(context) {
            override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State) {
                super.onLayoutChildren(recycler, state)
                initSpruce()
            }
        }
        jobRecycleView!!.layoutManager = linearLayoutManager
        jobRecycleView!!.setHasFixedSize(true)
        jobRecycleView!!.adapter = jobFilterSerch
        jobFilterSerch.notifyDataSetChanged()
        showdata()

    }


    @SuppressLint("ObjectAnimatorBinding")
    private fun initSpruce() {
        spruceAnimator = Spruce.SpruceBuilder(jobRecycleView)
            .sortWith(DefaultSort(100))
            .animateWith(
                DefaultAnimations.shrinkAnimator(jobRecycleView, 800),
                ObjectAnimator.ofFloat(
                    jobRecycleView,
                    "translationX",
                    -jobRecycleView!!.getWidth().toFloat(),
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
        val call = apiServicef.getJobSeeker()
        call.enqueue(object : Callback<JobModel> {
            override fun onFailure(call: Call<JobModel>, t: Throwable) {
                spotdialog!!.dismiss()
                if (context != null) {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call<JobModel>, response: Response<JobModel>) {
                if (response.code() == 401) {
                    spotdialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context!!, LoginActivity::class.java))
                    activity!!.finish()
                } else {
                    spotdialog!!.dismiss()
                    val joblists = response.body()
                    if (joblists != null) {
                        jobList!!.clear()
                        jobList!!.addAll(joblists.data)
                        jobFilterSerch.notifyDataSetChanged()
                    }

                }
            }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        jobFilterSerch.filter.filter(query)
        jobFilterSerch.notifyDataSetChanged()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        jobFilterSerch.filter.filter(newText)
        jobFilterSerch.notifyDataSetChanged()
        return false
    }

    override fun onJobSelected(job: JobModelData) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchjob!!.setQuery("", false)
        searchjob!!.setIconified(true)
    }

    /*Checking Internet Connection and Showing Message*/
    private fun showSnack(isConnected: String) {
        val message: String
//        val color: Int
        if (isConnected.equals("true")) {
//            message = getString(R.string.connect_internet)
//            color = Color.WHITE
        } else {
            message = context!!.getString(R.string.sorry_nointernet)
//            color = Color.RED
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
