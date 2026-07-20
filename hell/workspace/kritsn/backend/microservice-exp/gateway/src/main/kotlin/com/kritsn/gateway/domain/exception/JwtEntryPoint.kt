package com.kritsn.gateway.domain.exception

import com.kritsn.gateway.infrastructure.util.sendErrorResponse
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
        authException.printStackTrace()
        sendErrorResponse(
            response,
            HttpStatus.UNAUTHORIZED,
            "Auth Entry Error => ${authException::class.java.simpleName}: ${authException.message}"
        )
    }
}