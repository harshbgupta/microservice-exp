package com.kritsn.userservices.controller

import com.kritsn.lib.base.BaseResponse
import com.kritsn.lib.base.buildSuccessResponse
import com.kritsn.userservices.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserController {

    @Autowired
    lateinit var userService: UserService

    @GetMapping("/test")
    fun test(): String {
        return "User Controller Test Success"
    }

    @GetMapping("/dummy")
    private fun dummyApi(): BaseResponse {
        return buildSuccessResponse()
    }

    @GetMapping("/order")
    private fun feignOrderApi(): BaseResponse {
        return userService.getOrderTest();
    }
}