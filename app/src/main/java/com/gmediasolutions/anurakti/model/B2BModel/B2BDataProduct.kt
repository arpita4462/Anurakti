package com.gmediasolutions.anurakti.model.B2BModel

data class B2BDataProduct(
        var user_id: String,
        var product_name: String,
        var name: String? = null,
        var contact_number: String,
        var emailId: String,
        var address: String,
        var content: String,
        var image: String,
        var quantity: String,
        var type: String,
        var status: String,
        var updated_at: String,
        var created_at: String,
        var b_id: Int)