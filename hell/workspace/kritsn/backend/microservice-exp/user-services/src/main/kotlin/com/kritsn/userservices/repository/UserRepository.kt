package com.kritsn.userservices.repository

import com.kritsn.userservices.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

/**
 * Copyright © 2025 Kritsn LLP. All rights reserved.
 *
 * @author Radhey (hr-sh)
 * @since Sep 02, 2025
 */
@Repository
interface UserRepository: JpaRepository<UserEntity, Long> {

}