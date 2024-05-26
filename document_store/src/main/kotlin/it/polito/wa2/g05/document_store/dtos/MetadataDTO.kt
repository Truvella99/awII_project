package it.polito.wa2.g05.document_store.dtos

import it.polito.wa2.g05.document_store.entities.Document
import it.polito.wa2.g05.document_store.entities.Metadata
import java.time.LocalDateTime

class MetadataDTO (
    val id:Long,
    val name: String,
    val size: Number,
    val content_type: String,
    val creation_timestamp: LocalDateTime
)

fun Metadata.toDTO(): MetadataDTO =
    MetadataDTO(this.id,this.name,this.size,this.content_type,this.creation_timestamp)