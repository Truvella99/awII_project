package it.polito.customerrelationshipmanagement.repositories

import it.polito.customerrelationshipmanagement.entities.Address
import it.polito.customerrelationshipmanagement.entities.Customer
import it.polito.customerrelationshipmanagement.entities.Skill
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SkillRepository: JpaRepository<Skill,Long> {
}