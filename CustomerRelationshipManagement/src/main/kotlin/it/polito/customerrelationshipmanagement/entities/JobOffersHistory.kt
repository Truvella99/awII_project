package it.polito.customerrelationshipmanagement.entities

import jakarta.persistence.*
import java.util.*

@Entity
class JobOffersHistory {
    @Id
    @GeneratedValue
    var id: Long = 0

    lateinit var state: jobOfferStatus
    lateinit var date: Date
    var note: String? = null

    @ManyToOne
    lateinit var jobOffer: JobOffer

}
