package it.polito.analytics.repositories

import it.polito.analytics.dtos.CustomerOrProfessionalStatusCount
import it.polito.analytics.entities.Professional
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ProfessionalRepository: R2dbcRepository<Professional, String> {
    @Query("""
        SELECT p.id, p.name, p.surname, COUNT(pjo.professional_id) as count
        FROM professional p
        LEFT JOIN professionals_job_offers pjo ON p.id = pjo.professional_id AND pjo.final_status_professional = 0
        LEFT JOIN job_offer jo ON pjo.job_offer_id = jo.id
        GROUP BY p.id, p.name, p.surname
        ORDER BY p.id
    """)
    fun findProfessionalCompletedJobOffers(): Flux<CustomerOrProfessionalStatusCount>

    @Query("""
        SELECT p.id, p.name, p.surname, COUNT(pjo.professional_id) as count
        FROM professional p
        LEFT JOIN professionals_job_offers pjo ON p.id = pjo.professional_id AND pjo.final_status_professional = 1
        LEFT JOIN job_offer jo ON pjo.job_offer_id = jo.id
        GROUP BY p.id, p.name, p.surname
        ORDER BY p.id
    """)
    fun findProfessionalAbortedJobOffers(): Flux<CustomerOrProfessionalStatusCount>

    @Query("""
        SELECT p.id, p.name, p.surname, COUNT(pjo.professional_id) as count
        FROM professional p
        LEFT JOIN professionals_job_offers pjo ON p.id = pjo.professional_id AND pjo.final_status_professional = 2
        LEFT JOIN job_offer jo ON pjo.job_offer_id = jo.id
        GROUP BY p.id, p.name, p.surname
        ORDER BY p.id
    """)
    fun findProfessionalCandidatedJobOffers(): Flux<CustomerOrProfessionalStatusCount>
}