package com.kritsn.userservices.mapper

import com.kritsn.userservices.dto.request.CreateUserRequest
import com.kritsn.userservices.dto.response.UserResponse
import com.kritsn.userservices.entity.UserEntity


/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 02, 2025
 */
//@Mapper(componentModel = "spring")
//interface UserMapper {
//
//    // Request DTO → Entity
//    @Mapping(target = "id", ignore = true) // id is generated
////    @Mapping(target = "userType", constant = "USER") // setting constant value
////    @Mapping(target = "createdAt", ignore = true) // Auto generated
////    @Mapping(target = "updatedAt", ignore = true) // Auto generated
//    fun toEntity(request: UserRequest?): UserEntity
//
//    // Entity → Response DTO
//    @Mapping(target = "userType", constant = "USER") // setting constant value
//    fun toResponse(entity: UserEntity?): UserResponse
//}

class UserMapper{
    companion object{
        val INSTANCE = UserMapper()
    }

    fun toEntity(request: CreateUserRequest): UserEntity{
        return UserEntity(
            countryCode = request.countryCode,
            mobileNumber = request.mobileNumber,
            firstName = request.firstName,
            lastName = request.lastName,
            dob = request.dob,
            email = request.email,
            userType = "USER"
        )
    }

    fun toResponse(entity: UserEntity): UserResponse{
        return UserResponse(
            countryCode = entity.countryCode,
            mobileNumber = entity.mobileNumber,
            firstName = entity.firstName,
            lastName = entity.lastName,
            dob = entity.dob,
            email = entity.email,
            userType = "USER"
        )
    }
}