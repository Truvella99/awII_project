package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.*
import it.polito.customerrelationshipmanagement.entities.state
import java.util.*


data class HistoryDTO(
    val id: Long,
    val state: state,
    val date: Date,
    val comment: String
)

fun History.toDTO(): HistoryDTO =
    HistoryDTO(this.id, this.state, this.date, this.comment)

