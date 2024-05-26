package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.Email
import it.polito.customerrelationshipmanagement.entities.contactInfoState


data class EmailDTO(
    val id: Long,
    val email: String,
    val state: contactInfoState
)

fun Email.toDTO(): EmailDTO =
    EmailDTO(
        this.id,
        this.email,
        this.state
    )

