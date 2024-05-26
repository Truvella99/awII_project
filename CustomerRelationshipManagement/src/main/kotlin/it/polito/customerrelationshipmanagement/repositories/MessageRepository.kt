package it.polito.customerrelationshipmanagement.repositories

import it.polito.customerrelationshipmanagement.entities.Message
import it.polito.customerrelationshipmanagement.entities.state
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository: JpaRepository<Message, Long> {
    fun findByCurrentState(currentState: state, p: PageRequest): List<Message>
    fun findByCurrentState(currentState: state, sort: Sort): List<Message>
    fun findByCurrentState(currentState: state): List<Message>
}


