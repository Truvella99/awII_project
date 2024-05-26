package it.polito.wa2.g05.document_store.repositories

import it.polito.wa2.g05.document_store.entities.Document
import it.polito.wa2.g05.document_store.entities.Metadata
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MetadataRepository: JpaRepository<Metadata, Long> {
}


