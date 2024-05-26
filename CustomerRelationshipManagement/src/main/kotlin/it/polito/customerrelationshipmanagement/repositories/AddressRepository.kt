package it.polito.customerrelationshipmanagement.repositories

import it.polito.customerrelationshipmanagement.entities.Address
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AddressRepository: JpaRepository<Address,Long> {
    fun findByAddress(address: String): List<Address>
}