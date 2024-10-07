package it.polito.customerrelationshipmanagement.entities

import com.fasterxml.jackson.databind.BeanDescription
import jakarta.persistence.*


@Entity
class JobOffer {
    @Id
    @GeneratedValue
    var id: Long = 0
    lateinit var name: String
    lateinit var description: String
    lateinit var currentState: jobOfferStatus
    var currentStateNote: String? = null
    lateinit var duration: Number
    var value: Number? = null
    lateinit var profitMargin: Number


    @ManyToOne
    lateinit var customer: Customer

    @OneToMany(mappedBy = "jobOffer")
    val skills = mutableSetOf<Skill>()

    fun addSkill(skill: Skill) {
        skill.jobOffer = this
        skills.add(skill)
    }

    // professional who completed the joboffer
    @ManyToOne
    var professional:Professional? = null

    // professionals who are candidate for the joboffer
    @ManyToMany
    @JoinTable(
        name = "job_offer_candidate_professionals",
        joinColumns = [JoinColumn(name = "job_offer_id")],
        inverseJoinColumns = [JoinColumn(name = "professional_id")]
    )
    val candidateProfessionals = mutableSetOf<Professional>()
    fun addCandidateProfessional(candidateProfessional: Professional) {
        candidateProfessionals.add(candidateProfessional)
        candidateProfessional.candidateJobOffers.add(this)
    }

    // professionals who are aborted for the job offer
    @ManyToMany
    @JoinTable(
        name = "job_offer_aborted_professionals",
        joinColumns = [JoinColumn(name = "job_offer_id")],
        inverseJoinColumns = [JoinColumn(name = "professional_id")]
    )
    val abortedProfessionals = mutableSetOf<Professional>()
    fun addAbortedProfessional(abortedProfessional: Professional) {
        abortedProfessionals.add(abortedProfessional)
        abortedProfessional.candidateJobOffers.remove(this)
        abortedProfessional.abortedJobOffers.add(this)
    }

    @OneToMany(mappedBy = "jobOffer")
    val histories = mutableSetOf<JobOffersHistory>()
    fun addHistory(history: JobOffersHistory) {
        history.jobOffer = this;
        histories.add(history);
    }

}
