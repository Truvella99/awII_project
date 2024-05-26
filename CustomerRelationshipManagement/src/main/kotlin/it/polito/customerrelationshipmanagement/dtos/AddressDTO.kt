package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.Address
import it.polito.customerrelationshipmanagement.entities.contactInfoState


data class AddressDTO(
    val id: Long,
    val address: String,
    val state: contactInfoState
)

fun Address.toDTO(): AddressDTO =
    AddressDTO(
        this.id,
        this.address,
        this.state
    )

