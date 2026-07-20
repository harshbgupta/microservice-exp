package com.kritsn.userservices

import com.kritsn.userservices.mapper.UserMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 05, 2025
 */

@SpringBootTest
class MapperBeanTest(@Autowired val userMapper: UserMapper) {
    @Test
    fun beanExists() { assertNotNull(userMapper) }
}