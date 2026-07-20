package com.kritsn.gateway.filter

import com.kritsn.gateway.util.PUBLIC_URLS
import com.kritsn.lib.jwt.JwtUtil
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.IOException
import java.util.*

@Component
class JwtAuthenticationFilter(val jwtUtil: JwtUtil) : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(JwtAuthenticationFilter::class.java)

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val isExcludedPath = PUBLIC_URLS.any { request.servletPath.contains(it) }
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
            // Check for Authorization header
            val authHeader = request.getHeader("Authorization")
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Missing or invalid Authorization header")
                return
            }

            val token = authHeader.substring(7)

            val subject: String
            try {
                val claims = Jwts.parser().setSigningKey(jwtUtil.secretKey.toByteArray()).parseClaimsJws(token).getBody()
                subject = claims.subject // Usually the username or userId
            } catch (e: JwtException) {
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.writer.write("Invalid JWT token")
                return
            }

            // Wraps request to add header for downstream service
            val wrappedRequest: HttpServletRequest = object : HttpServletRequestWrapper(request) {
                override fun getHeader(name: String?): String? {
                    if ("X-User-Id".equals(name, ignoreCase = true)) {
                        return subject
                    }
                    return super.getHeader(name)
                }

                override fun getHeaders(name: String?): Enumeration<String?>? {
                    if ("X-User-Id".equals(name, ignoreCase = true)) {
                        return Collections.enumeration(listOf(subject))
                    }
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