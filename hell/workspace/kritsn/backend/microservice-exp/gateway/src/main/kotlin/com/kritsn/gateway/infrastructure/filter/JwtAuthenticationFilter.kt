package com.kritsn.gateway.infrastructure.filter

import com.kritsn.gateway.domain.service.JwtService
import com.kritsn.gateway.domain.util.PUBLIC_URLS
import com.kritsn.gateway.infrastructure.session.DeviceSessionLimiter
import com.kritsn.gateway.infrastructure.util.getDeviceIdOrWebFingerprintFromHeader
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.security.SignatureException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.IOException
import java.time.Instant
import java.util.*

@Component
class JwtAuthenticationFilter(
    private val limiter: DeviceSessionLimiter,
    private val jwtService: JwtService
) : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val isExcludedPath = PUBLIC_URLS.any { request.servletPath.contains(it) }
        log.debug("Checking exclusion for path: ${request.servletPath}, isExcluded: $isExcludedPath")
        return isExcludedPath
    }

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        val startTime = System.currentTimeMillis()
        val requestWrapper = ContentCachingRequestWrapper(request)
        val responseWrapper = ContentCachingResponseWrapper(response)
        try {
            // Parse & validate JWT; writes 401 itself on failure
            val claims: Claims = parseClaimsOrUnauthorized(requestWrapper, responseWrapper) ?: return

            // extract data
            val userId = claims.subject ?: run {
                unauthorized(
                    responseWrapper,
                    "unauthorized",
                    "JWT missing subject"
                ); return
            }
            val deviceId = getDeviceIdOrWebFingerprintFromHeader(requestWrapper)
            val sessionId = claims["sid"]?.toString()
            /* val exp = (claims.expiration?.time ?: 0L) / 1000L
            if (exp <= 0) { unauthorized(responseWrapper, "JWT missing/invalid expiration"); return } */

            // check if the device session is valid
//            if (!limiter.checkValidDeviceSessionExists(userId, deviceId)) {
//                unauthorized(
//                    responseWrapper,
//                    "unauthorized",
//                    "Session Expired, Please login again."
//                ); return
//            }

            // Populate Authentication into SecurityContext -> Imp step
            val roles = claims["roles"] as? List<*> ?: emptyList<String>()
            val authorities = roles.map { SimpleGrantedAuthority(it.toString()) }

            val authentication = UsernamePasswordAuthenticationToken(
                userId, // Principal (user ID or username)
                null,  // Credentials (not needed for JWTs)
                authorities // Roles/authorities (extracted from the JWT token)
            )
            // Save the authentication into the SecurityContext
            SecurityContextHolder.getContext().authentication = authentication

            // Wraps request to add header for downstream service
            val wrappedRequest: HttpServletRequest = object : HttpServletRequestWrapper(request) {
                override fun getHeader(name: String?): String? {
                    if ("X-User-Id".equals(name, ignoreCase = true)) return userId
                    if ("X-Device-Id".equals(name, ignoreCase = true)) return deviceId
                    if ("X-Session-Id".equals(name, ignoreCase = true)) return sessionId
                    return super.getHeader(name)
                }

                override fun getHeaders(name: String?): Enumeration<String?>? {
                    if ("X-User-Id".equals(name, ignoreCase = true)) return Collections.enumeration(listOf(userId))
                    if ("X-Device-Id".equals(name, ignoreCase = true)) return Collections.enumeration(listOf(deviceId))
                    if ("X-Session-Id".equals(
                            name,
                            ignoreCase = true
                        )
                    ) return Collections.enumeration(listOf(sessionId))
                    return super.getHeaders(name)
                }
            }
            filterChain.doFilter(wrappedRequest, response)
        } finally {
            val duration = System.currentTimeMillis() - startTime
            val requestBody = String(requestWrapper.contentAsByteArray)
            val responseBody = String(responseWrapper.contentAsByteArray)
            logApis(request, response, duration, requestBody, responseBody)
            responseWrapper.copyBodyToResponse()
        }
    }


    /**
     * Extracts the Bearer token, validates it using JwtService, and returns Claims.
     * If anything fails, writes a proper 401 JSON with RFC6750 WWW-Authenticate header and returns null.
     */
    private fun parseClaimsOrUnauthorized(request: HttpServletRequest, response: HttpServletResponse): Claims? {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            unauthorized(response, "missing_bearer_token", "Missing or invalid Authorization header")
            return null
        }

        val token = authHeader.substring(7)
        return try {
            jwtService.parseAndValidateToken(token)
        } catch (ex: ExpiredJwtException) {
            log.debug("JWT expired: {}", ex.message)
            println("Expired: now=${Instant.now()} exp=${ex.claims?.expiration}")
            unauthorized(response, "token_expired", "Token has expired")
            null
        } catch (ex: SignatureException) {
            log.debug("JWT signature invalid: {}", ex.message)
            unauthorized(response, "invalid_token", "Invalid token signature")
            null
        } catch (ex: JwtException) {
            log.debug("JWT invalid: {}", ex.message)
            unauthorized(response, "invalid_token", "Token validation failed")
            null
        } catch (ex: Exception) {
            log.warn("JWT parsing error: {}", ex.message)
            unauthorized(response, "invalid_request", "Authentication validation failed")
            null
        }
    }

    /** Sends 401 with a JSON body and RFC6750-compliant WWW-Authenticate header */
    private fun unauthorized(res: HttpServletResponse, error: String, description: String) {
        res.status = HttpServletResponse.SC_UNAUTHORIZED
        res.contentType = "application/json"
        res.setHeader(
            "WWW-Authenticate",
            "Bearer realm=\"kritsn-services\", error=\"$error\", error_description=\"$description\""
        )
        res.writer.write("""{"error":"$error","message":"$description"}""")
    }

    ///////////////////////////////////////////////////////////////////////////
    // Other helpers method
    ///////////////////////////////////////////////////////////////////////////
    private fun getHeadersAsMap(request: HttpServletRequest): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val key = headerNames.nextElement()
            headers[key] = request.getHeader(key)
        }
        return headers
    }

    ///////////////////////////////////////////////////////////////////////////
    // Logging apis
    ///////////////////////////////////////////////////////////////////////////
    private fun logApis(
        request: HttpServletRequest,
        response: HttpServletResponse,
        duration: Long,
        requestBody: String,
        responseBody: String
    ) {
        val dividerLine = "--------------------------------------"
        log.info(dividerLine)
        log.info(
            "➡️ [{}] {} | Headers: {} | Body: {}",
            request.method,
            request.requestURI,
            getHeadersAsMap(request),
            requestBody.ifBlank { "<empty>" })

        log.info(
            "⬅️ [{}] {} | Status: {} | Duration: {} ms | Response: {}",
            request.method,
            request.requestURI,
            response.status,
            duration,
            responseBody.ifBlank { "<empty>" })
        log.info(dividerLine)
    }
}