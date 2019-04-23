package com.gmediasolutions.anurakti.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.gmediasolutions.anurakti.R
import com.bumptech.glide.Glide
import android.widget.*
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.gmediasolutions.anurakti.custom.CustomVendorDialog
import com.gmediasolutions.anurakti.model.Vendors.VendorsModelData


class VendorsRVAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var movieResults: MutableList<VendorsModelData>? = null

    private var isLoadingAdded = false
    private var customVendorDialog: CustomVendorDialog? = null

    var movies: MutableList<VendorsModelData>?
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
        val v1 = inflater.inflate(R.layout.vendor_rv_view, parent, false)
        viewHolder = MovieVH(v1)
        return viewHolder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val result = movieResults!![position] // Movie

        when (getItemViewType(position)) {
            ITEM -> {
                val movieVH = holder as MovieVH

                movieVH.myTextView.setText(result.vendor_name)
                movieVH.vendor_id=result.id!!
                movieVH.couponCode=result.coupon_code!!


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
                    .into(movieVH.vendoeImg)
                customVendorDialog = CustomVendorDialog(movieVH.itemView.context,movieVH.couponCode)

                movieVH.btnGetCode.setOnClickListener {
                    customVendorDialog!!.window!!.attributes.windowAnimations = R.style.DialogAnimation
//            customVendorDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    customVendorDialog!!.setCanceledOnTouchOutside(false)
                    customVendorDialog!!.show()
                }


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

    fun add(r: VendorsModelData) {
        movieResults!!.add(r)
        notifyItemInserted(movieResults!!.size - 1)
    }

    fun addAll(moveResults: List<VendorsModelData>) {
        for (result in moveResults) {
            add(result)
        }
    }

    fun remove(r: VendorsModelData?) {
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
        add(VendorsModelData())
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

    fun getItem(position: Int): VendorsModelData? {
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
        internal var myTextView: TextView
        internal var vendoeImg: ImageView
        internal var btnGetCode: Button
        internal var vendor_id: Int
        internal var couponCode: String

        init {

            myTextView = itemView.findViewById(R.id.tv_vendorName)
            vendoeImg = itemView.findViewById(R.id.photo_vendor)
            btnGetCode = itemView.findViewById(R.id.btn_getcode)
            couponCode=""
            customVendorDialog = CustomVendorDialog(itemView.context,couponCode)
            vendor_id=0
        }
    }


    inner class LoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {

        private val ITEM = 0
        private val LOADING = 1
//        private val BASE_URL_IMG = "https://image.tmdb.org/t/p/w150"
    }


}