package it.polito.analytics.dtos

data class CustomerDTO(
    val id: String,
    val name: String,
    val surname: String,
    val completedJobOffers: Long,
    val abortedJobOffers: Long,
    val createdJobOffers: Long,
    val kpi: Float
)