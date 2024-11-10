package it.polito.analytics.services

import it.polito.analytics.dtos.CustomerDTO
import it.polito.analytics.dtos.ProfessionalDTO
import reactor.core.publisher.Flux

interface AnalyticsService {
    fun computeCustomersData(): Flux<CustomerDTO>
    fun computeProfessionalsData(): Flux<ProfessionalDTO>
}