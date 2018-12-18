package com.gmediasolutions.anurakti.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings.Global.getString
import android.support.annotation.NonNull
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.blogger.SpecificBloggerActivity


class BloggerRVAdapter// data is passed into the constructor
internal constructor(context: Context, private val mData: Array<String>) :
    RecyclerView.Adapter<BloggerRVAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater
    private var mClickListener: ItemClickListener? = null

    init {
        this.mInflater = LayoutInflater.from(context)
    }

    // inflates the cell layout from xml when needed
    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.blog_rv_view, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each cell
    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        holder.myTextView.setOnClickListener {
            var intenttranstion = Intent(holder.itemView.context, SpecificBloggerActivity::class.java)
            intenttranstion.putExtra("array", mData.get(position))
            val transitionName = holder.itemView.context.getString(R.string.transiton_text)
            val transitionCard = holder.itemView.context.getString(R.string.transiton_card)
            //            val sharedView = findViewById<TextView>(R.id.blog_headline_tv)
            //            val sharedCardView = findViewById<CardView>(R.id.blog_rv_cardview)
            //            val p1 = Pair.create(sharedCardView as CardView, transitionCard)
            //            val p2 = Pair.create(sharedView, transitionName)
//            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                (Activity)holder.itemView.context,
//                holder.myTextView, // Starting view
//                transitionName    // The String
//            )
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                holder.itemView.context as Activity,
                holder.myTextView as View,
                transitionName
            )
            ActivityCompat.startActivity(holder.itemView.context, intenttranstion, options.toBundle())

        }
    }

    // total number of cells
    override fun getItemCount(): Int {
        return mData.size
    }


    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        internal var myTextView: TextView

        init {
            myTextView = itemView.findViewById(R.id.blog_headline_tv)
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }
    }

    // convenience method for getting data at click position
    internal fun getItem(id: Int): String {
        return mData[id]
    }

    // allows clicks events to be caught
    internal fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}