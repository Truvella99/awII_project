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
        SELECT p.id, p.name, p.surname, 
               COUNT(jo.final_status_professional) as count
        FROM professional p
        JOIN professionals_job_offers pjo ON p.id = pjo.professional_id
        JOIN job_offer jo ON pjo.job_offer_id = jo.id
        WHERE jo.final_status_professional = 0
        GROUP BY p.id, p.name, p.surname
    """)
    fun findProfessionalCompletedJobOffers(): Flux<CustomerOrProfessionalStatusCount>

    @Query("""
        SELECT p.id, p.name, p.surname, 
               COUNT(jo.final_status_professional) as count
        FROM professional p
        JOIN professionals_job_offers pjo ON p.id = pjo.professional_id
        JOIN job_offer jo ON pjo.job_offer_id = jo.id
        WHERE jo.final_status_professional = 1
        GROUP BY p.id, p.name, p.surname
    """)
    fun findProfessionalAbortedJobOffers(): Flux<CustomerOrProfessionalStatusCount>

    @Query("""
        SELECT p.id, p.name, p.surname, 
               COUNT(jo.final_status_professional) as count
        FROM professional p
        JOIN professionals_job_offers pjo ON p.id = pjo.professional_id
        JOIN job_offer jo ON pjo.job_offer_id = jo.id
        WHERE jo.final_status_professional = 2
        GROUP BY p.id, p.name, p.surname
    """)
    fun findProfessionalCandidatedJobOffers(): Flux<CustomerOrProfessionalStatusCount>
}