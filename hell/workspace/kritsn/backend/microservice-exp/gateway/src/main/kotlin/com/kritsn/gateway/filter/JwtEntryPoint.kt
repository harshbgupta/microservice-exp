package com.kritsn.gateway.filter

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class JwtEntryPoint : AuthenticationEntryPoint {
    private val log = LoggerFactory.getLogger(JwtEntryPoint::class.java)

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        sendErrorResponse(
            response,
            HttpStatus.UNAUTHORIZED,
            "Auth Entry Error => ${authException::class.java.simpleName}: ${authException.message}"
        )
    }
}

fun sendErrorResponse(response: HttpServletResponse, status: HttpStatus, message: String) {
    response.status = status.value()
    response.contentType = "application/json"
    response.writer.write("""{"success":false,"code": ${status.value()},"message": "$message","timestamp":${System.currentTimeMillis()}}""")
}