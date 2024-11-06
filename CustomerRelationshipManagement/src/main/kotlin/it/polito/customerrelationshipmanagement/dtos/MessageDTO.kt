package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.*
import java.util.*


data class MessageDTO(
    val id: Long,
    val date: Date,
    val channel: channel,
    val priority: priority,
    val currentState: state,
    val subject: String?,
    val body: String?,
    val email: String?,
    val telephone: String?,
    val address: String?
)

fun Message.toDTO(includeBody: Boolean = true): MessageDTO =
    MessageDTO(
        this.id,
        this.date,
        this.channel,
        this.priority,
        this.currentState,
        this.subject,
        if (includeBody) this.body else null,
        this.email,
        this.telephone,
        this.address
    )

