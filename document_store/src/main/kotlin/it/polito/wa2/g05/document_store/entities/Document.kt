package it.polito.wa2.g05.document_store.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id


@Entity
 class Document {
    @Id
    @GeneratedValue
    var id: Long = 0
    lateinit var binary_data: ByteArray
}