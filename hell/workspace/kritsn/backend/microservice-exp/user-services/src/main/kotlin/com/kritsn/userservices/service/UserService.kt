package com.kritsn.userservices.service

import com.kritsn.lib.base.BaseResponse
import com.kritsn.userservices.feign.UserOrderFeignClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Aug 21, 2025
 */
@Service
class UserService {
    @Autowired
    private lateinit var userOrderFeignClient: UserOrderFeignClient

    fun getOrderTest(): BaseResponse {
        return userOrderFeignClient.getOrderTest()
    }

}