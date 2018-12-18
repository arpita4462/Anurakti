package com.gmediasolutions.anurakti.socialmedia

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.adapter.FriendSuggetionListAdapter
import com.gmediasolutions.anurakti.alert.AVLoadingDialog
import com.gmediasolutions.anurakti.alert.NetworkStateReceiver
import com.gmediasolutions.anurakti.alert.SessionManagment
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.UserSocialModel.GetFrndSuggModel
import com.gmediasolutions.anurakti.model.UserSocialModel.GetFrndSuggModelData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FriendSuggetionFragment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener,
    SearchView.OnQueryTextListener, FriendSuggetionListAdapter.AllFrindlistAdapterListener {

    private var allfrindlistRecycleView: RecyclerView? = null
    private var searchfrindlist: SearchView? = null
    private lateinit var allfrindlistFilterSerch: FriendSuggetionListAdapter
    private var allfrindlist: MutableList<GetFrndSuggModelData>? = null
    private var userID: String? = null

    private var spotdialog: AVLoadingDialog? = null
    private var networkStateReceiver: NetworkStateReceiver? = null

    var session: SessionManagment? = null
    private var user_token: String? = null
    private var user_id: String? = null

    companion object {
        fun newInstance(): FriendSuggetionFragment = FriendSuggetionFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myView = inflater.inflate(R.layout.fragment_friendlist, container, false)

//      initialize the layout view
        allfrindlistRecycleView = myView.findViewById(R.id.allfrndlist_recycleView)
        searchfrindlist = myView.findViewById(R.id.serach_frndlist)

        basicFunction()
        setupRecyclerViewAndSearchView()

        return myView

    }

    private fun basicFunction() {

//      internet connection check
        networkStateReceiver = NetworkStateReceiver()
        networkStateReceiver!!.addListener(this)
        context!!.registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

//      loading view
        spotdialog = AVLoadingDialog(context!!)

//      call session managment
        session = SessionManagment(context!!)
        session!!.checkLogin()

//      get user data from session
        val loginuser: HashMap<String, String> = session!!.userDetails
        user_id = loginuser.get(SessionManagment.USER_ID)
        user_token = loginuser.get(SessionManagment.USER_TOKEN)
        userID = user_id
    }

    private fun setupRecyclerViewAndSearchView() {
        allfrindlist = ArrayList()


        searchfrindlist!!.setOnQueryTextListener(this)
        allfrindlistFilterSerch =
                FriendSuggetionListAdapter(allfrindlist, R.layout.frnd_list_name_rv_view, context!!, this, userID!!)

        allfrindlistRecycleView!!.layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL, false)
        allfrindlistRecycleView!!.setHasFixedSize(true)
        allfrindlistRecycleView!!.adapter = allfrindlistFilterSerch
        allfrindlistFilterSerch.notifyDataSetChanged()

        showallfrndlist()
    }


    private fun showallfrndlist() {
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
        val call = apiServicef.getFriendSuggestion(userID!!)
        call.enqueue(object : Callback<GetFrndSuggModel> {
            override fun onFailure(call: Call<GetFrndSuggModel>, t: Throwable) {
                spotdialog!!.dismiss()
                if (context != null) {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call<GetFrndSuggModel>, response: Response<GetFrndSuggModel>) {
                if (response.code() == 401) {
                    spotdialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context!!, LoginActivity::class.java))
                    activity!!.finish()
                } else {
                    val allfrndlists = response.body()
                    if (allfrndlists != null) {
                        allfrindlist!!.clear()
                        spotdialog!!.dismiss()

                        /*    var frndToRemove: AllUserdataModel? = null
                            for (frnd in allfrndlists.data) {
                                if (frnd.userId.equals(user_id)) {
                                    frndToRemove = frnd
                                    allfrindlistFilterSerch.notifyDataSetChanged()
                                    break
                                } else {
                                }
                            }*/
                        allfrindlist!!.addAll(allfrndlists.data)
                        /*     if (frndToRemove != null) {
                                 allfrindlist!!.remove(frndToRemove)
                                 allfrindlistFilterSerch.notifyDataSetChanged()
                             }*/
                        allfrindlistFilterSerch.notifyDataSetChanged()
                    } else {
                        spotdialog!!.dismiss()
                        if (context != null) {
                            Toast.makeText(context,"No Friends Added", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        allfrindlistFilterSerch.filter.filter(query)
        allfrindlistFilterSerch.notifyDataSetChanged()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        allfrindlistFilterSerch.filter.filter(newText)
        allfrindlistFilterSerch.notifyDataSetChanged()
        return false
    }

    override fun onAllfrndlistSelected(allfrndlist: GetFrndSuggModelData) {
    }


    override fun onDestroyView() {
        super.onDestroyView()
        searchfrindlist!!.setQuery("", false)
        searchfrindlist!!.setIconified(true)
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
        showallfrndlist()
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
