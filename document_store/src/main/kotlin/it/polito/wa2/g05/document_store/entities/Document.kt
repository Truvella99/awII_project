package it.polito.wa2.g05.document_store.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToOne


@Entity
 class Document {
    @Id
    @GeneratedValue
    var id: Long = 0
    lateinit var binaryData: ByteArray
    @OneToOne(mappedBy = "document")
    lateinit var metaData: Metadata
}