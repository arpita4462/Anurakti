package com.gmediasolutions.anurakti.fragment

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
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.daimajia.slider.library.SliderLayout
import com.daimajia.slider.library.SliderTypes.DefaultSliderView
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.base.MainActivity
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.alert.AVLoadingDialog
import com.gmediasolutions.anurakti.alert.NetworkStateReceiver
import com.gmediasolutions.anurakti.alert.SessionManagment
import com.gmediasolutions.anurakti.b2b.B2BActivity
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.blogger.BloggerActivity
import com.gmediasolutions.anurakti.vendor.VendorsActivity
import com.gmediasolutions.anurakti.careerandtalent.CandTActivity
import com.gmediasolutions.anurakti.helpsupport.HelpSupportActivity
import com.gmediasolutions.anurakti.model.BannerModel
import com.gmediasolutions.anurakti.model.NewsModel.NewsModel
import com.gmediasolutions.anurakti.model.NewsModel.UpcomingNE
import com.gmediasolutions.anurakti.newsevent.NEFilterSerch
import com.gmediasolutions.anurakti.newsevent.NewsEventActivity
import com.gmediasolutions.anurakti.socialmedia.UserSocialActivity
import com.wang.avi.AVLoadingIndicatorView
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_main.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList

class MainFragment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener, NEFilterSerch.NewsAdapterListener {


    private var sectionAdapter: SectionedRecyclerViewAdapter? = null
    var mActivity: MainActivity? = null
    private var sliderShow: SliderLayout? = null
    private var item1Layout: LinearLayout? = null
    private var item2Layout: LinearLayout? = null
    private var item3Layout: LinearLayout? = null
    private var item4Layout: LinearLayout? = null
    private var item5Layout: LinearLayout? = null
    private var item6Layout: LinearLayout? = null
    private var item7Layout: LinearLayout? = null
    private var progressBarBannerText: TextView? = null
    private var progressBarBanner: AVLoadingIndicatorView? = null

    var spotDialog: AVLoadingDialog? = null
    var networkStateReceiver: NetworkStateReceiver? = null
    var session: SessionManagment? = null
    var user_token: String? = null
    var user_id: String? = null


    var recyclerView: RecyclerView? = null

