package com.kritsn.userservices.controller

import com.kritsn.lib.base.BaseResponse
import com.kritsn.lib.base.buildSuccessResponse
import com.kritsn.userservices.dto.request.CreateUserRequest
import com.kritsn.userservices.dto.request.UpdateUserNameRequest
import com.kritsn.userservices.service.UserService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class UserController {

    @Autowired
    lateinit var userService: UserService

    @GetMapping("/test")
    fun test(): BaseResponse {
        return buildSuccessResponse()
    }

    @PostMapping("/create")
    private fun createUser(@Valid @RequestBody request: CreateUserRequest): BaseResponse {
        return userService.createUser(request)
    }

    @PatchMapping("/{id}/update-name")
    private fun updateUserFirstNameAndLastName(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserNameRequest
    ): BaseResponse {
        return userService.updateUserFirstNameAndLastName(id, request)
    }


    @GetMapping("/order")
    private fun feignOrderApi(): String {
        return userService.getOrderTest();
    }


}