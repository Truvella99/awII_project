package it.polito.customerrelationshipmanagement.dtos

data class UserDTO(
    val userName: String,
    val email: String,
    val password: String,
    val firstname: String,
    val lastName: String,
)