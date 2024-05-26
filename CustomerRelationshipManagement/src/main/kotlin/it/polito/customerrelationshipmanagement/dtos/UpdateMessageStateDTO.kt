package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.state
import jakarta.validation.constraints.Pattern

data class UpdateMessageStateDTO(
    val targetState: state,
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val comment: String?
)