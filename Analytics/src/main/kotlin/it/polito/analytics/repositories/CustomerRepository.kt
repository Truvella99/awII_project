package it.polito.analytics.repositories

import it.polito.analytics.dtos.CustomerOrProfessionalStatusCount
import it.polito.analytics.entities.Customer
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface CustomerRepository: R2dbcRepository<Customer, String> {
    @Query("""
        SELECT c.id, c.name, c.surname, 
               COUNT(cjo.final_status_customer) as count
        FROM customer c
        JOIN customers_job_offers cjo ON c.id = cjo.customer_id
        JOIN job_offer jo ON cjo.job_offer_id = jo.id
        WHERE cjo.final_status_customer = 0
        GROUP BY c.id, c.name, c.surname
    """)
    fun findCustomerCompletedJobOffers(): Flux<CustomerOrProfessionalStatusCount>

    @Query("""
        SELECT c.id, c.name, c.surname, 
               COUNT(cjo.final_status_customer) as count
        FROM customer c
        JOIN customers_job_offers cjo ON c.id = cjo.customer_id
        JOIN job_offer jo ON cjo.job_offer_id = jo.id
        WHERE cjo.final_status_customer = 1
        GROUP BY c.id, c.name, c.surname
    """)
    fun findCustomerAbortedJobOffers(): Flux<CustomerOrProfessionalStatusCount>

    @Query("""
        SELECT c.id, c.name, c.surname, 
               COUNT(cjo.final_status_customer) as count
        FROM customer c
        JOIN customers_job_offers cjo ON c.id = cjo.customer_id
        JOIN job_offer jo ON cjo.job_offer_id = jo.id
        WHERE cjo.final_status_customer = 2
        GROUP BY c.id, c.name, c.surname
    """)
    fun findCustomerCreatedJobOffers(): Flux<CustomerOrProfessionalStatusCount>
}