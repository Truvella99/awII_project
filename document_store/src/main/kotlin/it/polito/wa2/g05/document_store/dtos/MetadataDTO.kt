package it.polito.wa2.g05.document_store.dtos

import it.polito.wa2.g05.document_store.entities.Metadata
import java.time.LocalDateTime

class MetadataDTO (
    val keyId:String,
    val keyVersion: Long,
    val documentId: Long,
    val name: String,
    val size: Number,
    val contentType: String,
    val creationTimestamp: LocalDateTime
)

fun Metadata.toDTO(): MetadataDTO =
    MetadataDTO(this.key.id,this.key.version,this.document.id,this.name,this.size,this.contentType,this.creationTimestamp)