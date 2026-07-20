package com.kritsn.userservices.service

//import jakarta.transaction.Transactional ///better to user spring framework one
import com.kritsn.lib.base.BaseResponse
import com.kritsn.lib.base.buildSuccessResponse
import com.kritsn.userservices.dto.request.CreateUserRequest
import com.kritsn.userservices.dto.request.UpdateUserNameRequest
import com.kritsn.userservices.feign.UserOrderFeignClient
import com.kritsn.userservices.mapper.UserMapper
import com.kritsn.userservices.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Aug 21, 2025
 */
@Service
class UserService @Autowired constructor(
    val userOrderFeignClient: UserOrderFeignClient,
    val repository: UserRepository,
//    val userMapper: UserMapper
) {

    val userMapper = UserMapper.INSTANCE // requires componentModel default

    fun getOrderTest(): String {
        println("TEST:  UserService -> getOrderTest")
        return userOrderFeignClient.getOrderTest()
    }

    @Transactional(readOnly = true)
    fun getUserDetails(id: Long): BaseResponse {
        val user = repository.findById(id).orElseThrow{
            throw NoSuchElementException("User with ID $id not found")
        }
        val entity = userMapper.toResponse(entity = user)
        return buildSuccessResponse(entity)
    }

    @Transactional
    fun createUser(request: CreateUserRequest): BaseResponse {
        val entity = userMapper.toEntity(request)
        val user = repository.save(entity)
        return buildSuccessResponse(user)
    }

    @Transactional
    fun updateUserFirstNameAndLastName(id: Long, request: UpdateUserNameRequest): BaseResponse {
        val user = repository.findById(id).orElseThrow {
            throw NoSuchElementException("User with ID $id not found")
        }
        user.firstName = request.firstName
        user.lastName = request.lastName
        repository.save(user) // Saves the updated name
        return buildSuccessResponse()
    }
}