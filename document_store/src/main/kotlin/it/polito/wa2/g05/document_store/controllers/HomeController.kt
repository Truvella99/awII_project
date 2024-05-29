package it.polito.wa2.g05.document_store.controllers

import it.polito.wa2.g05.document_store.dtos.*
import it.polito.wa2.g05.document_store.services.DocumentService
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class HomeController(private val documentService: DocumentService){

    /**
     * GET /API/documents/
     *
     * List all registered documents in the DB.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/documents/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun getAllDocuments(@RequestParam("pageNumber")pageNumber: Int,
                        @RequestParam("limit")limit: Int) : List<MetadataDTO> {
        return documentService.listAll(pageNumber, limit)
    }

    /**
     * GET /API/documents/{metadataId}/
     *
     * Details of document {documentId} or fail if it does not exist.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/documents/{metadataId}/")
    @PreAuthorize("isAuthenticated()")
    fun getDocument(@PathVariable("metadataId")metadataId: Long): MetadataDTO {
        return documentService.findById(metadataId)
    }

    /**
     * GET /API/documents/{metadataId}/data/
     *
     * Byte content of document {metadataId} or fail if it does not exist.
     */
    @GetMapping("/API/documents/{metadataId}/data/")
    @PreAuthorize("isAuthenticated()")
    fun getByteDocument(@PathVariable("metadataId")metadataId: Long): ResponseEntity<ByteArrayResource> {
        val (metadata,document) = documentService.getBinaryById(metadataId)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${metadata.name}\"")
            .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
            .body(ByteArrayResource(document.binary_data));
    }

    /**
     * POST /API/documents/
     *
     * Convert the request param into a DocumentMetadataDTO and store it in the DB, provided that a file with that name doesn't already exist.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/API/documents/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun createDocument(@ModelAttribute d: DocumentMetadataDTO): MetadataDTO {
        return documentService.createDocument(d)
    }

    /**
     * PUT /API/documents/{metadataId}/
     *
     * Convert the request param into a DocumentMetadataDTO and replace the corresponding entry in the DB.
     * Fail if the document does not exist.
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/API/documents/{metadataId}/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun updateDocument(@PathVariable("metadataId") metadataId: Long,@ModelAttribute d: DocumentMetadataDTO): MetadataDTO {
        return documentService.updateDocument(metadataId,d)
    }

    /**
     * DELETE /API/documents/{metadataId}/
     *
     * Remove document {documentId} or fail if it does not exist.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/API/documents/{metadataId}/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun deleteDocument(@PathVariable("metadataId")metadataId: Long) {
        return documentService.deleteDocument(metadataId)
    }
}