package it.polito.customerrelationshipmanagement.repositories

import it.polito.customerrelationshipmanagement.entities.Address
import it.polito.customerrelationshipmanagement.entities.Customer
import it.polito.customerrelationshipmanagement.entities.Professional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository: JpaRepository<Customer,String> {

    @Query("SELECT c FROM Customer c " +
            "LEFT JOIN c.jobOffers j " +
            "WHERE (:jobOffers IS NULL OR j.id IN :jobOffers)")
    fun findByJobOffers(@Param("jobOffers") jobOffers: List<String>?): List<Customer>
}