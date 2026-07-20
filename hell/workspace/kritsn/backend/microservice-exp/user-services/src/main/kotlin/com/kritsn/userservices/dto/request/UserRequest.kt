package com.kritsn.userservices.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDate

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 02, 2025
 */

data class CreateUserRequest(
    @field:NotBlank(message = "First name cannot be blank")
    @field:Size(max = 50, message = "First name cannot exceed 50 characters")
    var firstName: String,

    @field:Size(max = 50, message = "Last name cannot exceed 50 characters")
    var lastName: String? = null,

    @field:NotBlank(message = "Country Code cannot be blank")
    @field:Size(max = 5, message = "Mobile number can not exceed 50 characters")
    var countryCode: String,

    @field:NotBlank(message = "Mobile number cannot be blank")
    @field:Size(max = 13, message = "Mobile number cannot exceed 50 characters")
    var mobileNumber: String,


    var email: String? = null,
    var dob: LocalDate? = null,
    var userType: String? = "USER"
)

data class UpdateUserNameRequest(
    @field:NotBlank(message = "First name cannot be blank")
    @field:Size(max = 50, message = "First name cannot exceed 50 characters")
    var firstName: String,

    @field:Size(max = 50, message = "Last name cannot exceed 50 characters")
    var lastName: String? = null,
)

data class UpdateUserMobileNumberRequest(
    @field:NotBlank(message = "Country Code cannot be blank")
    @field:Size(max = 5, message = "Mobile number can not exceed 50 characters")
    var countryCode: String,

    @field:NotBlank(message = "Mobile number cannot be blank")
    @field:Size(max = 13, message = "Mobile number cannot exceed 50 characters")
    var mobileNumber: String,
)