package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.Telephone
import it.polito.customerrelationshipmanagement.entities.contactInfoState


data class TelephoneDTO(
    val id: Long,
    val telephone: String,
    val state: contactInfoState
)

fun Telephone.toDTO(): TelephoneDTO =
    TelephoneDTO(
        this.id,
        this.telephone,
        this.state
    )

