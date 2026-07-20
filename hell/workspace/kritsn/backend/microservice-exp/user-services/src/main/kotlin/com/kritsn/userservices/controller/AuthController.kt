package com.kritsn.userservices.controller

import com.kritsn.lib.base.BaseResponse
import com.kritsn.lib.base.buildSuccessResponse
import com.kritsn.userservices.dto.request.CreateUserRequest
import com.kritsn.userservices.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user-auth")
class AuthController {

    @Autowired
    lateinit var authService: AuthService

    @GetMapping("/test")
    fun test(): BaseResponse {
        return buildSuccessResponse()
    }

    @PostMapping("/create", consumes = ["application/json"], produces = ["application/json"])
    private fun createNewToken(@RequestBody userDto: CreateUserRequest): BaseResponse {
        return authService.handleGenerateToken(userDto.mobileNumber)
    }

    @GetMapping("/refresh")
    fun refreshToken(@RequestHeader("Authorization") token: String?): BaseResponse {
        return authService.handleRefreshToken(token)
    }
    @GetMapping("/circuitBreaker")
    fun refreshToken(): BaseResponse {
        return authService.handleCircuitBreaker()
    }
}