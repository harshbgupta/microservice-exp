package com.kritsn.gateway.api.controller

import com.kritsn.gateway.api.dto.request.LoginRequest
import com.kritsn.gateway.domain.service.AuthService
import com.kritsn.lib.logger.Timber
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.ContentCachingRequestWrapper

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 06, 2025
 */
@RestController
@RequestMapping("/api/v1/public")
class PublicController(
    private val authService: AuthService,
) {
    @PostMapping("/login")
    fun login(
        request: HttpServletRequest,
        @RequestBody req: LoginRequest
    ): ResponseEntity<Any> {
        Timber.d {
            "check logging login"
        }
        return authService.loginMobile(req, request)
    }

    /*@PostMapping("/register")
    fun register(
        @RequestBody req: LoginRequest
    ): ResponseEntity<Any> {
        Timber.d {
            "check logging: register"
        }
        return authService.loginMobile(req, ua)
    }*/

}