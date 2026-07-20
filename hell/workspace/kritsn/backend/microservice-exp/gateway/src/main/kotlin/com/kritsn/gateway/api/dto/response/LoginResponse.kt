package com.kritsn.gateway.api.dto.response

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 06, 2025
 */
data class LoginResponse(
    val accessToken: String,
    val accessTokenExpiresAt: Long,
    val refreshToken: String?,
    val refreshTokenExpiresAt: Long?,
    val deviceId: String,
    val sessionId: String
)