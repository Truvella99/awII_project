package it.polito.customerrelationshipmanagement.services

import it.polito.customerrelationshipmanagement.controllers.JobOfferController
import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.*
import it.polito.customerrelationshipmanagement.exceptions.*
import it.polito.customerrelationshipmanagement.repositories.*
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class JobOfferServiceImpl(
    private val customerRepository: CustomerRepository,
    private val professionalRepository: ProfessionalRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val skillRepository: SkillRepository,
    private val jobOfferHistoryRepository: JobOfferHistoryRepository
) : JobOfferService {
    // logger to log messages in the APIs
    private val logger = LoggerFactory.getLogger(JobOfferController::class.java)

    override fun deleteJobOfferSkill(jobOfferId: Long, skillId: Long): JobOfferDTO {
        if (jobOfferId < 0 && skillId < 0) {
            throw IllegalIdException("Invalid jobOfferId and skillId Parameter.")
        } else if (jobOfferId < 0) {
            throw IllegalIdException("Invalid jobOfferId Parameter.")
        } else if (skillId < 0) {
            throw IllegalIdException("Invalid skillId Parameter.")
        }
        val jobOffer = jobOfferRepository.findById(jobOfferId).orElseThrow{
            throw JobOfferNotFoundException("JobOffer with JobOfferId:$jobOfferId not found.")
        }
        val s = skillRepository.findById(skillId).orElseThrow{
            throw SkillNotFoundException("Skill with SkillId:$skillId not found.")
        }
        if (s.jobOffer != jobOffer) {
            throw NoDeletePermissionException("Skill with SkillId:${skillId} does not belong to this job offer.")
        }
        if (s.state == contactInfoState.deleted) {
            throw SkillAlreadyDeletedException("Skill with SkillId:${skillId} already deleted.")
        }
        s.state = contactInfoState.deleted
        skillRepository.save(s)
        val updatedJobOffer = jobOfferRepository.save(jobOffer).toDTO()
        logger.info("JobOffer skill ${s.skill} of JobOffer ${jobOffer.name} marked as deleted.")
        return updatedJobOffer

    }

    // ----- Create a new job offer -----
    override fun createJobOffer(
        jobOfferDTO: CreateUpdateJobOfferDTO
    ): JobOfferDTO {
        val jobOffer = JobOffer()
        if (jobOfferDTO.customerId == null) {
            throw IllegalIdException("Customer ID is required to Link The Job Offer.")
        }
        var customer = customerRepository.findById(jobOfferDTO.customerId).orElseThrow {
            throw CustomerNotFoundException("Customer with CustomerId:${jobOfferDTO.customerId} not found")
        }
        jobOffer.name = jobOfferDTO.name
        jobOffer.description = jobOfferDTO.description
        // initial status created
        jobOffer.currentState = jobOfferStatus.created
        jobOffer.currentStateNote = jobOfferDTO.currentStateNote
        jobOffer.duration = jobOfferDTO.duration
        jobOffer.profitMargin = jobOfferDTO.profitMargin
        //jobOffer.customer = customer
        // link the job offer to the customer
        customer.addJobOffer(jobOffer)
        //customer = customerRepository.save(customer)
        // initially not bound to any professional, so leave it null
        
        // create the skills
        for (skillDTO in jobOfferDTO.skills) {
            // create the skill, then link to each others and save
            val s = Skill()
            s.skill = skillDTO.skill
            jobOffer.addSkill(s)
            skillRepository.save(s)
        }

        logger.info("jobOffer ${jobOfferDTO.name} created.")
        return jobOfferRepository.save(jobOffer).toDTO()
    }


    // ----- Get a job offer by its ID -----
    override fun findJobOfferById(
        jobOfferId: Long
    ): JobOfferDTO {
        if (jobOfferId < 0) {
            throw IllegalIdException("Invalid jobOfferId Parameter.")
        }
        
        val jobOffer = jobOfferRepository.findById(jobOfferId).orElseThrow{
            throw JobOfferNotFoundException("Job Offer with jobOfferId:${jobOfferId} not found.")
        }
        
        return jobOffer.toDTO()
    }


    // ----- Update an existing job offer -----
    override fun updateJobOffer(
        jobOfferId: Long,
        jobOfferDTO: CreateUpdateJobOfferDTO
    ): JobOfferDTO {
        if (jobOfferId < 0) {
            throw IllegalIdException("Invalid jobOfferId Parameter.")
        }
        if (jobOfferDTO.customerId == null) {
            throw IllegalIdException("Customer ID is required to Link The Job Offer.")
        }
        val jobOffer = jobOfferRepository.findById(jobOfferId).orElseThrow{
            throw JobOfferNotFoundException("Job Offer with JobOfferId:${jobOfferId} not found.")
        }
        val customer = customerRepository.findById(jobOfferDTO.customerId).orElseThrow {
            throw CustomerNotFoundException("Customer with CustomerId:${jobOfferDTO.customerId} not found.")
        }
        // update the job offer
        jobOffer.name = jobOfferDTO.name
        jobOffer.description = jobOfferDTO.description
        jobOffer.currentStateNote = jobOfferDTO.currentStateNote
        jobOffer.duration = jobOfferDTO.duration
        jobOffer.profitMargin = jobOfferDTO.profitMargin
        //jobOffer.customer = customer
        customer.addJobOffer(jobOffer)
        
        // delete all previous skills (mark as deleted)
        /*for (skill in jobOffer.skills) {
            var s = skill
            s.state = contactInfoState.deleted
            skillRepository.save(s)
        }
        logger.info("jobOffer ${jobOfferDTO.name} old skills deleted.")
        */
        jobOfferDTO.skillsToDelete?.forEach { skillId ->
            deleteJobOfferSkill(jobOfferId, skillId)
        }
        // add the new ones
        for (skillDTO in jobOfferDTO.skills) {
            // create the skill, then link to each others and save
            val s = Skill()
            s.skill = skillDTO.skill
            jobOffer.addSkill(s)
            skillRepository.save(s)
        }
        
        logger.info("jobOffer ${jobOfferDTO.name} new skills added and successfully updated.")
        return jobOfferRepository.save(jobOffer).toDTO()
    }


    // ----- Get the history of the job offer -----
    override fun listJobOfferHistory(
        jobOfferId: Long
    ): List<JobOfferHistoryDTO> {
        if (jobOfferId < 0) {
            throw IllegalIdException("Invalid jobOfferId Parameter.")
        }
        
        val jobOffer = jobOfferRepository.findById(jobOfferId).orElseThrow {
            throw JobOfferNotFoundException("Job Offer with JobOfferId:${jobOfferId} not found.")
        }
        
        return jobOffer.histories.map { it.toDTO() }
    }


    // ----- Get the customer for open job offers -----
    override fun getCustomerOpenJobOffers(
        pageNumber: Int?, 
        limit: Int?, 
        customerId: Long
    ): List<JobOfferDTO> {
        if (customerId < 0) {
            throw IllegalIdException("Invalid customerId Parameter.")
        }
        if (pageNumber != null && limit != null) {
            val customer = customerRepository.findById(customerId).orElseThrow {
                throw CustomerNotFoundException("Customer with CustomerId:${customerId} not found.")
            }
            // job offers related to customer having a status different from aborted/consolidated/done
            val excludedStates = listOf(jobOfferStatus.aborted,jobOfferStatus.consolidated,jobOfferStatus.done)
            val p = PageRequest.of(pageNumber, limit)
            return jobOfferRepository.findByCustomerAndCurrentStateNotIn(customer,excludedStates,p).map { it.toDTO() }
        } else if (pageNumber == null && limit == null) {
            val customer = customerRepository.findById(customerId).orElseThrow {
                throw CustomerNotFoundException("Customer with CustomerId:${customerId} not found.")
            }
            // job offers related to customer having a status different from aborted/consolidated/done
            val excludedStates = listOf(jobOfferStatus.aborted,jobOfferStatus.consolidated,jobOfferStatus.done)
            return jobOfferRepository.findByCustomerAndCurrentStateNotIn(customer,excludedStates,null).map { it.toDTO() }
        } else {
            throw IllegalPageNumberLimitException("PageNumber and limit must be both provided or both not provided.")
        }
    }


    // ----- Get the professional for accepted job offers -----
    override fun getProfessionalAcceptedJobOffers(
        pageNumber: Int?,
        limit: Int?,
        professionalId: Long
    ): List<JobOfferDTO> {
        if (professionalId < 0) {
            throw IllegalIdException("Invalid professionalId Parameter.")
        }
        if (pageNumber != null && limit != null) {
            val professional = professionalRepository.findById(professionalId).orElseThrow {
                throw ProfessionalNotFoundException("Professional with ProfessionalId:${professionalId} not found.")
            }
            // job offers related to professional having status consolidated/done
            val includedStates = listOf(jobOfferStatus.consolidated,jobOfferStatus.done)
            val p = PageRequest.of(pageNumber, limit)
            return jobOfferRepository.findJobOffersByProfessionalAndCurrentStateIn(professional,professional.jobOffers.toList(),includedStates,p).map { it.toDTO() }
        } else if (pageNumber == null && limit == null) {
            val professional = professionalRepository.findById(professionalId).orElseThrow {
                throw ProfessionalNotFoundException("Professional with ProfessionalId:${professionalId} not found.")
            }
            // job offers related to professional having status consolidated/done
            val includedStates = listOf(jobOfferStatus.consolidated,jobOfferStatus.done)
            return jobOfferRepository.findJobOffersByProfessionalAndCurrentStateIn(professional,professional.jobOffers.toList(),includedStates,null).map { it.toDTO() }
        } else {
            throw IllegalPageNumberLimitException("PageNumber and limit must be both provided or both not provided.")
        }
    }


    // ----- Get the list of aborted job offers -----
    override fun getAbortedJobOffers(
        pageNumber: Int?,
        limit: Int?,
        customerId: Long?,
        professionalId: Long?
    ): List<JobOfferDTO> {
        if ((customerId != null && customerId < 0) && (professionalId != null && professionalId < 0)) {
            throw IllegalIdException("Invalid customerId and professionalId Parameter.")
        } else if (customerId != null && customerId < 0) {
            throw IllegalIdException("Invalid customerId Parameter.")
        } else if (professionalId != null && professionalId < 0) {
            throw IllegalIdException("Invalid professionalId Parameter.")
        }
        
        if (pageNumber != null && limit != null) {
            if (customerId != null && professionalId != null) {
                val customer = customerRepository.findById(customerId).orElseThrow {
                    throw CustomerNotFoundException("Customer with CustomerId:${customerId} not found.")
                }
                val professional = professionalRepository.findById(professionalId).orElseThrow {
                    throw ProfessionalNotFoundException("Professional with ProfessionalId:${professionalId} not found.")
                }
                val p = PageRequest.of(pageNumber, limit)
                return jobOfferRepository.findByCurrentStateAndCustomerOrProfessional(jobOfferStatus.aborted,customer,professional,p).map { it.toDTO() }
            } else if (customerId != null) {
                val customer = customerRepository.findById(customerId).orElseThrow {
                    throw CustomerNotFoundException("Customer with CustomerId:${customerId} not found.")
                }
                val p = PageRequest.of(pageNumber, limit)
                return jobOfferRepository.findByCurrentStateAndCustomerOrProfessional(jobOfferStatus.aborted, customer,null,p).map { it.toDTO() }
            } else if (professionalId != null) {
                val professional = professionalRepository.findById(professionalId).orElseThrow {
                    throw ProfessionalNotFoundException("Professional with ProfessionalId:${professionalId} not found.")
                }
                val p = PageRequest.of(pageNumber, limit)
                return jobOfferRepository.findByCurrentStateAndCustomerOrProfessional(jobOfferStatus.aborted, null,professional,p).map { it.toDTO() }
            }  else {
                val p = PageRequest.of(pageNumber, limit)
                return jobOfferRepository.findByCurrentStateAndCustomerOrProfessional(jobOfferStatus.aborted, null,null,p).map { it.toDTO() }
            }
        } else if (pageNumber == null && limit == null) {
            if (customerId != null && professionalId != null) {
                val customer = customerRepository.findById(customerId).orElseThrow {
                    throw CustomerNotFoundException("Customer with CustomerId:${customerId} not found.")
                }
                val professional = professionalRepository.findById(professionalId).orElseThrow {
                    throw ProfessionalNotFoundException("Professional with ProfessionalId:${professionalId} not found.")
                }
                return jobOfferRepository.findByCurrentStateAndCustomerOrProfessional(jobOfferStatus.aborted, customer,professional,null).map { it.toDTO() }
            } else if (customerId != null) {
                val customer = customerRepository.findById(customerId).orElseThrow {
                    throw CustomerNotFoundException("Customer with CustomerId:${customerId} not found.")
                }
                return jobOfferRepository.findByCurrentStateAndCustomerOrProfessional(jobOfferStatus.aborted, customer,null,null).map { it.toDTO() }
            } else if (professionalId != null) {
                val professional = professionalRepository.findById(professionalId).orElseThrow {
                    throw ProfessionalNotFoundException("Professional with ProfessionalId:${professionalId} not found.")
                }
                return jobOfferRepository.findByCurrentStateAndCustomerOrProfessional(jobOfferStatus.aborted, null,professional,null).map { it.toDTO() }
            }  else {
                return jobOfferRepository.findByCurrentStateAndCustomerOrProfessional(jobOfferStatus.aborted, null,null,null).map { it.toDTO() }
            }
        } else {
            throw IllegalPageNumberLimitException("PageNumber and limit must be both provided or both not provided.")
        }
    }


    // ----- Update the status of a job offer -----
    override fun updateJobOfferStatus(
        jobOfferId: Long,
        data: UpdateJobOfferStatusDTO
    ): JobOfferDTO {
        if (jobOfferId < 0) {
            throw IllegalIdException("Invalid jobOfferId Parameter.")
        }
        val jobOffer = jobOfferRepository.findById(jobOfferId).orElseThrow{
            throw JobOfferNotFoundException("JobOffer with JobOfferId:$jobOfferId not found.")
        }

        when (jobOffer.currentState) {
            jobOfferStatus.created -> if (data.targetStatus != jobOfferStatus.aborted && data.targetStatus != jobOfferStatus.selection_phase) {
                throw JobOfferStatusException("Invalid jobOffer status transition (from 'created' only 'selection_phase/aborted' are possible).")
            }

            jobOfferStatus.selection_phase -> if (data.targetStatus != jobOfferStatus.aborted && data.targetStatus != jobOfferStatus.candidate_proposal) {
                throw JobOfferStatusException("Invalid jobOffer status transition (from 'selection_phase' only 'candidate_proposal/aborted' are possible).")
            } else if (data.targetStatus == jobOfferStatus.candidate_proposal && data.professionalsId.isEmpty()) {
                throw JobOfferStatusException("ProfessionalId is required for this status transition.")
            } else if (data.targetStatus == jobOfferStatus.candidate_proposal && data.professionalsId.isNotEmpty()) {
                data.professionalsId.forEach { professionalId ->
                    val professional = professionalRepository.findById(professionalId).orElseThrow{
                        throw ProfessionalNotFoundException("Professional with ProfessionalId:${data.professionalsId} not found.")
                    }
                    // not available is not added
                    if (professional.employmentState == employmentState.available) {
                        jobOffer.addCandidateProfessional(professional)
                    }
                }
            }

            jobOfferStatus.candidate_proposal -> if (data.targetStatus != jobOfferStatus.aborted && data.targetStatus != jobOfferStatus.consolidated && data.targetStatus != jobOfferStatus.selection_phase) {
                throw JobOfferStatusException("Invalid jobOffer status transition (from 'candidate_proposal' only 'consolidated/selection_phase/aborted' are possible).")
            } else if (data.targetStatus == jobOfferStatus.consolidated && data.consolidatedProfessionalId != null) {
                val pId = data.consolidatedProfessionalId
                val consolidatedProfessional = professionalRepository.findById(pId).orElseThrow{
                    throw ProfessionalNotFoundException("Professional with ProfessionalId:${data.professionalsId} not found.")
                }
                if(consolidatedProfessional.employmentState == employmentState.not_available) {
                    throw ProfessionalException("Professional with ProfessionalId:${pId} is not available for work anymore.")
                    /*val history = JobOffersHistory()
                    jobOffer.professional?.addJobOffer(jobOffer)
                    jobOffer.professional?.currentJobOffer = null
                    jobOffer.currentState = jobOfferStatus.selection_phase
                    history.state = jobOfferStatus.selection_phase
                    history.note = data.note
                    history.date = Date()
                    val savedHistory = jobOfferHistoryRepository.save(history)
                    logger.info("History with historyId ${savedHistory.id} saved.")
                    jobOffer.addHistory(history)
                    logger.info("Professional with ProfessionalId:${pId} is not available for work anymore, thus the JobOffer returns to the selection phase.")*/
                }
                //return jobOfferRepository.save(jobOffer).toDTO()
            } else if (data.targetStatus == jobOfferStatus.aborted) {
                // flush the professional candidates
                jobOffer.abortedProfessionals.addAll(jobOffer.candidateProfessionals)
                jobOffer.candidateProfessionals.clear()
            } else {
                // Update consolidatedProfessional state to employed
                val pId = data.consolidatedProfessionalId
                if (pId != null) {
                    val consolidatedProfessional = professionalRepository.findById(pId).orElseThrow{
                        throw ProfessionalNotFoundException("Professional with ProfessionalId:${data.professionalsId} not found.")
                    }
                    consolidatedProfessional.employmentState = employmentState.employed
                    consolidatedProfessional.currentJobOffer = jobOffer
                    jobOffer.professional = consolidatedProfessional
                    jobOffer.candidateProfessionals.remove(consolidatedProfessional)
                    jobOffer.abortedProfessionals.addAll(jobOffer.candidateProfessionals)
                    jobOffer.candidateProfessionals.clear()
                }
            }


            jobOfferStatus.consolidated -> /*
            if (jobOffer.professional?.employmentState != employmentState.employed) {
                val pId = jobOffer.professional?.id
                val history = JobOffersHistory()
                jobOffer.addAbortedProfessional(jobOffer.professional!!)
                //jobOffer.professional?.addJobOffer(jobOffer)
                jobOffer.professional?.currentJobOffer = null
                jobOffer.currentState = jobOfferStatus.selection_phase
                history.state = jobOfferStatus.selection_phase
                history.note = data.note
                history.date = Date()
                val savedHistory = jobOfferHistoryRepository.save(history)
                logger.info("History with historyId ${savedHistory.id} saved.")
                jobOffer.addHistory(history)
                logger.info("Professional with ProfessionalId:${pId} is not working anymore on this job, thus the JobOffer returns to the selection phase.")
                return jobOfferRepository.save(jobOffer).toDTO()
            } else */if (data.targetStatus != jobOfferStatus.aborted && data.targetStatus != jobOfferStatus.done) {
                throw JobOfferStatusException("Invalid jobOffer status transition (from 'consolidated' only 'done/aborted' are possible).")
            } else if (data.targetStatus == jobOfferStatus.aborted){
                jobOffer.professional?.employmentState = employmentState.available
                jobOffer.addAbortedProfessional(jobOffer.professional!!)
                //jobOffer.professional?.addJobOffer(jobOffer)
                jobOffer.professional?.currentJobOffer = null
            } else {
                // done status all correct
                jobOffer.professional?.employmentState = employmentState.available
                //jobOffer.addAbortedProfessional(jobOffer.professional!!)
                jobOffer.professional?.addJobOffer(jobOffer)
                jobOffer.professional?.currentJobOffer = null
            }

            jobOfferStatus.done -> if (data.targetStatus != jobOfferStatus.selection_phase) {
                throw JobOfferStatusException("Invalid jobOffer status transition (from 'done' only 'selection_phase' is possible).")
            }

            jobOfferStatus.aborted -> throw JobOfferStatusException("Invalid jobOffer status transition (from 'aborted' the status cannot change anymore).")
        }

        if (data.note != null) {
            jobOffer.currentStateNote = data.note
        }

        //Update status
        jobOffer.currentState = data.targetStatus

        //Add history
        val history = JobOffersHistory()
        history.state = data.targetStatus
        history.note = data.note
        history.date = Date()
        
        val savedHistory = jobOfferHistoryRepository.save(history)
        logger.info("History with historyId ${savedHistory.id} saved.")
        jobOffer.addHistory(history)
        logger.info("JobOffer ${jobOffer.name} status updated.")
        return jobOfferRepository.save(jobOffer).toDTO()
    }


    // ----- Get the value of a job offer -----
    override fun getJobOfferValue(jobOfferId: Long): Number {
        if (jobOfferId < 0) {
            throw IllegalIdException("Invalid jobOfferId Parameter.")
        }
        val jobOffer = jobOfferRepository.findById(jobOfferId).orElseThrow{
            throw JobOfferNotFoundException("JobOffer with JobOfferId:$jobOfferId not found.")
        }
        if (jobOffer.professional == null) {
            throw JobOfferStatusException("JobOffer with JobOfferId:$jobOfferId is not bound to a professional.")
        }

        return (jobOffer.duration.toDouble() * jobOffer.professional!!.dailyRate.toDouble() * jobOffer.profitMargin.toDouble())
    }
}