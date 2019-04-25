package com.gmediasolutions.anurakti.model.SupportModel

data class ComplaintModel(val subject: String,
                          val body: String,
                          val name: String,
                          val phone: String,
                          val email: String,
                          val request_type: String)