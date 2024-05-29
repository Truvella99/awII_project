package it.polito.customerrelationshipmanagement

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthConverterProperties: JwtAuthConverterProperties
) {

    @Bean
    fun jwtAuthConverter(): JwtAuthConverter {
        return JwtAuthConverter(jwtAuthConverterProperties)
    }

    @Bean
    fun filterChain(httpSecurity: HttpSecurity, jwtAuthConverter: JwtAuthConverter): SecurityFilterChain {
        return httpSecurity
            .authorizeHttpRequests {
                it.requestMatchers("/").permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer {
                it.jwt { jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthConverter) }
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .csrf { it.disable() }
            .cors { it.disable() }
            .build()
    }
}