package com.kritsn.userservices.dto.response

import java.time.LocalDate
import java.util.Locale

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 02, 2025
 */

data class UserResponse (
    var firstName: String? = null,
    var lastName: String? = null,
    var countryCode: String? = null,
    var mobileNumber: String? = null,
    var email: String? = null,
    var dob: LocalDate? = null,
    var userType: String? = null
)