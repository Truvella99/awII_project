package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.category
import jakarta.validation.constraints.Pattern


data class UpdateNameDTO(
    val name: String
)

