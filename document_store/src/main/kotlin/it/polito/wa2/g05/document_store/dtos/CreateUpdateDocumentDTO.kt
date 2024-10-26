package it.polito.wa2.g05.document_store.dtos

import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime

data class CreateUpdateDocumentDTO (
    val userId: String,
    val file: MultipartFile,
    val name: String,
    val contentType: String,
    val creationTimestamp: LocalDateTime
)