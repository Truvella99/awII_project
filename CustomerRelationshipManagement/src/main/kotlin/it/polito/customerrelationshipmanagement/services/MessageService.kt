package it.polito.customerrelationshipmanagement.services


import it.polito.customerrelationshipmanagement.dtos.*
import org.springframework.data.domain.Sort
import it.polito.customerrelationshipmanagement.entities.priority
import it.polito.customerrelationshipmanagement.entities.state

interface MessageService {
    fun listAllMessages(pageNumber: Int?,limit:Int?,state: state?,sort:String?):List<MessageDTO>
    fun addMessage(data: CreateMessageDTO):MessageDTO
    fun getMessageById(messageId: Long):MessageDTO
    fun updateMessageState(messageId: Long, data: UpdateMessageStateDTO):MessageDTO
    fun getChanges(messageId: Long):List<HistoryDTO>
    fun updateMessagePriority(messageId: Long, data: UpdateMessagePriorityDTO):MessageDTO
    fun deleteMessage(messageId: Long):MessageDTO
}