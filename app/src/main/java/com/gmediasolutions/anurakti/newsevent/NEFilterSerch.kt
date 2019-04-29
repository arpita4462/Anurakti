package com.gmediasolutions.anurakti.newsevent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.widget.Filterable
import java.util.ArrayList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gmediasolutions.anurakti.R
import com.gmediasolutions.anurakti.model.NewsModel.NewsModel


class NEFilterSerch(
    val neList: List<NewsModel>?,
    val rowLayout: Int,
    val context: Context,
    val listeners: NewsAdapterListener
) : RecyclerView.Adapter<NEFilterSerch.NESearchViewHolder>(), Filterable {

    private var newsListFiltered: List<NewsModel>? = null
    private var listener: NewsAdapterListener? = null

    init {
        this.newsListFiltered = neList
        this.listener = listeners
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NESearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(rowLayout, parent, false)
        return NESearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: NESearchViewHolder, position: Int) {

//        var newsItem = neList!!.get(position)

        if (position < newsListFiltered!!.size) {
            val newsfilter: NewsModel = newsListFiltered!!.get(position)
            holder.newsTitle.text = newsfilter.heading
//            holder.newsTime.text = newsfilter.dateTime
//            holder.newsCategory.text = newsfilter.category
            if (newsfilter.photos.equals("")) {
                holder.newsImage.visibility = View.GONE
            } else {
                holder.newsImage.visibility = View.VISIBLE
                Glide.with(context).load(newsfilter.photos)
//                    .override(2000,400)
                    .placeholder(R.drawable.noimage)
                    .centerCrop()
                    .into(holder.newsImage)
            }

//            ViewCompat.setTransitionName(holder.newsImage, newsItem.heading)

            holder.itemView.setOnClickListener {
                //                val clickintent = Intent(context, ::class.java)
//                clickintent.putExtra("news_id", newsfilter.id)
//               holder.itemView.context.startActivity(clickintent)

                var intenttranstion = Intent(holder.itemView.context, SpecificNewsEventActivity::class.java)
                intenttranstion.putExtra("news_id", newsfilter.id)

                val transitionImage = holder.itemView.context.getString(R.string.transiton_news_image)
//                val transitionCard= holder.itemView.context.getString(R.string.transiton_card)
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
                    holder.newsImage as View,
                    transitionImage
                )
                ActivityCompat.startActivity(holder.itemView.context, intenttranstion, options.toBundle())

            }
        }

    }

    inner class NESearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var newsImage: ImageView
        var newsTitle: TextView
        var newsTime: TextView
//        var newsCategory: TextView

        init {
            newsImage = view.findViewById(R.id.ne_img) as ImageView
            newsTitle = view.findViewById(R.id.ne_head) as TextView
            newsTime = view.findViewById(R.id.ne_time) as TextView
//            newsCategory = view.findViewById(R.id.ne_cat) as TextView
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    newsListFiltered = neList
                } else {
                    val filteredList = ArrayList<NewsModel>()
                    for (row in neList!!) {

                        if (row.heading!!.toLowerCase().contains(charString.toLowerCase()) || row.category!!.toLowerCase().contains(
                                charString.toLowerCase()
                            )
                        ) {
                            filteredList.add(row)
                        }
                    }

                    newsListFiltered = filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = newsListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {

                newsListFiltered = filterResults.values as ArrayList<NewsModel>
                notifyDataSetChanged()
            }
        }
    }

    interface NewsAdapterListener {
        fun onNewsSelected(news: NewsModel)
    }
}