package com.kritsn.gateway.domain.service

import com.kritsn.gateway.domain.dto.TokenPair
import com.kritsn.lib.util.secretKey
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.json.JSONObject
import org.springframework.stereotype.Service
import java.security.Key
import java.time.Instant
import java.util.*
import javax.crypto.spec.SecretKeySpec

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 06, 2025
 */
@Service
class JwtService(      // e.g., "kritsn-services"
    private val accessTtlSeconds: Long = 24 * 60 * 60,   // 24 Hours
    private val refreshTtlSeconds: Long = 30L * 24 * 3600 // 30 days (if you use refresh)
) {

    private val issuer: String = "kritsn-gateway"
    private val audience: String = "kritsn-services"
    private val key: Key = hmacShaKey(secretKey)

    fun generateAccessToken(
        userId: String,
        deviceId: String,
        sessionId: String,
        roles: List<String> = emptyList(),
        scope: List<String> = emptyList(),
        tenant: String? = null,
        ipHash: String? = null,
        uaHash: String? = null,
    ): TokenPair {
        val now = Instant.now()
        val iat = Date.from(now)
//        val nbf = Date.from(now.plusSeconds(notBeforeSkewSeconds))
        val exp = Date.from(now.plusSeconds(accessTtlSeconds))
        val jti = UUID.randomUUID().toString()

        val builder = Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .setIssuer(issuer)
            .setSubject(userId)
            .setAudience(audience)
            .setIssuedAt(iat)
//            .setNotBefore(nbf)
            .setExpiration(exp)
            .setId(jti)
            // Custom claims (names are conventional but free-form)
            .claim("sid", sessionId)      // session id (server-managed)
            .claim("did", deviceId)       // device id used in limiter
            .claim("roles", roles)
            .claim("scope", scope)
            .claim("auth_time", now.epochSecond)
            .also { if (tenant != null) it.claim("tenant", tenant) }
            .also { if (ipHash != null) it.claim("ip_hash", ipHash) }
            .also { if (uaHash != null) it.claim("ua_hash", uaHash) }
            .signWith(key, SignatureAlgorithm.HS256)

        val access = builder.compact()

        // Optional: generate refresh token (opaque or JWT). Shown as JWT for completeness.
        val refreshExp = Date.from(now.plusSeconds(refreshTtlSeconds))
        val refresh = Jwts.builder()
            .setIssuer(issuer)
            .setSubject(userId)
            .setAudience("$audience:refresh")
            .setIssuedAt(iat)
            .setExpiration(refreshExp)
            .setId("rt-$jti")
            .claim("sid", sessionId)
            .claim("did", deviceId)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()

        return TokenPair(
            accessToken = access,
            accessTokenExpiresAt = exp.time / 1000,
            refreshToken = refresh,
            refreshTokenExpiresAt = refreshExp.time / 1000
        )
    }

    private fun hmacShaKey(secret: String): Key {
        val raw = secret.trim()
        // Accept both raw and base64 secrets
        val bytes = try {
            Base64.getDecoder().decode(raw)// treat as Base64 if it decodes
        } catch (_: IllegalArgumentException) {
            secret.toByteArray(Charsets.UTF_8)
        }
//        require(bytes.size >= 32) { "HS256 requires >= 32-byte secret" }
        return SecretKeySpec(bytes, SignatureAlgorithm.HS256.jcaName)
    }

    fun parseAndValidateToken(token: String): Claims {
//            return Jwts.parser()
//                    .setSigningKey(key)
//                    .parseClaimsJws(token)
//                    .body
        isExpiredByClock(token)
        return Jwts.parserBuilder()
            .setSigningKey(key) // Use your signing key
            .setAllowedClockSkewSeconds(120)   // 2 minutes skew for exp/nbf/iat
            .build()
            .parseClaimsJws(token)
            .body
    }

    fun expFrom(token: String): Long {
        val payloadB64 = token.split(".")[1]
        val json = String(Base64.getUrlDecoder().decode(payloadB64))
        return JSONObject(json).getLong("exp") // seconds since epoch
    }

    fun isExpiredByClock(token: String): Boolean {
        val expSec = expFrom(token)
        val nowSec = Instant.now().epochSecond
        println("nowSec=$nowSec, expSec=$expSec, diff=${expSec - nowSec}s")
        return nowSec > expSec
    }
}