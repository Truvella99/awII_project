package it.polito.customerrelationshipmanagement.dtos

import it.polito.customerrelationshipmanagement.entities.eventType

// processing (one of the middle state of the professional jobOffer that for analytics does not matter)
// removed (candidate proposal -> selection_phase case): remove the candidates so in the analytics the same
enum class professionalJobOfferState {
    Completed,Aborted,Candidated,Processing,Removed
}

// processing (one of the middle state of the customer jobOffer that for analytics does not matter)
enum class customerJobOfferState {
    Completed,Aborted,Created,Processing
}

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