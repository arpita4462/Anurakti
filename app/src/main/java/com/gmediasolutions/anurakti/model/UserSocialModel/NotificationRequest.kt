package com.gmediasolutions.anurakti.model.UserSocialModel

 data class NotificationRequest(
        val userId:String,
        val title: String,
        val content: String,
        val image: String,
        val token: String)



