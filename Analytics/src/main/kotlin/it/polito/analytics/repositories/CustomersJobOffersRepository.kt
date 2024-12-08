package it.polito.analytics.repositories
import it.polito.analytics.entities.CompositeCustomersJobOffersKey
import it.polito.analytics.entities.CustomersJobOffers
import it.polito.analytics.entities.customerJobOfferState
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface CustomersJobOffersRepository: R2dbcRepository<CustomersJobOffers, CompositeCustomersJobOffersKey> {

    @Modifying
    @Query("INSERT INTO customers_job_offers (customer_id, job_offer_id, final_status_customer) VALUES (:customerId, :jobOfferId, :finalStatusCustomer)")
    fun insert(
        @Param("customerId") customerId: String,
        @Param("jobOfferId") jobOfferId: Long,
        @Param("finalStatusCustomer") finalStatusCustomer: customerJobOfferState
    ): Mono<Void>

    @Modifying
    @Query("UPDATE customers_job_offers SET final_status_customer=:finalStatusCustomer WHERE (customer_id=:customerId AND job_offer_id=:jobOfferId)")
    fun update(
        @Param("customerId") customerId: String,
        @Param("jobOfferId") jobOfferId: Long,
        @Param("finalStatusCustomer") finalStatusCustomer: customerJobOfferState
    ): Mono<Void>

}