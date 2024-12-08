package it.polito.analytics.entities

import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.annotation.Id

@Table(name = "job_offer")
data class JobOffer(
    @Id
    var id: Long
)