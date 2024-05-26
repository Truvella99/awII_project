package it.polito.customerrelationshipmanagement.dtos

import jakarta.validation.constraints.Pattern


data class CreateUpdateEmailDTO(
    @field:Pattern(regexp = EMAIL)
    val email: String
)
