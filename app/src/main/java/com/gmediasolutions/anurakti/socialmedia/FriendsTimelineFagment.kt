package com.gmediasolutions.anurakti.socialmedia

import android.annotation.SuppressLint
import android.content.Intent
import android.support.v4.app.Fragment
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.adapter.TimeLineRVAdapter
import com.gmediasolutions.anurakti.alert.AVLoadingDialog
import com.gmediasolutions.anurakti.alert.NetworkStateReceiver
import com.gmediasolutions.anurakti.alert.SessionManagment
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.ApiReturn
import com.gmediasolutions.anurakti.model.UserSocialModel.*
import com.wang.avi.AVLoadingIndicatorView
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class FriendsTimelineFagment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener {

    private var timelineRecycleView: RecyclerView? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var frndUserID: String? = null
    private var frndUserName: String? = null
    private var NotificationContent: String? = null
    private var regId: String? = null
    private var currentUserID: String? = null
    private var addfrnd: TextView? = null
    private var canelfrnd: TextView? = null
    private var mClient: OkHttpClient? = null
    private var post_frnd_text: TextView? = null
    private var post_frnd_pic: ImageView? = null
    private var post_error_text: TextView? = null
    private var edu_tv: TextView? = null
    private var des_tv: TextView? = null
    private var tv_profileplace: TextView? = null
    private var tv_profilename: TextView? = null
    private var coverPics: ImageView? = null
    private var profile_imageView: ImageView? = null
    private var spotdialog: AVLoadingDialog? = null
    private var networkStateReceiver: NetworkStateReceiver? = null
    var session: SessionManagment? = null
    private var user_token: String? = null
    private var user_id: String? = null
    private var id: String? = null
    private var progressBart: AVLoadingIndicatorView? = null
    private var timeline_ProgressBar: AVLoadingIndicatorView? = null

    companion object {
        fun newInstance(): FriendsTimelineFagment = FriendsTimelineFagment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myView = inflater.inflate(R.layout.fragment_frnd_timeline, container, false)

        //        initialize the layout view
        timelineRecycleView = myView.findViewById(R.id.allfrndlist_recycleView)
        timelineRecycleView = myView.findViewById(R.id.recycler_view)
        timeline_ProgressBar = myView.findViewById(R.id.timeline_ProgressBar)
        progressBart = myView.findViewById(R.id.progressBart)
        addfrnd = myView.findViewById(R.id.tv_addfrnd)
        canelfrnd = myView.findViewById(R.id.tv_cancelfrnd)
        post_error_text = myView.findViewById(R.id.post_error_text)
        edu_tv = myView.findViewById(R.id.edu_tv)
        des_tv = myView.findViewById(R.id.des_tv)
        tv_profileplace = myView.findViewById(R.id.tv_profileplace)
        tv_profilename = myView.findViewById(R.id.tv_profilename)
        profile_imageView = myView.findViewById(R.id.profile_imageView)
        coverPics = myView.findViewById(R.id.coverPic)
        mSwipeRefreshLayout = myView.findViewById(R.id.swipeContainer)
        post_frnd_text = myView.findViewById(R.id.post_timel)
        post_frnd_pic = myView.findViewById(R.id.user_picpost)

//        get intent data values
        frndUserID = arguments!!.getString("frnd_id")
        frndUserName = arguments!!.getString("frnd_name")

//        firebase for notification
//        regId = FirebaseInstanceId.getInstance().token
        mClient = OkHttpClient()


        basicFunction()
        setupFrndDetailLayout()
        setupRecyclerView()
        setupSwipeRefresh()

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
        currentUserID = user_id
    }

    private fun setupRecyclerView() {
        timelineRecycleView!!.layoutManager = LinearLayoutManager(activity, LinearLayout.VERTICAL, false)
        timelineRecycleView!!.setHasFixedSize(true)
    }

    private fun setupSwipeRefresh() {
        mSwipeRefreshLayout!!.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                getuser(frndUserID!!)
                loadProfile(frndUserID!!)
                getfrndStatus(currentUserID!!, frndUserID!!)
            }
        })
        mSwipeRefreshLayout!!.post {
            mSwipeRefreshLayout!!.setRefreshing(true)
            getuser(frndUserID!!)
            loadProfile(frndUserID!!)
            getfrndStatus(currentUserID!!, frndUserID!!)
        }
    }


    private fun setupFrndDetailLayout() {
        //initial display of btn
        addfrnd!!.visibility = View.VISIBLE
        canelfrnd!!.visibility = View.GONE
        post_frnd_text!!.visibility = View.GONE
        post_frnd_pic!!.visibility = View.GONE


        getuser(frndUserID!!)
        loadProfile(frndUserID!!)
        getfrndStatus(currentUserID!!, frndUserID!!)


        addfrnd!!.setOnClickListener {
            if (addfrnd!!.text.toString().trim().equals("Add Friend")) {
//                val userReqst = AddFrndRequest(currentUserID!!, frndUserID!!, "requesting")
//                sendFrndRequest(userReqst)
            } else if (addfrnd!!.text.toString().trim().equals("Accept Request")) {
//                acceptFrndRequest(id!!)
            }
        }
        canelfrnd!!.setOnClickListener {
//            deleteFrndRequest(id!!)
        }

        post_frnd_text!!.setOnClickListener {
            val postintent = Intent(context, AddPostActivity::class.java)
            postintent.putExtra("post_user", currentUserID)
            context!!.startActivity(postintent)
        }

    }

