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
    None(0),
    CreateCustomer(1),
    UpdateCustomer(2),
    CreateProfessional(3),
    UpdateProfessional(4),
    CreateJobOffer(5),
    UpdateJobOffer(6);

    companion object {
        fun fromType(type: Short): eventType {
            return entries.firstOrNull { it.type == type }
                ?: throw IllegalArgumentException("Unknown event type: $type")
        }
    }
}