package com.gmediasolutions.anurakti.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gmediasolutions.anurakti.careerandtalent.SpecificCandTActivity
import com.gmediasolutions.anurakti.model.CandTModel.CompanyModelData
import kotlinx.android.synthetic.main.candt_rv_view.view.*
import java.util.ArrayList

/**
 * Created by Arpita Patel on 09-11-2018.
 */
class CandCompanyAdapter(
    val candtList: List<CompanyModelData>?,
    val rowLayout: Int,
    val context: Context,
    val listeners: CompanyAdapterListener
) : RecyclerView.Adapter<CandCompanyAdapter.CompanyViewHolder>(), Filterable {
    private var companyListFiltered: List<CompanyModelData>? = null
    private var listener: CompanyAdapterListener? = null

    init {
        this.companyListFiltered = candtList
        this.listener = listeners
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandCompanyAdapter.CompanyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return CompanyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return companyListFiltered!!.size
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        if (position < companyListFiltered!!.size && holder.itemView != null) {
            holder.bind(companyListFiltered!![position], position)

        }

    }

    class CompanyViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        @SuppressLint("MissingPermission")
        fun bind(candt: CompanyModelData, position: Int) {
            itemView.candt_name.text = candt.name
            itemView.candt_emailid.text = candt.emailId
//            itemView.candt_hirapp.text = candt.hiringFor
            itemView.candt_contacprev.text = candt.contactPesron
//            itemView.candt_contact.text=candt.mobileNumber
            Glide.with(itemView.context!!).load(candt.image)
                .centerCrop()
                .into(itemView.candt_iv)

//            itemView.candt_contact.setOnClickListener{
//                val callintent = Intent(Intent.ACTION_CALL)
//                callintent.data = Uri.parse("tel:" +candt.mobileNumber)
//                itemView.context.startActivity(callintent)
//            }
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
                    companyListFiltered = candtList
                } else {
                    val filteredList = ArrayList<CompanyModelData>()
                    for (row in candtList!!) {

                        if (row.name!!.toLowerCase().contains(charString.toLowerCase()) || row.hiringFor!!.toLowerCase().contains(
                                charString.toLowerCase()
                            ) || row.keywords!!.toLowerCase().contains(charString.toLowerCase())
                        ) {
                            filteredList.add(row)
                        }
                    }

                    companyListFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = companyListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                companyListFiltered = filterResults.values as ArrayList<CompanyModelData>
                notifyDataSetChanged()
            }
        }
    }

    interface CompanyAdapterListener {
        fun onCompanySelected(company: CompanyModelData)
    }
}
