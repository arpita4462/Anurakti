package com.gmediasolutions.anurakti.b2b

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
import com.gmediasolutions.anurakti.adapter.B2BPropertyAdapter
import com.gmediasolutions.anurakti.alert.AVLoadingDialog
import com.gmediasolutions.anurakti.alert.NetworkStateReceiver
import com.gmediasolutions.anurakti.alert.SessionManagment
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.B2BModel.PropertyModel
import com.gmediasolutions.anurakti.model.B2BModel.PropertyModelData

import okhttp3.Interceptor
import okhttp3.OkHttpClient


class PropertyFragment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener, SearchView.OnQueryTextListener,
    B2BPropertyAdapter.propertyAdapterListener {

    //     base fragments variable
    private var spotdialog: AVLoadingDialog? = null
    private var networkStateReceiver: NetworkStateReceiver? = null
    private var session: SessionManagment? = null
    private var user_token: String? = null
    private var user_id: String? = null

    private var propertyRecycleView: RecyclerView? = null
    private var searchjwellery: SearchView? = null
    private lateinit var propertyFilterSerch: B2BPropertyAdapter
    private var propertyList: MutableList<PropertyModelData>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

//        Inflate the layout for this fragment
        val myView = inflater.inflate(R.layout.fragment_property, container, false)

//     initialize the layout view
        propertyRecycleView = myView.findViewById(R.id.property_recycleView)
        searchjwellery = myView.findViewById(R.id.serach_view_property)

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

        propertyList = ArrayList()
        searchjwellery!!.setOnQueryTextListener(this)

        propertyFilterSerch = B2BPropertyAdapter(propertyList, R.layout.b2b_rv_view, context!!, this)

        propertyRecycleView!!.layoutManager = LinearLayoutManager(activity!!, LinearLayout.VERTICAL, false)
        propertyRecycleView!!.setHasFixedSize(true)
        propertyRecycleView!!.adapter = propertyFilterSerch
        propertyFilterSerch.notifyDataSetChanged()
        showdata()
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
        val call = apiServicef.getProperty()
        call.enqueue(object : Callback<PropertyModel> {
            override fun onFailure(call: Call<PropertyModel>, t: Throwable) {
                spotdialog!!.dismiss()
                if (context != null) {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call<PropertyModel>, response: Response<PropertyModel>) {
                if (response.code() == 401) {
                    spotdialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context!!, LoginActivity::class.java))
                    activity!!.finish()
                } else {
                    spotdialog!!.dismiss()
                    val propertylists = response.body()
                    if (propertylists != null) {
                        propertyList!!.clear()
                        propertyList!!.addAll(propertylists.data)
                        propertyFilterSerch.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        propertyFilterSerch.filter.filter(query)
        propertyFilterSerch.notifyDataSetChanged()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        propertyFilterSerch.filter.filter(newText)
        propertyFilterSerch.notifyDataSetChanged()
        return false
    }

    override fun onPropertySelected(property: PropertyModelData) {
//        Toast.makeText(context, "Selected: " + itemb2b.name, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchjwellery!!.setQuery("", false)
        searchjwellery!!.setIconified(true)
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
