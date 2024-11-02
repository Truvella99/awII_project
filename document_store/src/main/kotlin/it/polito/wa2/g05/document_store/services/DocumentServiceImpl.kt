package it.polito.wa2.g05.document_store.services

import it.polito.wa2.g05.document_store.KeycloakConfig
import it.polito.wa2.g05.document_store.controllers.HomeController
import it.polito.wa2.g05.document_store.dtos.*
import it.polito.wa2.g05.document_store.entities.CompositeKey
import it.polito.wa2.g05.document_store.entities.Document
import it.polito.wa2.g05.document_store.entities.Metadata
import it.polito.wa2.g05.document_store.exceptions.*
import it.polito.wa2.g05.document_store.repositories.DocumentRepository
import it.polito.wa2.g05.document_store.repositories.MetadataRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
@Transactional
class DocumentServiceImpl(private val documentRepository: DocumentRepository,
                          private val metadataRepository: MetadataRepository): DocumentService{

    // logger to log messages in the APIs
    private val logger = LoggerFactory.getLogger(HomeController::class.java)

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

    override fun findById(userId:String): List<MetadataDTO> {
        // check if valid keycloak id
        KeycloakConfig.checkExistingUserById(userId)
        try {
            return metadataRepository.findMetadataByUserId(userId).map{ it.toDTO() }
        } catch (e: RuntimeException) {
            throw DocumentNotFoundException("Document Metadata related to User with userId:$userId not found.")
        }
    }

    override fun getBinaryById(documentId: Long): DocumentDTO {
        if (documentId < 0) {
            throw IllegalIdException("Invalid documentId Parameter.")
        }
        try {
            // get document from id
            val document = documentRepository.findById(documentId).get()
            // return the corresponding metadata document dto pair
            return document.toDTO()
        } catch (e: RuntimeException) {
            throw DocumentNotFoundException("Document Binary Data of Document with DocumentId:$documentId not found.")
        }
    }

    override fun createDocument(data: CreateUpdateDocumentDTO):MetadataDTO {
        // check if valid keycloak id
        KeycloakConfig.checkExistingUserById(data.userId)
        if (findById(data.userId).isNotEmpty()) {
            // query found documents related to this user, so throw the exception
            throw DocumentAlreadyExistsException("Documents Related to User with userId:${data.userId} Already Exists.")
        }
        // documents related to this user do not exist, proceed
        // create and insert the document
        val d = Document();
        d.binaryData = data.file.bytes;
        documentRepository.save(d)
        // Log the changes made to the file at info level
        logger.info("Document ${data.name} uploaded.")
        // then create and insert the metadata
        val m = Metadata();
        m.key = CompositeKey(data.userId,1)
        m.document = d;
        m.name = data.name;
        m.size = data.file.size;
        m.contentType = data.contentType;
        m.creationTimestamp = data.creationTimestamp
        // return the metadata
        // Log the changes made to the metadata at info level
        logger.info("${data.name} metadata saved.")
        return metadataRepository.save(m).toDTO()
    }

    override fun updateDocument(data: CreateUpdateDocumentDTO):MetadataDTO {
        // check if valid keycloak id
        KeycloakConfig.checkExistingUserById(data.userId)
        val documents = findById(data.userId);
        if (documents.isEmpty()) {
            // query found no documents related to this user, so cannot update must create first, throw the exception
            throw DocumentNotFoundException("No Documents Related to User with userId${data.userId} Found.")
        }
        // find the current maxVersionNumber
        val maxVersionNumber = documents.maxByOrNull { it.keyVersion }!!.keyVersion
        // now repeat the creation process for this new document version
        val d = Document();
        d.binaryData = data.file.bytes;
        documentRepository.save(d)
        // Log the changes made to the file at info level
        logger.info("Document ${data.name} uploaded.")
        // then create and insert the metadata
        val m = Metadata();
        m.key = CompositeKey(data.userId,maxVersionNumber + 1)
        m.document = d;
        m.name = data.name;
        m.size = data.file.size;
        m.contentType = data.contentType;
        m.creationTimestamp = data.creationTimestamp
        // return the metadata
        // Log the changes made to the metadata at info level
        logger.info("${data.name} metadata saved.")
        return metadataRepository.save(m).toDTO()
    }

    override fun deleteDocument(userId: String, metadataVersion: Long) {
        // check if valid keycloak id
        KeycloakConfig.checkExistingUserById(userId)
        if (metadataVersion < 0) {
            throw IllegalIdException("Invalid version Parameter.")
        }
        // get the metadata entry
        val metadata = metadataRepository.findById(CompositeKey(userId, metadataVersion)).orElseThrow {
            throw DocumentNotFoundException("Document related to User with userId:$userId and version:$metadataVersion not found")
        }
        // delete the metadata
        metadataRepository.deleteById(metadata.key)
        // Log the changes made at info level
        logger.info("${metadata.name} metadata deleted.")
        // delete the corresponding document
        documentRepository.deleteById(metadata.document.id)
        // Log the changes made at info level
        logger.info("Document ${metadata.name} deleted.")
    }
}
