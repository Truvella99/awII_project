package it.polito.customerrelationshipmanagement.repositories

import it.polito.customerrelationshipmanagement.entities.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface JobOfferRepository: JpaRepository<JobOffer,Long> {
    fun findByCustomerAndCurrentStateNotIn(
        customer: Customer,
        states: List<jobOfferStatus>,
        p: PageRequest?
    ): List<JobOffer>
    @Query(
        "SELECT DISTINCT jo FROM JobOffer jo " +
                "LEFT JOIN jo.completedProfessional p " +
                "WHERE (jo IN :jobOffers OR p = :professional) " +
                "AND jo.currentState IN :states"
    )
    fun findJobOffersByProfessionalAndCurrentStateIn(
        @Param("professional") professional: Professional?,
        @Param("jobOffers") jobOffers: List<JobOffer>?,
        @Param("states") states: List<jobOfferStatus>,
        pageable: PageRequest?
    ): List<JobOffer>
    @Query("SELECT j FROM JobOffer j WHERE j.currentState = :state AND ((j.customer = :customer OR j.completedProfessional = :professional) OR (:customer is null AND :professional is null))")
    fun findByCurrentStateAndCustomerOrProfessional(
        state: jobOfferStatus = jobOfferStatus.aborted,
        customer: Customer? = null,
        professional: Professional? = null,
        p: PageRequest? = null
    ): List<JobOffer>

    @Query("SELECT j FROM JobOffer j " +
            "LEFT JOIN j.skills s " +
            "WHERE (:skills IS NULL OR s.skill IN :skills)")
    fun findBySkills(@Param("skills") skills: List<String>?): List<JobOffer>

    @Query("SELECT j FROM JobOffer j " +
            "LEFT JOIN j.skills s " +
            "LEFT JOIN j.candidateProfessionals cps " +
            "LEFT JOIN j.abortedProfessionals aps " +
            "LEFT JOIN j.consolidatedProfessional cnp " +
            "LEFT JOIN j.completedProfessional cmp " +
            "WHERE (:skills IS NULL AND :candidateProfessionals IS NULL AND :abortedProfessionals IS NULL " +
            "AND :consolidatedProfessionals IS NULL AND :completedProfessionals IS NULL) " +
            "OR (" +
            "(:skills IS NOT NULL AND s.skill IN :skills) " +
            "OR (:candidateProfessionals IS NOT NULL AND cps.id IN :candidateProfessionals) " +
            "OR (:abortedProfessionals IS NOT NULL AND aps.id IN :abortedProfessionals) " +
            "OR (:consolidatedProfessionals IS NOT NULL AND cnp.id IN :consolidatedProfessionals) " +
            "OR (:completedProfessionals IS NOT NULL AND cmp.id IN :completedProfessionals))"
    )
    fun filterHome(
        @Param("skills") skills: List<String>?,
        @Param("candidateProfessionals") candidateProfessionals: List<String>?,
        @Param("abortedProfessionals") abortedProfessionals: List<String>?,
        @Param("consolidatedProfessionals") consolidatedProfessionals: List<String>?,
        @Param("completedProfessionals") completedProfessionals: List<String>?
    ): List<JobOffer>
}