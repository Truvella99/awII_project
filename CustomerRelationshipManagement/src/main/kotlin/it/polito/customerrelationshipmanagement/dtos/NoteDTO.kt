package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.*
import jakarta.validation.constraints.Pattern


data class NoteDTO(
    val id: Long,
    val note: String,
    val customerId: Long?,
    val professionalId: Long?,
    val state : contactInfoState
)

fun Note.toDTO(): NoteDTO =
    NoteDTO(
        this.id,
        this.note,
        this.customer?.id,
        this.professional?.id,
        this.state
    )

