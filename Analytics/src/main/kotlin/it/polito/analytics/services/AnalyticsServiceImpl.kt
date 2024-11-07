package it.polito.analytics.services

import it.polito.analytics.KeycloakConfig
import it.polito.analytics.repositories.CustomerRepository
import it.polito.analytics.repositories.JobOfferRepository
import it.polito.analytics.repositories.ProfessionalRepository
import org.springframework.stereotype.Service

@Service
class AnalyticsServiceImpl(
    private val customerRepository: CustomerRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val professionalRepository: ProfessionalRepository): AnalyticsService {
    // TODO change the API to return the DTo for graphs
    override fun computeCustomerKPI(customerId: String): Float {
        KeycloakConfig.checkExistingUserById(customerId)
        val aborted = customerRepository.computeAllAborted(customerId)
        val completed = customerRepository.computeAllCompleted(customerId)
        return if ((completed+aborted).toInt() == 0) {
            0.0F
        } else {
            completed.toFloat()/(completed + aborted)
        }
    }

    override fun computeProfessionalKPI(professionalId: String): Float {
        KeycloakConfig.checkExistingUserById(professionalId)
        val aborted = jobOfferRepository.computeAllAborted(professionalId)
        val completed = jobOfferRepository.computeAllCompleted(professionalId)
        return if ((completed+aborted).toInt() == 0) {
            0.0F
        } else {
            completed.toFloat()/(completed + aborted)
        }
    }
}