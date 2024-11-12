package it.polito.customerrelationshipmanagement.services

import it.polito.customerrelationshipmanagement.dtos.*
import jakarta.validation.constraints.Pattern
import org.springframework.web.bind.annotation.RequestParam

interface ContactService {
    fun listAllPendingContacts(
        pageNumber: Int?,
        limit: Int?,
        email: String?,
        name: String?,
        telephone: String?
    ): List<ContactDTO>

    fun listAllContacts(
        pageNumber: Int?,
        limit: Int?,
        email: String?,
        name: String?,
        telephone: String?
    ): List<ContactDTO>

    fun findById(contactId: Long): ContactDTO
    fun newPending(data: CheckNewPendingDTO): Boolean
    fun createContact(c: CreateContactDTO, isPending: Boolean = false): ContactDTO
    fun updateContactCategory(contactId: Long, categoryDTO: UpdateCategoryDTO): ContactDTO
    fun updateContactName(contactId: Long, nameDTO: UpdateNameDTO): ContactDTO
    fun updateContactSurname(contactId: Long, surnameDTO: UpdateSurnameDTO): ContactDTO
    fun updateContactSSNCode(contactId: Long, ssnCodeDTO: UpdateSSNCodeDTO): ContactDTO

    fun addContactEmail(contactId: Long, emailDTO: CreateUpdateEmailDTO): ContactDTO
    fun deleteContactEmail(contactId: Long, emailId: Long): ContactDTO
    fun addContactAddress(contactId: Long, addressDTO: CreateUpdateAddressDTO): ContactDTO
    fun deleteContactAddress(contactId: Long, addressId: Long): ContactDTO
    fun addContactTelephone(contactId: Long, telephoneDTO: CreateUpdateTelephoneDTO): ContactDTO
    fun deleteContactTelephone(contactId: Long, telephoneId: Long): ContactDTO
}