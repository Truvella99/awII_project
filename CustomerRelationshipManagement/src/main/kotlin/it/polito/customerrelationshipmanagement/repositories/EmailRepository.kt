package it.polito.customerrelationshipmanagement.repositories

import it.polito.customerrelationshipmanagement.entities.Email
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailRepository: JpaRepository<Email,Long> {
    fun findByEmail(email: String): List<Email>
    @Transactional
    fun deleteByContactId(contactId: Long)
}