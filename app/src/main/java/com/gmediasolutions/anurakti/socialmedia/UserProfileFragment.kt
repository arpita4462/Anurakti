package com.gmediasolutions.anurakti.socialmedia

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Base64
import android.util.Log
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
import com.gmediasolutions.anurakti.model.AllUserModel
import com.gmediasolutions.anurakti.model.ApiReturn
import com.gmediasolutions.anurakti.model.UserSocialModel.CoverPicRequest
import com.gmediasolutions.anurakti.model.UserSocialModel.ProfilePicRequest
import com.gmediasolutions.anurakti.model.UserSocialModel.UserModel
import com.gmediasolutions.anurakti.model.UserSocialModel.UserProfileRequest
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.wang.avi.AVLoadingIndicatorView
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Created by Arpita Patel on 22-11-2018.
 */
class UserProfileFragment : Fragment(), NetworkStateReceiver.NetworkStateReceiverListener {

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

    private var tv_editProfile: Button? = null


    private var date_data: MutableList<String>? = null
    private var year_data: MutableList<String>? = null
    private var month_data: MutableList<String>? = null

    private var firstName: String? = null
    private var lastName: String? = null
    private var mobileNumber: String? = null
    private var emailId: String? = null
    private var workingIn: String? = null
    private var highSchool: String? = null
    private var graduation: String? = null
    private var currentCity: String? = null
    private var permanentCity: String? = null
    private var gender: String? = null
    private var dobdate: String? = null
    private var dobmonth: String? = null
    private var dobyear: String? = null
    private var dob: String? = null

    private var coverImage: String? = null
    private var coverImageUri: Uri? = null
    private var base64imgcover: String? = null
    private var profileImage: String? = null
    private var profileImageUri: Uri? = null
    private var base64imgprofile: String? = null
    private val SELECT_GALLERY_COVER = 10

    private var progressBarF: AVLoadingIndicatorView? = null
    private var spotdialog: AVLoadingDialog? = null
    private var networkStateReceiver: NetworkStateReceiver? = null

    private var mCropImageUri: Uri? = null

    var session: SessionManagment? = null
    private var user_token: String? = null
    private var user_id: String? = null

    companion object {
        fun newInstance(): UserProfileFragment = UserProfileFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

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

        basicFunction()
        isfieldEnabled(false)
        setupAllView()



        update_pic!!.setOnClickListener {
            CropImage.startPickImageActivity(context!!, this)
        }

        update_cover!!.setOnClickListener {
            val updloadimg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(updloadimg, SELECT_GALLERY_COVER)
        }

        tv_editProfile!!.setOnClickListener {
            isfieldEnabled(true)
            btn_update!!.visibility = View.VISIBLE
            aboutus_firstNames!!.requestFocus()
        }
        btn_update!!.setOnClickListener {
            isfieldEnabled(false)
            btn_update!!.visibility = View.GONE

            firstName = aboutus_firstNames!!.text.toString()
            lastName = aboutus_LastNames!!.text.toString()
//                    aboutus_UserName.setText(userdetails.data.get(0).username.toString())
            mobileNumber = aboutus_Mobiles!!.text.toString()
            emailId = aboutus_Emails!!.text.toString()
            workingIn = aboutus_Working_ins!!.text.toString()
            highSchool = aboutus_HighSkls!!.text.toString()
            graduation = aboutus_graduations!!.text.toString()
            currentCity = aboutus_current_citys!!.text.toString()
            permanentCity = aboutus_per_citys!!.text.toString()
            dob = dobyear + "-" + dobmonth + "-" + dobdate

            if (isValidate()) {

                val updateprofile = UserProfileRequest(user_id, "alternate@mail.id", firstName!!, lastName!!, gender!!, dob!!, mobileNumber!!, emailId!!, "9999999999", workingIn!!, highSchool!!, graduation!!, currentCity!!, permanentCity!!, "password")
                updateProfile(updateprofile)
            }
        }
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

    private fun setupAllView() {

        date_data = ArrayList<String>()
        date_data!!.add("Date")
        for (i in 1..31) {
            date_data!!.add(String.format("%02d", i))
        }

        month_data = ArrayList<String>()
        month_data!!.add("Month")
        for (i in 1..12) {
            month_data!!.add(String.format("%02d", i))
        }

        year_data = ArrayList<String>()
        year_data!!.add("Year")
        for (i in 1900..2050) {
            year_data!!.add(Integer.toString(i))
        }

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
                        if (userdetails.data.get(0).profilePic.toString().isNotEmpty()) {

                            Glide.with(context!!)
                                .load(userdetails.data.get(0).profilePic)
                                .placeholder(R.drawable.noimage)
                                .centerCrop()
                                .into(profile_IV_F)

                        }
                        if (userdetails.data.get(0).coverPic.toString().isNotEmpty()) {
                            Glide.with(context!!).load(userdetails.data.get(0).coverPic)
                                .placeholder(R.drawable.noimage)
                                .centerCrop()
                                .into(coverpic_f)

                        }
                        if (userdetails.data.get(0).firstName.toString().equals("null")) {

                        } else {
                            aboutus_firstNames!!.setText(userdetails.data.get(0).firstName.toString())
                        }
                        if (userdetails.data.get(0).lastName.toString().equals("null")) {

                        } else {
                            aboutus_LastNames!!.setText(userdetails.data.get(0).lastName.toString())
                        }
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


    private fun updateProfile(saveprofile: UserProfileRequest) {
        spotdialog!!.show()
        val requestBody = HashMap<String, UserProfileRequest>()
        requestBody.clear()
        requestBody.put("data", saveprofile)

        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                return chain.proceed(newRequest)
            }

        }).build()
        val retrofituser = Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .baseUrl(context!!.getString(R.string.base_url))
                .build()
        val apiServiceuser = retrofituser.create(ApiInterface::class.java)
        val postUser = apiServiceuser.addUser(user_id!!, requestBody)

