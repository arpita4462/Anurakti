package com.gmediasolutions.anurakti.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.app.Activity
import com.gmediasolutions.anurakti.ApiInterface
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.base.LoginActivity
import com.gmediasolutions.anurakti.model.ApiReturn
import com.gmediasolutions.anurakti.model.UserSocialModel.CoverPicCommentModelData
import com.gmediasolutions.anurakti.model.UserSocialModel.EditProfilePicCommentRequest
import com.gmediasolutions.anurakti.socialmedia.CoverImageOneViewActivity
import kotlinx.android.synthetic.main.comment_rv_view.view.*


class CoverImageOneViewAdapter(val commentList: List<CoverPicCommentModelData>, val rowLayout: Int, val context: Context, val user_id: String, val user_token: String, val getpicId: String, val getpicImage: String) : RecyclerView.Adapter<CoverImageOneViewAdapter.CommentViewHolder>() {
    private var current_userID: String? = null
    private var current_user_token: String? = null
    private var current_commentid: String? = null
    private var current_commentpic: String? = null

    init {
        this.current_userID = user_id
        this.current_user_token = user_token
        this.current_commentid = getpicId
        this.current_commentpic = getpicImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoverImageOneViewAdapter.CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return CommentViewHolder(view)
    }


    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(commentList[position], position, current_userID, current_user_token, current_commentid, current_commentpic)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    class CommentViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        fun bind(comment: CoverPicCommentModelData, position: Int, current_userID: String?, current_user_token: String?, current_commentid: String?, current_commentpic: String?) {
            itemView.comment_rv.isEnabled = false
            itemView.comment_rv.isClickable = false
            Glide.with(itemView.context!!).load(comment.profilePic)
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(itemView.user_pic_comrv)

            itemView.comment_rv.setText(comment.comment)
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
                deletecomment(comment.commentId, current_user_token, current_commentid, current_commentpic)
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
                val editcomment = EditProfilePicCommentRequest(comment.commentId, comment.picId, current_userID!!, itemView.comment_rv.text.toString())
                editcommentfun(comment.commentId, current_user_token, editcomment)
            }
        }

        private fun deletecomment(commentId: String?, current_user_token: String?, current_commentid: String?, current_commentpic: String?) {

            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                    val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $current_user_token").build()
                    return chain.proceed(newRequest)
                }

            }).build()
            val retrofitobjectf = Retrofit.Builder().client(client).baseUrl(itemView.context.getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build()
            val apiServicef = retrofitobjectf.create(ApiInterface::class.java)
            val call = apiServicef.deleteCoverPicComment(commentId!!)
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
                            val clickintent = Intent(itemView.context, CoverImageOneViewActivity::class.java)
                            clickintent.putExtra("pic_id", current_commentid)
                            clickintent.putExtra("pic_comment", current_commentpic)
                            itemView.context.startActivity(clickintent)
                            Toast.makeText(itemView.context, "Successfully Delete", Toast.LENGTH_SHORT).show()
                        } else {

                            Toast.makeText(itemView.context, "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })


        }

        private fun editcommentfun(commentId: String?, current_user_token: String?, editcomment: EditProfilePicCommentRequest) {

            val requestBody = HashMap<String, EditProfilePicCommentRequest>()
            requestBody.clear()
            requestBody.put("data", editcomment)
            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                    val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $current_user_token").build()
                    return chain.proceed(newRequest)
                }

            }).build()
            val retrofitobjectf = Retrofit.Builder().client(client).baseUrl(itemView.context.getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build()
            val apiServicef = retrofitobjectf.create(ApiInterface::class.java)
            val call = apiServicef.editCoverPicComment(commentId!!, requestBody)
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