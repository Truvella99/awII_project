package it.polito.wa2.g05.document_store.entities

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Metadata {
    @Id
    @GeneratedValue
    var id: Long = 0
    @JoinColumn(name = "document_id")
    @OneToOne(fetch = FetchType.LAZY)
    lateinit var document: Document
    lateinit var name: String
    lateinit var size: Number
    lateinit var content_type: String
    lateinit var creation_timestamp: LocalDateTime
}