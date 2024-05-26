package it.polito.customerrelationshipmanagement.services

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.employmentState

interface ProfessionalService {
    // gaetano
    fun addProfessionalNote(professionalId: Long, note: CreateUpdateNoteDTO): NoteDTO
    fun deleteProfessionalNote(professionalId: Long, noteId: Long): ProfessionalDTO
    fun deleteProfessionalSkill(professionalId: Long, skillId: Long): ProfessionalDTO

    fun listAllProfessionals(
        pageNumber: Int?,
        limit: Int?,
        skills: List<String>?,
        latitude: Double?,
        longitude: Double?,
        employmentState: employmentState?
    ): List<ProfessionalDTO>
    fun createProfessional(professional: CreateUpdateProfessionalDTO): ProfessionalDTO
    // ale costa
    fun findProfessionalById(professionalId: Long): ProfessionalDTO
    fun updateProfessional(professionalId: Long,professional: CreateUpdateProfessionalDTO): ProfessionalDTO
}