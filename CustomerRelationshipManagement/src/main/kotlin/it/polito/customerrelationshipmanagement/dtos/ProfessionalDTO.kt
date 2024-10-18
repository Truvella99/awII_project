package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.*


data class ProfessionalDTO(
    val id: Long,
    val name: String?,
    val surname: String?,
    val ssncode: String?,
    val category: category?,
    val emails: List<EmailDTO>,
    val telephones: List<TelephoneDTO>,
    val addresses: List<AddressDTO>,
    val notes: List<NoteDTO>,
    val jobOffers: List<JobOfferDTO>,
    val candidateJobOffers: List<JobOfferDTO>,
    val abortedJobOffers: List<JobOfferDTO>,
    val skills: List<SkillDTO>,
    val jobOffer: JobOfferDTO?,
    val employmentState: employmentState,
    val geographicalLocation: Pair<Double,Double>?,
    val dailyRate: Number
)

fun Professional.toDTO(): ProfessionalDTO =
    ProfessionalDTO(
        this.id,
        this.contact.name,
        this.contact.surname,
        this.contact.ssncode,
        this.contact.category,
        this.contact.emails.map { it.toDTO() },
        this.contact.telephones.map { it.toDTO() },
        this.contact.addresses.map { it.toDTO() },
        this.notes.map { it.toDTO() },
        this.jobOffers.map { it.toDTO() },
        this.candidateJobOffers.map { it.toDTO() },
        this.abortedJobOffers.map { it.toDTO() },
        this.skills.map { it.toDTO() },
        this.currentJobOffer?.toDTO(),
        this.employmentState,
        this.geographicalLocation,
        this.dailyRate
    )


