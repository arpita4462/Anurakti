package com.gmediasolutions.anurakti.socialmedia

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.alert.AVLoadingDialog
import com.gmediasolutions.anurakti.alert.NetworkStateReceiver
import com.gmediasolutions.anurakti.alert.SessionManagment
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.UserSocialModel.UserModel
import com.wang.avi.AVLoadingIndicatorView
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Arpita Patel on 09-10-2018.
 */
class FriendsProfileFragment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener {

    private var frndUserID: String? = null
    private var frndUserName: String? = null
    private var currentUserID: String? = null

    private var aboutus_firstNames: EditText? = null
    private var aboutus_LastNames: EditText? = null
    private var aboutus_Mobiles: EditText? = null
    private var aboutus_Emails: EditText? = null
    private var aboutus_Working_ins: EditText? = null
    private var aboutus_HighSkls: EditText? = null
    private var aboutus_graduations: EditText? = null
    private var aboutus_current_citys: EditText? = null
    private var aboutus_per_citys: EditText? = null
    private var spinner_abt_gender: Spinner? = null
    private var spinner_abt_date: Spinner? = null
    private var spinner_abt_month: Spinner? = null
    private var spinner_abt_year: Spinner? = null
    private var profile_IV_F: ImageView? = null
    private var coverpic_f: ImageView? = null
    private var update_cover: ImageView? = null
    private var update_pic: ImageView? = null
    private var btn_update: Button? = null

    private var tv_editProfile: TextView? = null


    private var date_data: MutableList<String>? = null
    private var year_data: MutableList<String>? = null
    private var month_data: MutableList<String>? = null


    private var gender: String? = null
    private var dobdate: String? = null
    private var dobmonth: String? = null
    private var dobyear: String? = null
    private var dob: String? = null

    private var progressBarF: AVLoadingIndicatorView? = null
    private var spotdialog: AVLoadingDialog? = null
    private var networkStateReceiver: NetworkStateReceiver? = null

    var session: SessionManagment? = null
    private var user_token: String? = null
    private var user_id: String? = null

    companion object {
        fun newInstance(): FriendsProfileFragment = FriendsProfileFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val myView = inflater.inflate(R.layout.fragment_user_profile, container, false)

//            initialize all layout view
        aboutus_firstNames = myView.findViewById(R.id.aboutus_firstName)
        aboutus_LastNames = myView.findViewById(R.id.aboutus_LastName)
        aboutus_Mobiles = myView.findViewById(R.id.aboutus_Mobile)
        aboutus_Emails = myView.findViewById(R.id.aboutus_Email)
        aboutus_Working_ins = myView.findViewById(R.id.aboutus_Working_in)
        aboutus_HighSkls = myView.findViewById(R.id.aboutus_HighSkl)
        aboutus_graduations = myView.findViewById(R.id.aboutus_graduation)
        aboutus_current_citys = myView.findViewById(R.id.aboutus_current_city)
        aboutus_per_citys = myView.findViewById(R.id.aboutus_per_city)
        spinner_abt_gender = myView.findViewById(R.id.spinner_pro_gender)
        spinner_abt_date = myView.findViewById(R.id.spinner_pro_date)
        spinner_abt_month = myView.findViewById(R.id.spinner_pro_month)
        spinner_abt_year = myView.findViewById(R.id.spinner_prof_year)
        update_cover = myView.findViewById(R.id.update_coveriv)
        update_pic = myView.findViewById(R.id.update_piciv)
        tv_editProfile = myView.findViewById(R.id.tv_editProfile)
        btn_update = myView.findViewById(R.id.button_update)
        progressBarF = myView.findViewById(R.id.progressBarF)
        profile_IV_F = myView.findViewById(R.id.profile_imageViewF)
        coverpic_f = myView.findViewById(R.id.coverPicf)


//        get intent data values
        frndUserID = arguments!!.getString("frnd_id")
        frndUserName = arguments!!.getString("frnd_name")
        currentUserID = frndUserID

//        initial display of view
        btn_update!!.visibility = View.GONE
        tv_editProfile!!.visibility = View.GONE
        update_pic!!.visibility = View.GONE
        update_cover!!.visibility = View.GONE

        basicFunction()
        isfieldEnabled(false)
        setupAllView()

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


    private fun setupAllView() {
        date_data = ArrayList<String>()
        date_data!!.add("Date")
        for (i in 1..31) {
            date_data!!.add(Integer.toString(i))
        }

        month_data = ArrayList<String>()
        month_data!!.add("Month")
        for (i in 1..12) {
            month_data!!.add(Integer.toString(i))
        }

        year_data = ArrayList<String>()
        year_data!!.add("Year")
        for (i in 1900..2018) {
            year_data!!.add(Integer.toString(i))
        }

//        val spinnerArrayAdapter = ArrayAdapter.createFromResource(context, date_data,android.R.layout.simple_spinner_item)

        val spinner_abt_genderA =
            ArrayAdapter.createFromResource(context!!, R.array.spinner_gender, android.R.layout.simple_spinner_item)
        val spinner_abt_dateA = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, date_data!!)
        val spinner_abt_monthA = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, month_data!!)
        val spinner_abt_yearA = ArrayAdapter(context!!, android.R.layout.simple_spinner_item, year_data!!)

