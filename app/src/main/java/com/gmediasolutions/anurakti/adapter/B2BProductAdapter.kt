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
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.model.B2BModel.ProductModelData
import com.gmediasolutions.anurakti.b2b.SpecificB2BActivity
import kotlinx.android.synthetic.main.b2b_rv_view.view.*
import java.util.ArrayList

/**
 * Created by Arpita Patel on 09-11-2018.
 */
class B2BProductAdapter(
    val productList: List<ProductModelData>?,
    val rowLayout: Int,
    val context: Context,
    val listeners: productAdapterListener
) : RecyclerView.Adapter<B2BProductAdapter.B2BViewHolder>(), Filterable {
    private var productListFiltered: List<ProductModelData>? = null
    private var listener: productAdapterListener? = null

    init {
        this.productListFiltered = productList
        this.listener = listeners
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): B2BProductAdapter.B2BViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return B2BViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productListFiltered!!.size
    }

    override fun onBindViewHolder(holder: B2BViewHolder, position: Int) {
        if (position < productListFiltered!!.size && holder.itemView != null) {
            holder.bind(productListFiltered!![position], position)

        }

    }

    class B2BViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        @SuppressLint("MissingPermission")
        fun bind(b2b: ProductModelData, position: Int) {
            itemView.b2b_name.text = b2b.productName
            itemView.b2b_content.text = b2b.productName
            itemView.b2b_contact.text = b2b.contactNumber
            Glide.with(itemView.context!!).load(b2b.image)
//                    .thumbnail(0.5f)
                .placeholder(R.drawable.noimage)
                .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(itemView.b2b_iv)
            itemView.b2b_contact.setOnClickListener {
                val callintent = Intent(Intent.ACTION_CALL)
                callintent.data = Uri.parse("tel:" + b2b.contactNumber)
                itemView.context.startActivity(callintent)
            }
            itemView.setOnClickListener {
                val clickintent = Intent(itemView.context, SpecificB2BActivity::class.java)
                clickintent.putExtra("product_id", b2b.id)
                clickintent.putExtra("product_type", "product")
                itemView.context.startActivity(clickintent)
            }


        }

    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    productListFiltered = productList
                } else {
                    val filteredList = ArrayList<ProductModelData>()
                    for (row in productList!!) {

                        if (row.productName!!.toLowerCase().contains(charString.toLowerCase()) || row.content!!.toLowerCase().contains(
                                charString.toLowerCase()
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }

                    productListFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = productListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                productListFiltered = filterResults.values as ArrayList<ProductModelData>
                notifyDataSetChanged()
            }
        }
    }

    interface productAdapterListener {
        fun onProductSelected(product: ProductModelData)
    }
}
