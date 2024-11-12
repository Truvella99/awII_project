package it.polito.customerrelationshipmanagement.repositories

import it.polito.customerrelationshipmanagement.entities.Telephone
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TelephoneRepository: JpaRepository<Telephone,Long> {
    fun findByTelephone(telephone: String): List<Telephone>
    @Transactional
    fun deleteByContactId(contactId: Long)
}