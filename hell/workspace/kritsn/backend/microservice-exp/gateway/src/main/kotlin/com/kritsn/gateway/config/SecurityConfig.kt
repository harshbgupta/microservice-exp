package com.kritsn.gateway.config

import com.kritsn.gateway.filter.JwtAuthenticationFilter
import com.kritsn.gateway.filter.JwtEntryPoint
import com.kritsn.lib.jwt.JwtUtil
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    val jwtAuthenticationFilter: JwtAuthenticationFilter,
    val jwtEntryPoint: JwtEntryPoint,
) {

    private val log = LoggerFactory.getLogger(SecurityConfig::class.java)

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .exceptionHandling { it.authenticationEntryPoint(jwtEntryPoint) }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/api/v1/user/**").permitAll()
                auth.requestMatchers("/api/v1/order/**").permitAll()
                auth.requestMatchers("/api/v1/open/**").permitAll()
                auth.requestMatchers("/health").permitAll()
                auth.requestMatchers("/v3/api-docs/**").permitAll()
                auth.requestMatchers("/actuator/**").permitAll()
                auth.anyRequest().authenticated()
            }

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}