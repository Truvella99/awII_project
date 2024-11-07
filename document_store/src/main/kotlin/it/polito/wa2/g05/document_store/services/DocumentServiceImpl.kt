package it.polito.wa2.g05.document_store.services

import it.polito.wa2.g05.document_store.KeycloakConfig
import it.polito.wa2.g05.document_store.controllers.HomeController
import it.polito.wa2.g05.document_store.dtos.*
import it.polito.wa2.g05.document_store.entities.CompositeKey
import it.polito.wa2.g05.document_store.entities.Document
import it.polito.wa2.g05.document_store.entities.Metadata
import it.polito.wa2.g05.document_store.exceptions.*
import it.polito.wa2.g05.document_store.getUserKeycloakIdRole
import it.polito.wa2.g05.document_store.repositories.DocumentRepository
import it.polito.wa2.g05.document_store.repositories.MetadataRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.core.Authentication
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

    override fun findById(userId:String, authentication: Authentication): List<MetadataDTO> {
        // check if valid keycloak id
        KeycloakConfig.checkExistingUserById(userId)
        val (keycloakId,keycloakRole) = getUserKeycloakIdRole(authentication)
        if (keycloakRole == "customer" || keycloakRole == "professional" && userId != keycloakId) {
            throw IllegalIdException("No permission to read details of Document of the User with Id $userId")
        }
        try {
            return metadataRepository.findMetadataByUserId(userId).map{ it.toDTO() }
        } catch (e: RuntimeException) {
            throw DocumentNotFoundException("Document Metadata related to User with userId:$userId not found.")
        }
    }

    override fun getBinaryById(documentId: Long, authentication: Authentication): DocumentDTO {
        if (documentId < 0) {
            throw IllegalIdException("Invalid documentId Parameter.")
        }
        val (keycloakId,keycloakRole) = getUserKeycloakIdRole(authentication)

        // get document from id
        val document = documentRepository.findById(documentId).orElseThrow {
            DocumentNotFoundException("Document Binary Data of Document with DocumentId:$documentId not found.")
        }
        // can download only your document if customer or professional
        if (keycloakRole == "customer" || keycloakRole == "professional" && document.metaData.key.id != keycloakId) {
            throw IllegalIdException("No permission to read details of Document of the User with Id ${document.metaData.key.id}")
        }
        // return the corresponding metadata document dto pair
        return document.toDTO()
    }

    override fun createDocument(data: CreateUpdateDocumentDTO):MetadataDTO {
        // check if valid keycloak id
        KeycloakConfig.checkExistingUserById(data.userId)
        if (metadataRepository.findMetadataByUserIdAndName(data.userId,data.name).isNotEmpty()) {
            // query found documents related to this user and with the same filename, so throw the exception
            throw DocumentAlreadyExistsException("Documents Related to User with userId:${data.userId} and fileName:${data.name} Already Exists.")
        }
        // documents related to this user and with the same filename do not exist, proceed
        // create and insert the document
        val d = Document();
        d.binaryData = data.file.bytes;
        documentRepository.save(d)
        // Log the changes made to the file at info level
        logger.info("Document ${data.name} uploaded.")
        // then create and insert the metadata
        val m = Metadata();
        m.key = CompositeKey(data.userId,1,data.name)
        m.document = d;
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
        val documents = metadataRepository.findMetadataByUserIdAndName(data.userId,data.name)
        if (documents.isEmpty()) {
            // query found no documents related to this user and with the same filename, so cannot update must create first, throw the exception
            throw DocumentNotFoundException("No Documents Related to User with userId:${data.userId} and fileName:${data.name} Found.")
        }
        // find the current maxVersionNumber
        val maxVersionNumber = documents.maxByOrNull { it.key.version }!!.key.version
        // now repeat the creation process for this new document version
        val d = Document();
        d.binaryData = data.file.bytes;
        documentRepository.save(d)
        // Log the changes made to the file at info level
        logger.info("Document ${data.name} uploaded.")
        // then create and insert the metadata
        val m = Metadata();
        m.key = CompositeKey(data.userId,maxVersionNumber + 1,data.name)
        m.document = d;
        m.size = data.file.size;
        m.contentType = data.contentType;
        m.creationTimestamp = data.creationTimestamp
        // return the metadata
        // Log the changes made to the metadata at info level
        logger.info("${data.name} metadata saved.")
        return metadataRepository.save(m).toDTO()
    }

    override fun deleteDocument(userId: String, metadataVersion: Long, fileName: String) {
        // check if valid keycloak id
        KeycloakConfig.checkExistingUserById(userId)
        if (metadataVersion < 0) {
            throw IllegalIdException("Invalid version Parameter.")
        }
        // get the metadata entry
        val metadata = metadataRepository.findById(CompositeKey(userId, metadataVersion,fileName)).orElseThrow {
            throw DocumentNotFoundException("Document related to User with userId:$userId and version:$metadataVersion not found")
        }
        // delete the metadata
        metadataRepository.deleteById(metadata.key)
        // Log the changes made at info level
        logger.info("${metadata.key.fileName} metadata deleted.")
        // delete the corresponding document
        documentRepository.deleteById(metadata.document.id)
        // Log the changes made at info level
        logger.info("Document ${metadata.key.fileName} deleted.")
    }
}
