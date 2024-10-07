package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.category
import it.polito.customerrelationshipmanagement.entities.Contact
import it.polito.customerrelationshipmanagement.entities.JobOffer
import it.polito.customerrelationshipmanagement.entities.jobOfferStatus
import jakarta.validation.constraints.Pattern


data class JobOfferDTO(
    val id: Long,
    val name: String,
    val description: String,
    val currentState: jobOfferStatus,
    val currentStateNote: String?,
    val duration: Number,
    val value: Number?,
    val profitMargin: Number,
    val customerId: Long,
    val professionalId: Long?,
    val candidateProfessionalsId: List<Long>,
    val abortedProfessionalsId: List<Long>,
    val skills: List<SkillDTO>
)

fun JobOffer.toDTO(): JobOfferDTO =
    JobOfferDTO(
        this.id,
        this.name,
        this.description,
        this.currentState,
        this.currentStateNote,
        this.duration,
        this.value,
        this.profitMargin,
        this.customer.id,
        this.professional?.id,
        this.candidateProfessionals.map { it.id },
        this.abortedProfessionals.map { it.id },
        this.skills.map { it.toDTO() }
    )

