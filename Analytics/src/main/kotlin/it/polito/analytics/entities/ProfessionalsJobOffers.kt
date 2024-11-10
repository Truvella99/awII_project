package it.polito.analytics.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable

data class CompositeProfessionalsJobOffersKey(
    val jobOfferId: Long,
    val professionalId: String
) : Serializable

@Table(name = "professionals_job_offers")
data class ProfessionalsJobOffers(
    @Id
    var id: CompositeProfessionalsJobOffersKey
)
