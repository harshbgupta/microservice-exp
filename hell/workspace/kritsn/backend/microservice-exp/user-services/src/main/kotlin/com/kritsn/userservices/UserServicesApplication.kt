package com.kritsn.userservices

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients


@SpringBootApplication(scanBasePackages = ["com.kritsn.userservices", "com.kritsn.lib"])
@EnableDiscoveryClient
@EnableFeignClients
class UserServicesApplication

fun main(args: Array<String>) {
    runApplication<UserServicesApplication>(*args)
}
