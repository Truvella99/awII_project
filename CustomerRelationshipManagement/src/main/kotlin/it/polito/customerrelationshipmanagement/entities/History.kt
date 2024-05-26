package it.polito.customerrelationshipmanagement.entities

import jakarta.persistence.*
import java.util.*

@Entity
class History {
    @Id
    @GeneratedValue
    var id: Long = 0

    lateinit var state: state
    lateinit var date: Date
    lateinit var comment: String

    @ManyToOne
    lateinit var message: Message
}
