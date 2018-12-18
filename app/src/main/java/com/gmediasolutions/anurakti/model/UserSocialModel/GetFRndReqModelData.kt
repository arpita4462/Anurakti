package com.gmediasolutions.anurakti.model.UserSocialModel

data class GetFRndReqModelData(
        var id:String?=null,
    var userId: String?=null,
    var friendId: String?=null,
    var firstName: String?=null,
    var lastName:String?=null,
    var profilePic:String?=null
)