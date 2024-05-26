package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.category
import jakarta.validation.constraints.Pattern


data class UpdateSSNCodeDTO(
    @field:Pattern(regexp = SSN_CODE)
    val ssncode: String
)

