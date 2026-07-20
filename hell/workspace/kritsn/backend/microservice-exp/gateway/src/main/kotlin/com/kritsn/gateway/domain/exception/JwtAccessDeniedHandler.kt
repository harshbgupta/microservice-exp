package com.kritsn.gateway.domain.exception

import com.kritsn.gateway.infrastructure.util.sendErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 08, 2025
 */
@Component
class JwtAccessDeniedHandler : AccessDeniedHandler {
    private val log = LoggerFactory.getLogger(JwtAccessDeniedHandler::class.java)

    override fun handle(
        req: HttpServletRequest,
        res: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        accessDeniedException.printStackTrace()
        sendErrorResponse(res, HttpStatus.FORBIDDEN, "Forbidden: insufficient privileges")
    }
}