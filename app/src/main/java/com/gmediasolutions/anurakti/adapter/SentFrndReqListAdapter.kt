package com.gmediasolutions.anurakti.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gmediasolutions.anurakti.model.UserSocialModel.GetFRndReqModelData
import com.gmediasolutions.anurakti.socialmedia.FriendsSocialActivity
import kotlinx.android.synthetic.main.frnd_list_name_rv_view.view.*
import java.util.ArrayList

/**
 * Created by Arpita Patel on 09-10-2018.
 */
class SentFrndReqListAdapter(
    val friendreqList: List<GetFRndReqModelData>?,
    val rowLayout: Int,
    val context: Context,
    listeners: FrindlistAdapterListener
) : RecyclerView.Adapter<SentFrndReqListAdapter.FriendListViewHolder>(), Filterable {
    private var frindlistListFiltered: List<GetFRndReqModelData>? = null
    private var listener: FrindlistAdapterListener? = null

    init {
        this.frindlistListFiltered = friendreqList
        this.listener = listeners
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SentFrndReqListAdapter.FriendListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return FriendListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return frindlistListFiltered!!.size
    }

    override fun onBindViewHolder(holder: FriendListViewHolder, position: Int) {
        if (position < frindlistListFiltered!!.size && holder.itemView != null) {
            holder.bind(frindlistListFiltered!![position], position)

        }

    }

    class FriendListViewHolder(v: View) : RecyclerView.ViewHolder(v) {


        @SuppressLint("MissingPermission")
        fun bind(frindlists: GetFRndReqModelData, position: Int) {
            if (frindlists.lastName == null) {
                itemView.frndlist_name.text = frindlists.firstName
            } else {
                itemView.frndlist_name.text = frindlists.firstName + " " + frindlists.lastName
            }//            itemView.frndlist_status.text = frindlists.
            Glide.with(itemView.context!!).load(frindlists.profilePic)
                .centerCrop()
                .into(itemView.frndlist_iv)
            itemView.setOnClickListener {
                val frndlistintent = Intent(itemView.context, FriendsSocialActivity::class.java)
                frndlistintent.putExtra("frnd_id", frindlists.friendId)
                itemView.context.startActivity(frndlistintent)
            }

        }

    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    frindlistListFiltered = friendreqList
                } else {
                    val filteredList = ArrayList<GetFRndReqModelData>()
                    for (row in friendreqList!!) {

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

                frindlistListFiltered = filterResults.values as ArrayList<GetFRndReqModelData>
                notifyDataSetChanged()
            }
        }
    }

    interface FrindlistAdapterListener {
        fun onFrndlistSelected(frndlist: GetFRndReqModelData)
    }
}
