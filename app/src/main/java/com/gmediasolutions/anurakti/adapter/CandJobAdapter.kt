package com.gmediasolutions.anurakti.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gmediasolutions.anurakti.careerandtalent.SpecificCandTActivity
import com.gmediasolutions.anurakti.model.CandTModel.JobModelData
import kotlinx.android.synthetic.main.candt_rv_view.view.*

import java.util.ArrayList

/**
 * Created by Arpita Patel on 09-11-2018.
 */
class CandJobAdapter(
    val candtList: List<JobModelData>?,
    val rowLayout: Int,
    val context: Context,
    val listeners: JobAdapterListener
) : RecyclerView.Adapter<CandJobAdapter.JobViewHolder>(), Filterable {
    private var jobListFiltered: List<JobModelData>? = null
    private var listener: JobAdapterListener? = null

    init {
        this.jobListFiltered = candtList
        this.listener = listeners
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandJobAdapter.JobViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return JobViewHolder(view)
    }

    override fun getItemCount(): Int {
        return jobListFiltered!!.size
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        if (position < jobListFiltered!!.size && holder.itemView != null) {
            holder.bind(jobListFiltered!![position], position)

        }

    }

    class JobViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        @SuppressLint("MissingPermission")
        fun bind(candt: JobModelData, position: Int) {
            itemView.candt_name.text = candt.name
            itemView.candt_emailid.text = candt.emailId
//            itemView.candt_hirapp.text = candt.applingFor
            itemView.candt_contacprev.text = candt.previousCompany
//            itemView.candt_contact.text=candt.mobileNumber
            Glide.with(itemView.context!!).load(candt.image)
                .centerCrop()
                .into(itemView.candt_iv)

/*
            itemView.candt_contact.setOnClickListener(View.OnClickListener {
                val callintent = Intent(Intent.ACTION_CALL)
                callintent.setData(Uri.parse("tel:" +candt.mobileNumber))
                itemView.context.startActivity(callintent)
            })
*/
            itemView.setOnClickListener {
                val clickintent = Intent(itemView.context, SpecificCandTActivity::class.java)
                clickintent.putExtra("candt_id", candt.id)
                itemView.context.startActivity(clickintent)
            }
        }

    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    jobListFiltered = candtList
                } else {
                    val filteredList = ArrayList<JobModelData>()
                    for (row in candtList!!) {

                        if (row.name!!.toLowerCase().contains(charString.toLowerCase()) || row.applingFor!!.toLowerCase().contains(
                                charString.toLowerCase()
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }

                    jobListFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = jobListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                jobListFiltered = filterResults.values as ArrayList<JobModelData>
                notifyDataSetChanged()
            }
        }
    }

    interface JobAdapterListener {
        fun onJobSelected(job: JobModelData)
    }
}
