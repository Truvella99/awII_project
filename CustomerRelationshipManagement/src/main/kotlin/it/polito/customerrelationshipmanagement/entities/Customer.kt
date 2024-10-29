package it.polito.customerrelationshipmanagement.entities

import jakarta.persistence.*


@Entity
class Customer{
    @Id
    lateinit var id: String
    @OneToMany(mappedBy = "customer")
    val notes = mutableSetOf<Note>()

    fun addNote(note: Note){
        note.customer = this
        notes.add(note)
    }

    @OneToOne(mappedBy = "customer",cascade = [CascadeType.ALL])
    lateinit var contact: Contact

    @OneToMany(mappedBy = "customer")
    val jobOffers = mutableSetOf<JobOffer>()

    fun addJobOffer(jobOffer: JobOffer) {
        jobOffer.customer = this;
        jobOffers.add(jobOffer);
    }

}
