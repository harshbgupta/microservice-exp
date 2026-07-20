package com.kritsn.userservices.service

import com.kritsn.lib.base.BaseResponse
import com.kritsn.lib.base.Response
import com.kritsn.lib.base.buildErrorResponse
import com.kritsn.lib.base.buildServerErrorResponse
import com.kritsn.lib.base.buildSuccessResponse
import com.kritsn.lib.jwt.JwtUtil
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AuthService(@Autowired val jwtUtil: JwtUtil) {

    fun handleGenerateToken(mobileNumber: String): BaseResponse {
        try {
            val jwtToken = jwtUtil.generateToken(mobileNumber)
            return buildSuccessResponse(jwtToken)
        } catch (e: Exception) {
            e.printStackTrace()
            return buildErrorResponse(e.message)
        }
    }


    fun handleRefreshToken(tokenWithBearer: String?): BaseResponse {
        try {
            //Refreshing token
            val refreshedJwtToken = jwtUtil.refreshToken(tokenWithBearer)
            return buildSuccessResponse(refreshedJwtToken)
        } catch (e: Exception) {
            e.printStackTrace()
            return buildErrorResponse(e.message)
        }
    }

    @CircuitBreaker(name = "circuitBreakerCB", fallbackMethod = "circuitBreakerFallback")
    @Retry(name = "circuitBreakerRetry")
    fun handleCircuitBreaker(): BaseResponse {
        return buildSuccessResponse("Success response for circuit Breaker")
    }

    // Fallback executed when retries & circuit breaker fail
    fun circuitBreakerFallback(userId: String, ex: Throwable): BaseResponse {
        return buildServerErrorResponse(Exception("Fallback response for circuit Breaker"))
    }
}