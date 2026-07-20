package com.kritsn.orderservice.cotroller

import com.kritsn.lib.base.BaseResponse
import com.kritsn.lib.base.buildSuccessResponse
import com.kritsn.lib.util.toJsonString
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Aug 20, 2025
 */
@RestController
@RequestMapping("/api/v1/order")
class OrderController {

    @RequestMapping("/test")
    fun test(): BaseResponse {
        val response = buildSuccessResponse("Order Service dummy")
        println("TEST:  OrderController -> /api/v1/order/test : ${response.toJsonString()}")
        return response;
    }
}