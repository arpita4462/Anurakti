package com.gmediasolutions.anurakti.model.Vendors

import com.gmediasolutions.anurakti.model.User

data class VendorsModelData(
        var id: Int? = null,
        var vendor_name: String? = null,
        var coupon_code: String? = null,
        var pic: String? = null,
        var status: Int? = null,
        var created_at: String? = null,
        var updated_at: String? = null)
