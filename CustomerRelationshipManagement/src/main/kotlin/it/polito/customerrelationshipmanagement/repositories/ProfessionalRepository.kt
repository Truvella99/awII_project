package it.polito.customerrelationshipmanagement.repositories

import it.polito.customerrelationshipmanagement.entities.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.RequestParam

@Repository
interface ProfessionalRepository: JpaRepository<Professional,String> {


    @Query("SELECT p FROM Professional p " +
            "LEFT JOIN p.skills s " +
            "WHERE (:employmentState IS NULL OR p.employmentState = :employmentState) " +
            "AND (:skills IS NULL OR s.skill IN :skills) " +
            "AND (:geographicalLocation IS NULL OR (p.geographicalLocation = :geographicalLocation))")
    fun findBySkillsOrLocationOrEmploymentState(@Param("skills") skills: List<String>?, @Param("geographicalLocation") geographicalLocation: Pair<Double, Double>?, @Param("employmentState") employmentState: employmentState?, p: PageRequest?): List<Professional>


    @Query("SELECT p FROM Professional p " +
            "LEFT JOIN p.skills s " +
            "WHERE (:skills IS NULL OR s.skill IN :skills)")
    fun findBySkills(@Param("skills") skills: List<String>?): List<Professional>


    @Query("SELECT p FROM Professional p " +
            "LEFT JOIN p.skills s " +
            "LEFT JOIN p.candidateJobOffers cjs " +
            "LEFT JOIN p.abortedJobOffers ajs " +
            "LEFT JOIN p.currentJobOffer cnj " +
            "LEFT JOIN p.jobOffers cmj " +
            "WHERE (:skills IS NULL AND :employmentState IS NULL AND :geographicalLocation IS NULL " +
            "AND :candidateJobOffers IS NULL AND :abortedJobOffers IS NULL AND :consolidatedJobOffers IS NULL " +
            "AND :completedJobOffers IS NULL) " +
            "OR (" +
            "(:skills IS NOT NULL AND s.skill IN :skills) " +
            "OR (:employmentState IS NOT NULL AND p.employmentState = :employmentState) " +
            "OR (:geographicalLocation IS NOT NULL AND p.geographicalLocation = :geographicalLocation) " +
            "OR (:candidateJobOffers IS NOT NULL AND cjs.id IN :candidateJobOffers) " +
            "OR (:abortedJobOffers IS NOT NULL AND ajs.id IN :abortedJobOffers) " +
            "OR (:consolidatedJobOffers IS NOT NULL AND cnj.id IN :consolidatedJobOffers) " +
            "OR (:completedJobOffers IS NOT NULL AND cmj.id IN :completedJobOffers))"
    )
    fun filterHome(
        @Param("skills") skills: List<String>?,
        @Param("geographicalLocation") geographicalLocation: Pair<Double, Double>?,
        @Param("employmentState") employmentState: employmentState?,
        p: PageRequest?,
        @Param("candidateJobOffers") candidateJobOffers: List<String>?,
        @Param("abortedJobOffers") abortedJobOffers: List<String>?,
        @Param("consolidatedJobOffers") consolidatedJobOffers: List<String>?,
        @Param("completedJobOffers") completedJobOffers: List<String>?
    ): List<Professional>
}
