package it.polito.customerrelationshipmanagement.entities

import jakarta.persistence.*


@Entity
class Professional{
    @Id
    @GeneratedValue
    var id: Long = 0

    lateinit var employmentState: employmentState
    lateinit var geographicalLocation: Pair<Double,Double>
    lateinit var dailyRate: Number

    @OneToOne
    lateinit var contact: Contact

    @OneToOne
    var currentJobOffer: JobOffer? = null

    @OneToMany(mappedBy = "professional")
    val jobOffers = mutableSetOf<JobOffer>()

    fun addJobOffer(jobOffer: JobOffer){
        jobOffer.professional = this
        jobOffers.add(jobOffer)
    }

    @OneToMany(mappedBy = "professional")
    val skills = mutableSetOf<Skill>()

    fun addSkill(skill: Skill){
        skill.professional = this
        skills.add(skill)
    }

    @OneToMany(mappedBy = "professional")
    val notes = mutableSetOf<Note>()

    fun addNote(note: Note){
        note.professional = this
        notes.add(note)
    }
}
