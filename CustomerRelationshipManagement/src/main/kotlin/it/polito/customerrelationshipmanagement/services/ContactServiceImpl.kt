package it.polito.customerrelationshipmanagement.services

import it.polito.customerrelationshipmanagement.controllers.ContactController
import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.*
import it.polito.customerrelationshipmanagement.exceptions.*
import it.polito.customerrelationshipmanagement.repositories.*
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
@Transactional
class ContactServiceImpl(
    private val contactRepository: ContactRepository,
    private val addressRepository: AddressRepository,
    private val telephoneRepository: TelephoneRepository,
    private val emailRepository: EmailRepository,
) : ContactService {
    // logger to log messages in the APIs
    private val logger = LoggerFactory.getLogger(ContactController::class.java)


    // ----- Get a list of all contacts -----
    override fun listAllContacts(
        pageNumber: Int?,
        limit: Int?,
        email: String?,
        name: String?,
        telephone: String?
    ): List<ContactDTO> {
        if (pageNumber != null && limit != null) {
            if (pageNumber >= 0 && limit > 0) {
                val p = PageRequest.of(pageNumber, limit)
                return contactRepository.findByEmailsOrNameOrTelephones(email,name,telephone,p).map { it.toDTO() }
            } else {
                if (pageNumber < 0 && limit <= 0) {
                    throw IllegalPageNumberLimitException("Invalid pageNumber and limit Parameter.")
                } else if (pageNumber < 0) {
                    throw IllegalPageNumberLimitException("Invalid pageNumber Parameter.")
                } else {
                    throw IllegalPageNumberLimitException("Invalid limit Parameter.")
                }
            }
        } else if (pageNumber == null && limit == null) {
            return contactRepository.findByEmailsOrNameOrTelephones(email,name,telephone,null).map { it.toDTO() }
        } else {
            throw IllegalPageNumberLimitException("PageNumber and limit must be both provided or both not provided.")
        }
    }


    // ----- Get a list of all contacts with a pending status -----
    override fun listAllPendingContacts(
        pageNumber: Int?,
        limit: Int?,
        email: String?,
        name: String?,
        telephone: String?
    ): List<ContactDTO> {
        if (pageNumber != null && limit != null) {
            if (pageNumber >= 0 && limit > 0) {
                val p = PageRequest.of(pageNumber, limit)
                return contactRepository.findBySurnameNullSsnCodeNullCategoryNullAndEmailsOrNameOrTelephones(email,name,telephone,p).map { it.toDTO() }
            } else {
                if (pageNumber < 0 && limit <= 0) {
                    throw IllegalPageNumberLimitException("Invalid pageNumber and limit Parameter.")
                } else if (pageNumber < 0) {
                    throw IllegalPageNumberLimitException("Invalid pageNumber Parameter.")
                } else {
                    throw IllegalPageNumberLimitException("Invalid limit Parameter.")
                }
            }
        } else if (pageNumber == null && limit == null) {
            return contactRepository.findBySurnameNullSsnCodeNullCategoryNullAndEmailsOrNameOrTelephones(email,name,telephone,null).map { it.toDTO() }
        } else {
            throw IllegalPageNumberLimitException("PageNumber and limit must be both provided or both not provided.")
        }
    }


    // ----- Get a contact by its ID -----
    override fun findById(
        contactId: Long
    ): ContactDTO {
        if (contactId < 0) {
            throw IllegalIdException("Invalid contactId Parameter.")
        }

        try {
            return contactRepository.findById(contactId).get().toDTO()
        } catch (e: RuntimeException) {
            throw ContactNotFoundException("Contact with ContactId:$contactId not found")
        }
    }

    
    // ----- Create a new contact -----
    override fun createContact(
        c: CreateContactDTO,
        isPending: Boolean
    ): ContactDTO {
        var contact = Contact()
        if (!isPending && (c.name == null || c.surname == null || c.ssncode == null || c.category == null)) {
            throw ContactException("Contact name, surname, ssncode and email cannot be empty.")
        }
        contact.name = c.name?.trim()
        contact.surname = c.surname?.trim()
        contact.ssncode = c.ssncode?.trim()
        contact.category = c.category

        contact = contactRepository.save(contact)

        if (c.email != null) {
            val dbEmails = emailRepository.findByEmail(c.email)
            if (dbEmails.isEmpty()) {
                // if not any email found create the entry and link it to contact
                var e = Email()
                e.email = c.email
                e = emailRepository.save(e)
                logger.info("Email ${c.email} created and linked to Contact.")
                contact.addEmail(e)
            } else {
                var isNotLinked = false
                for (dbEmail in dbEmails) {
                    if (dbEmail.contact == null) {
                        // if at least one of the emails found is not linked to any contact simply link it
                        contact.addEmail(dbEmail)
                        logger.info("Already Existing email ${c.email} linked to Contact.")
                        isNotLinked = true
                        break
                    }
                }
                if (!isNotLinked) {
                    // otherwise all emails found are linked to other contacts so create the new entry for this contact
                    var e = Email()
                    e.email = c.email
                    e = emailRepository.save(e)
                    logger.info("Email ${c.email} created and linked to Contact.")
                    contact.addEmail(e)
                }
            }
        }

        if (c.telephone != null) {
            val dbTelephones = telephoneRepository.findByTelephone(c.telephone)
            if (dbTelephones.isEmpty()) {
                // if not any telephone found create the entry and link it to contact
                var t = Telephone()
                t.telephone = c.telephone
                t = telephoneRepository.save(t)
                logger.info("Telephone ${c.telephone} created and linked to Contact.")
                contact.addTelephone(t)
            } else {
                var isNotLinked = false
                for (dbTelephone in dbTelephones) {
                    if (dbTelephone.contact == null) {
                        // if at least one of the telephones found is not linked to any contact simply link it
                        contact.addTelephone(dbTelephone)
                        logger.info("Already Existing telephone ${c.telephone} linked to Contact.")
                        isNotLinked = true
                        break
                    }
                }
                if (!isNotLinked) {
                    // otherwise all telephones found are linked to other contacts so create the new entry for this contact
                    var t = Telephone()
                    t.telephone = c.telephone
                    t = telephoneRepository.save(t)
                    logger.info("Telephone ${c.telephone} created and linked to Contact.")
                    contact.addTelephone(t)
                }
            }
        }

        if (c.address != null) {
            val dbAddresses = addressRepository.findByAddress(c.address)
            if (dbAddresses.isEmpty()) {
                // if not any address found create the entry and link it to contact
                var a = Address()
                a.address = c.address
                a = addressRepository.save(a)
                logger.info("Address ${a.address} created and linked to Contact.")
                contact.addAddress(a)
            } else {
                var isNotLinked = false
                for (dbAddress in dbAddresses) {
                    if (dbAddress.contact == null) {
                        // if at least one of the addresses found is not linked to any contact simply link it
                        contact.addAddress(dbAddress)
                        logger.info("Already Existing address ${c.address} linked to Contact.")
                        isNotLinked = true
                        break
                    }
                }
                if (!isNotLinked) {
                    // otherwise all addresses found are linked to other contacts so create the new entry for this contact
                    var a = Address()
                    a.address = c.address
                    a = addressRepository.save(a)
                    logger.info("Address ${a.address} created and linked to Contact.")
                    contact.addAddress(a)
                }
            }
        }
        logger.info("Contact ${c.name} created.")
        return contact.toDTO()
    }


    // ----- Update the category of a contact -----
    override fun updateContactCategory(
        contactId: Long,
        categoryDTO: UpdateCategoryDTO
    ): ContactDTO {
        if (contactId < 0) {
            throw IllegalIdException("Invalid contactId Parameter.")
        }

        val contact = contactRepository.findById(contactId).orElseThrow {
            throw ContactNotFoundException("Contact with ContactId:$contactId not found")
        }
        if ((contact.category == category.customer && categoryDTO.category == category.professional) ||
            (contact.category == category.professional && categoryDTO.category == category.customer)
        ) {
            throw IllegalCategoryTransitionException("Cannot Pass from Customer to Professional or Vice versa, must Create 2 Accounts for that.")
        }
        contact.category = categoryDTO.category
        val updatedContact = contactRepository.save(contact).toDTO()
        logger.info("Contact category ${updatedContact.category} of Contact ${contact.name} updated.")
        return updatedContact
    }


    // ----- Update the name of a contact -----
    override fun updateContactName(
        contactId: Long,
        nameDTO: UpdateNameDTO
    ): ContactDTO {
        if (contactId < 0) {
            throw IllegalIdException("Invalid contactId Parameter.")
        }
        try {
            val contact = contactRepository.findById(contactId).get()
            contact.name = nameDTO.name
            val updatedContact = contactRepository.save(contact).toDTO()
            logger.info("Contact name ${updatedContact.name} of Contact ${contactId} updated.")
            return updatedContact
        } catch (e: RuntimeException) {
            throw ContactNotFoundException("Contact with ContactId:$contactId not found")
        }
    }

    
    // ----- Update the surname of a contact -----
    override fun updateContactSurname(
        contactId: Long,
        surnameDTO: UpdateSurnameDTO
    ): ContactDTO {
        if (contactId < 0) {
            throw IllegalIdException("Invalid contactId Parameter.")
        }
        try {
            val contact = contactRepository.findById(contactId).get()
            contact.surname = surnameDTO.surname
            val updatedContact = contactRepository.save(contact).toDTO()
            logger.info("Contact surname ${updatedContact.surname} of Contact ${contact.name} updated.")
            return updatedContact
        } catch (e: RuntimeException) {
            throw ContactNotFoundException("Contact with ContactId:$contactId not found")
        }
    }


    // ----- Update the SSN code of a contact -----
    override fun updateContactSSNCode(
        contactId: Long,
        ssnCodeDTO: UpdateSSNCodeDTO
    ): ContactDTO {
        if (contactId < 0) {
            throw IllegalIdException("Invalid contactId Parameter.")
        }
        try {
            val contact = contactRepository.findById(contactId).get()
            contact.ssncode = ssnCodeDTO.ssncode
            val updatedContact = contactRepository.save(contact).toDTO()
            logger.info("Contact ssncode ${updatedContact.ssncode} of Contact ${contact.name} updated.")
            return updatedContact
        } catch (e: RuntimeException) {
            throw ContactNotFoundException("Contact with ContactId:$contactId not found")
        }
    }


    // ----- Add an email address to a contact -----
    override fun addContactEmail(
        contactId: Long,
        emailDTO: CreateUpdateEmailDTO
    ): ContactDTO {
        if (contactId < 0) {
            throw IllegalIdException("Invalid contactId Parameter.")
        }

        val contact = contactRepository.findById(contactId).orElseThrow{
            throw ContactNotFoundException("Contact with ContactId:$contactId not found")
        }
        val dbEmails = emailRepository.findByEmail(emailDTO.email)
        if (dbEmails.isEmpty()) {
            // if not any email found create the entry and link it to contact
            var e = Email()
            e.email = emailDTO.email
            e = emailRepository.save(e)
            logger.info("Email ${emailDTO.email} created and linked to Contact.")
            contact.addEmail(e)
        } else {
            var isNotLinked = false
            for (dbEmail in dbEmails) {
                if (dbEmail.contact == contact && dbEmail.state == contactInfoState.active) {
                    throw EmailAlreadyPresentException("Email ${emailDTO.email} already exists for this contact.")
                }
            }
            for (dbEmail in dbEmails) {
                if (dbEmail.contact == null) {
                    // if at least one of the emails found is not linked to any contact simply link it
                    contact.addEmail(dbEmail)
                    logger.info("Already Existing email ${emailDTO.email} linked to Contact.")
                    isNotLinked = true
                    break
                }
            }
            if (!isNotLinked) {
                // otherwise all emails found are linked to other contacts so create the new entry for this contact
                var e = Email()
                e.email = emailDTO.email
                e = emailRepository.save(e)
                logger.info("Email ${emailDTO.email} created and linked to Contact.")
                contact.addEmail(e)
            }
        }
        val updatedContact = contactRepository.save(contact).toDTO()
        logger.info("Contact email ${emailDTO.email} of Contact ${contact.name} added.")
        return updatedContact
    }

    // ----- Delete an email address from a contact -----
    override fun deleteContactEmail(
        contactId: Long,
        emailId: Long
    ): ContactDTO {
        if (contactId < 0 && emailId < 0) {
            throw IllegalIdException("Invalid contactId and emailId Parameter.")
        } else if (contactId < 0) {
            throw IllegalIdException("Invalid contactId Parameter.")
        } else if (emailId < 0) {
            throw IllegalIdException("Invalid emailId Parameter.")
        }

        val contact = contactRepository.findById(contactId).orElseThrow {
            throw ContactNotFoundException("Contact with ContactId:$contactId not found")
        }
        val e = emailRepository.findById(emailId).orElseThrow {
            throw EmailNotFoundException("Email with EmailId:$emailId not found")
        }
        if (e.state == contactInfoState.deleted) {
            throw EmailAlreadyDeletedException("Email with EmailId:${emailId} already deleted.")
        }
        if (e.contact != contact) {
            throw NoDeletePermissionException("Email with EmailId:${emailId} does not belong to this contact.")
        }
        e.state = contactInfoState.deleted
        emailRepository.save(e)
        val updatedContact = contactRepository.save(contact).toDTO()
        logger.info("Contact email ${e.email} of Contact ${contact.name} marked as deleted.")
        return updatedContact
    }

    // ----- Add an address to a contact -----
    override fun addContactAddress(
        contactId: Long,
        addressDTO: CreateUpdateAddressDTO
    ): ContactDTO {
        if (contactId < 0) {
            throw IllegalIdException("Invalid contactId Parameter.")
        }
        // retrieve and update the contact
        val contact = contactRepository.findById(contactId).orElseThrow {
            throw ContactNotFoundException("Contact with ContactId:$contactId not found")
        }
        val dbAddresses = addressRepository.findByAddress(addressDTO.address)
        if (dbAddresses.isEmpty()) {
            // if not any address found create the entry and link it to contact
            var a = Address()
            a.address = addressDTO.address
            a = addressRepository.save(a)
            logger.info("Address ${addressDTO.address} created and linked to Contact.")
            contact.addAddress(a)
        } else {
            var isNotLinked = false
            for (dbAddress in dbAddresses) {
                if (dbAddress.contact == contact && dbAddress.state == contactInfoState.active) {
                    throw AddressAlreadyPresentException("Address ${addressDTO.address} already exists for this contact.")
                }
            }
            for (dbAddress in dbAddresses) {
                if (dbAddress.contact == null) {
                    // if at least one of the addresses found is not linked to any contact simply link it
                    contact.addAddress(dbAddress)
                    logger.info("Already Existing address ${addressDTO.address} linked to Contact.")
                    isNotLinked = true
                    break
                }
            }
            if (!isNotLinked) {
                // otherwise all emails found are linked to other contacts so create the new entry for this contact
                var a = Address()
                a.address = addressDTO.address
                a = addressRepository.save(a)
                logger.info("Address ${addressDTO.address} created and linked to Contact.")
                contact.addAddress(a)
            }
        }
        val updatedContact = contactRepository.save(contact).toDTO();
        logger.info("Contact address ${addressDTO.address} of Contact ${contact.name} added.")
        return updatedContact;
    }

    // ----- Delete an address from a contact -----
    override fun deleteContactAddress(
        contactId: Long,
        addressId: Long
    ): ContactDTO {
        if (contactId < 0 && addressId < 0) {
            throw IllegalIdException("Invalid contactId and addressId.")
        } else if (contactId < 0) {
            throw IllegalIdException("Invalid contactId Parameter.")
        } else if (addressId < 0) {
            throw IllegalIdException("Invalid addressId Parameter.")
        }

        val contact = contactRepository.findById(contactId).orElseThrow {
            throw ContactNotFoundException("Contact with ContactId:$contactId not found")
        }
        val addr = addressRepository.findById(addressId).orElseThrow {
            throw AddressNotFoundException("Address with AddressId:$addressId not found")
        }
        if (addr.state == contactInfoState.deleted) {
            throw AddressAlreadyDeletedException("Address with AddressId:${addressId} already deleted.")
        }
        if (addr.contact != contact) {
            throw NoDeletePermissionException("Address with AddressId:${addressId} does not belong to this contact.")
        }
        addr.state = contactInfoState.deleted
        addressRepository.save(addr)
        val updatedContact = contactRepository.save(contact).toDTO()
        logger.info("Contact address ${addr.address} of Contact ${contact.name} marked as deleted.")
        return updatedContact
    }


    // ----- Add a telephone number to a contact -----
    override fun addContactTelephone(
        contactId: Long,
        telephoneDTO: CreateUpdateTelephoneDTO
    ): ContactDTO {
        if (contactId < 0) {
            throw IllegalIdException("Invalid contactId Parameter.")
        }
        // retrieve and update the contact
        val contact = contactRepository.findById(contactId).orElseThrow {
            throw ContactNotFoundException("Contact with ContactId:$contactId not found")
        }
        val dbTelephones = telephoneRepository.findByTelephone(telephoneDTO.telephone)
        if (dbTelephones.isEmpty()) {
            // if not any telephone found create the entry and link it to contact
            var t = Telephone()
            t.telephone = telephoneDTO.telephone
            t = telephoneRepository.save(t)
            logger.info("Telephone ${telephoneDTO.telephone} created and linked to Contact.")
            contact.addTelephone(t)
        } else {
            var isNotLinked = false
            for (dbTelephone in dbTelephones) {
                if (dbTelephone.contact == contact && dbTelephone.state == contactInfoState.active) {
                    throw TelephoneAlreadyPresentException("Telephone ${telephoneDTO.telephone} already exists for this contact.")
                }
            }
            for (dbTelephone in dbTelephones) {
                if (dbTelephone.contact == null) {
                    // if at least one of the telephones found is not linked to any contact simply link it
                    contact.addTelephone(dbTelephone)
                    logger.info("Already Existing telephone ${telephoneDTO.telephone} linked to Contact.")
                    isNotLinked = true
                    break
                }
            }
            if (!isNotLinked) {
                // otherwise all telephones found are linked to other contacts so create the new entry for this contact
                var t = Telephone()
                t.telephone = telephoneDTO.telephone
                t = telephoneRepository.save(t)
                logger.info("Telephone ${telephoneDTO.telephone} created and linked to Contact.")
                contact.addTelephone(t)
            }
        }
        val updatedContact = contactRepository.save(contact).toDTO();
        logger.info("Contact telephone ${telephoneDTO.telephone} of Contact ${contact.name} added.")
        return updatedContact;
    }

    // ----- Delete a telephone number from a contact -----
    override fun deleteContactTelephone(
        contactId: Long,
        telephoneId: Long
    ): ContactDTO {
        if (contactId < 0 && telephoneId < 0) {
            throw IllegalIdException("Invalid contactId and telephoneId.")
        } else if (contactId < 0) {
            throw IllegalIdException("Invalid contactId Parameter.")
        } else if (telephoneId < 0) {
            throw IllegalIdException("Invalid telephoneId Parameter.")
        }
        val contact = contactRepository.findById(contactId).orElseThrow {
            throw ContactNotFoundException("Contact with ContactId:$contactId not found")
        }
        val t = telephoneRepository.findById(telephoneId).orElseThrow {
            throw TelephoneNotFoundException("Telephone with TelephoneId:$telephoneId not found")
        }
        if (t.state == contactInfoState.deleted) {
            throw TelephoneAlreadyDeletedException("Telephone with TelephoneId:${telephoneId} already deleted.")
        }
        if (t.contact != contact) {
            throw NoDeletePermissionException("Telephone with TelephoneId:${telephoneId} does not belong to this contact.")
        }
        t.state = contactInfoState.deleted
        telephoneRepository.save(t)
        val updatedContact = contactRepository.save(contact).toDTO()
        logger.info("Contact telephone ${t.telephone} of Contact ${contact.name} marked as deleted.")
        return updatedContact
    }
}