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
}