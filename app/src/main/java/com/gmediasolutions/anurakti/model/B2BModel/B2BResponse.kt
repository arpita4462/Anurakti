package com.gmediasolutions.anurakti.model.B2BModel

data class B2BResponse(val reasonCode:Int,
                       val reasonText:String,
                       val data:B2BResponseData)