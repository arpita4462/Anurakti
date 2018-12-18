package com.gmediasolutions.anurakti.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.custom.CustomDelete
import com.gmediasolutions.anurakti.custom.CustomLikedNameDialog
import com.gmediasolutions.anurakti.custom.CustomShareNameDialog
import com.gmediasolutions.anurakti.model.AllUserModelData
import com.gmediasolutions.anurakti.model.ApiReturn
import com.gmediasolutions.anurakti.model.UserSocialModel.CountLikeData
import com.gmediasolutions.anurakti.model.UserSocialModel.LikeRequest
import com.gmediasolutions.anurakti.model.UserSocialModel.LikeStatusModel
import com.gmediasolutions.anurakti.model.UserSocialModel.TimelineData
import com.gmediasolutions.anurakti.socialmedia.TimelineSingleImageActivity
import kotlinx.android.synthetic.main.timeline_rv_view.view.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by Arpita Patel on 09-11-2018.
 */
class TimeLineRVAdapter(
    var timelineList: List<TimelineData>?,
    val rowLayout: Int,
    val context: Context,
    val userID: String,
    val user_token: String,
    val frndUserID: String?
) : RecyclerView.Adapter<TimeLineRVAdapter.TimelineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineRVAdapter.TimelineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return TimelineViewHolder(view, userID, user_token, frndUserID!!)
    }

    override fun getItemCount(): Int {
        return timelineList!!.size
    }

    override fun onBindViewHolder(holder: TimelineViewHolder, position: Int) {

        holder.bind(timelineList!![position])

    }

    class TimelineViewHolder(v: View, val userID: String, val user_token: String, val frndUserID: String) :
        RecyclerView.ViewHolder(v) {

        var timelineIds: String? = null
        fun bind(timeline: TimelineData) {
            if (timeline.postUserId != null) {
            }
            if (timeline.userId == userID) {
                itemView.delete_img.visibility = View.VISIBLE
            } else {
                itemView.delete_img.visibility = View.GONE
            }
            timelineIds = timeline.timelineId
            getcountlike(timelineIds)

            if (timeline.status.equals("shared")) {
                Glide.with(itemView.context!!).load(timeline.postUserProfilePic)
                    .centerCrop()
                    .into(itemView.user_pic)

                itemView.post_img.visibility = View.VISIBLE
                Glide.with(itemView.context!!).load(timeline.photos)
                    .centerCrop()
                    .into(itemView.post_img)
                itemView.tv_timelname.text = timeline.postUserfirstName + " " + timeline.postUserlastName
                itemView.tv_timeltime.text = timeline.postingTime
                itemView.tv_postcontent.text = timeline.textContent
            } else {
                Glide.with(itemView.context!!).load(timeline.profilePic)
                    .centerCrop()
                    .into(itemView.user_pic)

                itemView.post_img.visibility = View.VISIBLE
                Glide.with(itemView.context!!).load(timeline.photos)
                    .centerCrop()
                    .into(itemView.post_img)
                itemView.tv_timelname.text = timeline.firstName + " " + timeline.lastName
                itemView.tv_timeltime.text = timeline.postingTime
                itemView.tv_postcontent.text = timeline.textContent
            }
            getlikeStatus(userID, user_token, timelineIds!!)
            itemView.post_img.setOnClickListener {
                val clickintent = Intent(itemView.context, TimelineSingleImageActivity::class.java)
                clickintent.putExtra("timeline_id", timelineIds)
                clickintent.putExtra("timeline_text", timeline.textContent)
                clickintent.putExtra("timeline_img", timeline.photos)
                itemView.context.startActivity(clickintent)
            }
            itemView.comment_img.setOnClickListener {
                val clickintent = Intent(itemView.context, TimelineSingleImageActivity::class.java)
                clickintent.putExtra("timeline_id", timelineIds)
                clickintent.putExtra("timeline_text", timeline.textContent)
                clickintent.putExtra("timeline_img", timeline.photos)
                itemView.context.startActivity(clickintent)
            }

            itemView.btn_share.setOnClickListener {

                val sharename = CustomShareNameDialog(itemView.context, user_token, userID, timelineIds, frndUserID)
                sharename.requestWindowFeature(Window.FEATURE_NO_TITLE)
                sharename.show()
            }
            itemView.btn_like.setOnClickListener {
                val likepost = LikeRequest(userId = userID, timelineId = timelineIds!!)
//                LikePost(likepost,user_token,timelineIds!!)

            }
            itemView.delete_img.setOnClickListener {
                val deletealert = CustomDelete(itemView.context, user_token, timelineIds!!)
                deletealert.requestWindowFeature(Window.FEATURE_NO_TITLE)
                deletealert.show()

            }
            itemView.tv_like.setOnClickListener {
                val likename = CustomLikedNameDialog(itemView.context, user_token, userID, timelineIds)
                likename.requestWindowFeature(Window.FEATURE_NO_TITLE)
                likename.show()
            }
        }


        /*
                private fun LikePost(likepost: LikeRequest, user_token: String, timelineIds: String) {
                    val requestBody = HashMap<String, LikeRequest>()
                    requestBody.clear()
                    requestBody.put("data", likepost)
                    val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
                        override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                            val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                            return chain.proceed(newRequest)
                        }
                    }).build()
                    val retrofitobjectf = Retrofit.Builder().client(client).baseUrl(itemView.context.getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build()
                    val apiServicef = retrofitobjectf.create(ApiInterface::class.java)
                    val call = apiServicef.timelineLike(requestBody)
                    call.enqueue(object : Callback<ApiReturn> {
                        override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                            Toast.makeText(itemView.context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                        }
                        override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                            if (response.code() == 401) {
                                Toast.makeText(itemView.context, "Session Out", Toast.LENGTH_LONG).show()
                                itemView.context.startActivity(Intent(itemView.context, LoginActivity::class.java))
                                (itemView.context as Activity).finish()
                            } else {
                                if (response.isSuccessful) {
                                    getcountlike(timelineIds)
                                    getlikeStatus(userID, user_token, timelineIds)
                                } else {
                                    Toast.makeText(itemView.context, "Error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    })
                }
        */
        private fun getcountlike(timelineIds: String?) {
            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                    val newRequest =
                        chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                    return chain.proceed(newRequest)
                }

            }).build()
            val retrofitobject =
                Retrofit.Builder().client(client).baseUrl(itemView.context.getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val apiService = retrofitobject.create(ApiInterface::class.java)
            val call = apiService.countTimelineLike(timelineIds!!)
            call.enqueue(object : Callback<CountLikeData> {
                override fun onFailure(call: Call<CountLikeData>, t: Throwable) {
                    Toast.makeText(itemView.context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<CountLikeData>, response: Response<CountLikeData>) {
                    if (response.code() == 401) {
                        Toast.makeText(itemView.context, "Session Out", Toast.LENGTH_LONG).show()
                        itemView.context.startActivity(Intent(itemView.context, LoginActivity::class.java))
                        (itemView.context as Activity).finish()
                    } else {
                        val count = response.body()
                        if (count != null) {
                            itemView.tv_like.text = count.data
                        } else {
                            itemView.tv_like.text = "0"
                            Toast.makeText(itemView.context, "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }

        private fun getlikeStatus(userID: String, user_token: String, timelineIds: String) {
            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                    val newRequest =
                        chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                    return chain.proceed(newRequest)
                }

            }).build()
            val retrofitobject =
                Retrofit.Builder().client(client).baseUrl(itemView.context.getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val apiService = retrofitobject.create(ApiInterface::class.java)
            val call = apiService.timelineLikeStatus(userID, timelineIds)
            call.enqueue(object : Callback<LikeStatusModel> {
                override fun onFailure(call: Call<LikeStatusModel>, t: Throwable) {
                    Toast.makeText(itemView.context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<LikeStatusModel>, response: Response<LikeStatusModel>) {
                    if (response.code() == 401) {
                        Toast.makeText(itemView.context, "Session Out", Toast.LENGTH_LONG).show()
                        itemView.context.startActivity(Intent(itemView.context, LoginActivity::class.java))
                        (itemView.context as Activity).finish()
                    } else {
                        val likelists = response.body()
                        if (likelists != null) {
                            getcountlike(timelineIds)
                            if (likelists.data != null) {
                                if (likelists.data.size != 0) {
                                    if (likelists.data.get(0).status.equals("liked")) {
                                        itemView.tv_like.text = "Unlike"
                                        itemView.likeimg.setBackgroundResource(R.drawable.baseline_thumb_down_alt_24)
                                    } else {
                                        itemView.tv_like.text = "Like"
                                        itemView.likeimg.setBackgroundResource(R.drawable.baseline_thumb_up_alt_24)
                                    }
                                }
                            }
//                        Toast.makeText(itemView.context, "Post Unliked", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(itemView.context, "Downloading Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }

    }
}