        postUser.enqueue(object : Callback<ApiReturn> {

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
                    if (response.isSuccessful) {
                        spotdialog!!.dismiss()
                        isfieldEnabled(false)
                        btn_update!!.visibility = View.GONE
                        if (context != null) {
                            Toast.makeText(context, "Successfully Upload", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        spotdialog!!.dismiss()
                        if (context != null) {
                            Toast.makeText(context, "Uploading Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_GALLERY_COVER && resultCode == Activity.RESULT_OK && data != null) {
            coverImageUri = data.data

            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context!!.getContentResolver().query(coverImageUri, filePathColumn, null, null, null)
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            coverImage = cursor.getString(columnIndex)
            cursor.close()

            val photoBitmap = MediaStore.Images.Media.getBitmap(context!!.getContentResolver(), coverImageUri)
            coverpic_f!!.setImageBitmap(photoBitmap)

            updateCover()
        }
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            profileImageUri = CropImage.getPickImageResultUri(context!!, data)
            mCropImageUri = profileImageUri
            startCropImageActivity(profileImageUri)
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data != null) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    val result_uri = result.uri
                    val myFile = File(result_uri.getPath())
                    profile_IV_F!!.setImageURI(result_uri)

                    val selectedcropImage = getImageContentUri(context, myFile)

                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor =
                        context!!.getContentResolver().query(selectedcropImage, filePathColumn, null, null, null)
                    cursor.moveToFirst()

                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    profileImage = cursor.getString(columnIndex)
                    cursor.close()

                    updateprofilePic()
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    if (context != null) {
                        Toast.makeText(context, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show()
                    }
                }
            } else {

            }
        }
    }

    private fun getImageContentUri(context: Context?, myFile: File): Uri? {
        val filePath = myFile.getAbsolutePath()
        val cursor = context!!.getContentResolver().query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf<String>(MediaStore.Images.Media._ID),
            MediaStore.Images.Media.DATA + "=? ",
            arrayOf<String>(filePath), null
        )

        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(
                cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID)
            )
            val baseUri = Uri.parse("content://media/external/images/media")
            return Uri.withAppendedPath(baseUri, "" + id)
        } else {
            if (myFile.exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                return context.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                )
            } else {
                return null
            }
        }
    }

    private fun startCropImageActivity(profileImageUri: Uri?) {
        CropImage.activity(profileImageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .setAutoZoomEnabled(true)
            .setAspectRatio(100, 100)
            .setFixAspectRatio(true)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(context!!, this)
    }

    private fun updateCover() {
        if (coverImage != null) {
            val bm = BitmapFactory.decodeFile(coverImage)
            val bao = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bao)
            val byte = bao.toByteArray()
            base64imgcover = Base64.encodeToString(byte, Base64.NO_WRAP)

            val savecoverpic = CoverPicRequest(currentUserID!!, base64imgcover!!)
            saveindatabasecover(savecoverpic)
        } else {
            if (context != null) {
                Toast.makeText(context, "Upload Image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateprofilePic() {
        if (profileImage != null) {
            val bm = BitmapFactory.decodeFile(profileImage)
            val bao = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bao)
            val byte = bao.toByteArray()
            base64imgprofile = Base64.encodeToString(byte, Base64.NO_WRAP)

            val saveprofilepic = ProfilePicRequest(currentUserID!!, base64imgprofile!!)
            saveindatabasepropic(saveprofilepic)
        } else {
            if (context != null) {
                Toast.makeText(context, "Upload Image", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun saveindatabasecover(savecoverpic: CoverPicRequest) {
        spotdialog!!.show()
        val requestBody = HashMap<String, CoverPicRequest>()
        requestBody.clear()
        requestBody.put("data", savecoverpic)
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
        val postUser = apiServiceuser.updateCoverPic(requestBody)
        postUser.enqueue(object : Callback<ApiReturn> {

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
                    if (response.isSuccessful) {
                        spotdialog!!.dismiss()
                        if (context != null) {
                            Toast.makeText(context, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        spotdialog!!.dismiss()
                        if (context != null) {
                            Toast.makeText(context, "Uploading Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        })

    }



    private fun saveindatabasepropic(savecoverpic: ProfilePicRequest) {
        spotdialog!!.show()
        val requestBody = HashMap<String, ProfilePicRequest>()
        requestBody.clear()
        requestBody.put("data", savecoverpic)
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
        val postUser = apiServiceuser.updateProfilePic(requestBody)
        postUser.enqueue(object : Callback<ApiReturn> {

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
                    if (response.isSuccessful) {
                        spotdialog!!.dismiss()
                        if (context != null) {
                            Toast.makeText(context, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        spotdialog!!.dismiss()
                        if (context != null) {
                            Toast.makeText(context, "Uploading Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        })

    }


    private fun isValidate(): Boolean {
        if (aboutus_firstNames!!.getText().toString().trim().length < 1) {
            aboutus_firstNames!!.error = getString(R.string.ErrorField)
            aboutus_firstNames!!.requestFocus()
            return false

        } else if (aboutus_Emails!!.text.toString().trim().length < 1 || isEmailValid(aboutus_Emails!!.text.toString()) == false) {
            aboutus_Emails!!.error = getString(R.string.invalid_entry)
            aboutus_Emails!!.requestFocus()
            return false

        } else
            return true

    }

    fun isEmailValid(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
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