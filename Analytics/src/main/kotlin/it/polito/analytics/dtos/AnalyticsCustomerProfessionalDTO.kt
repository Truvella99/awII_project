package it.polito.analytics.dtos

import it.polito.analytics.entities.eventType

data class AnalyticsCustomerProfessionalDTO(
    val id: String,
    val name: String,
    val surname: String,
    val event: eventType
){
    // Costruttore senza argomenti, necessario per Jackson
    constructor() : this("", "", "", eventType.None)
}