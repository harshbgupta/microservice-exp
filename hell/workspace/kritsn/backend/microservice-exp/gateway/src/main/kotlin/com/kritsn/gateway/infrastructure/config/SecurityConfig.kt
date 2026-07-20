package com.kritsn.gateway.infrastructure.config

import com.kritsn.gateway.domain.util.PUBLIC_URLS
import com.kritsn.gateway.domain.exception.JwtAccessDeniedHandler
import com.kritsn.gateway.infrastructure.filter.JwtAuthenticationFilter
import com.kritsn.gateway.domain.exception.JwtEntryPoint
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
    val accessDeniedHandler: JwtAccessDeniedHandler
) {

    private val log = LoggerFactory.getLogger(SecurityConfig::class.java)

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                PUBLIC_URLS.forEach {url->
                    auth.requestMatchers("$url/**").permitAll()
                }
                auth.anyRequest().authenticated()
            }

            .exceptionHandling {
                it.authenticationEntryPoint(jwtEntryPoint)
                it.accessDeniedHandler(accessDeniedHandler) // for
            }
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}