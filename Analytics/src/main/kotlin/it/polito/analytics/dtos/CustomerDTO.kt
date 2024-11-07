package it.polito.analytics.dtos

data class CustomerDTO(
    val completedJobOffers: Long,
    val abortedJobOffers: Long,
    val createdJobOffers: Long,
    val kpi: Float
)