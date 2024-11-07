package it.polito.analytics.repositories

import it.polito.analytics.entities.JobOffer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface JobOfferRepository: JpaRepository<JobOffer, Long> {
    @Query("SELECT COUNT(*) FROM JobOffer j JOIN j.professionals p WHERE p.id = :professionalId AND j.finalStatus = 0")
    fun computeAllCompleted(@Param("professionalId") professionalId: String): Long

    @Query("SELECT COUNT(*) FROM JobOffer j JOIN j.professionals p WHERE p.id = :professionalId AND j.finalStatus = 1")
    fun computeAllAborted(@Param("professionalId") professionalId: String): Long
}