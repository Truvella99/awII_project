package it.polito.customerrelationshipmanagement.services

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.category
import it.polito.customerrelationshipmanagement.entities.jobOfferStatus
import org.springframework.security.core.Authentication

interface JobOfferService {
    fun deleteJobOfferSkill(jobOfferId: Long, skillId: Long): JobOfferDTO
    fun createJobOffer(jobOfferDTO: CreateUpdateJobOfferDTO): JobOfferDTO
    fun findJobOfferById(jobOfferId: Long, authentication: Authentication): JobOfferDTO
    fun updateJobOffer(jobOfferId: Long,jobOfferDTO: CreateUpdateJobOfferDTO): JobOfferDTO
    fun listJobOfferHistory(jobOfferId: Long, authentication: Authentication): List<JobOfferHistoryDTO>
    fun getCustomerOpenJobOffers(
        pageNumber: Int?,
        limit: Int?,
        customerId: String,
    ): List<JobOfferDTO>
    fun getProfessionalAcceptedJobOffers(
        pageNumber: Int?,
        limit: Int?,
        professionalId: String,
    ): List<JobOfferDTO>
    fun getAbortedJobOffers(
        pageNumber: Int?,
        limit: Int?,
        customerId: String?,
        professionalId: String?,
    ): List<JobOfferDTO>
    // ale costa
    fun updateJobOfferStatus(jobOfferId: Long, data: UpdateJobOfferStatusDTO): JobOfferDTO
    //Pay attention that value is confirmed only if a job offer is bound to a professional
    fun getJobOfferValue(jobOfferId: Long, authentication: Authentication): Number
    fun getAllJobOffers(
        skills: List<String>?,
        candidateProfessionals: List<String>?,
        abortedProfessionals: List<String>?,
        consolidatedProfessionals: List<String>?,
        completedProfessionals: List<String>?
    ): List<JobOfferDTO>
    fun getOpenJobOffers(
        skills: List<String>?,
        candidateProfessionals: List<String>?,
        abortedProfessionals: List<String>?,
        consolidatedProfessionals: List<String>?,
        completedProfessionals: List<String>?
    ): List<JobOfferDTO>
}