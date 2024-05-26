package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.category
import it.polito.customerrelationshipmanagement.entities.employmentState
import jakarta.validation.constraints.Pattern


data class CreateUpdateProfessionalDTO(
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val name: String?,
    @field:Pattern(regexp = NOT_EMPTY_IF_NOT_NULL)
    val surname: String?,
    @field:Pattern(regexp = SSN_CODE)
    val ssncode: String?,
    val category: category?,
    @field:Pattern(regexp = EMAIL)
    val email: String?,
    @field:Pattern(regexp = TELEPHONE)
    val telephone: String?,
    @field:Pattern(regexp = ADDRESS)
    val address: String?,
    val employmentState: employmentState?,
    val geographicalLocation: Pair<Double,Double>?,
    val dailyRate: Number?,
    val skills: List<CreateSkillDTO>?,
    val skillsToDelete: List<Long>? = null,
    val notes: List<String>?,
    val notesToDelete: List<Long>? = null,
    val jobOfferId: Long?
)
