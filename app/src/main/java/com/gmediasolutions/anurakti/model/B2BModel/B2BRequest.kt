package com.gmediasolutions.anurakti.model.B2BModel

data class B2BRequest(
        val userId:String,
        val productName:String,
        val name: String,
        val contactNumber: String,
        val emailId: String,
        val image: String,
        val content: String,
        val areaDescription: String,
        val address: String,
        val type: String,
        val quantity: String
)



