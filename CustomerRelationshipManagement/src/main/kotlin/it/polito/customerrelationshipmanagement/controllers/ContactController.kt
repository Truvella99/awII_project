package it.polito.customerrelationshipmanagement.controllers

import com.nimbusds.jwt.JWT
import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.services.ContactService
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
class ContactController(private val contactService: ContactService) {

    @GetMapping("/data")
    fun getRoles(authentication: Authentication): Map<String, Any> {
        val principal = authentication.principal as Jwt//prima era JWT TODO
        val realmAccess = principal.getClaim<Map<String, List<String>>>("realm_access")
        val roles = realmAccess["roles"] ?: emptyList()
        return mapOf("roles" to roles)
    }

    /**
     * GET /API/contacts/
     *
     * list all registered contacts in the DB. Allow for
     * pagination, limiting results, and filtering by content, using request parameters.
     */
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/contacts/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun listAllContacts(
        @RequestParam("pageNumber") pageNumber: Int?,
        @RequestParam("limit") limit: Int?,
        @RequestParam("email") @Pattern(regexp = EMAIL) email: String?,
        @RequestParam("name") @Pattern(regexp = NOT_EMPTY_IF_NOT_NULL) name: String?,
        @RequestParam("telephone") @Pattern(regexp = TELEPHONE) telephone: String?
    ): List<ContactDTO> {
        return contactService.listAllContacts(pageNumber, limit, email, name, telephone)
    }

    /**
     * GET /API/contacts/pendings/
     *
     * list all registered pending contacts in the DB. Allow for
     * pagination, limiting results, and filtering by content, using request parameters.
     */
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/contacts/pendings/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun listAllPendingContacts(
        @RequestParam("pageNumber") pageNumber: Int?,
        @RequestParam("limit") limit: Int?,
        @RequestParam("email") @Pattern(regexp = EMAIL) email: String?,
        @RequestParam("name") @Pattern(regexp = NOT_EMPTY_IF_NOT_NULL) name: String?,
        @RequestParam("telephone") @Pattern(regexp = TELEPHONE) telephone: String?
    ): List<ContactDTO> {
        return contactService.listAllPendingContacts(pageNumber, limit, email, name, telephone)
    }

    /**
     * GET /API/contacts/{contactId}/
     *
     * details of contact {contactId} or fail if it does not exist.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/contacts/{contactId}/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun getContact(@PathVariable("contactId") contactId: Long): ContactDTO {
        return contactService.findById(contactId)
    }

    /**
     * GET /API/contacts/newPending/
     *
     * returns true if a new pending has been created with the given contact.
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/API/contacts/newPending/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun newPending(@RequestBody @Valid data: CheckNewPendingDTO): Boolean {
        return contactService.newPending(data)
    }

    /**
     * POST /API/contacts/
     *
     * create a new contact
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/API/contacts/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun createContact(@RequestBody @Valid c: CreateContactDTO): ContactDTO {
        return contactService.createContact(c)
    }

    /**
     * PUT /API/contacts/{contactId}/category
     *
     * update category of contact {contactId}
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/API/contacts/{contactId}/category")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun updateContactCategory(
        @PathVariable("contactId") contactId: Long,
        @RequestBody @Valid c: UpdateCategoryDTO
    ): ContactDTO {
        return contactService.updateContactCategory(contactId, c)
    }


    /**
     * PUT /API/contacts/{contactId}/name
     *
     * update name of contact {contactId}
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/API/contacts/{contactId}/name")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun updateContactName(
        @PathVariable("contactId") contactId: Long,
        @RequestBody  nameDTO: UpdateNameDTO
    ): ContactDTO {
        return contactService.updateContactName(contactId, nameDTO)
    }

    /**
     * PUT /API/contacts/{contactId}/surname
     *
     * update surname of contact {contactId}
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/API/contacts/{contactId}/surname")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun updateContactSurname(
        @PathVariable("contactId") contactId: Long,
        @RequestBody  surname: UpdateSurnameDTO
    ): ContactDTO {
        return contactService.updateContactSurname(contactId, surname)
    }

    /**
     * PUT /API/contacts/{contactId}/ssncode
     *
     * update ssncode of contact {contactId}
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/API/contacts/{contactId}/ssncode")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun updateContactSSNCode(
        @PathVariable("contactId") contactId: Long,
        @RequestBody @Valid ssnCodeDTO: UpdateSSNCodeDTO
    ): ContactDTO {
        return contactService.updateContactSSNCode(contactId, ssnCodeDTO)
    }
    /**
     * POST /API/contacts/{contactId}/email
     *
     * add an email to contact {contactId}
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/API/contacts/{contactId}/email")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun addContactEmail(@PathVariable("contactId") contactId: Long, @RequestBody @Valid e: CreateUpdateEmailDTO): ContactDTO {
        return contactService.addContactEmail(contactId, e)
    }

    /**
     * DELETE /API/contacts/{contactId}/email/{emailId}
     *
     * delete email {emailId} of contact {contactId}
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/API/contacts/{contactId}/email/{emailId}")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun deleteContactEmail(
        @PathVariable("contactId") contactId: Long,
        @PathVariable("emailId") emailId: Long
    ): ContactDTO {
        return contactService.deleteContactEmail(contactId, emailId)
    }

    /**
     * POST /API/contacts/{contactId}/address
     *
     * add an address to contact {contactId}
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/API/contacts/{contactId}/address")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun addContactAddress(@PathVariable("contactId") contactId: Long, @RequestBody @Valid a: CreateUpdateAddressDTO): ContactDTO {
        return contactService.addContactAddress(contactId, a)
    }

    /**
     * DELETE /API/contacts/{contactId}/address/{addressId}
     *
     * delete address {addressId} of contact {contactId}
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/API/contacts/{contactId}/address/{addressId}")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun deleteContactAddress(
        @PathVariable("contactId") contactId: Long,
        @PathVariable("addressId") addressId: Long
    ): ContactDTO {
        return contactService.deleteContactAddress(contactId, addressId)
    }

    /**
     *POST /API/contacts/{contactId}/telephone
     *
     * add a telephone to contact {contactId}
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/API/contacts/{contactId}/telephone")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun addContactTelephone(@PathVariable("contactId") contactId: Long, @RequestBody @Valid t: CreateUpdateTelephoneDTO): ContactDTO {
        return contactService.addContactTelephone(contactId, t)
    }

    /**
     * DELETE /API/contacts/{contactId}/telephone/{telephoneId}
     *
     * delete telephone {telephoneId} of contact {contactId}
     */
    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/API/contacts/{contactId}/telephone/{telephoneId}")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun deleteContactTelephone(
        @PathVariable("contactId") contactId: Long,
        @PathVariable("telephoneId") telephoneId: Long
    ): ContactDTO {
        return contactService.deleteContactTelephone(contactId, telephoneId)
    }

// * DELETE /API/contacts/{contactId}/
// *
// * Remove contact {contactId} or fail if it does not exist.
@DeleteMapping("/API/contacts/{contactId}")
fun deleteContact(@PathVariable("contactId") contactId: Long) {
    return contactService.deleteContact(contactId)
}
}