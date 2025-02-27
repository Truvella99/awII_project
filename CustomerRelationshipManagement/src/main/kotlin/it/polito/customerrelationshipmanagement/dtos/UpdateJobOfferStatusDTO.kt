package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.jobOfferStatus

class UpdateJobOfferStatusDTO(
    val targetStatus: jobOfferStatus,
    val note: String?,
    val professionalsId: List<String>,
    val consolidatedProfessionalId: String?
)