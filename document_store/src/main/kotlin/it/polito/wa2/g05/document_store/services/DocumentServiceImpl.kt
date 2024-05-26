package it.polito.wa2.g05.document_store.services

import it.polito.wa2.g05.document_store.controllers.HomeController
import it.polito.wa2.g05.document_store.dtos.*
import it.polito.wa2.g05.document_store.entities.Document
import it.polito.wa2.g05.document_store.entities.Metadata
import it.polito.wa2.g05.document_store.exceptions.*
import it.polito.wa2.g05.document_store.repositories.DocumentRepository
import it.polito.wa2.g05.document_store.repositories.MetadataRepository
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class DocumentServiceImpl(private val documentRepository: DocumentRepository,
                          private val metadataRepository: MetadataRepository): DocumentService{

    // logger to log messages in the APIs
    private val logger = LoggerFactory.getLogger(HomeController::class.java)
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun listAll(pageNumber: Int,limit:Int): List<MetadataDTO> {

        if (pageNumber >= 0 && limit > 0) {
            val p: Pageable = PageRequest.of(pageNumber, limit)
            return metadataRepository.findAll(p).content.map{it.toDTO()}
        } else {
            // handle error through MetadataExceptionHandler
            if (pageNumber < 0 && limit <= 0) {
                throw IllegalPageNumberLimitException("Invalid pageNumber and limit Parameter.")
            } else if (pageNumber < 0) {
                throw IllegalPageNumberLimitException("Invalid pageNumber Parameter.")
            } else {
                throw IllegalPageNumberLimitException("Invalid limit Parameter.")
            }
        }
    }

    override fun findById(metadataId: Long): MetadataDTO {
        if (metadataId < 0) {
            throw IllegalMetadataIdException("Invalid metadataId Parameter.")
        }
        try {
            return metadataRepository.findById(metadataId).map{ it.toDTO() }.get()
        } catch (e: RuntimeException) {
            throw DocumentNotFoundException("Document Metadata with MetadataId:$metadataId not found.")
        }
    }

    @Query
    override fun findByName(name: String): MetadataDTO? {
        try {
            val query = entityManager.createQuery(
                "SELECT m FROM Metadata m WHERE m.name = :name", Metadata::class.java
            )
            query.setParameter("name", name)
            return query.singleResult.toDTO()
        } catch (e: RuntimeException) {
            throw DocumentNotFoundException("Document Metadata with Name:$name not found.")
        }
    }

    override fun getBinaryById(metadataId: Long): Pair<MetadataDTO,DocumentDTO> {
        if (metadataId < 0) {
            throw IllegalMetadataIdException("Invalid metadataId Parameter.")
        }
        try {
            // get metadata from id
            val metadata = metadataRepository.findById(metadataId).get()
            // return the corresponding metadata document dto pair
            return Pair(metadata.toDTO(),metadata.document.toDTO())
        } catch (e: RuntimeException) {
            throw DocumentNotFoundException("Document Binary Data with MetadataId:$metadataId not found.")
        }
    }

    @Transactional
    override fun createDocument(data: DocumentMetadataDTO): MetadataDTO {
        try {
            // see if a file with the same name already exists
            this.findByName(data.name)
        } catch (e: RuntimeException) {
            // document with this name does not exist, proceed
            // create and insert the document
            val d = Document();
            d.binary_data = data.file.bytes;
            documentRepository.save(d).toDTO();
            // Log the changes made to the file at info level
            logger.info("Document ${data.name} uploaded.")
            // then create and insert the metadata
            val m = Metadata();
            m.document = d;
            m.name = data.name;
            m.size = data.file.size;
            m.content_type = data.contentType;
            m.creation_timestamp = data.creationTimestamp
            // return the metadata
            // Log the changes made to the metadata at info level
            logger.info("${data.name} metadata saved.")
            return metadataRepository.save(m).toDTO()
        }
        // if exists (query succeed and did not throw any exception) throw the exception
        throw DocumentAlreadyExistsException("Document Named ${data.name} Already Exists.")
    }

    @Transactional
    override fun updateDocument(metadataId: Long,data: DocumentMetadataDTO): MetadataDTO {
        if (metadataId < 0) {
            throw IllegalMetadataIdException("Invalid metadataId Parameter.")
        }
        try {
            // retrieve and update the metadata
            val existingMetadata = metadataRepository.findById(metadataId).get()
            existingMetadata.name = data.name;
            existingMetadata.size = data.file.size;
            existingMetadata.content_type = data.contentType;
            existingMetadata.creation_timestamp = data.creationTimestamp;
            val updatedMetadata = metadataRepository.save(existingMetadata).toDTO();
            // Log the changes made to the metadata at info level
            logger.info("${data.name} metadata updated.")
            // retrieve and update the document
            val existingDocument = existingMetadata.document;
            existingDocument.binary_data = data.file.bytes;
            documentRepository.save(existingDocument).toDTO();
            // Log the changes made to the file at info level
            logger.info("Document ${data.name} updated.")
            // return the updated metadata
            return updatedMetadata;
        } catch (e: RuntimeException) {
            throw DocumentNotFoundException("Document with MetadataId:$metadataId not found")
        }
    }

    @Transactional
    override fun deleteDocument(metadataId: Long) {
        if (metadataId < 0) {
            throw IllegalMetadataIdException("Invalid metadataId Parameter.")
        }
        try {
            // get the metadata entry
            val metadata = metadataRepository.findById(metadataId).get()
            // delete the corresponding document
            documentRepository.deleteById(metadata.document.id);
            // Log the changes made at info level
            logger.info("Document ${metadata.name} deleted.")
            // delete the metadata
            metadataRepository.deleteById(metadataId)
            // Log the changes made at info level
            logger.info("${metadata.name} metadata deleted.")
        } catch (e: RuntimeException) {
            throw DocumentNotFoundException("Document with MetadataId:$metadataId not found")
        }
    }
}
