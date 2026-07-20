package com.kritsn.gateway.domain.exception

import com.kritsn.lib.base.buildErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.AuthorizationServiceException
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.time.format.DateTimeParseException

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since May 21, 2025
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(ex: NoHandlerFoundException): ResponseEntity<Any> {
        ex.printStackTrace()
        val response = buildErrorResponse(
            message = "The requested URL ${ex.requestURL} does not exist, Error => ${ex::class.java.simpleName}: ${ex.message}",
            httpStatus = HttpStatus.NOT_FOUND,
        )
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<Any> {
        ex.printStackTrace()
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }
        var errorMessage = ""
        errors.values.map { error -> if (errorMessage.isNotEmpty() || error.isNotEmpty()) errorMessage = "$error: $errorMessage" }
        val response = buildErrorResponse(
            message = errorMessage.ifEmpty { "Issue => ${ex::class.java.simpleName}: ${ex.message}" },
            httpStatus = HttpStatus.BAD_REQUEST,
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DateTimeParseException::class)
    fun handleDateTimeParseException(ex: DateTimeParseException): ResponseEntity<Any> {
        ex.printStackTrace()
        val response = buildErrorResponse(
            message = "Invalid date/time format => ${ex::class.java.simpleName}: ${ex.message}",
            httpStatus = HttpStatus.BAD_REQUEST,
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Any> {
        ex.printStackTrace()
        val response = buildErrorResponse(
            message = "Invalid argument => ${ex::class.java.simpleName}: ${ex.message}",
            httpStatus = HttpStatus.BAD_REQUEST,
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(AuthorizationServiceException::class)
    fun handleAuthorizationServiceException(ex: AuthorizationServiceException): ResponseEntity<Any> {
        ex.printStackTrace()
        val response = buildErrorResponse(
            message = "Authorization error => ${ex::class.java.simpleName}: ${ex.message}",
            httpStatus = HttpStatus.FORBIDDEN,
        )
        return ResponseEntity(response, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<Any> {
        ex.printStackTrace()
        val response = buildErrorResponse(
            message = "Access denied => ${ex::class.java.simpleName}: ${ex.message}",
            httpStatus = HttpStatus.FORBIDDEN,
        )
        return ResponseEntity(response, HttpStatus.FORBIDDEN)
    }


    @ExceptionHandler(NoResourceFoundException::class)
    fun handleResourceFoundException(ex: NoResourceFoundException): ResponseEntity<Any> {
        ex.printStackTrace()
        val response = buildErrorResponse(
            message = "No Resource => ${ex::class.java.simpleName}: ${ex.message}",
            httpStatus = HttpStatus.NOT_FOUND,
        )
        return ResponseEntity(response, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(InsufficientAuthenticationException::class)
    fun handleInsufficientAuthenticationException(ex: InsufficientAuthenticationException): ResponseEntity<Any> {
        ex.printStackTrace()
        val response = buildErrorResponse(
            message = "Authentication is required => ${ex::class.java.simpleName}: ${ex.message}",
            httpStatus = HttpStatus.FORBIDDEN,
        )
        return ResponseEntity(response, HttpStatus.FORBIDDEN)
    }


    ///////////////////////////////////////////////////////////////////////////
    // uncomment following the code, to debug any UnHandledException, the exception will be caught here, once You
    // done with debug comment back
    //////////////////////////////////`/////////////////////////////////////////

    @ExceptionHandler(Exception::class)
    fun forDebugAnyUnHandledException(ex: Exception): ResponseEntity<Any> {
        ex.printStackTrace()
        val response = buildErrorResponse(
            message = "Debug UnHandled Exception => ${ex::class.java.simpleName}: ${ex.message}",
            httpStatus = HttpStatus.BAD_GATEWAY,
        )
        return ResponseEntity(response, HttpStatus.BAD_GATEWAY)
    }

}