package it.polito.apigateway

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig(val crr: ClientRegistrationRepository) {

    fun oidcLogoutSuccessHandler() = OidcClientInitiatedLogoutSuccessHandler(crr)
        .also { it.setPostLogoutRedirectUri("http://localhost:8080/") /*Load homepage after logout*/ }

    // httpSecurity: HttpSecurity => builder for my security configuration
    @Bean // is configuring the securityFilterChain which operates before forwarding request to my controllers
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .authorizeHttpRequests {
                // rules examples
                it.requestMatchers("/", "/login","/logout").permitAll() // permit all requests to /
                it.requestMatchers("/secure").authenticated()
                it.requestMatchers("/anon").anonymous()
                it.anyRequest().denyAll()
            }
            .oauth2Login {  }
            .logout { it.logoutSuccessHandler(oidcLogoutSuccessHandler()) }
            .build()
    }
}