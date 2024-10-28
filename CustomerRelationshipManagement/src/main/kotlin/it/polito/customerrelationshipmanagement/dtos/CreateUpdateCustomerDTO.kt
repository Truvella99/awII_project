package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.category
import jakarta.validation.constraints.Pattern


data class CreateUpdateCustomerDTO(
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val username: String?,
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val name: String?,
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val surname: String?,
    @field:Pattern(regexp = SSN_CODE)
    val ssncode: String?,
    val category: category?,
    @field:Pattern(regexp = EMAIL)
    val email: String?,
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val password: String?,
    @field:Pattern(regexp = TELEPHONE)
    val telephone: String?,
    @field:Pattern(regexp = ADDRESS)
    val address: String?,
    val notes: List<String>?,
    val notesToDelete: List<Long>? = null,
    val jobOffers: List<Pair<Long?,CreateUpdateJobOfferDTO>>? = null,
    val emailsToDelete: List<Long>? = null,
    val telephonesToDelete: List<Long>? = null,
    val addressesToDelete: List<Long>? = null
)
