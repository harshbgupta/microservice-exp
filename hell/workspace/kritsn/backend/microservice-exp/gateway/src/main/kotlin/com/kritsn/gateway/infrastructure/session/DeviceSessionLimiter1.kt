package com.kritsn.gateway.infrastructure.session

import com.kritsn.gateway.domain.util.MAX_SESSION
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 05, 2025
 */
@Component
class DeviceSessionLimiter1(
    private val redis: StringRedisTemplate
) {
    private fun key(userId: String) = "u:$userId:devices"
    private fun sidKey(userId: String, deviceId: String) = "u:$userId:sid:$deviceId"

    ///////////////////////////////////////////////////////////////////////////
    // Session with time out means the session has expiration with time
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Ensures at most [maxDevices] active device entries for a user.
     * Returns true if the device is allowed/registered; false if limit exceeded.
     */
    fun checkAndRegisterWithSessionTimeOut(
        userId: String,
        deviceId: String,
        tokenExpiryEpochSeconds: Long,
        maxDevices: Int = MAX_SESSION
    ): Boolean {
        val ops = redis.opsForZSet()
        val k = key(userId)
        val now = Instant.now().epochSecond

        // 1) Purge expired device entries
        ops.removeRangeByScore(k, Double.NEGATIVE_INFINITY, now.toDouble())

        // 2) If already present → refresh expiry score
        val existingScore = ops.score(k, deviceId)
        if (existingScore != null) {
            ops.add(k, deviceId, tokenExpiryEpochSeconds.toDouble())
            return true
        }

        // 3) Enforce limit
        val activeCount = ops.size(k) ?: 0
        if (activeCount < maxDevices) {
            ops.add(k, deviceId, tokenExpiryEpochSeconds.toDouble())
            return true
        }

        return false
    }

    /** Explicit logout: remove device */
    fun removeDeviceWithSessionTimeOut(userId: String, deviceId: String): Boolean {
        try {
            val key = "u:$userId:devices"
            redis.opsForZSet().remove(key, deviceId)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /** List all active devices */
    fun listDevicesWithSessionTimeOut(userId: String): List<Map<String, Any?>> {
        val key = "u:$userId:devices"
        val now = Instant.now().epochSecond.toDouble()
        val ops = redis.opsForZSet()
        ops.removeRangeByScore(key, Double.NEGATIVE_INFINITY, now) // cleanup
        val members = ops.rangeWithScores(key, 0, -1).orEmpty()
            .map { mapOf("deviceId" to it.value, "expiresAtEpochSec" to (it.score?.toLong() ?: 0L)) }
        return members
    }

    ///////////////////////////////////////////////////////////////////////////
    // Session, no time out
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Ensures at most [maxDevices] active device entries for a user.
     * Returns true if the device is allowed/registered; false if limit exceeded.
     */
    fun checkAndRegister(userId: String, deviceId: String, maxDevices: Int = 3): Boolean {
        val ops = redis.opsForSet()
        val k = key(userId)

        // 1) Already registered? allow
        if (ops.isMember(k, deviceId) == true) {
            return true
        }

        // 2) Count active devices
        val activeCount = ops.size(k) ?: 0
        if (activeCount < maxDevices) {
            ops.add(k, deviceId)
            return true
        }

        return false
    }

    /** Explicit logout: remove device */
    fun removeDevice(userId: String, deviceId: String): Boolean {
        try {
            redis.opsForSet().remove(key(userId), deviceId)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /** List all active devices */
    fun listDevices(userId: String): Set<String> {
        return redis.opsForSet().members(key(userId)) ?: emptySet()
    }


    /** Persist the session-id for the user-device (helpful for targeted logout/revocation) */
    fun setSessionId(userId: String, deviceId: String, sessionId: String) {
        redis.opsForValue().set(sidKey(userId, deviceId), sessionId)
    }

    fun getSessionId(userId: String, deviceId: String): String? =
        redis.opsForValue().get(sidKey(userId, deviceId))
}