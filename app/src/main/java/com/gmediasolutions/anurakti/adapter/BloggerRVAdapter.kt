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
import android.content.ClipData.Item
import android.support.v7.util.DiffUtil
import android.widget.Toast
import com.bumptech.glide.Glide
import android.arch.paging.PagedListAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.gmediasolutions.anurakti.model.BlogModel.BlogData


class BloggerRVAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var movieResults: MutableList<BlogData>? = null

    private var isLoadingAdded = false

    var movies: MutableList<BlogData>?
        get() = movieResults
        set(movieResults) {
            this.movieResults = movieResults
        }

    val isEmpty: Boolean
        get() = itemCount == 0

    init {
        movieResults = ArrayList()
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
        val v1 = inflater.inflate(R.layout.blog_rv_view, parent, false)
        viewHolder = MovieVH(v1)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val result = movieResults!![position] // Movie

        when (getItemViewType(position)) {
            ITEM -> {
                val movieVH = holder as MovieVH

                movieVH.mBlogTitle.setText(result.subject)


                movieVH.mBlogTime.setText(result.updated_at)
                movieVH.mBlogId=result.id!!


                /**
                 * Using Glide to handle image loading.
                 */
                Glide
                    .with(context)
                    .load(result.pic)
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
                    .placeholder(R.drawable.noimage)
                    .into(movieVH.mPBlogImg)
            }

            LOADING -> {
            }
        }//                Do nothing

    }

    override fun getItemCount(): Int {
        return if (movieResults == null) 0 else movieResults!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if ((position == movieResults!!.size - 1 && isLoadingAdded)) LOADING else ITEM
    }


    /*
      Helpers
      _________________________________________________________________________________________________
       */

    fun add(r: BlogData) {
        movieResults!!.add(r)
        notifyItemInserted(movieResults!!.size - 1)
    }

    fun addAll(moveResults: List<BlogData>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun remove(r: BlogData?) {
        val position = movieResults!!.indexOf(r)
        if (position > -1) {
            movieResults!!.removeAt(position)
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
        add(BlogData())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

        val position = movieResults!!.size - 1
        val result = getItem(position)

        if (result != null) {
            movieResults!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): BlogData? {
        return movieResults!![position]
    }


    /*
      View Holders
      _________________________________________________________________________________________________
       */

    /**
     * Main list's content ViewHolder
     */
     inner class MovieVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val mBlogTitle: TextView
         val mBlogTime: TextView
         val mPBlogImg: ImageView
         var mBlogId: Int

        init {

            mBlogTime = itemView.findViewById(R.id.blog_time_tv) as TextView
            mBlogTitle = itemView.findViewById(R.id.blog_headline_tv) as TextView
            mPBlogImg = itemView.findViewById(R.id.photo_iv) as ImageView
            mBlogId=0


            itemView.setOnClickListener {
                val i = Intent(context, SpecificBloggerActivity::class.java)
//                i.putExtra("mBlogTitle", mBlogTitle.text)
                i.putExtra("blog_id", mBlogId)
                //i.putExtra("mPosterImg",extras);
                context.startActivity(i)
            }

        }
    }


    inner class LoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {

        private val ITEM = 0
        private val LOADING = 1
//        private val BASE_URL_IMG = "https://image.tmdb.org/t/p/w150"
    }


}