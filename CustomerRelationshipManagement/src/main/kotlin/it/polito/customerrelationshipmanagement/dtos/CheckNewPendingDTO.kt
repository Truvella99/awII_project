package it.polito.customerrelationshipmanagement.dtos

import jakarta.validation.constraints.Pattern

data class CheckNewPendingDTO(
    @field:Pattern(regexp = EMAIL)
    val email: String?,
    @field:Pattern(regexp = TELEPHONE)
    val telephone: String?,
    @field:Pattern(regexp = ADDRESS)
    val address: String?
)