package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.category
import it.polito.customerrelationshipmanagement.entities.jobOfferStatus
import jakarta.validation.constraints.Pattern


data class CreateSkillDTO(
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val skill: String,
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val professionalId: Long?,
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val jobOfferId: Long?,
)