    private lateinit var neFilterSerch: NEFilterSerch
    private var newsList: MutableList<NewsModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as MainActivity?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_main, container, false)

        networkStateReceiver = NetworkStateReceiver()
        networkStateReceiver!!.addListener(this)
        context!!.registerReceiver(networkStateReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        spotDialog = AVLoadingDialog(context!!)

        session = SessionManagment(context!!)
        session!!.checkLogin()

        // get user data from session
        val loginuser: HashMap<String, String> = session!!.userDetails
        user_id = loginuser.get(SessionManagment.USER_ID)
        user_token = loginuser.get(SessionManagment.USER_TOKEN)

        inflateImageSlider(view)
        iconOption(view)

        setupRecycleView(view)

        return view
    }


    private fun inflateImageSlider(view: View) {

        // Using Image Slider -----------------------------------------------------------------------
        sliderShow = view.findViewById(R.id.main_banner_slider) as SliderLayout
        progressBarBanner = view.findViewById(R.id.progressBar_banner)
        progressBarBannerText = view.findViewById(R.id.progressBar_banner_text)


        //populating Image slider

        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                return chain.proceed(newRequest)
            }

        }).build()
        val retrofitobject =
            Retrofit.Builder().client(client).baseUrl(getString(R.string.base_url)).addConverterFactory(
                GsonConverterFactory.create()
            ).build()
        val apiService = retrofitobject.create(ApiInterface::class.java)
        val call = apiService.getBanners()
        call.enqueue(object : Callback<BannerModel> {
            override fun onFailure(call: Call<BannerModel>, t: Throwable) {
                progressBarBanner!!.visibility = View.GONE
                progressBarBannerText!!.text = "No image to display"
                sliderShow!!.visibility = View.GONE
            }

            override fun onResponse(call: Call<BannerModel>, response: Response<BannerModel>) {
                val mproductlist = response.body()
                if (mproductlist != null) {
                    progressBarBanner!!.visibility = View.GONE
                    progressBarBannerText!!.visibility = View.GONE
                    sliderShow!!.visibility = View.VISIBLE

                    val sliderImages = ArrayList<String>()
                    sliderImages.add(mproductlist.data.get(0).url)
                    sliderImages.add(mproductlist.data.get(1).url)
                    sliderImages.add(mproductlist.data.get(2).url)
                    sliderImages.add(mproductlist.data.get(3).url)
                    sliderImages.add(mproductlist.data.get(4).url)


                    for (s in sliderImages) {
                        val sliderView = DefaultSliderView(context)
                        sliderView.image(s)
                        sliderShow!!.addSlider(sliderView)
                    }

//                        sliderShow!!.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom)
                    sliderShow!!.setPresetTransformer(SliderLayout.Transformer.Accordion)

                } else {
                    progressBarBanner!!.visibility = View.GONE
                    progressBarBannerText!!.visibility = View.VISIBLE
                    progressBarBannerText!!.text = "No image to display"
                    sliderShow!!.visibility = View.GONE
                }
                if (response.code() == 401) {
                    session!!.logoutUser()
                    Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context!!, LoginActivity::class.java))
                    activity!!.finish()


                }
            }

        })

    }


    private fun iconOption(view: View) {

//        set findViewById----------------------------------
        item1Layout = view.findViewById(R.id.item1)
        item2Layout = view.findViewById(R.id.item2)
        item3Layout = view.findViewById(R.id.item3)
        item4Layout = view.findViewById(R.id.item4)
        item5Layout = view.findViewById(R.id.item5)
        item6Layout = view.findViewById(R.id.item6)
        item7Layout = view.findViewById(R.id.item7)

//        item1Layout!!.setVisibility(View.GONE)
//        item2Layout!!.setVisibility(View.GONE)
//        item3Layout!!.setVisibility(View.GONE)
//        item4Layout!!.setVisibility(View.GONE)
//        item5Layout!!.setVisibility(View.GONE)
//        item6Layout!!.setVisibility(View.GONE)
//        item7Layout!!.setVisibility(View.GONE)

//        val animSlideleft1 = AnimationUtils.loadAnimation(context, R.anim.slide_left)
//        val animSlideleft2 = AnimationUtils.loadAnimation(context, R.anim.slide_left)
//        val animSlideleft3 = AnimationUtils.loadAnimation(context, R.anim.slide_left)
//        val animSlideleft4 = AnimationUtils.loadAnimation(context, R.anim.slide_left)
//        val animSlideleft5 = AnimationUtils.loadAnimation(context, R.anim.slide_left)
//        val animSlideleft6 = AnimationUtils.loadAnimation(context, R.anim.slide_left)
//        val animSlideleft7 = AnimationUtils.loadAnimation(context, R.anim.slide_left)
//        val animSlideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up)
//        val animSlideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down)
//        val animSlideRight = AnimationUtils.loadAnimation(context, R.anim.slide_right)

//        item1Layout!!.startAnimation(animSlideleft1)
//        item2Layout!!.startAnimation(animSlideleft)
//        item3Layout!!.startAnimation(animSlideleft)
//
//        item7Layout!!.startAnimation(animSlideDown)

        YoYo.with(Techniques.RollIn).duration(1000).playOn(item1Layout)
        YoYo.with(Techniques.RotateInUpLeft).duration(1000).playOn(item2Layout)
        YoYo.with(Techniques.RotateInDownLeft).duration(1000).playOn(item3Layout)
        YoYo.with(Techniques.RollIn).duration(1000).playOn(item4Layout)
        YoYo.with(Techniques.RotateInUpLeft).duration(1000).playOn(item5Layout)
        YoYo.with(Techniques.RotateInDownLeft).duration(1000).playOn(item6Layout)
        YoYo.with(Techniques.RollIn).duration(1000).playOn(item7Layout)

//        animSlideleft1.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {
//                item1Layout!!.setVisibility(View.VISIBLE)
//            }
//
//            override fun onAnimationEnd(animation: Animation) {
//                item2Layout!!.startAnimation(animSlideleft2)
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {
//            }
//        })
//        animSlideleft2.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {
//                item2Layout!!.setVisibility(View.VISIBLE)
//            }
//
//            override fun onAnimationEnd(animation: Animation) {
//                item3Layout!!.startAnimation(animSlideleft3)
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {
//            }
//        })
//        animSlideleft3.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {
//                item3Layout!!.setVisibility(View.VISIBLE)
//            }
//
//            override fun onAnimationEnd(animation: Animation) {
//                item4Layout!!.startAnimation(animSlideleft4)
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {
//            }
//        })
//        animSlideleft4.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {
//                item4Layout!!.setVisibility(View.VISIBLE)
//            }
//
//            override fun onAnimationEnd(animation: Animation) {
//                item5Layout!!.startAnimation(animSlideleft5)
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {
//            }
//        })
//        animSlideleft5.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {
//                item5Layout!!.setVisibility(View.VISIBLE)
//            }
//
//            override fun onAnimationEnd(animation: Animation) {
//                item6Layout!!.startAnimation(animSlideleft6)
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {
//            }
//        })
//        animSlideleft6.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {
//                item6Layout!!.setVisibility(View.VISIBLE)
//            }
//
//            override fun onAnimationEnd(animation: Animation) {
//                item7Layout!!.startAnimation(animSlideleft7)
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {
//            }
//        })
//        animSlideleft7.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {
//                item7Layout!!.setVisibility(View.VISIBLE)
//            }
//
//            override fun onAnimationEnd(animation: Animation) {
////                item7Layout!!.startAnimation(animSlideRight)
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {
//            }
//        })
////        animSlideRight.setAnimationListener(object : Animation.AnimationListener {
////            override fun onAnimationStart(animation: Animation) {
////                item4Layout!!.setVisibility(View.VISIBLE)
////                item5Layout!!.setVisibility(View.VISIBLE)
////                item6Layout!!.setVisibility(View.VISIBLE)
////            }
////
////            override fun onAnimationEnd(animation: Animation) {
////                item7Layout!!.startAnimation(animSlideUp)
////            }
////
////            override fun onAnimationRepeat(animation: Animation) {
////            }
////        })
////        animSlideUp.setAnimationListener(object : Animation.AnimationListener {
////            override fun onAnimationStart(animation: Animation) {
////                item7Layout!!.setVisibility(View.VISIBLE)
////            }
////
////            override fun onAnimationEnd(animation: Animation) {
////                item7Layout!!.setVisibility(View.VISIBLE)
////            }
////
////            override fun onAnimationRepeat(animation: Animation) {
////            }
////        })
        item1Layout!!.setOnClickListener {
            startActivity(Intent(context!!, UserSocialActivity::class.java))
        }
        item2Layout!!.setOnClickListener {
            startActivity(Intent(context!!, BloggerActivity::class.java))
        }

        item3Layout!!.setOnClickListener {
            startActivity(Intent(context!!, NewsEventActivity::class.java))
        }
        item4Layout!!.setOnClickListener {
            startActivity(Intent(context!!, VendorsActivity::class.java))
        }
        item5Layout!!.setOnClickListener {
            startActivity(Intent(context!!, B2BActivity::class.java))
        }
        item6Layout!!.setOnClickListener {
            startActivity(Intent(context!!, HelpSupportActivity::class.java))
        }
        item7Layout!!.setOnClickListener {
            startActivity(Intent(context!!, CandTActivity::class.java))
        }

    }

    private fun setupRecycleView(view: View) {

        recyclerView = view.findViewById(R.id.main_ne_recycleView)
        newsList = ArrayList()

        neFilterSerch = NEFilterSerch(newsList, R.layout.news_rv_view, context!!, this)

        recyclerView!!.layoutManager = LinearLayoutManager(context!!, LinearLayout.VERTICAL, false)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.adapter = neFilterSerch
        neFilterSerch.notifyDataSetChanged()
        showNews()

    }

    private fun showNews() {
        spotDialog!!.show()
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                return chain.proceed(newRequest)
            }

        }).build()
        val retrofitobject = Retrofit.Builder().client(client).baseUrl(getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create()).build()
        val apiService = retrofitobject.create(ApiInterface::class.java)
        val call = apiService.getNews()
        call.enqueue(object : Callback<UpcomingNE> {
            override fun onFailure(call: Call<UpcomingNE>, t: Throwable) {
                spotDialog!!.dismiss()
                Toast.makeText(context!!, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<UpcomingNE>, response: Response<UpcomingNE>) {
                if (response.code() == 401) {
                    spotDialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context!!, LoginActivity::class.java))
                    activity!!.finish()
                } else {
                    val mnews = response.body()
                    if (mnews != null) {
                        spotDialog!!.dismiss()
                        newsList!!.clear()
                        newsList!!.addAll(mnews.data)
                        neFilterSerch.notifyDataSetChanged()
                    } else {
                        spotDialog!!.dismiss()
                        Toast.makeText(context!!, "Downloading News Error", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        })
    }


    override fun onNewsSelected(news: NewsModel) {
    }


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
        inflateImageSlider(view!!)
        showSnack("true")
    }

    override fun networkUnavailable() {
        showSnack("false")
    }

    override fun onResume() {
        super.onResume()
        inflateImageSlider(view!!)

    }

    override fun onDestroy() {
        super.onDestroy()
        networkStateReceiver!!.removeListener(this)
        context!!.unregisterReceiver(networkStateReceiver)
    }

}
