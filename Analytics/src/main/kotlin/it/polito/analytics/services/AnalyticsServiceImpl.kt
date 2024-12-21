package it.polito.analytics.services

import it.polito.analytics.KeycloakConfig
import it.polito.analytics.dtos.CustomerDTO
import it.polito.analytics.dtos.ProfessionalDTO
import it.polito.analytics.repositories.CustomerRepository
import it.polito.analytics.repositories.JobOfferRepository
import it.polito.analytics.repositories.ProfessionalRepository
//import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
//@Transactional
class AnalyticsServiceImpl(
    private val customerRepository: CustomerRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val professionalRepository: ProfessionalRepository,
    private val keycloakConfig: KeycloakConfig
) : AnalyticsService {

    override fun computeCustomersData(): Flux<CustomerDTO> {
        val abortedJobOffersFlux = customerRepository.findCustomerAbortedJobOffers()
        val completedJobOffersFlux = customerRepository.findCustomerCompletedJobOffers()
        val createdJobOffersFlux = customerRepository.findCustomerCreatedJobOffers()

        return completedJobOffersFlux
            .zipWith(abortedJobOffersFlux) { completedDTO, abortedDTO ->
                // Return a tuple containing the two flux items
                Pair(completedDTO, abortedDTO)
            }
            .zipWith(createdJobOffersFlux) { pairDTO, createdDTO ->
                // Destructure the tuple and access the values
                val (completedDTO, abortedDTO) = pairDTO
                // check if the id exists in keycloak
                KeycloakConfig.checkExistingUserById(completedDTO.id);

                val kpi = if ((completedDTO.count + abortedDTO.count).toInt() == 0) {
                    0.0F
                } else {
                    100 * (completedDTO.count.toFloat() / (completedDTO.count + abortedDTO.count))
                }
                // Now you have all three sources
                CustomerDTO(
                    id = completedDTO.id,
                    name = completedDTO.name,
                    surname = completedDTO.surname,
                    completedJobOffers = completedDTO.count,
                    abortedJobOffers = abortedDTO.count,
                    createdJobOffers = createdDTO.count,
                    kpi = kpi,
                )
            }
    }

    override fun computeProfessionalsData(): Flux<ProfessionalDTO> {
        val abortedJobOffersFlux = professionalRepository.findProfessionalAbortedJobOffers()
        val completedJobOffersFlux = professionalRepository.findProfessionalCompletedJobOffers()
        val candidatedJobOffersFlux = professionalRepository.findProfessionalCandidatedJobOffers()

        return completedJobOffersFlux
            .zipWith(abortedJobOffersFlux) { completedDTO, abortedDTO ->
                // Return a tuple containing the two flux items
                Pair(completedDTO, abortedDTO)
            }
            .zipWith(candidatedJobOffersFlux) { pairDTO, candidatedDTO ->
                // Destructure the tuple and access the values
                val (completedDTO, abortedDTO) = pairDTO
                // check if the id exists in keycloak
                KeycloakConfig.checkExistingUserById(completedDTO.id);

                val kpi = if ((completedDTO.count + abortedDTO.count).toInt() == 0) {
                    0.0F
                } else {
                    100 * (completedDTO.count.toFloat() / (completedDTO.count + abortedDTO.count))
                }
                // Now you have all three sources
                ProfessionalDTO(
                    id = completedDTO.id,
                    name = completedDTO.name,
                    surname = completedDTO.surname,
                    completedJobOffers = completedDTO.count,
                    abortedJobOffers = abortedDTO.count,
                    candidatedJobOffers = candidatedDTO.count,
                    kpi = kpi,
                )
            }
    }
}