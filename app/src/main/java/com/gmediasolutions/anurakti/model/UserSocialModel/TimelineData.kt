package com.gmediasolutions.anurakti.model.UserSocialModel

data class TimelineData(
        var timelineId: String? = null,
        var userId: String,
        var firstName: String? = null,
        var lastName: String,
        var textContent: String,
        var photos: String,
        var postingTime: String,
        var profilePic: String,
        var postUserId: String,
        var postUserfirstName: String,
        var postUserlastName : String,
        var postUserProfilePic : String,
        var status : String
        )