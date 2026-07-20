package com.kritsn.gateway.api.controller

import com.kritsn.gateway.infrastructure.session.DeviceSessionLimiter
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 05, 2025
 */
@RestController
@RequestMapping("/sessions")
class SessionController(private val limiter: DeviceSessionLimiter) {

    @DeleteMapping("/{userId}/{deviceId}")
    fun evictDevice(@PathVariable userId: String, @PathVariable deviceId: String): ResponseEntity<Any> {

//        val status = limiter.removeDeviceWithSessionTimeOut(userId, deviceId)
        val status = limiter.removeDevice(userId, deviceId)
        return if (status) {
            ResponseEntity.ok(mapOf("status" to "removed"))
        } else {
            ResponseEntity.ok(mapOf("status" to "error while removing"))
        }
    }

    @GetMapping("/{userId}")
    fun listDevices(@PathVariable userId: String): ResponseEntity<Any> {
//        val members = limiter.listDevicesWithSessionTimeOut(userId)
        val members = limiter.listDevices(userId)
        return ResponseEntity.ok(members)
    }
}