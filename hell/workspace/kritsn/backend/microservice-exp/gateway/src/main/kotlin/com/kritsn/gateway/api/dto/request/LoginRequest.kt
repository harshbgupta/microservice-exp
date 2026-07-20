package com.kritsn.gateway.api.dto.request

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 06, 2025
 */

data class LoginRequest(
    val countryCode: String,
    val mobileNumber: String,
    val otp: String,
    val deviceId: String? = null
)
