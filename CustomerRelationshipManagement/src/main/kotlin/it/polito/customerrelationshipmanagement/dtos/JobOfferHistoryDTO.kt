package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.*
import jakarta.validation.constraints.Pattern
import java.util.*


data class JobOfferHistoryDTO(
    val id: Long,
    val state: jobOfferStatus,
    val date: Date,
    val note: String?,
    val jobOfferId: Long
)

fun JobOffersHistory.toDTO(): JobOfferHistoryDTO =
    JobOfferHistoryDTO(
        this.id,
        this.state,
        this.date,
        this.note,
        this.jobOffer.id
    )

