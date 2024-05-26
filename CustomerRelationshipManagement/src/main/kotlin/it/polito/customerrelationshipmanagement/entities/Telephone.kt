package it.polito.customerrelationshipmanagement.entities

import jakarta.persistence.*


@Entity
class Telephone {
    @Id
    @GeneratedValue
    var id: Long = 0
    lateinit var telephone: String

    @ManyToOne
    var contact: Contact? = null

    var state: contactInfoState = contactInfoState.active

}