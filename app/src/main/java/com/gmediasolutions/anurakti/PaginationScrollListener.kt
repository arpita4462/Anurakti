package com.gmediasolutions.anurakti

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView


abstract class PaginationScrollListener
/**
 * Supporting only LinearLayoutManager for now.
 *
 * @param layoutManager
 */
protected constructor(private val layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    abstract val totalPageCount: Int

    abstract val isLastPage: Boolean

    abstract var isLoading: Boolean

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading && !isLastPage) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                loadMoreItems()
            }
        }

    }

    protected abstract fun loadMoreItems()

}