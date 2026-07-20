package com.kritsn.userservices.dto

import com.kritsn.userservices.model.User

//for registering User
data class ReqUser(
    var firstName: String? = null,
    var lastName: String? = null,
    var mobileNumber: String = "",
    var email: String? = null,
    var userType: String? = null
) {
    constructor(user: User) : this(
        firstName = user.firstName,
        lastName = user.lastName,
        mobileNumber = user.mobileNumber,
        email = user.email,
        userType = user.userType
    )
}

//for verify Otp
data class ReqVerifyOtp(
    val mobileNumber: String?,
    val otpKey: String?,
)