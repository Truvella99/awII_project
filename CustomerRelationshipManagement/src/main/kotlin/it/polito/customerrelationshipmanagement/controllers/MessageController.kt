package it.polito.customerrelationshipmanagement.controllers

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.priority
import it.polito.customerrelationshipmanagement.entities.state
import it.polito.customerrelationshipmanagement.services.MessageService
import jakarta.validation.Valid
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class MessageController(private val messageService: MessageService){
    /**
     * GET /API/messages/
     *
     * list all registered messages in the DB. Allow
     * for pagination, limiting results, sorting, and filtering by state,
     * using request parameters
     *
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/messages/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun listAllMessages(@RequestParam("pageNumber")pageNumber: Int?,
                        @RequestParam("limit")limit: Int?,
                        @RequestParam("state")state: state?,
                        @RequestParam("sort")sort: String?
    ): List<MessageDTO> {
        return messageService.listAllMessages(pageNumber, limit, state, sort)
    }


    /**
     * POST /API/messages/
     *
     * create a message, providing required information (sender, channel, subject, body)
     *
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/API/messages/")
//    @PreAuthorize("isAuthenticated()")
    fun addMessage(@RequestBody @Valid data: CreateMessageDTO): MessageDTO {
        return messageService.addMessage(data)
    }

    /**
     * GET /API/messages/{messageId}
     *
     *  retrieve a specific message
     *
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/messages/{messageId}")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun getMessageById(@PathVariable("messageId") messageId: Long): MessageDTO {
        return messageService.getMessageById(messageId)
    }

    /**
     * POST /API/messages/{messageId}
     *
     * change the state of a specific message. This endpoint must receive the target state and possibly a
     * comment to enrich the history of actions on the message. Manage the
     * case where the new state is not an allowed one for the message
     *
     *
     */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/API/messages/{messageId}")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun updateMessageState(@PathVariable("messageId") messageId: Long,
                           @RequestBody @Valid data: UpdateMessageStateDTO): MessageDTO {
        return messageService.updateMessageState(messageId, data)
    }

    /**
     * GET /API/messages/{messageId}/history
     *
     * retrieve the list of state changes, with their comments, for a specific message
     *
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/messages/{messageId}/history")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun getChanges(@PathVariable("messageId") messageId: Long): List<HistoryDTO> {
        return messageService.getChanges(messageId)
    }

    /**
     * PUT /API/messages/{messageId}/priority
     *
     * modify the priority value of a message
     *
     *
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/API/messages/{messageId}/priority")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun updateMessagePriority(@PathVariable("messageId") messageId: Long,
                              @RequestBody @Valid data: UpdateMessagePriorityDTO
    ): MessageDTO {
        return messageService.updateMessagePriority(messageId, data)
    }

    /**
     * PUT /API/messages/{messageId}/discard
     *
     * modify the state of a message to DISCARDED
     *
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/API/messages/{messageId}/discard")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun deleteMessage(@PathVariable("messageId") messageId: Long): MessageDTO {
        return messageService.deleteMessage(messageId)
    }
}