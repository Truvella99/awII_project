package it.polito.customerrelationshipmanagement.repositories

import it.polito.customerrelationshipmanagement.entities.*
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ProfessionalRepository: JpaRepository<Professional,Long> {


    @Query("SELECT p FROM Professional p " +
            "LEFT JOIN p.skills s " +
            "WHERE (:employmentState IS NULL OR p.employmentState = :employmentState) " +
            "AND (:skills IS NULL OR s.skill IN :skills) " +
            "AND (:geographicalLocation IS NULL OR (p.geographicalLocation = :geographicalLocation))")
    fun findBySkillsOrLocationOrEmploymentState(@Param("skills") skills: List<String>?, @Param("geographicalLocation") geographicalLocation: Pair<Double, Double>?, @Param("employmentState") employmentState: employmentState?, p: PageRequest?): List<Professional>

}
