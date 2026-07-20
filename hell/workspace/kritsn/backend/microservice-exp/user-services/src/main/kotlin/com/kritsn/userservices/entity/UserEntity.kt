package com.kritsn.userservices.entity

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate


@Entity
@Table(name = "users")
open class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null, // Mutable field for JPA

    @Column(unique = false, nullable = false)
    open var countryCode: String? = null,
    @Column(unique = true, nullable = false)
    open var mobileNumber: String? = null,
    @Column(unique = false, nullable = false)
    open var firstName: String? = null,
    @Column(nullable = true)//handling case if last name is not there
    open var lastName: String? = null,
    open var dob: LocalDate? = null,
    open var email: String? = null,
    open var userType: String? = null,

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private var createdAt: Instant = Instant.now(),
    @Column(nullable = false, updatable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private var updatedAt: Instant = Instant.now()
) {
    //Use @PrePersist to set the createdAt field only when a new entity is being persisted.
    @PrePersist
    fun onCreate() {
        createdAt = Instant.now()
        updatedAt = Instant.now()
    }

    //    Use @PreUpdate to update the updatedAt field every time the entity is updated.
    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}