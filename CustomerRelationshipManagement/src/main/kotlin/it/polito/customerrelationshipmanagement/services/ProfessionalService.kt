package it.polito.customerrelationshipmanagement.services

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.employmentState
import org.springframework.web.bind.annotation.RequestParam

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
        employmentState: employmentState?,
        candidateJobOffers: List<String>?,
        abortedJobOffers: List<String>?,
        consolidatedJobOffers: List<String>?,
        completedJobOffers: List<String>?
    ): List<ProfessionalDTO>
    fun listProfessionalsDistance(
        skills: List<String>?,
        latitude: Double,
        longitude: Double,
        km: Double,
        candidateJobOffers: List<String>?,
        abortedJobOffers: List<String>?,
        consolidatedJobOffers: List<String>?,
        completedJobOffers: List<String>?
    ): List<ProfessionalDTO>
    fun createProfessional(professional: CreateUpdateProfessionalDTO): ProfessionalDTO
    // ale costa
    fun findProfessionalById(professionalId: String): ProfessionalDTO
    fun getProfessionalsInfo(
        candidateIds: List<String>?,
        abortedIds: List<String>?,
        consolidatedIds: List<String>?,
        completedIds: List<String>?
    ): Map<String, List<Pair<String?, String?>>>
    fun updateProfessional(professionalId: String, professional: CreateUpdateProfessionalDTO): ProfessionalDTO

    fun findProfessionals(filter: String): List<ProfessionalDTO>
}