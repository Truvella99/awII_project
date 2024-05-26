package it.polito.customerrelationshipmanagement.dtos

import jakarta.validation.constraints.Pattern


data class CreateUpdateTelephoneDTO(
    @field:Pattern(regexp = TELEPHONE)
    val telephone: String
)
