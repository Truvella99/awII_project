package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.OutBox
import it.polito.customerrelationshipmanagement.entities.eventType
import java.time.LocalDateTime
import org.apache.kafka.connect.data.Struct
import java.time.Instant
import java.time.ZoneId

data class OutBoxDTO(
    val id: Long,
    val eventType: eventType,
    val data: String,
    val creationDate: LocalDateTime
)

fun OutBox.toDTO(): OutBoxDTO =
    OutBoxDTO(
        this.id,
        this.eventType,
        this.data,
        this.creationDate
    )

fun Struct.toOutBoxDTO(): OutBoxDTO {
    val afterStruct = this.getStruct("after")
    val id = afterStruct.getInt64("id")
    val eventType = eventType.fromType(afterStruct.getInt16("event_type"))
    val data = afterStruct.getString("data")
    val creationDateMillis = afterStruct.getInt64("creation_date")
    val creationDate = LocalDateTime.ofInstant(
        Instant.ofEpochMilli(creationDateMillis / 1000),
        ZoneId.systemDefault()
    )

    return OutBoxDTO(id, eventType, data, creationDate)
}