package com.kritsn.gateway.domain.service

import com.kritsn.gateway.api.dto.request.LoginRequest
import com.kritsn.gateway.api.dto.response.LoginResponse
import com.kritsn.gateway.domain.dto.AuthUser
import com.kritsn.gateway.infrastructure.session.DeviceSessionLimiter
import com.kritsn.gateway.infrastructure.util.getDeviceIdOrWebFingerprintFromHeader
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.util.*

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 06, 2025
 */
@Service
class AuthService(
    private val limiter: DeviceSessionLimiter,
    private val jwt: JwtService
) {

    fun loginMobile(req: LoginRequest, servletRequest: HttpServletRequest): ResponseEntity<Any> {
        val user = authenticate(req.mobileNumber, req.otp) ?: return ResponseEntity.status(401)
            .body(mapOf("error" to "invalid_credentials"))

        // Pick deviceId: use header/body if provided else derive a web fingerprint
        val deviceId = (req.deviceId?.takeIf { it.isNotBlank() }) ?: getDeviceIdOrWebFingerprintFromHeader(servletRequest)
        val sessionId = UUID.randomUUID().toString()

//        // Enforce max 3 devices (no TTL in Redis)
//        if (!limiter.checkAndRegisterEvictOldestToKeepMaxMinusOne(user.id, deviceId, maxDevices = 3)) {
//            return ResponseEntity.status(429)
//                .body(mapOf("error" to "device_limit_exceeded", "message" to "Max devices (3) reached."))
//        }

//        // Create a sessionId you can later revoke per device
//        limiter.setSessionId(user.id, deviceId, sessionId)

        // Optional hashes for claims (avoid PII)
        val ipHash: String? = null      // set from request.remoteAddr if you want
        val ua  = servletRequest.getHeader("User-Agent")
        val uaHash = ua?.let { sha256(it).take(16) }

        val tokenPair = jwt.generateAccessToken(
            userId = user.id,
            deviceId = deviceId,
            sessionId = sessionId,
            roles = user.roles,
            scope = listOf("user.read", "user.write"),
            tenant = user.tenant,
            ipHash = ipHash,
            uaHash = uaHash
        )

        return ResponseEntity.ok(
            LoginResponse(
                accessToken = tokenPair.accessToken,
                accessTokenExpiresAt = tokenPair.accessTokenExpiresAt,
                refreshToken = tokenPair.refreshToken,
                refreshTokenExpiresAt = tokenPair.refreshTokenExpiresAt,
                deviceId = deviceId,
                sessionId = sessionId
            )
        )
    }


    private fun webFingerprint(ua: String?): String =
        "WEB:${sha256(ua ?: "unknown").take(16)}"

    private fun sha256(s: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(s.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    private fun authenticate(mobileNumber: String, otp: String): AuthUser? {
        return if (otp == "254265") AuthUser("1", listOf("ADMIN"), "default") else null
    }
}