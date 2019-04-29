package com.gmediasolutions.anurakti.socialmedia

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.alert.AVLoadingDialog
import com.gmediasolutions.anurakti.alert.NetworkStateReceiver
import com.gmediasolutions.anurakti.alert.SessionManagment
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.UserSocialModel.AllCoverPicModel
import com.gmediasolutions.anurakti.model.UserSocialModel.AllCoverPicModelData
import com.gmediasolutions.anurakti.model.UserSocialModel.AllProfilePicModel
import com.gmediasolutions.anurakti.model.UserSocialModel.AllProfilePicModelData

import java.util.ArrayList

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FriendsPhotosFragment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener {

    private var sectionAdapter: SectionedRecyclerViewAdapter? = null
    private var spotdialog: AVLoadingDialog? = null
    private var networkStateReceiver: NetworkStateReceiver? = null
    private var userID: String? = null
    private var frndUserID: String? = null
    private var frndUserName: String? = null

    private var recyclerView: RecyclerView? = null
    private var profilepicList: MutableList<AllProfilePicModelData>? = null
    private var coverpicList: MutableList<AllCoverPicModelData>? = null

    var session: SessionManagment? = null
    private var user_token: String? = null
    private var user_id: String? = null


    companion object {
        fun newInstance(): FriendsPhotosFragment = FriendsPhotosFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val myView = inflater.inflate(R.layout.fragment_user_photos, container, false)

//        initialize all layout view
        recyclerView = myView.findViewById(R.id.recyclerview_main) as RecyclerView

//        get intent data values
        frndUserID = arguments!!.getString("frnd_id")
        frndUserName = arguments!!.getString("frnd_name")
        userID = frndUserID


        basicFunction()
        setupSectionRecyclerView()

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

    private fun setupSectionRecyclerView() {
        profilepicList = ArrayList()
        coverpicList = ArrayList()

        sectionAdapter = SectionedRecyclerViewAdapter()

        sectionAdapter!!.addSection(ProfilePhotosSection("Profile Photos", getAllProfilePhotos(userID!!)))
        sectionAdapter!!.addSection(CoverPhotosSection("Cover Photos", getAllCoverPhotos(userID!!)))

        val glm = GridLayoutManager(context, 3)
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                when (sectionAdapter!!.getSectionItemViewType(position)) {
                    SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER -> return 3
                    else -> return 1
                }
            }
        }
        recyclerView!!.layoutManager = glm
        recyclerView!!.adapter = sectionAdapter
    }


    private fun getAllCoverPhotos(user_id: String): MutableList<AllCoverPicModelData> {
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
        val call = apiServicef.getCoverPic(user_id)
        call.enqueue(object : Callback<AllCoverPicModel> {
            override fun onFailure(call: Call<AllCoverPicModel>, t: Throwable) {
                spotdialog!!.dismiss()
                if (context != null) {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call<AllCoverPicModel>, response: Response<AllCoverPicModel>) {
                val coverlists = response.body()
                if (response.code() == 401) {
                    spotdialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context!!, LoginActivity::class.java))
                    activity!!.finish()
                } else {
                    if (coverlists != null) {
                        spotdialog!!.dismiss()
                        coverpicList!!.clear()
                        coverpicList!!.addAll(coverlists.data)

                        if (true) {
                            recyclerView!!.adapter = sectionAdapter
                        } else {
                            if (context != null) {
                                Toast.makeText(context, "No image to display", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        spotdialog!!.dismiss()
                        if (context != null) {
                            Toast.makeText(context, "Image Loading Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
        return coverpicList!!
    }

    private fun getAllProfilePhotos(user_id: String): MutableList<AllProfilePicModelData> {
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
        val call = apiServicef.getAllProfilePic(user_id)
        call.enqueue(object : Callback<AllProfilePicModel> {
            override fun onFailure(call: Call<AllProfilePicModel>, t: Throwable) {
                spotdialog!!.dismiss()
                if (context != null) {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call<AllProfilePicModel>, response: Response<AllProfilePicModel>) {
                if (response.code() == 401) {
                    spotdialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context!!, LoginActivity::class.java))
                    activity!!.finish()
                } else {
                    val photolists = response.body()
                    if (photolists != null) {
                        spotdialog!!.dismiss()
                        profilepicList!!.clear()
                        profilepicList!!.addAll(photolists.data)
                        if (true) {
                            recyclerView!!.adapter = sectionAdapter

                        } else {
                            if (context != null) {
                                Toast.makeText(context, "No image to display", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        spotdialog!!.dismiss()
                        if (context != null) {
                            Toast.makeText(context, "Image Loading Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
        return profilepicList!!
    }

    override fun onResume() {
        super.onResume()

    }

    private inner class CoverPhotosSection internal constructor(
        internal var title: String,
        internal var list: List<AllCoverPicModelData>
    ) : StatelessSection(
        SectionParameters.builder()
            .itemResourceId(R.layout.photo_rv_oneview)
            .headerResourceId(R.layout.header_section)
            .build()
    ) {

        override fun getContentItemsTotal(): Int {
            return list.size
        }

        override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
            return ItemViewHolder(view)
        }

        override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val itemHolder = holder as ItemViewHolder

            val image = list[position].coverPic

            Glide.with(context!!)
                .load(image)
                .placeholder(R.drawable.noimage)
                .centerCrop()
                .into(itemHolder.imageView)
            itemHolder.rootView.setOnClickListener {
                val clickintent = Intent(context, TimelineSingleImageActivity::class.java)
                clickintent.putExtra("pic_id", list[position].picId)
                clickintent.putExtra("pic_comment", list[position].coverPic)
                context!!.startActivity(clickintent)
            }
        }

        override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
            return HeaderViewHolder(view)
        }

        override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
            val headerHolder = holder as HeaderViewHolder?

            headerHolder!!.tvTitle.text = title
        }
    }

    private inner class ProfilePhotosSection internal constructor(
        internal var title: String,
        internal var list: List<AllProfilePicModelData>
    ) : StatelessSection(
        SectionParameters.builder()
            .itemResourceId(R.layout.photo_rv_oneview)
            .headerResourceId(R.layout.header_section)
            .build()
    ) {

        override fun getContentItemsTotal(): Int {
            return list.size
        }

        override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
            return ItemViewHolder(view)
        }

        override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val itemHolder = holder as ItemViewHolder

            val image = list[position].pic

            Glide.with(context!!)
                .load(image)
                .placeholder(R.drawable.noimage)
                .centerCrop()
                .into(itemHolder.imageView)
            itemHolder.rootView.setOnClickListener {
                val clickintent = Intent(context, TimelineSingleImageActivity::class.java)
                clickintent.putExtra("pic_id", list[position].picId)
                clickintent.putExtra("pic_comment", list[position].pic)
                context!!.startActivity(clickintent)
            }
        }

        override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
            return HeaderViewHolder(view)
        }

        override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
            val headerHolder = holder as HeaderViewHolder?

            headerHolder!!.tvTitle.text = title
        }
    }

    private inner class HeaderViewHolder
    internal constructor(view: View) : RecyclerView.ViewHolder(view) {

        val tvTitle: TextView

        init {

            tvTitle = view.findViewById(R.id.header_id) as TextView
        }
    }

    private inner class ItemViewHolder internal constructor(val rootView: View) : RecyclerView.ViewHolder(rootView) {
        var imageView: ImageView? = null

        init {
            imageView = rootView.findViewById(R.id.timeline_photo_image)
        }
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
        getAllProfilePhotos(userID!!)
        getAllCoverPhotos(userID!!)
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
