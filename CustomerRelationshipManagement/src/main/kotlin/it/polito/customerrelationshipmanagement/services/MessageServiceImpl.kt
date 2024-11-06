package it.polito.customerrelationshipmanagement.services

import it.polito.customerrelationshipmanagement.controllers.MessageController
import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.*
import it.polito.customerrelationshipmanagement.repositories.*
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import it.polito.customerrelationshipmanagement.exceptions.*
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import java.util.*

@Transactional
@Service
class MessageServiceImpl(
    private val messageRepository: MessageRepository,
    private val emailRepository: EmailRepository,
    private val telephoneRepository: TelephoneRepository,
    private val addressRepository: AddressRepository,
    private val historyRepository: HistoryRepository,
    private val contactService: ContactService
): MessageService{
    // logger to log messages in the APIs
    private val logger = LoggerFactory.getLogger(MessageController::class.java)


    // ----- Get a list of all messages -----
    override fun listAllMessages(
        pageNumber: Int?,
        limit: Int?,
        state: state?,
        sort: String?
    ): List<MessageDTO> {
        if (pageNumber != null && limit != null) {
            if (pageNumber >= 0 && limit > 0) {
                lateinit var p : PageRequest

                if (sort != null) {
                    if (sort.split("_")[1] != "id" && sort.split("_")[1] != "date")
                        throw IllegalSortParameterException("Invalid sort Parameter.")
                    else {
                        if (sort.split("_")[0] == "ASC") {
                            val s = Sort.by(Sort.Direction.ASC, sort.split("_")[1])
                            p = PageRequest.of(pageNumber, limit, s)
                        } else if (sort.split("_")[0] == "DESC") {
                            val s = Sort.by(Sort.Direction.DESC, sort.split("_")[1])
                            p = PageRequest.of(pageNumber, limit, s)
                        } else {
                            throw IllegalSortParameterException("Invalid sort Parameter.")
                        }
                    }
                } else
                    p = PageRequest.of(pageNumber, limit)

                if (state != null)
                    return messageRepository.findByCurrentState(state, p).map{ it.toDTO(false) }
                else
                    return messageRepository.findAll(p).content.map{ it.toDTO(false) }
            } else {
                // handle error
                if (pageNumber < 0 && limit <= 0) {
                    throw IllegalPageNumberLimitException("Invalid pageNumber and limit Parameter.")
                } else if (pageNumber < 0) {
                    throw IllegalPageNumberLimitException("Invalid pageNumber Parameter.")
                } else {
                    throw IllegalPageNumberLimitException("Invalid limit Parameter.")
                }
            }
        } else if (pageNumber == null && limit == null) {
            if (sort != null) {
                lateinit var s : Sort

                if (sort.split("_")[1] != "id" && sort.split("_")[1] != "date")
                    throw IllegalSortParameterException("Invalid sort Parameter.")
                else {
                    if (sort.split("_")[0] == "ASC") {
                        s = Sort.by(Sort.Direction.ASC, sort.split("_")[1])
                    } else if (sort.split("_")[0] == "DESC") {
                        s = Sort.by(Sort.Direction.DESC, sort.split("_")[1])
                    } else {
                        throw IllegalSortParameterException("Invalid sort Parameter.")
                    }
                }
                if (state != null)
                    return messageRepository.findByCurrentState(state, s).map{ it.toDTO(false) }
                else
                    return messageRepository.findAll(s).map{ it.toDTO(false) }

            } else {
                if (state != null)
                    return messageRepository.findByCurrentState(state).map{ it.toDTO(false) }
                else
                    return messageRepository.findAll().map{ it.toDTO(false) }
            }
        } else {
            throw IllegalPageNumberLimitException("PageNumber and limit must be both provided or both not provided.")
        }
    }


    // ----- Add a new message -----
    override fun addMessage(
        data: CreateMessageDTO
    ): MessageDTO {
        val message = Message()

        message.date = data.date
        message.channel = data.channel
        message.priority = data.priority
        message.subject = data.subject ?: ""
        message.body = data.body ?: ""
        message.currentState = state.received

        //No sender provided error
        if (data.email == null && data.telephone == null && data.address == null)
            throw SenderNotProvidedException("No sender provided for the message")
        //Wrong channel error
        if ( (data.channel == channel.email && data.email == null) || (data.channel == channel.phonecall && data.telephone == null) || (data.channel == channel.textmessage && data.address == null) )
            throw WrongChannelException("Wrong channel provided for the message")
        //Multiple senders error
        if ( (data.email != null && data.telephone != null) || (data.email != null && data.address != null) || (data.telephone != null && data.address != null) )
            throw MultipleSendersException("Multiple senders provided for the message")

        if (data.email != null) {
            val emails = emailRepository.findByEmail(data.email)
            // if no emails found or the ones found are either all marked as deleted or not linked to any contact, create a pending one
            if (emails.isEmpty() || emails.all { it.state == contactInfoState.deleted || it.contact == null }) {
                val pendingContact = CreateContactDTO(
                    null, null, null, null, data.email, null, null
                )
                contactService.createContact(pendingContact,isPending = true)
            }
            message.email = data.email
        }

        if (data.telephone != null) {
            val telephones = telephoneRepository.findByTelephone(data.telephone)
            // if no telephones found or the ones found are either all marked as deleted or not linked to any contact, create a pending one
            if (telephones.isEmpty() || telephones.all { it.state == contactInfoState.deleted || it.contact == null }) {
                val pendingContact = CreateContactDTO(
                    null, null, null, null, null, data.telephone, null
                )
                contactService.createContact(pendingContact,isPending = true)
            }
            message.telephone = data.telephone
        }

        if (data.address != null) {
            val addresses = addressRepository.findByAddress(data.address)
            // if no addresses found or the ones found are either all marked as deleted or not linked to any contact, create a pending one
            if (addresses.isEmpty() || addresses.all { it.state == contactInfoState.deleted || it.contact == null }) {
                val pendingContact = CreateContactDTO(
                    null, null, null, null, null, null, data.address
                )
                contactService.createContact(pendingContact,isPending = true)
            }
            message.address = data.address
        }

        val createdMessage = messageRepository.save(message).toDTO()
        logger.info("Message with messageId ${createdMessage.id} saved.")
        return createdMessage
    }


    // ----- Get a message by its ID -----
    override fun getMessageById(
        messageId: Long
    ): MessageDTO {
        if (messageId < 0)
            throw IllegalIdException("Invalid messageId Parameter.")

        try {
            return messageRepository.findById(messageId).map{ it.toDTO() }.get()
        } catch (e: RuntimeException) {
            throw MessageNotFoundException("Message with messageId:$messageId not found.")
        }
    }


    // ----- Update the state of a message -----
    override fun updateMessageState(
        messageId: Long,
        data: UpdateMessageStateDTO
    ): MessageDTO {
        if (messageId < 0)
            throw IllegalIdException("Invalid messageId Parameter.")

        val message = messageRepository.findById(messageId).orElseThrow {
            throw MessageNotFoundException("Message with messageId:$messageId not found.")
        }
        // Check if the target state is a valid transition from the current state
        when (message.currentState) {
            state.received -> if (data.targetState != state.read) {
                throw IllegalStateTransitionException("Invalid state transition")
            }
            state.read -> if (data.targetState !in listOf(state.discarded, state.failed, state.processing, state.done)) {
                throw IllegalStateTransitionException("Invalid state transition")
            }
            state.processing -> if (data.targetState !in listOf(state.done, state.failed)) {
                throw IllegalStateTransitionException("Invalid state transition")
            }
            else -> throw IllegalStateTransitionException("Invalid current state: no state transition allowed")
        }
        message.currentState = data.targetState

        // Add history
        val history = History()
        history.state = data.targetState
        history.date = Date()
        history.comment = data.comment ?: ""
        
        val savedHistory = historyRepository.save(history)
        logger.info("History with historyId ${savedHistory.id} saved.")
        message.addHistory(history)
        logger.info("State ${message.currentState} saved for message: $message")
        return messageRepository.save(message).toDTO()
    }


    // ----- Get the history of changes of a message -----
    override fun getChanges(
        messageId: Long
    ): List<HistoryDTO> {
        if (messageId < 0)
            throw IllegalIdException("Invalid messageId Parameter.")

        try {
            val message = messageRepository.findById(messageId).get()
            return historyRepository.findByMessage(message).map{ it.toDTO() }
        } catch (e: RuntimeException) {
            throw MessageNotFoundException("Message with messageId:$messageId not found.")
        }
    }


    // ----- Update the priority of a message -----
    override fun updateMessagePriority(
        messageId: Long,
        data: UpdateMessagePriorityDTO
    ): MessageDTO {
        if (messageId < 0)
            throw IllegalIdException("Invalid messageId Parameter.")

        try {
            val message = messageRepository.findById(messageId).get()
            message.priority = data.priority
            //Log and save the message
            logger.info("Priority ${message.priority} saved for message with messageId${message.id}.")
            return messageRepository.save(message).toDTO()
        } catch (e: RuntimeException) {
            throw MessageNotFoundException("Message with messageId:$messageId not found.")
        }
    }


    // ----- Delete a message -----
    override fun deleteMessage(
        messageId: Long
    ): MessageDTO {
        if (messageId < 0)
            throw IllegalIdException("Invalid messageId Parameter.")

        try {
            val message = messageRepository.findById(messageId).get()
            // Add last history for the message
            val history = History()
            history.state = state.discarded
            history.date = Date()
            history.comment = "Message Discarded."
            val savedHistory = historyRepository.save(history)
            logger.info("History with historyId ${savedHistory.id} saved.")
            message.addHistory(history)
            logger.info("State ${message.currentState} saved for message: $message")
            // set message as discarded
            message.currentState = state.discarded
            //Log and save the message
            logger.info("State ${message.currentState} saved for message with messageId${message.id}.")
            return messageRepository.save(message).toDTO()
        } catch (e: RuntimeException) {
            throw MessageNotFoundException("Message with messageId:$messageId not found.")
        }
    }
}