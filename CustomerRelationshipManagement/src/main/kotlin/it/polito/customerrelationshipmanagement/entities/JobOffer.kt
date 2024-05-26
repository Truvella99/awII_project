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

    @ManyToOne
    var professional:Professional? = null


    @OneToMany(mappedBy = "jobOffer")
    val histories = mutableSetOf<JobOffersHistory>()
    fun addHistory(history: JobOffersHistory) {
        history.jobOffer = this;
        histories.add(history);
    }

}
