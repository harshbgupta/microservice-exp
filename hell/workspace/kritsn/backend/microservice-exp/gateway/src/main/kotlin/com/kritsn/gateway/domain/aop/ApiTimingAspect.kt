package com.kritsn.gateway.domain.aop

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.*
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.util.ContentCachingRequestWrapper


/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Aug 17, 2025
 */
@Aspect
@Component
@EnableAspectJAutoProxy
class ApiTimingAspect(private val meterRegistry: MeterRegistry) {

    @Before("within(@org.springframework.web.bind.annotation.RestController *)")
    fun incrementRequestCounter() {
        meterRegistry.counter("requests").increment()
        val counter = Counter
                .builder("user.requests")
                .description("Auto-counted metric from AOP")
                .register(meterRegistry);

        counter.increment();
    }

    @AfterReturning(
        pointcut = "within(@org.springframework.web.bind.annotation.RestController *)",
        returning = "result"
    )
    fun logAfterControllerMethods(result: Any?) {
        println("AfterReturning Advice $result")
    }

    @AfterThrowing(
        pointcut = "within(@org.springframework.web.bind.annotation.RestController *)",
        throwing = "exception"
    )
    fun logAfterThrowingControllerMethods(exception: Exception) {
       println("Exception thrown from controller method: " + exception.message)
    }

    //    @Around("execution(* com.kritsn.userservices..*Controller.*(..))")
    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    @Throws(Throwable::class)
    fun logAroundControllerMethods(joinPoint: ProceedingJoinPoint): Any? {
        val start = System.currentTimeMillis()
        val result = joinPoint.proceed() // execute the controller method
        val duration = System.currentTimeMillis() - start

        // Get current HTTP request and response
        val attrs = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
        val request = attrs.request
        val response = attrs.response

        // Extract details
        val method = request.method // GET / POST
        val uri = request.requestURI // e.g. /api/v1/users
        val query = request.queryString // e.g. id=5&page=2
        val fullUrl = request.requestURL.toString() + (if (query != null) "?$query" else "")
        val status = response?.status ?: -1 // HTTP status code
        val requestWrapper = ContentCachingRequestWrapper(request)
        val requestBody = String(requestWrapper.contentAsByteArray)

        println("\n====================== API Starts ===========================")
        println(
            "➡️➡️➡️ \nURL: [$method] $fullUrl \nHeaders: ${getHeadersAsMap(request)} \nBody: ${requestBody.ifBlank { "<empty>" }}"
                    + "\n\n⬅️⬅️⬅️ \nStatus: $status \nDuration: $duration ms \nResponse: $result"
        )
        println("======================  API Ends  ===========================\n")
        return result
    }

    private fun getHeadersAsMap(request: HttpServletRequest): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val key = headerNames.nextElement()
            headers[key] = request.getHeader(key)
        }
        return headers
    }
}