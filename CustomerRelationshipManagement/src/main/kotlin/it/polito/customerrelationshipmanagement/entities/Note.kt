package it.polito.customerrelationshipmanagement.entities

import jakarta.persistence.*


@Entity
class Note {
    @Id
    @GeneratedValue
    var id: Long = 0
    lateinit var note: String

    @ManyToOne
    var customer: Customer? = null

    @ManyToOne
    var professional: Professional? = null

    var state: contactInfoState = contactInfoState.active

}