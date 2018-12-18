package com.gmediasolutions.anurakti.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gmediasolutions.anurakti.model.UserSocialModel.NotificationModelData
import com.gmediasolutions.anurakti.socialmedia.FriendsSocialActivity
import kotlinx.android.synthetic.main.user_notification_rv_view.view.*

/**
 * Created by Arpita Patel on 09-11-2018.
 */
class NotificationAdapter(val noticationList: List<NotificationModelData>?, val rowLayout: Int, val context: Context) :
    RecyclerView.Adapter<NotificationAdapter.NotificationListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapter.NotificationListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return NotificationListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return noticationList!!.size
    }

    override fun onBindViewHolder(holder: NotificationListViewHolder, position: Int) {
        holder.bind(noticationList!![position], position)
    }

    class NotificationListViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        @SuppressLint("MissingPermission")
        fun bind(lists: NotificationModelData, position: Int) {
            itemView.noti_text.text = lists.content



            itemView.setOnClickListener(View.OnClickListener {
                var notilistintent = Intent(itemView.context, FriendsSocialActivity::class.java)
                notilistintent.putExtra("frnd_id", lists.userId)
                itemView.context.startActivity(notilistintent)
            })

        }

    }
}
