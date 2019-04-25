package com.gmediasolutions.anurakti.model.BlogModel

data class AddBlogResponse(val reasonCode:Int,
                           val reasonText:String,
                           val data:AddBlogResponseData)