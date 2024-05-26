package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.channel
import it.polito.customerrelationshipmanagement.entities.priority
import jakarta.validation.constraints.Pattern
import java.util.*


data class CreateMessageDTO(
    val channel: channel,
    val priority: priority,
    val date: Date = Date(),
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val subject: String?,
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val body: String?,
    @field:Pattern(regexp = EMAIL)
    val email: String?,
    @field:Pattern(regexp = TELEPHONE)
    val telephone: String?,
    @field:Pattern(regexp = ADDRESS)
    val address: String?
)
