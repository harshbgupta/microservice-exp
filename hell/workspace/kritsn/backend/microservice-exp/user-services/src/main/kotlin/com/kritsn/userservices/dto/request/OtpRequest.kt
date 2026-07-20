package com.kritsn.userservices.dto.request

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 02, 2025
 */

data class OtpRequest(
    val mobileNumber: String?,
    val otpKey: String?,
)