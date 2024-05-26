package it.polito.customerrelationshipmanagement.entities

import jakarta.persistence.*


@Entity
class Customer{
    @Id
    @GeneratedValue
    var id: Long = 0
    @OneToMany(mappedBy = "customer")
    val notes = mutableSetOf<Note>()

    fun addNote(note: Note){
        note.customer = this
        notes.add(note)
    }

    @OneToOne
    lateinit var contact: Contact

    @OneToMany(mappedBy = "customer")
    val jobOffers = mutableSetOf<JobOffer>()

    fun addJobOffer(jobOffer: JobOffer) {
        jobOffer.customer = this;
        jobOffers.add(jobOffer);
    }

}
