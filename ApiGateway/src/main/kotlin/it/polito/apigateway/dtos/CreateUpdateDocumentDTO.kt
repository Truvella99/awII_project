package it.polito.apigateway.dtos

import java.time.LocalDateTime

data class CreateUpdateDocumentDTO (
    val userId: String,
    val name: String,
    val contentType: String,
    val creationTimestamp: LocalDateTime
)

