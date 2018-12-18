package com.gmediasolutions.anurakti.socialmedia

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.*
import android.widget.Toast
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.adapter.FriendsTabPageAdapter
import com.gmediasolutions.anurakti.alert.AVLoadingDialog
import com.gmediasolutions.anurakti.alert.NetworkStateReceiver
import com.gmediasolutions.anurakti.alert.SessionManagment


class UserFriendsFragment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener {
    private var user_token: String? = null
    private var user_id: String? = null
    private var frnds_tabs: TabLayout? = null
    private var frnds_viewpager: ViewPager? = null

    private var spotdialog: AVLoadingDialog? = null
    private var networkStateReceiver: NetworkStateReceiver? = null
    private var session: SessionManagment? = null

    companion object {
        fun newInstance(): UserFriendsFragment = UserFriendsFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val myView = inflater.inflate(R.layout.fragment_user_friends, container, false)
//      initialize layout view
        frnds_tabs = myView.findViewById(R.id.frnds_tabs)
        frnds_viewpager = myView.findViewById(R.id.frnds_viewpager)

        basicFunction()
        setupTabLayout()

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

    private fun setupTabLayout() {
        frnds_tabs!!.addTab(frnds_tabs!!.newTab().setText("Friends List"))
        frnds_tabs!!.addTab(frnds_tabs!!.newTab().setText("Friends Suggestion"))
        frnds_tabs!!.addTab(frnds_tabs!!.newTab().setText("Friends Request"))
        frnds_tabs!!.addTab(frnds_tabs!!.newTab().setText("Sent Request"))
        frnds_tabs!!.setTabGravity(TabLayout.GRAVITY_FILL)

        val adapter = FriendsTabPageAdapter(activity!!.supportFragmentManager, frnds_tabs!!.getTabCount())
        frnds_viewpager!!.setAdapter(adapter)
        frnds_tabs!!.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                frnds_viewpager!!.setCurrentItem(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        frnds_tabs!!.setupWithViewPager(frnds_viewpager)
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
