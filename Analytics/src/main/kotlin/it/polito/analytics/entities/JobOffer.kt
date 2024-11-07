package it.polito.analytics.entities

import jakarta.persistence.*

@Entity
class JobOffer {
    @Id
    @GeneratedValue
    var id : Long = 0

    @ManyToMany
    @JoinTable(
        name = "job_offer_professionals",
        joinColumns = [JoinColumn(name = "job_offer_id")],
        inverseJoinColumns = [JoinColumn(name = "professional_id")]
    )
    val professionals = mutableSetOf<Professional>()
    fun addProfessional(professional: Professional) {
        professionals.add(professional)
        professional.jobOffers.add(this)
    }

    var finalStatus: professionalJobOfferState? = null
}

enum class professionalJobOfferState {
    completed,aborted,candidated
}