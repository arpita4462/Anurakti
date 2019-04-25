package com.gmediasolutions.anurakti.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.ApiReturn
import com.gmediasolutions.anurakti.model.UserSocialModel.EditTimelineRequest
import com.gmediasolutions.anurakti.model.UserSocialModel.TimelineCommentModelData
import com.gmediasolutions.anurakti.socialmedia.TimelineSingleImageActivity
import kotlinx.android.synthetic.main.comment_rv_view.view.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class TimelineSingleImageAdapter(
    val commentList: List<TimelineCommentModelData>,
    val rowLayout: Int,
    val context: Context,
    val user_id: String,
    val user_token: String,
    val gettimelineId: String,
    val gettimelinetext: String,
    val gettimelineImage: String
) : RecyclerView.Adapter<TimelineSingleImageAdapter.CommentViewHolder>() {

    private var current_userID: String? = null
    private var current_user_token: String? = null
    private var current_timelineid: String? = null
    private var current_timelinetext: String? = null
    private var current_timelineimage: String? = null

    init {
        this.current_userID = user_id
        this.current_user_token = user_token
        this.current_timelineid = gettimelineId
        this.current_timelineimage = gettimelineImage
        this.current_timelinetext = gettimelinetext
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineSingleImageAdapter.CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return CommentViewHolder(view)
    }


    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(
            commentList[position],
            position,
            current_userID,
            current_user_token,
            current_timelinetext!!,
            current_timelineimage!!,
            current_timelineid!!
        )
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    /*fun remove(item: TimelineCommentModelData) {
        val position = commentList.indexOf(item)
        commentList.remove(position)
        notifyItemRemoved(position)
    }*/
    class CommentViewHolder(v: View) : RecyclerView.ViewHolder(v) {


        fun bind(
            comment: TimelineCommentModelData,
            position: Int,
            current_userID: String?,
            current_user_token: String?,
            current_timelinetext: String,
            current_timelineimage: String,
            current_timelineid: String
        ) {

            itemView.comment_rv.isEnabled = false
            itemView.comment_rv.isClickable = false
            if (comment.profilePic.equals("null")) {
                itemView.user_pic_comrv.visibility = View.GONE
            } else {
                itemView.user_pic_comrv.visibility = View.VISIBLE
                Glide.with(itemView.context!!).load(comment.profilePic)
                    .centerCrop()
                    .into(itemView.user_pic_comrv)

            }
                 if (comment.profilePic.equals("null")) {
                     itemView.user_pic_comrv.visibility=View.GONE
                 }else {
            itemView.comment_rv.setText(comment.comment)
            }
            itemView.commenttime_rv.text = comment.postingTime

            if (current_userID.equals(comment.userId)) {
                itemView.commentdelete_rv.visibility = View.VISIBLE
                itemView.commentedit_rv.visibility = View.VISIBLE
                itemView.commentdone_rv.visibility = View.GONE

            } else {
                itemView.commentdelete_rv.visibility = View.GONE
                itemView.commentedit_rv.visibility = View.GONE
                itemView.commentdone_rv.visibility = View.GONE
            }

            itemView.commentdelete_rv.setOnClickListener {
                deletecomment(
                    comment.commentId,
                    current_user_token,
                    current_timelineid,
                    current_timelineimage,
                    current_timelinetext
                )
            }
            itemView.commentedit_rv.setOnClickListener {
                itemView.comment_rv.isEnabled = true
                itemView.comment_rv.isClickable = true
                itemView.commentdone_rv.visibility = View.VISIBLE
            }
            itemView.commentdone_rv.setOnClickListener {
                itemView.comment_rv.isEnabled = false
                itemView.comment_rv.isClickable = false
                itemView.commentdone_rv.visibility = View.GONE
                val editcomment = EditTimelineRequest(
                    comment.commentId!!,
                    comment.timelineId!!,
                    current_userID!!,
                    itemView.comment_rv.text.toString()
                )
                editcommentfun(comment.commentId, current_user_token, editcomment)
            }
        }


        private fun deletecomment(
            commentId: String?,
            current_user_token: String?,
            current_timelineid: String,
            current_timelineimage: String,
            current_timelinetext: String
        ) {

            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                    val newRequest =
                        chain!!.request().newBuilder().addHeader("Authorization", "bearer $current_user_token").build()
                    return chain.proceed(newRequest)
                }

            }).build()
            val retrofitobjectf =
                Retrofit.Builder().client(client).baseUrl(itemView.context.getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val apiServicef = retrofitobjectf.create(ApiInterface::class.java)
            val call = apiServicef.deleteTimelineComment(commentId!!)
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
                            val clickintent = Intent(itemView.context, TimelineSingleImageActivity::class.java)
                            clickintent.putExtra("timeline_id", current_timelineid)
                            clickintent.putExtra("timeline_text", current_timelinetext)
                            clickintent.putExtra("timeline_img", current_timelineimage)
                            itemView.context.startActivity(clickintent)
                            Toast.makeText(itemView.context, "Successfully Delete", Toast.LENGTH_SHORT).show()
                        } else {

                            Toast.makeText(itemView.context, "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }


        private fun editcommentfun(commentId: String?, current_user_token: String?, editcomment: EditTimelineRequest) {

            val requestBody = HashMap<String, EditTimelineRequest>()
            requestBody.clear()
            requestBody.put("data", editcomment)
            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                    val newRequest =
                        chain!!.request().newBuilder().addHeader("Authorization", "bearer $current_user_token").build()
                    return chain.proceed(newRequest)
                }

            }).build()
            val retrofitobjectf =
                Retrofit.Builder().client(client).baseUrl(itemView.context.getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val apiServicef = retrofitobjectf.create(ApiInterface::class.java)
            val call = apiServicef.editTimelineComment(commentId!!, requestBody)
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
                            itemView.comment_rv.isEnabled = false
                            itemView.comment_rv.isClickable = false
                            Toast.makeText(itemView.context, "Successfully Edited", Toast.LENGTH_SHORT).show()
                        } else {

                            Toast.makeText(itemView.context, "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })


        }

    }
}