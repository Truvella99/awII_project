package it.polito.analytics.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

enum class professionalJobOfferState {
    completed,aborted,candidated
}

enum class customerJobOfferState {
    completed,aborted,created
}

@Table(name = "job_offer")
data class JobOffer(
    @Id
    var id: Long,
    var finalStatusCustomer: customerJobOfferState? = null,
    var finalStatusProfessional: professionalJobOfferState? = null
)