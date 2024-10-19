package it.polito.customerrelationshipmanagement.services

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.category
import it.polito.customerrelationshipmanagement.entities.jobOfferStatus

interface JobOfferService {
    fun deleteJobOfferSkill(jobOfferId: Long, skillId: Long): JobOfferDTO
    fun createJobOffer(jobOfferDTO: CreateUpdateJobOfferDTO): JobOfferDTO
    fun findJobOfferById(jobOfferId: Long): JobOfferDTO
    fun updateJobOffer(jobOfferId: Long,jobOfferDTO: CreateUpdateJobOfferDTO): JobOfferDTO
    fun listJobOfferHistory(jobOfferId: Long): List<JobOfferHistoryDTO>
    fun getCustomerOpenJobOffers(
        pageNumber: Int?,
        limit: Int?,
        customerId: Long,
    ): List<JobOfferDTO>
    fun getProfessionalAcceptedJobOffers(
        pageNumber: Int?,
        limit: Int?,
        professionalId: Long,
    ): List<JobOfferDTO>
    fun getAbortedJobOffers(
        pageNumber: Int?,
        limit: Int?,
        customerId: Long?,
        professionalId: Long?,
    ): List<JobOfferDTO>
    // ale costa
    fun updateJobOfferStatus(jobOfferId: Long, data: UpdateJobOfferStatusDTO): JobOfferDTO
    //Pay attention that value is confirmed only if a job offer is bound to a professional
    fun getJobOfferValue(jobOfferId: Long): Number
    fun getAllJobOffers(skills: List<String>?): List<JobOfferDTO>
}