package it.polito.communicationmanager.controllers

import it.polito.communicationmanager.dtos.CreateEmailDTO
import it.polito.communicationmanager.dtos.EmailDTO
import it.polito.communicationmanager.services.CommunicationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt


@RestController
class CommunicationController(private val communicationService: CommunicationService) {

    /**
     * POST /API/emails/
     *
     * send an email
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/API/emails/")
    @PreAuthorize("isAuthenticated()")
    fun addContactEmail(@RequestBody @Valid data: CreateEmailDTO): EmailDTO {
        return communicationService.sendEmail(data)
    }

    @GetMapping("/data")
    fun getRoles(authentication: Authentication): Map<String, Any> {
        val principal = authentication.principal as Jwt//prima era JWT TODO
        val realmAccess = principal.getClaim<Map<String, List<String>>>("realm_access")
        val roles = realmAccess["roles"] ?: emptyList()
        return mapOf("roles" to roles)
    }
}