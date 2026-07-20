package com.kritsn.userservices.feign

import com.kritsn.lib.base.BaseResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping


@FeignClient(name = "order-service")
interface UserOrderFeignClient {

    @GetMapping("/api/v1/order/test")
    fun getOrderTest(): BaseResponse
}