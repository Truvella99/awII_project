package it.polito.wa2.g05.document_store.controllers

import it.polito.wa2.g05.document_store.dtos.*
import it.polito.wa2.g05.document_store.services.DocumentService
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class HomeController(private val documentService: DocumentService){

    @GetMapping("/data")
    fun getRoles(authentication: Authentication): Map<String, Any> {
        val principal = authentication.principal as Jwt//prima era JWT TODO
        val realmAccess = principal.getClaim<Map<String, List<String>>>("realm_access")
        val roles = realmAccess["roles"] ?: emptyList()
        return mapOf("roles" to roles)
    }

    /**
     * GET /API/documents/
     *
     * List all registered documents in the DB.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/documents/")
    @PreAuthorize("isAuthenticated() && (hasRole('manager'))")
    fun getAllDocuments(@RequestParam("pageNumber")pageNumber: Int,
                        @RequestParam("limit")limit: Int) : List<MetadataDTO> {
        return documentService.listAll(pageNumber, limit)
    }

    /**
     * GET /API/documents/{userId}/
     *
     * Details of documents related to User with {userId} or fail if it does not exist.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/documents/{userId}/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun getDocument(@PathVariable("userId")userId: String): List<MetadataDTO> {
        return documentService.findById(userId)
    }

    /**
     * GET /API/documents/{documentId}/data/
     *
     * Byte content of document {documentId} or fail if it does not exist.
     */
    @GetMapping("/API/documents/{documentId}/data/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun getByteDocument(@PathVariable("documentId")documentId: Long): ResponseEntity<ByteArrayResource> {
        val document = documentService.getBinaryById(documentId)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${document.metaData.name}\"")
            .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
            .body(ByteArrayResource(document.binaryData));
    }

    /**
     * POST /API/documents/
     *
     * Convert the request param into a DocumentMetadataDTO and store it in the DB, provided that a document related to that user doesn't already exist.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/API/documents/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun createDocument(@ModelAttribute d: CreateUpdateDocumentDTO): MetadataDTO {
        return documentService.createDocument(d)
    }

    /**
     * PUT /API/documents/
     *
     * Convert the request param into a DocumentMetadataDTO and store in the DB.
     * Fail if not even one document related to the user exist.
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/API/documents/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun updateDocument(@ModelAttribute d: CreateUpdateDocumentDTO): MetadataDTO {
        return documentService.updateDocument(d)
    }

    /**
     * DELETE /API/documents/{userId}/{version}/
     *
     * Remove document related to userId and version or fail if it does not exist.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/API/documents/{userId}/{version}/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun deleteDocument(@PathVariable("userId")userId: String,@PathVariable("version")version: Long) {
        return documentService.deleteDocument(userId, version)
    }
}