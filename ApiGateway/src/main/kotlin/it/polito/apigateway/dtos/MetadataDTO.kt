package it.polito.apigateway.dtos
import java.time.LocalDateTime

class MetadataDTO (
    val keyId:String,
    val keyVersion: Long,
    val fileName: String,
    val documentId: Long,
    val size: Number,
    val contentType: String,
    val creationTimestamp: LocalDateTime
)