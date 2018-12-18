package com.gmediasolutions.anurakti.socialmedia

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.adapter.UserFrndListAdapter
import com.gmediasolutions.anurakti.alert.AVLoadingDialog
import com.gmediasolutions.anurakti.alert.NetworkStateReceiver
import com.gmediasolutions.anurakti.alert.SessionManagment
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.UserSocialModel.GetFriendModel
import com.gmediasolutions.anurakti.model.UserSocialModel.GetFriendModelData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserFriendsListFragment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener,
    SearchView.OnQueryTextListener, UserFrndListAdapter.FrindlistAdapterListener {

    private var frindlistRecycleView: RecyclerView? = null
    private var searchfrindlist: SearchView? = null
    private lateinit var frindlistFilterSerch: UserFrndListAdapter
    private var frindlist: MutableList<GetFriendModelData>? = null
    private var userID: String? = null

    private var spotdialog: AVLoadingDialog? = null
    private var networkStateReceiver: NetworkStateReceiver? = null
    var session: SessionManagment? = null
    private var user_token: String? = null
    private var user_id: String? = null

    companion object {
        fun newInstance(): UserFriendsListFragment = UserFriendsListFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myView = inflater.inflate(R.layout.fragment_friendlist, container, false)

        //        initialize the layout view
        frindlistRecycleView = myView.findViewById(R.id.allfrndlist_recycleView)
        searchfrindlist = myView.findViewById(R.id.serach_frndlist)

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
        userID = user_id
    }

    private fun setupRecyclerViewAndSearchView() {
        frindlist = ArrayList()
        searchfrindlist!!.setOnQueryTextListener(this)
        frindlistFilterSerch = UserFrndListAdapter(frindlist, R.layout.frnd_list_name_rv_view, context!!, this)
        frindlistRecycleView!!.layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL, false)
        frindlistRecycleView!!.setHasFixedSize(true)
        frindlistRecycleView!!.adapter = frindlistFilterSerch
        frindlistFilterSerch.notifyDataSetChanged()

        showfrndlist(userID!!)
    }

    private fun showfrndlist(user_id: String) {
        spotdialog!!.show()

        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest = chain.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
            chain.proceed(newRequest)
        }.build()
        val retrofitobjectf = Retrofit.Builder().client(client).baseUrl(context!!.getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create()).build()
        val apiServicef = retrofitobjectf.create(ApiInterface::class.java)
        val call = apiServicef.getFriends(user_id)
        call.enqueue(object : Callback<GetFriendModel> {
            override fun onFailure(call: Call<GetFriendModel>, t: Throwable) {
                spotdialog!!.dismiss()
                if (context != null) {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call<GetFriendModel>, response: Response<GetFriendModel>) {
                if (response.code() == 401) {
                    spotdialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context!!, LoginActivity::class.java))
                    activity!!.finish()
                } else {
                    val frndlists = response.body()
                    if (frndlists != null) {
                        frindlist!!.clear()
                        spotdialog!!.dismiss()
                        frindlist!!.addAll(frndlists.data)
                        frindlistFilterSerch.notifyDataSetChanged()
                    } else {
                        spotdialog!!.dismiss()
                        if (context != null) {
                            Toast.makeText(context, "No Friends Added", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        frindlistFilterSerch.filter.filter(query)
        frindlistFilterSerch.notifyDataSetChanged()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        frindlistFilterSerch.filter.filter(newText)
        frindlistFilterSerch.notifyDataSetChanged()
        return false
    }

    override fun onFrndlistSelected(frndlist: GetFriendModelData) {
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchfrindlist!!.setQuery("", false)
        searchfrindlist!!.setIconified(true)
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

    override fun networkAvailable() {
        showfrndlist(userID!!)
//        showallfrndlist()
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
