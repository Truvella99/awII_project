package it.polito.customerrelationshipmanagement.repositories

import it.polito.customerrelationshipmanagement.entities.History
import it.polito.customerrelationshipmanagement.entities.Message
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HistoryRepository: JpaRepository<History,Long> {
    fun findByMessage(message: Message): List<History>
}