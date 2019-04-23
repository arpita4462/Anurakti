package com.gmediasolutions.anurakti.model.NewsModel

data class GetAllNewsModel(val reasonCode:Int,
                           val reasonText:String,
                           val data:GetAllNewsModelData)