        spinner_abt_genderA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_abt_dateA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_abt_monthA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_abt_yearA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner_abt_gender!!.adapter = spinner_abt_genderA
        spinner_abt_date!!.adapter = spinner_abt_dateA
        spinner_abt_month!!.adapter = spinner_abt_monthA
        spinner_abt_year!!.adapter = spinner_abt_yearA

        if (spinner_abt_gender != null) {
            spinner_abt_gender!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    if (adapterView.selectedItemPosition == 0) {
                        (adapterView.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.hintcolor
                            )
                        )
                    } else {
                        (adapterView.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.black
                            )
                        )

                    }
                    gender = adapterView.getItemAtPosition(i).toString()
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {
                }
            }
        }
        if (spinner_abt_date != null) {
            spinner_abt_date!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    if (adapterView.selectedItemPosition == 0) {
                        (adapterView.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.hintcolor
                            )
                        )
                    } else {
                        (adapterView.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.black
                            )
                        )

                    }
                    dobdate = adapterView.getItemAtPosition(i).toString()
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {
                }
            }
        }
        if (spinner_abt_month != null) {
            spinner_abt_month!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    if (adapterView.selectedItemPosition == 0) {
                        (adapterView.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.hintcolor
                            )
                        )
                    } else {
                        (adapterView.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.black
                            )
                        )

                    }
                    dobmonth = adapterView.getItemAtPosition(i).toString()
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {
                }
            }
        }
        if (spinner_abt_year != null) {
            spinner_abt_year!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                    if (adapterView.selectedItemPosition == 0) {
                        (adapterView.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.hintcolor
                            )
                        )
                    } else {
                        (adapterView.getChildAt(0) as TextView).setTextColor(
                            ContextCompat.getColor(
                                context!!,
                                R.color.black
                            )
                        )

                    }
                    dobyear = adapterView.getItemAtPosition(i).toString()
                }

                override fun onNothingSelected(adapterView: AdapterView<*>) {
                }
            }

        }
        getuserDetail(currentUserID!!)

    }

    private fun getuserDetail(user_id: String) {
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
        val call = apiService.user(user_id)
        call.enqueue(object : Callback<UserModel> {

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                spotdialog!!.dismiss()
                if (context != null) {
                    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                if (response.code() == 401) {
                    spotdialog!!.dismiss()
                    session!!.logoutUser()
                    Toast.makeText(context!!, "Session Out", Toast.LENGTH_LONG).show()
                    startActivity(Intent(context!!, LoginActivity::class.java))
                    activity!!.finish()
                } else {
                    val userdetails = response.body()

                    if (userdetails != null) {
                        spotdialog!!.dismiss()
                        progressBarF!!.visibility = View.GONE
                        Glide.with(context!!).load(userdetails.data.get(0).profilePic)
                            .centerCrop()
                            .into(profile_IV_F)
                        Glide.with(context!!).load(userdetails.data.get(0).coverPic)
                            .centerCrop()
                            .into(coverpic_f)
                        if (userdetails.data.get(0).firstName.toString().equals("null")) {

                        } else {
                            aboutus_firstNames!!.setText(userdetails.data.get(0).firstName.toString())
                        }
                        if (userdetails.data.get(0).lastName.toString().equals("null")) {

                        } else {
                            aboutus_LastNames!!.setText(userdetails.data.get(0).lastName.toString())
                        }
//                    aboutus_UserName.setText(userdetails.data.get(0).username.toString())
                        if (userdetails.data.get(0).mobileNumber.toString() != ("null")) {
                            aboutus_Mobiles!!.setText(userdetails.data.get(0).mobileNumber.toString())
                        } else {

                        }
                        aboutus_Emails!!.setText(userdetails.data.get(0).emailId.toString())
                        if (userdetails.data.get(0).workingIn.toString().equals("null")) {

                        } else {
                            aboutus_Working_ins!!.setText(userdetails.data.get(0).workingIn.toString())
                        }
                        if (userdetails.data.get(0).highSchool.toString().equals("null")) {

                        } else {
                            aboutus_HighSkls!!.setText(userdetails.data.get(0).highSchool.toString())
                        }
                        if (userdetails.data.get(0).graduation.toString().equals("null")) {

                        } else {
                            aboutus_graduations!!.setText(userdetails.data.get(0).graduation.toString())
                        }
                        if (userdetails.data.get(0).currentCity.toString().equals("null")) {

                        } else {
                            aboutus_current_citys!!.setText(userdetails.data.get(0).currentCity.toString())
                        }
                        if (userdetails.data.get(0).permanentCity.toString().equals("null")) {

                        } else {
                            aboutus_per_citys!!.setText(userdetails.data.get(0).permanentCity.toString())
                        }
                        dob = userdetails.data.get(0).dob.toString()
                        gender = userdetails.data.get(0).gender.toString()
                        spinner_abt_gender!!.setSelection(
                            (spinner_abt_gender!!.getAdapter() as ArrayAdapter<String>).getPosition(
                                gender
                            )
                        )
                        if (dob != "null") {
                            dobyear = dob!!.substring(0, 4)
                            dobmonth = dob!!.substring(5, 7)
                            dobdate = dob!!.substring(8, 10)
                            spinner_abt_date!!.setSelection(
                                (spinner_abt_date!!.getAdapter() as ArrayAdapter<String>).getPosition(
                                    dobdate
                                )
                            )
                            spinner_abt_month!!.setSelection(
                                (spinner_abt_month!!.getAdapter() as ArrayAdapter<String>).getPosition(
                                    dobmonth
                                )
                            )
                            spinner_abt_year!!.setSelection(
                                (spinner_abt_year!!.getAdapter() as ArrayAdapter<String>).getPosition(
                                    dobyear
                                )
                            )
                        } else {

                        }
                    } else {
                        spotdialog!!.dismiss()
                        if (context != null) {
                            Toast.makeText(context, "Downloading Data Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })

    }

    private fun isfieldEnabled(isEnable: Boolean) {
        aboutus_firstNames!!.isEnabled = isEnable
        aboutus_LastNames!!.isEnabled = isEnable
        aboutus_Mobiles!!.isEnabled = isEnable
        aboutus_Emails!!.isEnabled = isEnable
        aboutus_Working_ins!!.isEnabled = isEnable
        aboutus_HighSkls!!.isEnabled = isEnable
        aboutus_graduations!!.isEnabled = isEnable
        aboutus_current_citys!!.isEnabled = isEnable
        aboutus_per_citys!!.isEnabled = isEnable
        spinner_abt_gender!!.isEnabled = isEnable
        spinner_abt_date!!.isEnabled = isEnable
        spinner_abt_month!!.isEnabled = isEnable
        spinner_abt_year!!.isEnabled = isEnable
    }

    /*Checking Internet Connection and Showing Message*/
    private fun showSnack(isConnected: String) {
        val message: String
//        val color: Int
        if (isConnected.equals("true")) {
//            message =getString(R.string.connect_internet)
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
        getuserDetail(currentUserID!!)
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