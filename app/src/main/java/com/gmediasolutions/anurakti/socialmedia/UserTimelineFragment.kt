package com.gmediasolutions.anurakti.socialmedia

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.*
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.adapter.TimeLineRVAdapter
import com.gmediasolutions.anurakti.alert.AVLoadingDialog
import com.gmediasolutions.anurakti.alert.NetworkStateReceiver
import com.gmediasolutions.anurakti.alert.SessionManagment
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.UserSocialModel.TimeLineModel
import com.wang.avi.AVLoadingIndicatorView
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserTimelineFragment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener {

    private var timelineRecycleView: RecyclerView? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var post: TextView? = null
    private var post_error_text: TextView? = null
    private var user_pic: ImageView? = null

    private var spotdialog: AVLoadingDialog? = null
    private var progressBar: AVLoadingIndicatorView? = null
    private var networkStateReceiver: NetworkStateReceiver? = null
    private var session: SessionManagment? = null
    private var user_token: String? = null
    private var user_id: String? = null
    private var userID: String? = null

    companion object {
        fun newInstance(): UserTimelineFragment = UserTimelineFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val myView = inflater.inflate(R.layout.fragment_user_timeline, container, false)

//        initialize all layout view
        post = myView.findViewById(R.id.post_timel)
        post_error_text = myView.findViewById(R.id.post_error_text)
        user_pic = myView.findViewById(R.id.user_pic)
        mSwipeRefreshLayout = myView.findViewById(R.id.swipeContainer)
        progressBar = myView.findViewById(R.id.timeline_ProgressBaravi)
        timelineRecycleView = myView.findViewById(R.id.recycler_view)

        basicFunction()
        setupRecyclerView()
        setupSwipeRefresh()
        postSomethingLayout()

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
        userID = user_id
    }

    private fun setupRecyclerView() {

        val resId = R.anim.layout_animation_from_bottom
        val animation = AnimationUtils.loadLayoutAnimation(context!!, resId)

        timelineRecycleView!!.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        timelineRecycleView!!.setHasFixedSize(true)
        timelineRecycleView!!.setLayoutAnimation(animation)

        loadTimeline(userID!!)

    }

    private fun postSomethingLayout() {

        post!!.setOnClickListener {
            val postintent = Intent(context, AddPostActivity::class.java)
            postintent.putExtra("post_user", userID)
            context!!.startActivity(postintent)
        }
    }

    private fun setupSwipeRefresh() {

        mSwipeRefreshLayout!!.setOnRefreshListener {
            loadTimeline(userID!!)
        }

        mSwipeRefreshLayout!!.post {
            mSwipeRefreshLayout!!.setRefreshing(true)
            loadTimeline(userID!!)
        }

    }

    override fun onStart() {
        super.onStart()
        loadTimeline(userID!!)
    }

    private fun loadTimeline(id: String) {
        progressBar!!.visibility = View.GONE
        mSwipeRefreshLayout!!.setRefreshing(true)

        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest = chain.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
            chain.proceed(newRequest)
        }.build()
        val retrofitobject2 = Retrofit.Builder().client(client).baseUrl(context!!.getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create()).build()
        val apiService2 = retrofitobject2.create(ApiInterface::class.java)
        val call2 = apiService2.getTimeline(id)
        call2.enqueue(object : Callback<TimeLineModel> {

            override fun onFailure(call: Call<TimeLineModel>, t: Throwable) {

                mSwipeRefreshLayout!!.setRefreshing(false)
            }

            override fun onResponse(call: Call<TimeLineModel>, response: Response<TimeLineModel>) {
                if (response.code() == 401) {
                    mSwipeRefreshLayout!!.setRefreshing(false)
                    session!!.logoutUser()
                    Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context!!, LoginActivity::class.java))
                    activity!!.finish()
                } else {
                    mSwipeRefreshLayout!!.setRefreshing(false)
                    if (response.code() == 429) {
                        loadTimeline(userID!!)

                    } else {
                        val mTimeLinelist = response.body()

                        if (mTimeLinelist != null) {
                            if (timelineRecycleView != null) {
                                try {
                                    timelineRecycleView!!.adapter = TimeLineRVAdapter(
                                        mTimeLinelist.data, R.layout.timeline_rv_view, context!!,
                                        userID!!, user_token!!, userID
                                    )
                                } catch (e: Exception) {
                                    if (context != null) {
                                        Toast.makeText(context, "Loading Error", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {

                        }
                    }
                }
            }
        })
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

    override fun networkAvailable() {
        loadTimeline(userID!!)
        showSnack("true")
    }

    override fun networkUnavailable() {
        showSnack("false")
    }

    override fun onResume() {
        super.onResume()
        loadTimeline(userID!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        networkStateReceiver!!.removeListener(this)
        context!!.unregisterReceiver(networkStateReceiver)
    }
}