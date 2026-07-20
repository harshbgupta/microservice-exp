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
class DeviceSessionLimiter(
    private val redis: StringRedisTemplate
) {
    private fun key(userId: String) = "u:$userId:devices"
    private fun sidKey(userId: String, deviceId: String) = "u:$userId:sid:$deviceId"
    private fun orderKey(userId: String) = "u:$userId:devices:order"

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
    fun checkValidDeviceSessionExists(userId: String, deviceId: String, sessionId: String): Boolean {
        val ops = redis.opsForSet()
        val k = key(userId)
        val exists = ops.isMember(k, deviceId) == true

        if (!exists) {
            println("Device session <<INVALID>>: userId=$userId, deviceId=$deviceId, key=$k")
        } else {
            println("Device's particular session is <<valid>>: userId=$userId, deviceId=$deviceId, key=$k")
        }

        // session expire check
        val kSession = sidKey(userId, deviceId)
        val validSession = ops.isMember(kSession, sessionId) == true
        if (validSession) {
            println("Device session <<INVALID>>: userId=$userId, deviceId=$deviceId, key=$k")
        } else {
            println("Device's Pirti session <<INVALID>>: userId=$userId, deviceId=$deviceId, key=$k")
        }
        return exists && validSession
    }

    /**
     * Registers the device for the user. If active sessions are already >= maxDevices,
     * evicts the oldest sessions so that exactly (maxDevices - 1) sessions remain, then
     * adds the current device. Order is tracked in a ZSET (by last seen epoch seconds).
     *
     * This ensures that after this call, the user has at most [maxDevices] active devices,
     * with the newest device included.
     */
    fun checkAndRegisterEvictOldestToKeepMaxMinusOne(userId: String, deviceId: String, maxDevices: Int = 3): Boolean {
        require(maxDevices > 0) { "maxDevices must be > 0" }
        val now = Instant.now().epochSecond.toDouble()
        val kSet = key(userId)
        val kOrder = orderKey(userId)
        val setOps = redis.opsForSet()
        val zOps = redis.opsForZSet()

        // If already registered, bump recency and allow.
        if (setOps.isMember(kSet, deviceId) == true) {
            setOps.add(kSet, deviceId)//add or update
            zOps.add(kOrder, deviceId, now)
            return true
        }

        // Count current active sessions
        val activeCount = setOps.size(kSet) ?: 0

        if (activeCount >= maxDevices) {
            // We want to keep (maxDevices - 1) before adding the new device.
            val toEvict = (activeCount - (maxDevices - 1)).toLong()
            if (toEvict > 0) {
                // Fetch the oldest devices by rank
                val oldest = zOps.range(kOrder, 0, toEvict - 1) ?: emptySet()
                if (oldest.isNotEmpty()) {
                    // Remove them from both the SET and the ORDER ZSET
                    setOps.remove(kSet, *oldest.toTypedArray())
                    zOps.remove(kOrder, *oldest.toTypedArray())
                }
            }
        }

        // Finally, register the new device and set its recency
        setOps.add(kSet, deviceId)
        zOps.add(kOrder, deviceId, now)
        return true
    }


    /** Explicit logout: remove device */
    fun removeDevice(userId: String, deviceId: String): Boolean {
        try {
            val kSet = key(userId)
            val kOrder = orderKey(userId)
            val setOps = redis.opsForSet()
            val zOps = redis.opsForZSet()
            setOps.remove(kSet, deviceId)//remove entries for userid and device id

            zOps.remove(kOrder, deviceId) // remove entries for orders of userid& device id
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

    /**
     * get session Id
     */
    fun getSessionId(userId: String, deviceId: String): String? =
        redis.opsForValue().get(sidKey(userId, deviceId))

    /**
     * get session Id
     */
    fun removeSessionId(userId: String, deviceId: String) =
        redis.opsForSet().remove(sidKey(userId, deviceId))
}