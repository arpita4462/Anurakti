package com.gmediasolutions.anurakti.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.widget.Filterable
import java.util.ArrayList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.TextView
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.model.UserSocialModel.LikeNameModelData


class LikeNameListAdapter(
    val likeList: List<LikeNameModelData>?,
    val rowLayout: Int,
    val context: Context,
    val listeners: LikeAdapterListener
) : RecyclerView.Adapter<LikeNameListAdapter.LikeListViewHolder>(), Filterable {

    private var likeListFiltered: List<LikeNameModelData>? = null
    private var listener: LikeAdapterListener? = null

    init {
        this.likeListFiltered = likeList
        this.listener = listeners
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikeListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return LikeListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return likeListFiltered!!.size
    }

    override fun onBindViewHolder(holder: LikeListViewHolder, position: Int) {

        if (position < likeListFiltered!!.size) {
            val likefilter: LikeNameModelData = likeListFiltered!!.get(position)
            holder.fullName.text = likefilter.firstName + " " + likefilter.lastName
        }

    }

    inner class LikeListViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        var fullName: TextView

        init {
            fullName = view.findViewById(R.id.likef_name) as TextView
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    likeListFiltered = likeList
                } else {
                    val filteredList = ArrayList<LikeNameModelData>()
                    for (row in likeList!!) {

                        if (row.firstName.toLowerCase().contains(charString.toLowerCase()) || row.lastName.toLowerCase().contains(
                                charString.toLowerCase()
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }

                    likeListFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = likeListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                likeListFiltered = filterResults.values as ArrayList<LikeNameModelData>
                notifyDataSetChanged()
            }
        }
    }

    interface LikeAdapterListener {
        fun onLikeSelected(like: LikeNameModelData)
    }
}