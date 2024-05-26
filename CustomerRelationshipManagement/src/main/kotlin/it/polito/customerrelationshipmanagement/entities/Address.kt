package it.polito.customerrelationshipmanagement.entities

import jakarta.persistence.*


@Entity
class Address {
    @Id
    @GeneratedValue
    var id: Long = 0
    lateinit var address: String

    @ManyToOne
    var contact: Contact? = null

    var state: contactInfoState = contactInfoState.active
}