//    If someone send the friend request then only status API work
    private fun getfrndStatus(currentUserID: String, frndUserID: String) {
        spotdialog!!.show()
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                return chain.proceed(newRequest)
            }

        }).build()
        val retrofitobject = Retrofit.Builder().client(client).baseUrl(context!!.getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create()).build()
        val apiService = retrofitobject.create(ApiInterface::class.java)
        val call = apiService.getFriendStatus(currentUserID, frndUserID)
        call.enqueue(object : Callback<FrndStatusModel> {

            override fun onFailure(call: Call<FrndStatusModel>, t: Throwable) {
                spotdialog!!.dismiss()
            }

            override fun onResponse(call: Call<FrndStatusModel>, response: Response<FrndStatusModel>) {
                if (response.code() == 401) {
                    spotdialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context!!, LoginActivity::class.java))
                    activity!!.finish()
                } else {
                    spotdialog!!.dismiss()
                    val statusfrnd = response.body()
                    if (statusfrnd != null) {
                        progressBart!!.visibility = View.GONE


                        if (statusfrnd.data.isEmpty()) {
                            addfrnd!!.setText("Add Friend")
                            canelfrnd!!.visibility = View.GONE
                            addfrnd!!.visibility = View.VISIBLE
                        } else {
                            id = statusfrnd.data.get(0).id
                            if (statusfrnd.data.get(0).status.equals("requesting")) {
                                if (statusfrnd.data.get(0).friendId.equals(currentUserID)) {
                                    canelfrnd!!.visibility = View.VISIBLE
                                    addfrnd!!.visibility = View.VISIBLE
                                    addfrnd!!.setText("Accept Request")
                                    canelfrnd!!.setText("Cancel Request")
                                } else {
                                    addfrnd!!.visibility = View.GONE
                                    canelfrnd!!.visibility = View.VISIBLE
                                    canelfrnd!!.setText("Cancel Request")
                                }
                            } else if (statusfrnd.data.get(0).status.equals("friends")) {
                                addfrnd!!.visibility = View.GONE
                                canelfrnd!!.visibility = View.VISIBLE
                                canelfrnd!!.setText("Delete Friend")
                                post_frnd_text!!.visibility = View.VISIBLE
                                post_frnd_pic!!.visibility = View.VISIBLE
                            } else {
                                addfrnd!!.visibility = View.VISIBLE
                                addfrnd!!.setText("Add Friend")
                                canelfrnd!!.visibility = View.GONE
                                post_frnd_text!!.visibility = View.GONE
                                post_frnd_pic!!.visibility = View.GONE
                            }

                        }
                    } else {
                        canelfrnd!!.visibility = View.GONE
                        addfrnd!!.setText(getString(R.string.add_frnd))
                    }
                }
            }
        })

    }

