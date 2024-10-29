package it.polito.customerrelationshipmanagement.controllers

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.category
import it.polito.customerrelationshipmanagement.entities.employmentState
import it.polito.customerrelationshipmanagement.entities.jobOfferStatus
import it.polito.customerrelationshipmanagement.services.JobOfferService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
class JobOfferController(private val jobOfferService: JobOfferService){
    /**
     * POST /API/joboffers/
     *
     * Create a Job Offer
     *
    */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/API/joboffers/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun createJobOffer(@RequestBody @Valid createUpdateJobOfferDTO: CreateUpdateJobOfferDTO): JobOfferDTO {
        return jobOfferService.createJobOffer(createUpdateJobOfferDTO)
    }

    /**
     * GET /API/joboffers/{jobOfferId}
     *
     * Get a Job Offer by his Id
     *
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/joboffers/{jobOfferId}")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun getJobOfferById(@PathVariable("jobOfferId") jobOfferId: Long): JobOfferDTO {
        return jobOfferService.findJobOfferById(jobOfferId)
    }

    /**
     * PUT /API/joboffers/{jobOfferId}
     *
     * Update a Job Offer by his Id
     *
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/API/joboffers/{jobOfferId}")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun UpdateJobOfferById(
        @PathVariable("jobOfferId") jobOfferId: Long,
        @RequestBody @Valid createUpdateJobOfferDTO: CreateUpdateJobOfferDTO
    ): JobOfferDTO {
        return jobOfferService.updateJobOffer(jobOfferId, createUpdateJobOfferDTO)
    }

    /**
     * GET /API/joboffers/{jobOfferId}/history
     *
     * Get a Job Offer history by his Id
     *
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/joboffers/{jobOfferId}/history")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun getJobOfferHistoryById(@PathVariable("jobOfferId") jobOfferId: Long): List<JobOfferHistoryDTO> {
        return jobOfferService.listJobOfferHistory(jobOfferId)
    }

    /**
     * GET /API/joboffers/open/{customerId}
     *
     * list all registered job offers
     * related to customer {customerId} having a status different from
     * aborted/consolidated/done. Allow for pagination, and limiting results,
     * using request parameters.
     *
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/joboffers/open/{customerId}")
    @PreAuthorize("isAuthenticated() && (hasRole('manager'))")
    fun getCustomerOpenJobOffers(
        @RequestParam("pageNumber") pageNumber: Int?,
        @RequestParam("limit") limit: Int?,
        @PathVariable("customerId") customerId: String
    ): List<JobOfferDTO> {
        return jobOfferService.getCustomerOpenJobOffers(pageNumber, limit, customerId)
    }

    /**
     * GET /API/joboffers/accepted/{professionalId}
     *
     * list all registered job
     * offers related to professional {professionalId} having status
     * consolidated/done. Allow for pagination, and limiting results, using
     * request parameters.
     *
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/joboffers/accepted/{professionalId}")
    @PreAuthorize("isAuthenticated() && (hasRole('manager'))")
    fun getProfessionalAcceptedJobOffers(
        @RequestParam("pageNumber") pageNumber: Int?,
        @RequestParam("limit") limit: Int?,
        @PathVariable("professionalId") professionalId: String
    ): List<JobOfferDTO> {
        return jobOfferService.getProfessionalAcceptedJobOffers(pageNumber, limit, professionalId)
    }

    /**
     * GET /API/joboffers/aborted/
     *
     * list all registered job offers in the DB.
     * Allow for pagination, limiting results, and filtering by customer or
     * professional, using request parameters.
     *
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/joboffers/aborted/")
    @PreAuthorize("isAuthenticated() && (hasRole('manager'))")
    fun getAbortedJobOffers(
        @RequestParam("pageNumber") pageNumber: Int?,
        @RequestParam("limit") limit: Int?,
        @RequestParam("customerId") customerId: String?,
        @RequestParam("professionalId") professionalId: String?,
    ): List<JobOfferDTO> {
        return jobOfferService.getAbortedJobOffers(pageNumber, limit, customerId, professionalId)
    }
    /**
     * POST /API/jobOffers/{jobOfferId}
     * change the status of a specific Job offer.
     * This endpoint must receive the target status and, if necessary,
     * a note and reference to a professional
    */
    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/API/joboffers/{jobOfferId}")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun updateJobOfferStatus(
        @PathVariable("jobOfferId") jobOfferId: Long,
        @RequestBody data: UpdateJobOfferStatusDTO
    ): JobOfferDTO {
        return jobOfferService.updateJobOfferStatus(jobOfferId, data)
    }
    /**
     * GET /API/jobOffers/{jobOfferId}/value
     * retrieve the value of a specific Job offer.
     * The value is confirmed only if a job offer is bound to a professional
    */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/joboffers/{jobOfferId}/value")
    @PreAuthorize("isAuthenticated() && (hasRole('manager'))")
    fun getJobOfferValue(@PathVariable("jobOfferId") jobOfferId: Long): Number {
        return jobOfferService.getJobOfferValue(jobOfferId)
    }
    /**
     * GET /API/joboffers/
     *
     * get all the jobOffers in the DB. Allow for filtering by skills.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/joboffers/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun getAllJobOffers( @RequestParam("skills") skills: List<String>? ): List<JobOfferDTO> {
        return jobOfferService.getAllJobOffers(skills)
    }
}