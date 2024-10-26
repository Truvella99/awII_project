package it.polito.wa2.g05.document_store.dtos

import it.polito.wa2.g05.document_store.entities.Document
import it.polito.wa2.g05.document_store.entities.Metadata

class DocumentDTO (
    val id: Long ,
    val binaryData: ByteArray,
    val metaData: Metadata
)

fun Document.toDTO(): DocumentDTO =
    DocumentDTO(this.id,this.binaryData, this.metaData)