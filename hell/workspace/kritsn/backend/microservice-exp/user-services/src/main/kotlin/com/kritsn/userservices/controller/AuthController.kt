package com.kritsn.userservices.controller

import com.kritsn.lib.base.BaseResponse
import com.kritsn.lib.base.Response
import com.kritsn.lib.base.buildSuccessResponse
import com.kritsn.userservices.dto.ReqUser
import com.kritsn.userservices.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    @GetMapping("/test")
    fun test(): String {
        return "Login Controller Test Success"
    }

    @Autowired
    lateinit var authService: AuthService

    @GetMapping("/dummy")
    private fun dummyApi(): BaseResponse {
        return buildSuccessResponse()
    }

    @PostMapping("/create", consumes = ["application/json"], produces = ["application/json"])
    private fun createNewToken(@RequestBody reqUser: ReqUser): BaseResponse {
        return authService.handleGenerateToken(reqUser.mobileNumber)
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