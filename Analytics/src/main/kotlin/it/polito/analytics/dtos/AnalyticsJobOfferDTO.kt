package it.polito.analytics.dtos

import it.polito.analytics.entities.customerJobOfferState
import it.polito.analytics.entities.eventType
import it.polito.analytics.entities.professionalJobOfferState

data class FinalStatusCustomerAndId(
    val first: customerJobOfferState,
    val second: String
){
    // Costruttore senza argomenti, necessario per Jackson
    constructor() : this(customerJobOfferState.Processing, "")
}

data class FinalStatusProfessionalAndId(
    val first: professionalJobOfferState,
    val second: String
){
    // Costruttore senza argomenti, necessario per Jackson
    constructor() : this(professionalJobOfferState.Processing, "")
}

data class AnalyticsJobOfferDTO(
    val jobOfferId: Long,
    val finalStatusCustomerAndId: FinalStatusCustomerAndId? = null,
    val finalStatusProfessionalsAndIds: List<FinalStatusProfessionalAndId> = listOf(),
    val event: eventType
){
    // Costruttore senza argomenti, necessario per Jackson
    constructor() : this(0L, null, listOf(), eventType.None)
}