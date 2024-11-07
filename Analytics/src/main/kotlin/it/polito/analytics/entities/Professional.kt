package it.polito.analytics.entities

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany

@Entity
class Professional {
    @Id
    lateinit var id: String

    @ManyToMany(mappedBy = "professionals")
    val jobOffers = mutableSetOf<JobOffer>()
    fun addJobOffer(jobOffer: JobOffer){
        jobOffers.add(jobOffer)
        jobOffer.professionals.add(this)
    }
}