package it.polito.wa2.g05.document_store.repositories

import it.polito.wa2.g05.document_store.entities.CompositeKey
import it.polito.wa2.g05.document_store.entities.Metadata
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MetadataRepository: JpaRepository<Metadata, CompositeKey> {

    @Query("SELECT m from Metadata m where m.key.id = :userId")
    fun findMetadataByUserId(@Param("userId") userId: String): List<Metadata>

//    fun findByName(name: String): List<Metadata>
}


