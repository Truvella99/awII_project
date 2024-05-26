package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.*
import jakarta.validation.constraints.Pattern


data class SkillDTO(
    val id: Long,
    val skill: String,
    val state: contactInfoState,
    val jobOfferId: Long?,
    val professionalId: Long?
)

fun Skill.toDTO(): SkillDTO =
    SkillDTO(
        this.id,
        this.skill,
        this.state,
        this.jobOffer?.id,
        this.professional?.id
    )

