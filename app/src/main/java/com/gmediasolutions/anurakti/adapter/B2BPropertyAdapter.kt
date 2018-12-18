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
import com.gmediasolutions.anurakti.model.B2BModel.PropertyModelData
import com.gmediasolutions.anurakti.b2b.SpecificB2BActivity
import kotlinx.android.synthetic.main.b2b_rv_view.view.*
import java.util.ArrayList

/**
 * Created by Arpita Patel on 09-10-2018.
 */
class B2BPropertyAdapter(
    val propertyList: List<PropertyModelData>?,
    val rowLayout: Int,
    val context: Context,
    val listeners: propertyAdapterListener
) : RecyclerView.Adapter<B2BPropertyAdapter.B2BViewHolder>(), Filterable {
    private var propertyListFiltered: List<PropertyModelData>? = null
    private var listener: propertyAdapterListener? = null

    init {
        this.propertyListFiltered = propertyList
        this.listener = listeners
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): B2BPropertyAdapter.B2BViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return B2BViewHolder(view)
    }

    override fun getItemCount(): Int {
        return propertyListFiltered!!.size
    }

    override fun onBindViewHolder(holder: B2BViewHolder, position: Int) {
        if (position < propertyListFiltered!!.size && holder.itemView != null) {
            holder.bind(propertyListFiltered!![position], position)

        }

    }

    class B2BViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        @SuppressLint("MissingPermission")
        fun bind(b2b: PropertyModelData, position: Int) {
            itemView.b2b_name.text = b2b.name
            itemView.b2b_content.text = b2b.productName
            itemView.b2b_contact.text = b2b.contactNumber
            Glide.with(itemView.context!!).load(b2b.image)
                .centerCrop()
                .into(itemView.b2b_iv)

            itemView.b2b_contact.setOnClickListener {
                val callintent = Intent(Intent.ACTION_CALL)
                callintent.data = Uri.parse("tel:" + b2b.contactNumber)
                itemView.context.startActivity(callintent)
            }
            itemView.setOnClickListener {
                val clickintent = Intent(itemView.context, SpecificB2BActivity::class.java)
                clickintent.putExtra("product_id", b2b.id)
                clickintent.putExtra("product_type", "property")
                itemView.context.startActivity(clickintent)
            }

        }

    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    propertyListFiltered = propertyList
                } else {
                    val filteredList = ArrayList<PropertyModelData>()
                    for (row in propertyList!!) {

                        if (row.productName!!.toLowerCase().contains(charString.toLowerCase()) || row.content!!.toLowerCase().contains(
                                charString.toLowerCase()
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }

                    propertyListFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = propertyListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                propertyListFiltered = filterResults.values as ArrayList<PropertyModelData>
                notifyDataSetChanged()
            }
        }
    }

    interface propertyAdapterListener {
        fun onPropertySelected(property: PropertyModelData)
    }
}
