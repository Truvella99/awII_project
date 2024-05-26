package it.polito.customerrelationshipmanagement.entities

import jakarta.persistence.*


@Entity
class Email {
    @Id
    @GeneratedValue
    var id: Long = 0
    lateinit var email: String

    @ManyToOne
    var contact: Contact? = null

    var state: contactInfoState = contactInfoState.active

}
