package it.polito.analytics.repositories

import it.polito.analytics.entities.Professional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfessionalRepository: JpaRepository<Professional, String> {
}