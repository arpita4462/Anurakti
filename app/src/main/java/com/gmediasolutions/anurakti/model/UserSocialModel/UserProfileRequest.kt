package com.gmediasolutions.anurakti.model.UserSocialModel

data class UserProfileRequest(
        private var userId: String? = null,
        private var alternateEmailId: String? = null,
        private var firstName: String? = null,
        private var lastName: String? = null,
        private var gender: String? = null,
        private var dob: String? = null,
        private var mobileNumber: String? = null,
        private var emailId: String? = null,
        private var alternateMobileNumber: String? = null,
        private var workingIn: String? = null,
        private var highSchool: String? = null,
        private var graduation: String? = null,
        private var currentCity: String? = null,
        private var permanentCity: String? = null,
        private var password: String? = null
)



