package it.polito.customerrelationshipmanagement.repositories

import it.polito.customerrelationshipmanagement.entities.OutBox
import org.springframework.data.jpa.repository.JpaRepository

interface OutboxRepository: JpaRepository<OutBox, Long> {
}