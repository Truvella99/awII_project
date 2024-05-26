package it.polito.customerrelationshipmanagement.entities

import jakarta.persistence.*


@Entity
class Skill {
    @Id
    @GeneratedValue
    var id: Long = 0
    lateinit var skill: String
    var state: contactInfoState = contactInfoState.active

    @ManyToOne
    var jobOffer: JobOffer? = null

    @ManyToOne
    var professional: Professional? = null
}