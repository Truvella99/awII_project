package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.category
import it.polito.customerrelationshipmanagement.entities.Contact
import it.polito.customerrelationshipmanagement.entities.Customer
import jakarta.validation.constraints.Pattern


data class CustomerDTO(
    val id: String,
    val name: String?,
    val surname: String?,
    val ssncode: String?,
    val category: category?,
    val emails: List<EmailDTO>,
    val telephones: List<TelephoneDTO>,
    val addresses: List<AddressDTO>,
    val notes: List<NoteDTO>,
    val jobOffers: List<JobOfferDTO>
)

fun Customer.toDTO(): CustomerDTO =
    CustomerDTO(
        this.id,
        this.contact.name,
        this.contact.surname,
        this.contact.ssncode,
        this.contact.category,
        this.contact.emails.map { it.toDTO() },
        this.contact.telephones.map { it.toDTO() },
        this.contact.addresses.map { it.toDTO() },
        this.notes.map { it.toDTO() },
        this.jobOffers.map { it.toDTO() }
    )

