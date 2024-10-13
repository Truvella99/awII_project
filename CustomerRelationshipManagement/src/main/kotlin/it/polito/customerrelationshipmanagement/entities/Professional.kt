package it.polito.customerrelationshipmanagement.entities

import jakarta.persistence.*


@Entity
class Professional{
    @Id
    @GeneratedValue
    var id: Long = 0

    lateinit var employmentState: employmentState
    var geographicalLocation: Pair<Double,Double>? = null
    lateinit var dailyRate: Number

    @OneToOne
    lateinit var contact: Contact

    // joboffer on which is currently working on
    @OneToOne
    var currentJobOffer: JobOffer? = null

    // joboffers correctly completed
    @OneToMany(mappedBy = "professional")
    val jobOffers = mutableSetOf<JobOffer>()

    fun addJobOffer(jobOffer: JobOffer){
        jobOffer.professional = this
        jobOffers.add(jobOffer)
    }

    // joboffers to which the professional is candidate
    @ManyToMany(mappedBy = "candidateProfessionals")
    val candidateJobOffers = mutableSetOf<JobOffer>()
    fun addCandidateJobOffer(jobOffer: JobOffer){
        candidateJobOffers.add(jobOffer)
        jobOffer.candidateProfessionals.add(this)
    }

    // joboffers to which the professional is aborted
    @ManyToMany(mappedBy = "abortedProfessionals")
    val abortedJobOffers = mutableSetOf<JobOffer>()
    fun addAbortedJobOffer(jobOffer: JobOffer){
        abortedJobOffers.add(jobOffer)
        jobOffer.abortedProfessionals.add(this)
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
