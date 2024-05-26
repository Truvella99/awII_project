package it.polito.customerrelationshipmanagement.dtos

import jakarta.validation.constraints.Pattern


data class CreateUpdateAddressDTO(
    @field:Pattern(regexp = ADDRESS)
    val address: String
)
