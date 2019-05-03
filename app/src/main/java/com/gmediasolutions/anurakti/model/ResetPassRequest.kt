package com.gmediasolutions.anurakti.model

 data class ResetPassRequest(
        val emailId:String,
        val password:String,
        val confirmPassword:String,
        val otp:String
)



