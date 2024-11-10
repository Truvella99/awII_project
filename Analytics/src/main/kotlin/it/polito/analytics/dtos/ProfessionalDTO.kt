package it.polito.analytics.dtos

data class ProfessionalDTO(
    val id: String,
    val name: String,
    val surname: String,
    val completedJobOffers: Long,
    val abortedJobOffers: Long,
    val candidatedJobOffers: Long,
    val kpi: Float
)