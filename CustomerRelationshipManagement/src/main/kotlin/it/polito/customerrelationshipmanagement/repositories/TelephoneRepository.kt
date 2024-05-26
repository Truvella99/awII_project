package it.polito.customerrelationshipmanagement.repositories

import it.polito.customerrelationshipmanagement.entities.Telephone
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TelephoneRepository: JpaRepository<Telephone,Long> {
    fun findByTelephone(telephone: String): List<Telephone>
}