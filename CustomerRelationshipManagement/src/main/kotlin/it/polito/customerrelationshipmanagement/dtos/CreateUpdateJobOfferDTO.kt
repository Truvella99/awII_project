package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.jobOfferStatus
import jakarta.validation.constraints.Pattern


data class CreateUpdateJobOfferDTO(
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val name: String,
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val description: String,
    val currentState: jobOfferStatus? = null,
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val currentStateNote: String?,
    val duration: Number,
    val profitMargin: Number,
    val customerId: Long? = null,//null when create a customer with a JobOffer - not null when create a jobOffer
    val skills: List<CreateSkillDTO>,
    val skillsToDelete: List<Long>? = null
)
