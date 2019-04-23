package com.gmediasolutions.anurakti.model

data class SignUpModel(
        var firstName:String?=null,
    var lastName: String?=null,
    var emailId: String?=null,
    var password: String?=null,
    var confirmPassword: String?=null,
    var is_army: Boolean?=null,
    var gender: String?=null,
    var country: String?=null,
    var state: String?=null,
    var mobile_number: String?=null)