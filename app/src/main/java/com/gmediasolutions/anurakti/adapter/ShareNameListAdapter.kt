package com.gmediasolutions.anurakti.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.gmediasolutions.anurakti.model.UserSocialModel.GetFriendModelData
import com.gmediasolutions.anurakti.model.UserSocialModel.ShareFrndRequest
import kotlinx.android.synthetic.main.listname_rv_view.view.*
import java.util.ArrayList

/**
 * Created by Arpita Patel on 09-10-2018.
 */
class ShareNameListAdapter(
    val friendList: List<GetFriendModelData>?,
    val rowLayout: Int,
    val context: Context,
    val listeners: SharelistAdapterListener,
    val userID: String,
    val user_token: String,
    val timelineIds: String?
) : RecyclerView.Adapter<ShareNameListAdapter.FriendListViewHolder>(), Filterable {
    private var frindlistListFiltered: List<GetFriendModelData>? = null
    private var listener: SharelistAdapterListener? = null

    init {
        this.frindlistListFiltered = friendList
        this.listener = listeners
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareNameListAdapter.FriendListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return FriendListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return frindlistListFiltered!!.size
    }

    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) {
        if (position < frindlistListFiltered!!.size && holder.itemView != null) {
            holder.bind(frindlistListFiltered!![position], position, userID, user_token, timelineIds)

        }

    }

    class FriendListViewHolder(v: View) : RecyclerView.ViewHolder(v) {


        @SuppressLint("MissingPermission")
        fun bind(
            frindlists: GetFriendModelData,
            position: Int,
            userID: String,
            user_token: String,
            timelineIds: String?
        ) {
            if (frindlists.lastName == null) {
                itemView.likef_name.text = frindlists.firstName
            } else {
                itemView.likef_name.text = frindlists.firstName + " " + frindlists.lastName
            }
            itemView.setOnClickListener {
//                val sharecmmt = ShareFrndRequest(frindlists.friendId!!, userId = userID)
//                sharePost(sharecmmt,timelineIds,user_token,frindlists.friendId!!)
            }

        }

/*
        fun sharePost(sharecmmt: ShareFrndRequest, timelineId: String?, user_token: String, friendId: String) {
            val requestBody = HashMap<String, ShareFrndRequest>()
            requestBody.clear()
            requestBody.put("data", sharecmmt)
            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain?): okhttp3.Response {
                    val newRequest = chain!!.request().newBuilder().addHeader("Authorization", "bearer $user_token").build()
                    return chain.proceed(newRequest)
                }
            }).build()
            val retrofitobjectf = Retrofit.Builder().client(client).baseUrl(itemView.context.getString(R.string.base_url)).addConverterFactory(GsonConverterFactory.create()).build()
            val apiServicef = retrofitobjectf.create(ApiInterface::class.java)
            val call = apiServicef.shareTimelineInFriendProfile(timelineId!!,requestBody)
            call.enqueue(object : Callback<ApiReturn> {
                override fun onFailure(call: Call<ApiReturn>, t: Throwable) {
                    val clickintent = Intent(itemView.context, FriendPrfileActivity::class.java)
                    clickintent.putExtra("frnd_id", friendId)
                    itemView.context.startActivity(clickintent)
                    Toast.makeText(itemView.context, "No Internet Connection", Toast.LENGTH_SHORT).show()
                }
                override fun onResponse(call: Call<ApiReturn>, response: Response<ApiReturn>) {
                    if (response.code() == 401) {
                        Toast.makeText(itemView.context, "Session Out", Toast.LENGTH_LONG).show()
                        itemView.context.startActivity(Intent(itemView.context, LoginActivity::class.java))
                        (itemView.context as Activity).finish()
                    } else {
                        if (response.isSuccessful) {
                            Toast.makeText(itemView.context, "Post Share", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(itemView.context, "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }
*/

    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    frindlistListFiltered = friendList
                } else {
                    val filteredList = ArrayList<GetFriendModelData>()
                    for (row in friendList!!) {

                        if (row.firstName!!.toLowerCase().contains(charString.toLowerCase()) || row.lastName!!.toLowerCase().contains(
                                charString.toLowerCase()
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }

                    frindlistListFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = frindlistListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                frindlistListFiltered = filterResults.values as ArrayList<GetFriendModelData>
                notifyDataSetChanged()
            }
        }
    }

    interface SharelistAdapterListener {
        fun onShareSelected(share: GetFriendModelData)
    }
}
