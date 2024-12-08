package it.polito.analytics.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import org.springframework.data.annotation.Transient;

data class CompositeProfessionalsJobOffersKey(
    val jobOfferId: Long,
    val professionalId: String
) : Serializable

@Table(name = "professionals_job_offers")
data class ProfessionalsJobOffers(
    @Id
    @Transient
    var id: CompositeProfessionalsJobOffersKey,
    var finalStatusProfessional: professionalJobOfferState
)
