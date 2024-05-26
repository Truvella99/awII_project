package it.polito.communicationmanager.dtos

import jakarta.validation.constraints.Pattern

data class CreateEmailDTO (
    @field:Pattern(regexp = EMAIL)
    val from: String,
    @field:Pattern(regexp = EMAIL)
    val to: String,
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val subject: String,
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val body: String,
)