package it.polito.communicationmanager.controllers

import it.polito.communicationmanager.dtos.CreateEmailDTO
import it.polito.communicationmanager.dtos.EmailDTO
import it.polito.communicationmanager.services.CommunicationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@RestController
class CommunicationController(private val communicationService: CommunicationService) {

    /**
     * POST /API/emails/
     *
     * send an email
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/API/emails/")
    fun addContactEmail(@RequestBody @Valid data: CreateEmailDTO): EmailDTO {
        return communicationService.sendEmail(data)
    }

}