package it.polito.analytics.services

interface AnalyticsService {
    fun computeCustomerKPI(customerId: String): Float
    fun computeProfessionalKPI(professionalId: String): Float
}