/*  //Add friend

  private fun sendFrndRequest(users: AddFrndRequest) {
      NotificationContent = "You got a friend request."
      spotdialog!!.show()
      val requestBody = HashMap<String, AddFrndRequest>()
      requestBody.clear()
      requestBody.put("data", users)

      val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
          override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
              val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
              return chain.proceed(newRequest)
          }
      }).build()
      val retrofituser = Retrofit.Builder().client(client).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
              .addConverterFactory(GsonConverterFactory.create())
              .baseUrl(context!!.getString(R.string.base_url))
              .build()
      val apiServiceuser = retrofituser.create(ApiInterface::class.java)
      val postUser = apiServiceuser.sendRequest(requestBody)

      postUser.enqueue(object : Callback<ApiReturn> {

          override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
              spotdialog!!.dismiss()
          }

          override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
              if (response.code() == 401) {
                  spotdialog!!.dismiss()
                  session!!.logoutUser()
                  Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                  startActivity(Intent(context!!, LoginActivity::class.java))
                  activity!!.finish()
              } else {
                  if (response.isSuccessful) {
                      spotdialog!!.dismiss()
                      canelfrnd!!.visibility = View.VISIBLE
                      addfrnd!!.visibility = View.GONE
                      canelfrnd!!.setText("Cancel Request")
                      post_frnd_text!!.visibility = View.GONE
                      post_frnd_pic!!.visibility = View.GONE
                      if (context != null) {
                          Toast.makeText(context, "Friend Request Send", Toast.LENGTH_LONG).show()
                      }
                      val savenoti = NotificationRequest(frndUserID!!, "Friend Request", NotificationContent!!, "myicon", regId!!)
                      saveNotification(savenoti, regId, frndUserID!!)
                  } else {
                      spotdialog!!.dismiss()
                      if (context != null) {
                          Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                      }
                  }

              }
          }
      })
  }
*/


/*
  private fun saveNotification(savenoti: NotificationRequest, regId: String?, user_id: String) {
      spotdialog!!.show()
      val requestBody = HashMap<String, NotificationRequest>()
      requestBody.clear()
      requestBody.put("data", savenoti)
      val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
          override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
              val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
              return chain.proceed(newRequest)
          }

      }).build()
      val retrofituser = Retrofit.Builder().client(client).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
              .addConverterFactory(GsonConverterFactory.create())
              .baseUrl(context!!.getString(R.string.base_url))
              .build()
      val apiServiceuser = retrofituser.create(ApiInterface::class.java)
      val postUser = apiServiceuser.notification(requestBody)
      postUser.enqueue(object : Callback<ApiReturn> {

          override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
              spotdialog!!.dismiss()
              if (context != null){
                  Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
          }}

          override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
              if (response.code() == 401) {
                  spotdialog!!.dismiss()
                  session!!.logoutUser()
                  Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                  startActivity(Intent(context!!, LoginActivity::class.java))
                  activity!!.finish()
              } else {
                  if (response.isSuccessful) {
                      spotdialog!!.dismiss()
                      sendNotification(regId!!, user_id)
                  } else {
                      spotdialog!!.dismiss()
                  }

              }
          }
      })
  }
*/

  //Delete Friend and Cancel Friend
