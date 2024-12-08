package it.polito.customerrelationshipmanagement.consumers

import it.polito.customerrelationshipmanagement.dtos.CreateMessageDTO
import it.polito.customerrelationshipmanagement.services.MessageService
import org.springframework.stereotype.Service

@Service
class CmConsumer(private val messageService: MessageService) {

    fun saveMessage(data: CreateMessageDTO) {
        println("Saving message $data")
        messageService.addMessage(data)
    }

}
