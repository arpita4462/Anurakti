package com.gmediasolutions.anurakti.model.LoginModel

 data class ResultModel(
        val token:String,
        val token_type: String,
        val expires_in: String,
        val userData: UserDataModel)



