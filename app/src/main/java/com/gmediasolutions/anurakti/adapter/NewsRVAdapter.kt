package com.gmediasolutions.anurakti.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.gmediasolutions.anurakti.R
import com.bumptech.glide.Glide
import android.widget.*
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gmediasolutions.anurakti.custom.CustomVendorDialog
import com.gmediasolutions.anurakti.model.NewsModel.NewsModel
import com.gmediasolutions.anurakti.newsevent.SpecificNewsEventActivity


class NewsRVAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var newsResults: MutableList<NewsModel>? = null

    private var isLoadingAdded = false
    private var customVendorDialog: CustomVendorDialog? = null

    var movies: MutableList<NewsModel>?
        get() = newsResults
        set(newsResults) {
            this.newsResults = newsResults
        }

    val isEmpty: Boolean
        get() = itemCount == 0

    init {
        newsResults = ArrayList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)

        when (viewType) {
            ITEM -> viewHolder = getViewHolder(parent, inflater)
            LOADING -> {
                val v2 = inflater.inflate(R.layout.item_progress, parent, false)
                viewHolder = LoadingVH(v2)
            }
        }
        return viewHolder!!
    }

    private fun getViewHolder(parent: ViewGroup, inflater: LayoutInflater): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val v1 = inflater.inflate(R.layout.news_rv_view, parent, false)
        viewHolder = MovieVH(v1)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val result = newsResults!![position] // Movie

        when (getItemViewType(position)) {
            ITEM -> {
                val movieVH = holder as MovieVH

                movieVH.newsTime.setText(result.created_at)


                movieVH.newsTitle.setText(result.heading)
//                movieVH.mBlogId=result.id!!

                if (result.photos.equals("")) {
                    holder.newsImage.visibility = View.GONE
                } else {
                    holder.newsImage.visibility = View.VISIBLE
                    Glide
                        .with(context)
                        .load(result.photos)
                        /*.listener(object : RequestListener<String, GlideDrawable> {
                            override fun onException(
                                e: Exception,
                                model: String,
                                target: Target<GlideDrawable>,
                                isFirstResource: Boolean
                            ): Boolean {
                                // TODO: 08/11/16 handle failure
                                movieVH.mProgress.visibility = View.GONE
                                return false
                            }

                          override  fun onResourceReady(
                                resource: GlideDrawable,
                                model: String,
                                target: Target<GlideDrawable>,
                                isFromMemoryCache: Boolean,
                                isFirstResource: Boolean
                            ): Boolean {
                                // image ready, hide progress now
                                movieVH.mProgress.visibility = View.GONE
                                return false   // return false if you want Glide to handle everything else.
                            }
                        })*/
                        .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                        .centerCrop()
                        .crossFade()
                        .into(movieVH.newsImage)
                }

                holder.itemView.setOnClickListener {
                    //                val clickintent = Intent(context, ::class.java)
//                clickintent.putExtra("news_id", newsfilter.id)
//               holder.itemView.context.startActivity(clickintent)

                    var intenttranstion = Intent(holder.itemView.context, SpecificNewsEventActivity::class.java)
                    intenttranstion.putExtra("news_id", result.id)

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

            LOADING -> {
            }
        }//                Do nothing

    }

    override fun getItemCount(): Int {
        return if (newsResults == null) 0 else newsResults!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if ((position == newsResults!!.size - 1 && isLoadingAdded)) LOADING else ITEM
    }


    /*
      Helpers
      _________________________________________________________________________________________________
       */

    fun add(r: NewsModel) {
        newsResults!!.add(r)
        notifyItemInserted(newsResults!!.size - 1)
    }

    fun addAll(moveResults: List<NewsModel>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun remove(r: NewsModel?) {
        val position = newsResults!!.indexOf(r)
        if (position > -1) {
            newsResults!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }


    fun addLoadingFooter() {
        isLoadingAdded = true
        add(NewsModel())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = newsResults!!.size - 1
        val result = getItem(position)

        if (result != null) {
            newsResults!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): NewsModel? {
        return newsResults!![position]
    }


    /*
      View Holders
      _________________________________________________________________________________________________
       */

    /**
     * Main list's content ViewHolder
     */
     inner class MovieVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var newsImage: ImageView
        var newsTitle: TextView
        var newsTime: TextView

        init {

            newsImage = itemView.findViewById(R.id.ne_img) as ImageView
            newsTitle = itemView.findViewById(R.id.ne_head) as TextView
            newsTime = itemView.findViewById(R.id.ne_time) as TextView
        }
    }


    inner class LoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {

        private val ITEM = 0
        private val LOADING = 1
//        private val BASE_URL_IMG = "https://image.tmdb.org/t/p/w150"
    }


}