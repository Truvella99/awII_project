package it.polito.customerrelationshipmanagement.services

import it.polito.customerrelationshipmanagement.controllers.ProfessionalController
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
class ProfessionalServiceImpl(
    private val customerRepository: CustomerRepository,
    private val professionalRepository: ProfessionalRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val jobOfferService: JobOfferService,
    private val contactRepository: ContactRepository,
    private val skillRepository: SkillRepository,
    private val contactService: ContactService,
    private val noteRepository: NoteRepository
) : ProfessionalService {
    // logger to log messages in the APIs
    private val logger = LoggerFactory.getLogger(ProfessionalController::class.java)
    
    
    // ----- Get a list of all professionals -----
    override fun listAllProfessionals(
        pageNumber: Int?,
        limit: Int?,
        skills: List<String>?,
        latitude: Double?,
        longitude: Double?,
        employmentState: employmentState?
    ): List<ProfessionalDTO> {
        if (latitude != null && longitude == null) {
            throw IllegalGeographicalLocationException("Longitude must be provided if Latitude is provided.")
        } else if (latitude == null && longitude != null) {
            throw IllegalGeographicalLocationException("Latitude must be provided if Longitude is provided.")
        }
        val geographicalLocation = if (latitude != null && longitude != null) Pair(latitude, longitude) else null

        if (pageNumber != null && limit != null) {
            if (pageNumber >= 0 && limit > 0) {
                val p = PageRequest.of(pageNumber, limit)
               return professionalRepository.findBySkillsOrLocationOrEmploymentState(skills, geographicalLocation, employmentState, p).map { it.toDTO() }
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
            return professionalRepository.findBySkillsOrLocationOrEmploymentState(skills, geographicalLocation, employmentState, null).map { it.toDTO() }
        } else {
            throw IllegalPageNumberLimitException("PageNumber and limit must be both provided or both not provided.")
        }
    }


    // ----- Create a new professional -----
    override fun createProfessional(
        professional: CreateUpdateProfessionalDTO
    ): ProfessionalDTO {
        var p = Professional()
        val contactDTO = contactService.createContact(CreateContactDTO(
            name = professional.name,
            surname = professional.surname,
            ssncode = professional.ssncode,
            category = category.professional,
            email = professional.email,
            telephone = professional.telephone,
            address = professional.address
        ))
        val contact = contactRepository.findById(contactDTO.id).get()
        p.contact = contact
        contact.professional = p
        contactRepository.save(contact)

        if (professional.employmentState == null || professional.geographicalLocation == null || professional.dailyRate == null ) {
            throw ProfessionalException("EmploymentState, geographicalLocation and dailyRate cannot be empty.")
        }
        p.employmentState = professional.employmentState
        p.geographicalLocation =  professional.geographicalLocation//Pair(0.0, 0.0)
        p.dailyRate = professional.dailyRate.toDouble()

        professionalRepository.save(p)

        professional.skills?.forEach { skillDTO ->
            val skill = Skill()
            skill.skill = skillDTO.skill
            skillRepository.save(skill)
            logger.info("Skill '${skillDTO.skill}' created and linked to Professional '${p.contact?.name} ${p.contact?.surname}'.")
            p.addSkill(skill)
        }

        professional.notes?.forEach { noteDTO ->
            addProfessionalNote(p.id, CreateUpdateNoteDTO(note = noteDTO))
        }

        logger.info("Professional ${p.contact.name} created.")
        return p.toDTO()
    }

    // ----- Get a professional by its ID -----
    override fun findProfessionalById(professionalId: Long): ProfessionalDTO {
        if (professionalId < 0) {
            throw IllegalIdException("Invalid professionalId Parameter.")
        }
        val professional = professionalRepository.findById(professionalId).orElseThrow{
            throw ProfessionalNotFoundException("Professional with ProfessionalId:$professionalId not found")
        }
        return professional.toDTO()
    }

    // ----- Update a professional -----
    override fun updateProfessional(professionalId: Long, professional: CreateUpdateProfessionalDTO): ProfessionalDTO {
        var updateJobOffer = false

        if (professionalId < 0) {
            throw IllegalIdException("Invalid professionalId Parameter.")
        }
        val p = professionalRepository.findById(professionalId).orElseThrow{
            throw ProfessionalNotFoundException("Professional with ProfessionalId:$professionalId not found.")
        }
        if (professional.name != null) {
            contactService.updateContactName(p.contact.id, UpdateNameDTO(name = professional.name))
        }
        if (professional.surname != null) {
            contactService.updateContactSurname(p.contact.id, UpdateSurnameDTO(surname = professional.surname))
        }
        if (professional.ssncode != null) {
            contactService.updateContactSSNCode(p.contact.id, UpdateSSNCodeDTO(ssncode = professional.ssncode))
        }
        if (professional.category != null) {
            contactService.updateContactCategory(p.contact.id, UpdateCategoryDTO(category = professional.category))
        }
        if (professional.email != null) {
            contactService.addContactEmail(p.contact.id, CreateUpdateEmailDTO(email = professional.email))
        }
        if (professional.telephone != null) {
            contactService.addContactTelephone(p.contact.id, CreateUpdateTelephoneDTO(telephone = professional.telephone))
        }
        if (professional.address != null) {
            contactService.addContactAddress(p.contact.id, CreateUpdateAddressDTO(address = professional.address))
        }
        if (professional.geographicalLocation != null) {
            p.geographicalLocation = professional.geographicalLocation
        }
        if (professional.dailyRate != null) {
            p.dailyRate = professional.dailyRate.toDouble()
        }
        professional.skillsToDelete?.forEach { skillId ->
            deleteProfessionalSkill(p.id, skillId)
        }
        professional.skills?.forEach { skillDTO ->
            val skill = Skill()
            skill.skill = skillDTO.skill
            skillRepository.save(skill)
            logger.info("Skill '${skillDTO.skill}' created and linked to Professional '${p.contact?.name} ${p.contact?.surname}'.")
            p.addSkill(skill)
        }
        professional.notesToDelete?.forEach { noteId ->
            deleteProfessionalNote(p.id, noteId)
        }
        professional.notes?.forEach { noteDTO ->
            addProfessionalNote(p.id, CreateUpdateNoteDTO(note = noteDTO))
        }

        if (professional.jobOfferId != null) {
            val jobOffer = jobOfferRepository.findById(professional.jobOfferId).orElseThrow {
                throw JobOfferNotFoundException("JobOffer with JobOfferId:${professional.jobOfferId} not found.")
            }
            if (professional.employmentState != employmentState.employed) {
                throw ProfessionalStateException("EmploymentState 'Employed' is required in order to link a jobOffer to the professional.")
            }
            if (p.employmentState != employmentState.available) {
                throw ProfessionalStateException("Professional with ProfessionalId:${p.id} is not available for work, thus cannot be linked to the JobOffer with JobOfferId:${jobOffer.id}.")
            }
            if (jobOffer.currentState != jobOfferStatus.candidate_proposal && p.id != jobOffer.professional?.id) {
                throw JobOfferStatusException("A proposal for candidate with ProfessionalId:${p.id} has not been made, thus JobOffer with JobOfferId:${jobOffer.id} cannot be linked to the professional.")
            }
            p.currentJobOffer = jobOffer
            updateJobOffer = true
        }

        if (professional.employmentState != null) {
            when (p.employmentState) {
                employmentState.not_available -> if (professional.employmentState != employmentState.available) {
                    throw ProfessionalStateException("Invalid employmentState transition (from 'not_available' only 'available' is possible).")
                }
                employmentState.employed -> if (professional.employmentState != employmentState.available) {
                    throw ProfessionalStateException("Invalid employmentState transition (from 'employed' only 'available' is possible).")
                } else {
                    p.addJobOffer(p.currentJobOffer!!)
                    p.currentJobOffer = null
                }
                employmentState.available -> if (professional.employmentState == employmentState.available) {
                    throw ProfessionalStateException("Invalid employmentState transition (not possible to switch to the same employmentState).")
                } else if (professional.employmentState == employmentState.employed && p.currentJobOffer == null) {
                    throw ProfessionalStateException("Professional with ProfessionalId:${p.id} is not linked to any JobOffer.")
                }
            }
            p.employmentState = professional.employmentState
            if (updateJobOffer) {
                // Update JobOffer status to 'candidate_accepted'
                jobOfferService.updateJobOfferStatus(p.currentJobOffer!!.id, UpdateJobOfferStatusDTO(targetStatus = jobOfferStatus.consolidated, note = null, professionalId = p.id))
            }
        }

        professionalRepository.save(p)
        logger.info("Professional ${p.contact.name} updated.")
        return p.toDTO()
    }


    // ----- Add a note for a professional -----
    override fun addProfessionalNote(
        professionalId: Long,
        note: CreateUpdateNoteDTO
    ): NoteDTO {
        if (professionalId < 0) {
            throw IllegalIdException("Invalid professionalId Parameter.")
        }
        val professional = professionalRepository.findById(professionalId).orElseThrow{
            throw ProfessionalNotFoundException("Professional with ProfessionalId:$professionalId not found")
        }

        var newNote = Note()
        newNote.note = note.note
        newNote = noteRepository.save(newNote)
        logger.info("Note '$note' created and linked to Professional.")
        professional.addNote(newNote)

        val updatedProfessional = professionalRepository.save(professional).toDTO()
        logger.info("Note '$note' added to Professional ${updatedProfessional.name}.")
        return newNote.toDTO()
    }

    override fun deleteProfessionalNote(professionalId: Long, noteId: Long): ProfessionalDTO {
        if (professionalId < 0 && noteId < 0) {
            throw IllegalIdException("Invalid professionalId and noteId Parameter.")
        } else if (professionalId < 0) {
            throw IllegalIdException("Invalid professionalId Parameter.")
        } else if (noteId < 0) {
            throw IllegalIdException("Invalid noteId Parameter.")
        }
        val professional = professionalRepository.findById(professionalId).orElseThrow{
            throw ProfessionalNotFoundException("Professional with ProfessionalId:$professionalId not found")
        }
        val n = noteRepository.findById(noteId).orElseThrow{
            throw NoteNotFoundException("Note with NoteId:$noteId not found")
        }
        if (n.professional != professional) {
            throw NoDeletePermissionException("Note with NoteId:${noteId} does not belong to this professional.")
        }
        if (n.state == contactInfoState.deleted) {
            throw NoteAlreadyDeletedException("Note with NoteId:${noteId} already deleted.")
        }

        n.state = contactInfoState.deleted
        noteRepository.save(n)
        val updatedProfessional = professionalRepository.save(professional).toDTO()
        logger.info("Professional note ${n.note} of Professional ${professional.contact.name} marked as deleted.")
        return updatedProfessional

    }

    override fun deleteProfessionalSkill(professionalId: Long, skillId: Long): ProfessionalDTO {
        if (professionalId < 0 && skillId < 0) {
            throw IllegalIdException("Invalid professionalId and skillId Parameter.")
        } else if (professionalId < 0) {
            throw IllegalIdException("Invalid professionalId Parameter.")
        } else if (skillId < 0) {
            throw IllegalIdException("Invalid skillId Parameter.")
        }
        val professional = professionalRepository.findById(professionalId).orElseThrow{
            throw ProfessionalNotFoundException("Professional with ProfessionalId:$professionalId not found")
        }
        val s = skillRepository.findById(skillId).orElseThrow{
            throw SkillNotFoundException("Skill with SkillId:$skillId not found")
        }
        if (s.professional != professional) {
            throw NoDeletePermissionException("Skill with SkillId:${skillId} does not belong to this professional.")
        }
        if (s.state == contactInfoState.deleted) {
            throw SkillAlreadyDeletedException("Skill with SkillId:${skillId} already deleted.")
        }
        s.state = contactInfoState.deleted
        skillRepository.save(s)
        val updatedProfessional = professionalRepository.save(professional).toDTO()
        logger.info("Professional skill ${s.skill} of Professional ${professional.contact.name} marked as deleted.")
        return updatedProfessional

    }
}
