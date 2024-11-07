package it.polito.customerrelationshipmanagement.dtos

data class CreateUpdateUserDTO(
    val userName: String?, // in the update must not be passed
    val email: String?, // not necessarily is provided
    val password: String?,
    val firstname: String,
    val lastName: String,
)