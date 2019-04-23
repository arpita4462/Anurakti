package com.gmediasolutions.anurakti.model.BlogModel

import com.gmediasolutions.anurakti.model.User

data class BlogData(
        var id: Int? = null,
        var subject: String? = null,
        var body: String? = null,
        var pic: String? = null,
        var user_id: Int? = null,
        var created_at: String? = null,
        var updated_at: String? = null,
        var status: Int? = null,
        var user: User? = null)
