package it.polito.customerrelationshipmanagement.controllers

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.services.ContactService
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
class ContactController(private val contactService: ContactService) {
    /**
     * GET /API/contacts/
     *
     * list all registered contacts in the DB. Allow for
     * pagination, limiting results, and filtering by content, using request parameters.
     */
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/contacts/")
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
    fun getContact(@PathVariable("contactId") contactId: Long): ContactDTO {
        return contactService.findById(contactId)
    }

    /**
     * POST /API/contacts/
     *
     * create a new contact
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/API/contacts/")
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
    fun deleteContactTelephone(
        @PathVariable("contactId") contactId: Long,
        @PathVariable("telephoneId") telephoneId: Long
    ): ContactDTO {
        return contactService.deleteContactTelephone(contactId, telephoneId)
    }

}