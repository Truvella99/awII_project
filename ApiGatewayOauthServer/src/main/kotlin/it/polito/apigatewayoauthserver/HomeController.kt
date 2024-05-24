package it.polito.apigatewayoauthserver

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal
import java.time.LocalDateTime

@RestController
class HomeController {

    // nullable since can be accessed also not logged in
    @GetMapping("","/")
    fun home(authentication: Authentication?): Map<String,Any?> {
        return mapOf("name" to "server1:8081/", "principal" to authentication?.principal)
    }

    @GetMapping("/data")
    fun postData(@RequestBody data: Map<String, String>,authentication: Authentication): Map<String, Any> {
        val principal = authentication.principal as Jwt
        return data.entries.associate { e -> e.key to e.value.uppercase() }
            .plus("username" to principal.getClaim("preferred_username"))
            .plus("dateTime" to LocalDateTime.now().toString())
    }
    /*// principal: Principal ==> spring knows hot to inject
    @GetMapping("/secure")
    fun secure(): Map<String, Any?> {
        val authentication = SecurityContextHolder.getContext().authentication
        return mapOf(
            "name" to "secure",
            "date" to LocalDateTime.now(),
            "principal" to authentication.principal
        )
    }

    @GetMapping("/anon")
    fun anon(principal: Principal): Map<String, Any?> {
        return mapOf(
            "name" to "anon",
            "date" to LocalDateTime.now(),
            "principal" to principal
        )
    }*/
}