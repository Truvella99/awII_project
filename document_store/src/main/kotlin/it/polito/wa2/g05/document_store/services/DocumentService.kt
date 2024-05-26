package it.polito.wa2.g05.document_store.services

import it.polito.wa2.g05.document_store.dtos.DocumentDTO
import it.polito.wa2.g05.document_store.dtos.DocumentMetadataDTO
import it.polito.wa2.g05.document_store.dtos.MetadataDTO
import java.time.LocalDateTime

interface DocumentService {
    //get All
    fun listAll(pageNumber: Int,limit:Int): List<MetadataDTO>
    //get Metadata by Id
    fun findById(metadataId:Long): MetadataDTO
    // get Metadata by FileName
    fun findByName(name: String): MetadataDTO?
    //get binary of the document by metadataID
    fun getBinaryById(metadataId:Long): Pair<MetadataDTO,DocumentDTO>
    //create document and metadata
    fun createDocument(data: DocumentMetadataDTO):MetadataDTO
    //update document and metadata
    fun updateDocument(metadataId: Long,data: DocumentMetadataDTO):MetadataDTO
    //delete document and metadata
    fun deleteDocument(metadataId: Long)
}