/*
  private fun deleteFrndRequest(id: String) {
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
      val call = apiServicef.deleteFriend(id)
      call.enqueue(object : Callback<ApiReturn> {
          override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
              spotdialog!!.dismiss()
              if (context != null) {
                  Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
              }
          }

          override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
              if (response.code() == 401) {
                  spotdialog!!.dismiss()
                  session!!.logoutUser()
                  Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                  startActivity(Intent(context!!, LoginActivity::class.java))
                  activity!!.finish()
              } else {
                  spotdialog!!.dismiss()
                  if (response.isSuccessful) {
                      addfrnd!!.visibility = View.VISIBLE
                      canelfrnd!!.visibility = View.GONE
                      addfrnd!!.setText("Add Friend")
                      post_frnd_text!!.visibility = View.GONE
                      post_frnd_pic!!.visibility = View.GONE
                      if (context != null) {
                          Toast.makeText(context, "Successfully Delete", Toast.LENGTH_SHORT).show()
                      }
                  } else {

                      if (context != null) {
                          Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                      }
                  }
              }
          }
      })


  }
*/

  //Accept Friend Request
/*
  private fun acceptFrndRequest(id: String) {
      spotdialog!!.show()
      val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
          override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
              val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
              return chain.proceed(newRequest)
          }
      }).build()
      val retrofituser = Retrofit.Builder().client(client).addCallAdapterFactory(RxJava2CallAdapterFactory.create())
              .addConverterFactory(GsonConverterFactory.create())
              .baseUrl(context!!.getString(R.string.base_url))
              .build()
      val apiServiceuser = retrofituser.create(ApiInterface::class.java)
      val postUser = apiServiceuser.acceptFriendRequest(id)

      postUser.enqueue(object : Callback<ApiReturn> {

          override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
              spotdialog!!.dismiss()
          }

          override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
              if (response.code() == 401) {
                  spotdialog!!.dismiss()
                  session!!.logoutUser()
                  Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                  startActivity(Intent(context!!, LoginActivity::class.java))
                  activity!!.finish()
              } else {

                  if (response.isSuccessful) {
                      spotdialog!!.dismiss()
                      canelfrnd!!.visibility = View.VISIBLE
                      addfrnd!!.visibility = View.GONE
                      canelfrnd!!.setText("Delete Friend")
                      post_frnd_text!!.visibility = View.VISIBLE
                      post_frnd_pic!!.visibility = View.VISIBLE
                      if (context != null) {
                          Toast.makeText(context, "Friend Request Accept", Toast.LENGTH_LONG).show()
                      }
                  } else {
                      spotdialog!!.dismiss()
                      if (context != null) {
                          Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                      }
                  }

              }
          }
      })
  }
*/

  @SuppressLint("StaticFieldLeak")
  private fun sendNotification(regId: String, user_id: String) {
      object : AsyncTask<String, String, String>() {
          override fun doInBackground(vararg params: String): String? {
              try {

                  val root = JSONObject()
                  val notification = JSONObject()
                  notification.put("body", NotificationContent)
                  notification.put("title", "Daivajnya Brahmin")
                  // notification.put("icon", "http://res.cloudinary.com/ddky6bjui/image/upload/v1505451080/ic_stat_ic_notification_qcawdk.png")
                  notification.put("click_action", "Notifiy_Activity")
                  val data = JSONObject()
                  data.put("token_id", regId)
                  data.put("icon", "myicon")
                  root.put("notification", notification)
//                    root.put("condition", user_id)
                  root.put("data", data)
                  root.put("priority", "high")
                  root.put("to", "/topics/" + user_id)
                  val result = postToFCM(root.toString())
                  return result
              } catch (ex: Exception) {
                  ex.printStackTrace()
              }
              return null
          }

          override fun onPostExecute(result: String) {
          }
      }.execute()
  }

  @Throws(IOException::class)
  fun postToFCM(bodyString: String): String {
      val JSON = MediaType.parse("application/json; charset=utf-8")
      val body = RequestBody.create(JSON, bodyString)
      val request = Request.Builder()
          .url("https://fcm.googleapis.com/fcm/send")
          .post(body)
          .addHeader("Authorization", "key=AIzaSyAyzjSF8dhDLx9vtiWqaKYkwEXSuN2XYU4")
          .build()
      val response = mClient!!.newCall(request).execute()

      return response.body()!!.string()
  }

  private fun getuser(id: String) {
      spotdialog!!.show()
      val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
          override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
              val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
              return chain.proceed(newRequest)
          }

      }).build()
      val retrofitobject = Retrofit.Builder().client(client).baseUrl(context!!.getString(R.string.base_url))
          .addConverterFactory(GsonConverterFactory.create()).build()
      val apiService = retrofitobject.create(ApiInterface::class.java)
      val call = apiService.user(id)
      call.enqueue(object : Callback<UserModel> {

          override fun onFailure(call: Call<UserModel>, t: Throwable) {
              spotdialog!!.dismiss()
          }

          override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
              if (response.code() == 401) {
                  spotdialog!!.dismiss()
                  session!!.logoutUser()
                  Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                  startActivity(Intent(context!!, LoginActivity::class.java))
                  activity!!.finish()
              } else {
                  spotdialog!!.dismiss()
                  val mproductlist = response.body()

                  if (mproductlist != null) {
                      progressBart!!.visibility = View.GONE
                      Glide.with(context!!).load(mproductlist.data.get(0).profilePic)
                          .centerCrop()
                          .into(post_frnd_pic)
                      Glide.with(context!!).load(mproductlist.data.get(0).profilePic)
                          .centerCrop()
                          .into(profile_imageView)
                      Glide.with(context!!).load(mproductlist.data.get(0).coverPic)
                          .centerCrop()
                          .placeholder(R.color.light_grey)
                          .into(coverPics)
                      if (mproductlist.data.get(0).lastName.toString().equals("null")) {
                          tv_profilename!!.text = mproductlist.data.get(0).firstName
                      } else {
                          tv_profilename!!.text = mproductlist.data.get(0).firstName + " " +
                                  mproductlist.data.get(0).lastName
                      }
                      if (mproductlist.data.get(0).currentCity.equals(null)) {
                          tv_profileplace!!.text = "Place..."
                      } else {
                          tv_profileplace!!.text = mproductlist.data.get(0).currentCity

                      }
                      if (mproductlist.data.get(0).workingIn.equals(null)) {
                          des_tv!!.text = "Designation...."
                      } else {
                          des_tv!!.text = mproductlist.data.get(0).workingIn

                      }
                      if (mproductlist.data.get(0).graduation.equals(null)) {
                          edu_tv!!.text = "Education...."
                      } else {
                          edu_tv!!.text = mproductlist.data.get(0).graduation

                      }
                  } else {
                      tv_profileplace!!.text = "Place..."
                      des_tv!!.text = "Designation...."
                      edu_tv!!.text = "Education...."
                      if (progressBart != null) {
                          progressBart!!.visibility = View.GONE
                      }
                      post_error_text!!.visibility = View.VISIBLE
                      post_error_text!!.text = "No post to display"
                  }
              }
          }
      })

  }

  override fun onStart() {
      super.onStart()
      loadProfile(frndUserID!!)
  }

  private fun loadProfile(id: String) {
      mSwipeRefreshLayout!!.setRefreshing(true)
      val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
          override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
              val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
              return chain.proceed(newRequest)
          }

      }).build()
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
                  spotdialog!!.dismiss()
                  session!!.logoutUser()
                  Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                  startActivity(Intent(context!!, LoginActivity::class.java))
                  activity!!.finish()
              } else {
                  mSwipeRefreshLayout!!.setRefreshing(false)
                  val mTimeLinelist = response.body()

                  if (mTimeLinelist != null) {
                      timeline_ProgressBar!!.visibility = View.GONE
                      if (timelineRecycleView != null) {
                          try {
                              timelineRecycleView!!.adapter = TimeLineRVAdapter(
                                  mTimeLinelist.data,
                                  R.layout.timeline_rv_view,
                                  context!!,
                                  currentUserID!!,
                                  user_token!!,
                                  frndUserID
                              )
                          } catch (e: Exception) {
                          }
                      }
                  } else {
                      if (context != null) {
                          Toast.makeText(context, "Loading Error", Toast.LENGTH_SHORT).show()
                      }
                  }
              }
          }
      })
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
      loadProfile(frndUserID!!)
      getuser(frndUserID!!)
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
