package com.gmediasolutions.anurakti.model.BlogModel

data class BloggerModelData(
        var current_page: Int? = null,
        var data: List<BlogData>? = null,
        var first_page_url: String? = null,
        var from: Int? = null,
        var last_page: Int? = null,
        var last_page_url: String? = null,
        var next_page_url: String? = null,
        var path: String? = null,
        var per_page: Int? = null,
        var prev_page_url: String? = null,
        var to: Int? = null,
        var total: Int? = null

)
