package it.polito.apigateway

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.time.LocalDateTime

@RestController
class HomeController {

    // nullable since can be accessed also not logged in
    @GetMapping("","/")
    fun home(principal: Principal?): Map<String, Any?> {
        return mapOf(
            "name" to "home",
            "date" to LocalDateTime.now(),
            "principal" to principal
        )
    }

    // principal: Principal ==> spring knows hot to inject
    @GetMapping("/secure")
    fun secure(): Map<String, Any?> {
        val authentication = SecurityContextHolder.getContext().authentication
        return mapOf(
            "name" to "secure",
            "date" to LocalDateTime.now(),
            "principal" to authentication.principal
        )
    }
    @PostMapping("/data")
    fun postData(@RequestBody data: Map<String, String>,authentication: Authentication): Map<String, Any> {
        val principal = authentication.principal as OidcUser//prima era JWT TODO
        return data.entries.associate { e -> e.key to e.value.uppercase() }
            .plus("username" to principal.getClaim("preferred_username"))
            .plus("userInfo" to principal.getClaim("userInfo"))
            .plus("roles" to principal.authorities.map { it.authority })
            .plus("dateTime" to LocalDateTime.now().toString())
    }
    @GetMapping("/me")
    fun me(
        @CookieValue(name="XSRF-TOKEN", required = false)
        xsrf: String?,
        authentication: Authentication?
    ): Map<String, Any?> {
        val principal: OidcUser? = authentication?.principal as? OidcUser
        val name = principal?.preferredUsername ?: ""
        return mapOf(
            "name" to name,
            "loginUrl" to "/oauth2/authorization/crmclient",
            "logoutUrl" to "/logout",
            "principal" to principal,
            "xsrfToken" to xsrf
        )
    }
}