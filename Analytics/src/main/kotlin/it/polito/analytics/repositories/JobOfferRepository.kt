package it.polito.analytics.repositories

import it.polito.analytics.entities.JobOffer
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface JobOfferRepository: R2dbcRepository<JobOffer, Long> {
}