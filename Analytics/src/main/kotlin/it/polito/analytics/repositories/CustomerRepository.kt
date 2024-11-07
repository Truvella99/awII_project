package it.polito.analytics.repositories

import it.polito.analytics.entities.CompositeKey
import it.polito.analytics.entities.Customer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository: JpaRepository<Customer, CompositeKey> {
    @Query("SELECT count(*) FROM Customer c WHERE  c.key.id = :customerId AND c.finalStatus = 0")
    fun computeAllCompleted(@Param("customerId") customerId: String): Long

    @Query("SELECT count(*) FROM Customer c WHERE  c.key.id = :customerId AND c.finalStatus = 1")
    fun computeAllAborted(@Param("customerId") customerId: String): Long
}