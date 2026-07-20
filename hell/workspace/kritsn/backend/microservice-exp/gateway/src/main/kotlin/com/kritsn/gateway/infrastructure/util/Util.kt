package com.kritsn.gateway.infrastructure.util

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import java.security.MessageDigest

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 08, 2025
 */
fun sendErrorResponse(response: HttpServletResponse, status: HttpStatus, message: String) {
    response.status = status.value()
    response.contentType = "application/json"
    response.writer.write("""{"success":false,"code": ${status.value()},"message": "$message","timestamp":${System.currentTimeMillis()}}""")
}

/**
 * get sha256
 */
fun sha256(s: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    return md.digest(s.toByteArray())
        .joinToString("") { "%02x".format(it) }
}

fun getDeviceIdOrWebFingerprintFromHeader(request: HttpServletRequest): String {
    val header = request.getHeader("X-Device-Id")?.trim()
    if (!header.isNullOrBlank()) return header

    val ua = request.getHeader("User-Agent") ?: "unknown"
    val ip = request.remoteAddr ?: "0.0.0.0"
    val raw = "WEB|$ip|$ua"
    return "WEB:${sha256(raw).take(16)}"
}