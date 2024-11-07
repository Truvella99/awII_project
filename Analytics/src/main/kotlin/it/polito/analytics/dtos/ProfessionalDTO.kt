package it.polito.analytics.dtos

data class ProfessionalDTO(
    val completedJobOffers: Long,
    val abortedJobOffers: Long,
    val candidatedJobOffers: Long,
    val kpi: Float
)