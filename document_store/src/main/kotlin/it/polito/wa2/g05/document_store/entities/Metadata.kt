package it.polito.wa2.g05.document_store.entities

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime

@Entity
class Metadata {
    @EmbeddedId
    lateinit var key: CompositeKey;
    @JoinColumn(name = "document_id")
    @OneToOne(fetch = FetchType.LAZY)
    lateinit var document: Document
    lateinit var size: Number
    lateinit var contentType: String
    lateinit var creationTimestamp: LocalDateTime
}

@Embeddable
class CompositeKey: Serializable {
    @Column(name = "Id", nullable = false)
    var id: String
    @Column(name = "version", nullable = false)
    var version: Long = 1
    @Column(name = "filename", nullable = false)
    var fileName: String

    constructor(id: String, version: Long, fileName: String) {
        this.id = id
        this.version = version
        this.fileName = fileName
    }
}