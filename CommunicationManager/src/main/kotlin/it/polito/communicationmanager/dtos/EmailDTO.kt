package it.polito.communicationmanager.dtos

import java.util.Date

data class EmailDTO(
    val id: String,
    val from: String,
    val to: String,
    val subject: String,
    val body: String
)
