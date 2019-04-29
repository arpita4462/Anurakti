package com.gmediasolutions.anurakti.model.BlogModel

data class AddBlogRequest(val pic:String?=null,
                          val Subject:String,
                          val body:String)