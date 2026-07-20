package com.kritsn.gateway.domain.dto

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 06, 2025
 */

data class AuthUser(val id: String, val roles: List<String>, val tenant: String?)