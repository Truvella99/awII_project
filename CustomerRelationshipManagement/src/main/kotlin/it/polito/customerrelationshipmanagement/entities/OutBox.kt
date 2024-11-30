package it.polito.customerrelationshipmanagement.entities

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class OutBox {
    @Id
    @GeneratedValue
    var id: Long = 0
    lateinit var eventType: eventType
    lateinit var data: String
    var creationDate: LocalDateTime = LocalDateTime.now()
}

enum class eventType(val type: Short) {
    ProvaDto(0),
    B(1),
    C(2);

    companion object {
        fun fromType(type: Short): eventType {
            return entries.firstOrNull { it.type == type }
                ?: throw IllegalArgumentException("Unknown event type: $type")
        }
    }
}