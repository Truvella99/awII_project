package it.polito.customerrelationshipmanagement.services

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.employmentState

interface ProfessionalService {
    // gaetano
    fun addProfessionalNote(professionalId: String, note: CreateUpdateNoteDTO): NoteDTO
    fun deleteProfessionalNote(professionalId: String, noteId: Long): ProfessionalDTO
    fun deleteProfessionalSkill(professionalId: String, skillId: Long): ProfessionalDTO

    fun listAllProfessionals(
        pageNumber: Int?,
        limit: Int?,
        skills: List<String>?,
        latitude: Double?,
        longitude: Double?,
        employmentState: employmentState?
    ): List<ProfessionalDTO>
    fun listProfessionalsDistance(
        skills: List<String>?,
        latitude: Double,
        longitude: Double,
        km: Double): List<ProfessionalDTO>
    fun createProfessional(professional: CreateUpdateProfessionalDTO): ProfessionalDTO
    // ale costa
    fun findProfessionalById(professionalId: String): ProfessionalDTO
    fun updateProfessional(professionalId: String,professional: CreateUpdateProfessionalDTO): ProfessionalDTO

    fun findProfessionals(filter: String): List<ProfessionalDTO>
}