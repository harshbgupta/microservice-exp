package com.kritsn.lib.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*


/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since May 12, 2025
 */

@Component
class JwtUtil {
    val logger = LoggerFactory.getLogger(JwtUtil::class.java)
    val secretKey =
        "d2dca4b9760f23b7d46579d53786123d6c48c77c971fb0f5da24158106ac22d73d4ec0676312ce8ad0408d7f57c663bb522685835e93bbb4733d68db1a459282c7a22b00124499349d4b053d63df07530fc8b5ebfaec2d143f06accf9f4489ababdb64588881db3d69c369678422168e1669cb306e6ee5b30893cb5bc2ecbe02a85565e8af3bcdb9e2ea5f475d830e6c5133e76ad8e884bf3fcfb6828d3f4093d8fc24aeae6a1caef506e865a9eae262e74a98f170d32114587574185551d05d67b703ae0a034fdc71f09bdcf7da84dc0003a1134024d1b739b488e478381acf4411f423c0d4ae244a8dd7ce77981fc2ca525c3f68a09f2d5ea012e99dae33ab"
    private val expirationTime: Long = 1000 * 60 * 60 * 24 // 24 hour

    @Throws(Exception::class)
    fun generateToken(mobileNumber: String): String {
        val now = System.currentTimeMillis()
        return Jwts.builder().setSubject(mobileNumber)
//            .setClaims(claims)
            .setIssuer("Kritsn LLP").setIssuedAt(Date(now)).setExpiration(Date(now + expirationTime))
            .signWith(SignatureAlgorithm.HS256, secretKey).compact()
    }

    @Throws(Exception::class)
    fun refreshToken(tokenWithBearer: String?): String {
        tokenWithBearer ?: throw JwtException("Token is Null")

        //Validating Token
        val now = System.currentTimeMillis()
        val userName = getUserNameFromJwtToken(tokenWithBearer)
        //Refreshing Token
        return Jwts.builder().setSubject(userName)
//            .setClaims(claims)
            .setIssuer("Kritsn LLP").setIssuedAt(Date(now)).setExpiration(Date(now + expirationTime))
            .signWith(SignatureAlgorithm.HS256, secretKey).compact()

    }

    private fun key(): Key {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))
    }

    @Throws(Exception::class)
    fun getUserNameFromJwtToken(token: String): String {
        val jwtToken = token.removePrefix(BEARER)
        return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(jwtToken).body.subject
    }

    @Throws(Exception::class)
    fun validateJwtToken(token: String?): Boolean {
        if (token.isNullOrEmpty()) {
            throw JwtException("Token is null Or Empty")
        }
        try {
            val jwtToken = token.removePrefix(BEARER)
            Jwts.parserBuilder().setSigningKey(key()).build().parse(jwtToken)
            return true
        } catch (e: MalformedJwtException) {
            logger.error("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            logger.error("JWT token is expired: {}", e.message)
        } catch (e: UnsupportedJwtException) {
            logger.error("JWT token is unsupported: {}", e.message)
        } catch (e: java.lang.IllegalArgumentException) {
            logger.error("JWT claims string is empty: {}", e.message)
        }
        return false
    }


    ///////////////////////////////////////////////////////////////////////////
    // Old code not using as of now
    ///////////////////////////////////////////////////////////////////////////
    @Throws(Exception::class)
    fun validateTokenAndExtractClaims(tokenWithBearer: String?): Claims? {
        tokenWithBearer ?: throw JwtException("Token is Null")
        val jwtToken = if (tokenWithBearer.startsWith(BEARER)) {
            tokenWithBearer.removePrefix(BEARER)
        } else {
            throw JwtException("Token must start with appropriate \"key\"")
        }

        //below line will throw ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException exceptions in respective cases
        //so no need to check if token is expired or malformed etc., in so below line will take care token validation completely or throw appropriate exception
        val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).body
//        return if (claims?.expiration?.after(Date()) == true) claims else null
        return claims
    }

    @Throws(Exception::class)
    fun validateToken(tokenWithBearer: String?): Boolean {
        //Validating Token
        tokenWithBearer ?: throw JwtException("Token is Null")
        val jwtToken = if (tokenWithBearer.startsWith(BEARER)) {
            tokenWithBearer.removePrefix(BEARER)
        } else {
            throw JwtException("Token must start with appropriate \"key\"")
        }

        val tokenValidationStatus = try {
            val c = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken)
            true
        } catch (e: ExpiredJwtException) {
            // Token has expired
            e.printStackTrace()
            false
        } catch (e: SignatureException) {
            // Token is invalid
            e.printStackTrace()
            false
        } catch (e: MalformedJwtException) {
            e.printStackTrace()
            false
        } catch (e: UnsupportedJwtException) {
            e.printStackTrace()
            false
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            false
        }
        return tokenValidationStatus
    }

    @Throws(Exception::class)
    fun getClaimsFromToken(tokenWithBearer: String): Claims {
        //Validating Token
        if (!validateToken(tokenWithBearer)) throw JwtException("Token is not valid")

        val jwtToken = tokenWithBearer.removePrefix(BEARER)

        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken).body
    }
}