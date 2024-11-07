package it.polito.wa2.g05.document_store.services

import it.polito.wa2.g05.document_store.dtos.DocumentDTO
import it.polito.wa2.g05.document_store.dtos.CreateUpdateDocumentDTO
import it.polito.wa2.g05.document_store.dtos.MetadataDTO
import org.springframework.security.core.Authentication

interface DocumentService {
    //get All
    fun listAll(pageNumber: Int,limit:Int): List<MetadataDTO>
    //get Metadata by Id (given metadataId (userId) all document versions)
    fun findById(userId:String, authentication: Authentication): List<MetadataDTO>
    //get binary of the document by id
    fun getBinaryById(documentId: Long, authentication: Authentication): DocumentDTO
    //create document and metadata given the userId to which associate the document
    fun createDocument(data: CreateUpdateDocumentDTO, authentication: Authentication):MetadataDTO
    //update document and metadata (create another entry with incremented counter)
    fun updateDocument(data: CreateUpdateDocumentDTO, authentication: Authentication):MetadataDTO
    //delete document and metadata (one specific version of the document)
    fun deleteDocument(userId: String, metadataVersion: Long)
    // get Metadata by FileName (most recent)
    //fun findByName(name: String): MetadataDTO?
}