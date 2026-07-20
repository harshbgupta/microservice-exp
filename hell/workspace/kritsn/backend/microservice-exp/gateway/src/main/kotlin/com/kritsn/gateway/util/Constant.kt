package com.kritsn.gateway.util

const val MOBILE_NUMBER = "mobileNumber"
const val BEARER = "Bearer "
const val URL_PREFIX_OPEN = "/api/v1/open"
const val URL_PREFIX_PUBLIC = "/api/v1/public"
const val URL_PREFIX_TOKEN = "/api/v1/token"
const val URL_PREFIX_USER = "/api/v1/user"
const val URL_PREFIX_ORDER = "/api/v1/order"
const val URL_HEALTH = "/health"
const val URL_ACTUATOR = "/actuator"
const val URL_API_DOCS = "/v3/api-docs"

val PUBLIC_URLS = listOf(
    URL_PREFIX_OPEN, URL_PREFIX_PUBLIC, URL_PREFIX_TOKEN, URL_PREFIX_USER, URL_HEALTH,
    URL_ACTUATOR, URL_API_DOCS
)