package it.polito.communicationmanager.services

import it.polito.communicationmanager.dtos.CreateEmailDTO
import it.polito.communicationmanager.dtos.EmailDTO

interface CommunicationService {
    fun sendEmail(data: CreateEmailDTO): EmailDTO
}