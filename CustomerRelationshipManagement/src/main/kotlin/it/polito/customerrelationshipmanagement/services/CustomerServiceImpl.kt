package it.polito.customerrelationshipmanagement.services

import it.polito.customerrelationshipmanagement.controllers.ContactController
import it.polito.customerrelationshipmanagement.controllers.CustomerController
import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.*
import it.polito.customerrelationshipmanagement.exceptions.*
import it.polito.customerrelationshipmanagement.repositories.*
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import kotlin.math.log
import it.polito.customerrelationshipmanagement.dtos.UserDTO
import it.polito.customerrelationshipmanagement.Credentials
import org.keycloak.representations.idm.UserRepresentation
import org.keycloak.admin.client.Keycloak

@Service
@Transactional
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val noteRepository: NoteRepository,
    private val skillRepository: SkillRepository,
    private val contactRepository: ContactRepository,
    private val contactService: ContactService,
    private val jobOfferService: JobOfferService
) : CustomerService {
    // logger to log messages in the APIs
    private val logger = LoggerFactory.getLogger(CustomerController::class.java)

    fun addUser(userDTO: UserDTO) {
        val credential = Credentials.createPasswordCredentials(userDTO.password)
        val user = UserRepresentation().apply {
            username = userDTO.userName
            firstName = userDTO.firstname
            lastName = userDTO.lastName
            email = userDTO.emailId
            credentials = listOf(credential)
            isEnabled = true
        }

        val instance = KeycloakConfig.getInstance()
        instance.realm("CRMRealm").users().create(user)
    }
    // ----- Create a new customer -----
    override fun createCustomer(
        customer: CreateUpdateCustomerDTO
    ): CustomerDTO {
        val c = Customer()
        val contactDTO = contactService.createContact(
            CreateContactDTO(
                name = customer.name,
                surname = customer.surname,
                ssncode = customer.ssncode,
                category = category.customer,
                email = customer.email,
                telephone = customer.telephone,
                address = customer.address
            )
        )
        val contact = contactRepository.findById(contactDTO.id).get()
        c.contact = contact
        customerRepository.save(c)
        contact.customer = c
        contactRepository.save(contact)

        // Add jobOffer
        customer.jobOffers?.forEach { jobOffer ->
            val (jobOfferId,jobOfferDTO) = jobOffer
            // update the jobOffer if it already exists, otherwise create it
            val jDTO = CreateUpdateJobOfferDTO(
                name = jobOfferDTO.name,
                description = jobOfferDTO.description,
                currentStateNote = jobOfferDTO.currentStateNote,
                duration = jobOfferDTO.duration,
                profitMargin = jobOfferDTO.profitMargin,
                customerId = c.id,
                skillsToDelete = null,
                skills = jobOfferDTO.skills.map {
                    CreateSkillDTO(
                        skill = it.skill,
                        jobOfferId = null,
                        professionalId = null
                    )
                }
            )
            jobOfferService.createJobOffer(jDTO)
        }

        // Create Notes
        customer.notes?.forEach { note ->
            addCustomerNote(c.id, CreateUpdateNoteDTO(note = note))
        }
        addUser(UserDTO(
            userName = customer.name!!,
            emailId = customer.email!!,
            password = "password",
            firstname = customer.name!!,
            lastName = customer.surname!!,
            role = "customer"
        ))

        logger.info("Customer ${c.contact.name} created.")
        return c.toDTO()
    }


    // ----- Get a customer by its ID -----
    override fun findCustomerById(
        customerId: Long
    ): CustomerDTO {
        if (customerId < 0) {
            throw IllegalIdException("Invalid customerId Parameter.")
        }

        try {
            return customerRepository.findById(customerId).get().toDTO()
        } catch (e: RuntimeException) {
            throw CustomerNotFoundException("Customer with CustomerId:$customerId not found")
        }
    }


    // ----- Update data for a customer -----
    override fun updateCustomer(
        customerId: Long,
        customer: CreateUpdateCustomerDTO
    ): CustomerDTO {
        if (customerId < 0) {
            throw IllegalIdException("Invalid customerId Parameter.")
        }

        val c = customerRepository.findById(customerId).orElseThrow{
            throw CustomerNotFoundException("Customer with CustomerId:$customerId not found")
        }
        
        if (customer.name != null) {
            contactService.updateContactName(c.contact.id, UpdateNameDTO(name = customer.name))
        }
        if (customer.surname != null) {
            contactService.updateContactSurname(c.contact.id, UpdateSurnameDTO(surname = customer.surname))
        }
        if (customer.ssncode != null) {
            contactService.updateContactSSNCode(c.contact.id, UpdateSSNCodeDTO(ssncode = customer.ssncode))
        }
        if (customer.category != null) {
            contactService.updateContactCategory(c.contact.id, UpdateCategoryDTO(category = customer.category))
        }
        if (customer.email != null) {
            contactService.addContactEmail(c.contact.id, CreateUpdateEmailDTO(email = customer.email))
        }
        if (customer.telephone != null) {
            contactService.addContactTelephone(c.contact.id, CreateUpdateTelephoneDTO(telephone = customer.telephone))
        }
        if (customer.address != null) {
            contactService.addContactAddress(c.contact.id, CreateUpdateAddressDTO(address = customer.address))
        }
        customer.emailsToDelete?.forEach { emailId ->
            contactService.deleteContactEmail(c.contact.id, emailId)
        }
        customer.telephonesToDelete?.forEach { telephoneId ->
            contactService.deleteContactTelephone(c.contact.id, telephoneId)
        }
        customer.addressesToDelete?.forEach { addressId ->
            contactService.deleteContactAddress(c.contact.id, addressId)
        }
        //Delete notes
        customer.notesToDelete?.forEach { noteId ->
            deleteCustomerNote(c.id, noteId)
        }
        // Add notes
        customer.notes?.forEach { note ->
            addCustomerNote(c.id, CreateUpdateNoteDTO(note = note))
        }

        // Add job offers
        customer.jobOffers?.forEach { jobOffer ->
            val (jobOfferId,JobOfferDTO) = jobOffer
            // update the jobOffer if it already exists, otherwise create it
            val jDTO = CreateUpdateJobOfferDTO(
                name = JobOfferDTO.name,
                description = JobOfferDTO.description,
                currentStateNote = JobOfferDTO.currentStateNote,
                duration = JobOfferDTO.duration,
                profitMargin = JobOfferDTO.profitMargin,
                customerId = c.id,
                skillsToDelete = null,
                skills = JobOfferDTO.skills.map { CreateSkillDTO(
                    skill = it.skill,
                    jobOfferId = jobOfferId,
                    professionalId = null
                ) }
            )
            if (jobOfferId != null) {
                val existingJobOffer = jobOfferRepository.findById(jobOfferId).orElseThrow{
                    throw JobOfferNotFoundException("Job Offer with Id:$jobOfferId not found")
                }
                if (existingJobOffer.customer == c) {
                    jobOfferService.updateJobOffer(jobOfferId, jDTO)
                } else {
                    throw NoJobOfferPermissionException("Cannot Update Job Offer of Another Customer with Id:$jobOfferId")
                }
            } else {
                jobOfferService.createJobOffer(jDTO)
            }
        }

        logger.info("Customer ${c.contact.name} updated.")
        return c.toDTO()
    }


    // ----- Add a note for a customer -----
    override fun addCustomerNote(
        customerId: Long,
        note: CreateUpdateNoteDTO
    ): NoteDTO {
        if (customerId < 0) {
            throw IllegalIdException("Invalid customerId Parameter.")
        }

        val customer = customerRepository.findById(customerId).orElseThrow{
            throw CustomerNotFoundException("Customer with CustomerId:$customerId not found")
        }

        var newNote = Note()
        newNote.note = note.note
        newNote = noteRepository.save(newNote)
        logger.info("Note '${note.note}' created and linked to Customer.")
        customer.addNote(newNote)

        val updatedCustomer = customerRepository.save(customer).toDTO()
        logger.info("Note '$note' added to Customer ${updatedCustomer.name}.")
        return newNote.toDTO()
    }

    override fun deleteCustomerNote(customerId: Long, noteId: Long): CustomerDTO {
            if (customerId < 0 && noteId < 0) {
                throw IllegalIdException("Invalid customerId and noteId Parameter.")
            } else if (customerId < 0) {
                throw IllegalIdException("Invalid customerId Parameter.")
            } else if (noteId < 0) {
                throw IllegalIdException("Invalid noteId Parameter.")
            }
        val customer = customerRepository.findById(customerId).orElseThrow{
            throw CustomerNotFoundException("Customer with CustomerId:$customerId not found")
        }
        val n = noteRepository.findById(noteId).orElseThrow{
            throw NoteNotFoundException("Note with NoteId:$noteId not found")
        }
        if (n.customer != customer) {
            throw NoDeletePermissionException("Note with NoteId:${noteId} does not belong to this customer.")
        }
        if (n.state == contactInfoState.deleted) {
            throw NoteAlreadyDeletedException("Note with NoteId:${noteId} already deleted.")
        }
        n.state = contactInfoState.deleted
        noteRepository.save(n)
        val updatedCustomer = customerRepository.save(customer).toDTO()
        logger.info("Customer note ${n.note} of Customer ${customer.contact.name} marked as deleted.")
        return updatedCustomer
    }

    override fun findCustomers(filter: String): List<CustomerDTO> {
        return contactRepository.findByCategoryAndCustomFilter(category.customer,filter,filter,filter,filter,filter).mapNotNull { it.customer?.toDTO() }
    }

    override fun getAllCustomers(): List<CustomerDTO> {
        return customerRepository.findAll().map { it.toDTO() }
    }

}


