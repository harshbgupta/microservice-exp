package com.kritsn.userservices.model

//import jakarta.persistence.*
import java.util.*


//@Entity
//@Table(name = "user_table")
open class User(
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long = 0, // Mutable field for JPA
    open var firstName: String? = null,
    open var lastName: String? = null,
    open var mobileNumber: String = "",
    open var email: String? = null,
    open var userType: String? = null,
//    @Temporal(TemporalType.TIMESTAMP)
    private var createdAt: Date = Date(),
//    @Temporal(TemporalType.TIMESTAMP)
    private var updatedAt: Date = Date()
) {
    //Use @PrePersist to set the createdAt field only when a new entity is being persisted.
//    @PrePersist
    fun onCreate() {
        createdAt = Date()
        updatedAt = Date()
    }

    //Use @PreUpdate to update the updatedAt field every time the entity is updated.
//    @PreUpdate
    fun onUpdate() {
        updatedAt = Date()
    }
}