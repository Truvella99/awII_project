package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.category
import it.polito.customerrelationshipmanagement.entities.Contact


data class ContactDTO(
    val id: Long,
    val name: String?,
    val surname: String?,
    val ssncode: String?,
    val category: category?,
    val emails: List<EmailDTO>,
    val telephones: List<TelephoneDTO>,
    val addresses: List<AddressDTO>
)

fun Contact.toDTO(): ContactDTO =
    ContactDTO(
        this.id,
        this.name,
        this.surname,
        this.ssncode,
        this.category,
        this.emails.map { it.toDTO() },
        this.telephones.map { it.toDTO() },
        this.addresses.map { it.toDTO() }
    )

