package it.polito.analytics.consumers

import it.polito.analytics.dtos.AnalyticsCustomerProfessionalDTO
import it.polito.analytics.dtos.AnalyticsJobOfferDTO
import it.polito.analytics.entities.*
import it.polito.analytics.repositories.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CrmConsumer(
    private val r2dbcEntityTemplate: R2dbcEntityTemplate,
    private val customerRepository: CustomerRepository,
    private val professionalRepository: ProfessionalRepository,
    private val jobOfferRepository: JobOfferRepository,
    private val customersJobOffersRepository: CustomersJobOffersRepository,
    private val professionalsJobOffersRepository: ProfessionalsJobOffersRepository,
    private val transactionalOperator: TransactionalOperator // Inject TransactionalOperator
) {

    fun customerOrProfessionalEvent(data: AnalyticsCustomerProfessionalDTO) {
        when(data.event) {
            eventType.CreateCustomer -> {
                println("Adding customer ${data.id}");
                val c = Customer(
                    id = data.id,
                    name = data.name,
                    surname = data.surname
                )
                transactionalOperator.execute{
                    r2dbcEntityTemplate.insert(Customer::class.java)
                        .using(c)
                        .doOnSuccess { println("Customer ${data.id} saved successfully") }
                        .doOnError { error -> println("Failed to save customer ${data.id}: ${error.message}") }
                }.subscribe()
            }
            eventType.UpdateCustomer -> {
                println("Updating customer ${data.id}");
                val c = Customer(
                    id = data.id,
                    name = data.name,
                    surname = data.surname
                )
                transactionalOperator.execute {
                    customerRepository.save(c)
                        .doOnSuccess { println("Customer ${data.id} updated successfully") }
                        .doOnError { error -> println("Failed to update customer ${data.id}: ${error.message}") }
                }.subscribe()
            }
            eventType.CreateProfessional -> {
                println("Adding professional ${data.id}");
                val p = Professional(
                    id = data.id,
                    name = data.name,
                    surname = data.surname
                )
                transactionalOperator.execute {
                    r2dbcEntityTemplate.insert(Professional::class.java)
                        .using(p)
                        .doOnSuccess { println("Professional ${data.id} saved successfully") }
                        .doOnError { error -> println("Failed to save professional ${data.id}: ${error.message}") }
                }.subscribe()
            }
            eventType.UpdateProfessional -> {
                println("Updating professional ${data.id}");
                val p = Professional(
                    id = data.id,
                    name = data.name,
                    surname = data.surname
                )
                transactionalOperator.execute {
                    professionalRepository.save(p)
                        .doOnSuccess { println("Professional ${data.id} updated successfully") }
                        .doOnError { error -> println("Failed to update professional ${data.id}: ${error.message}") }
                }.subscribe()
            }
            else -> {}
        }
    }

    fun jobOfferEvent(data: AnalyticsJobOfferDTO) {
        when(data.event) {
            eventType.CreateJobOffer -> {
                println("Adding jobOffer ${data.jobOfferId}")
                val jobOffer = JobOffer(id = data.jobOfferId)
                // Save the jobOffer without returning the entity
                transactionalOperator.execute {
                    r2dbcEntityTemplate.insert(JobOffer::class.java)
                        .using(jobOffer)
                        .doOnSuccess { println("JobOffer ${data.jobOfferId} saved successfully") }
                        .doOnError { error -> println("Failed to save JobOffer ${data.jobOfferId}: ${error.message}") }
                        .then(
                            customersJobOffersRepository.insert(
                                customerId = data.finalStatusCustomerAndId!!.second,
                                jobOfferId = data.jobOfferId,
                                finalStatusCustomer = data.finalStatusCustomerAndId.first
                            )
                                .doOnSuccess { println("Customer-JobOffer association saved successfully") }
                                .doOnError { error -> println("Failed to save Customer-JobOffer association: ${error.message}") }
                        )
                }.subscribe()
            }
            eventType.UpdateJobOffer -> {
                println("Updating jobOffer ${data.jobOfferId}");
                // Update the customer-jobOffer join table entry
                transactionalOperator.execute {
                    customersJobOffersRepository.update(
                        customerId = data.finalStatusCustomerAndId!!.second,
                        jobOfferId = data.jobOfferId,
                        finalStatusCustomer = data.finalStatusCustomerAndId.first
                    ).doOnSuccess { println("Customer-JobOffer association updated successfully") }
                        .doOnError { error -> println("Failed to update Customer-JobOffer association: ${error.message}") }
                        .then(
                            Flux.fromIterable(data.finalStatusProfessionalsAndIds) // Convert to Flux
                                .flatMap { (professionalState, professionalId) ->
                                    if (professionalState != professionalJobOfferState.Removed) {
                                        professionalsJobOffersRepository.existsById(professionalId, data.jobOfferId)
                                            .flatMap { exists ->
                                                if (exists) {
                                                    professionalsJobOffersRepository.update(
                                                        professionalId = professionalId,
                                                        jobOfferId = data.jobOfferId,
                                                        finalStatusProfessional = professionalState
                                                    ).doOnSuccess { println("Professional-JobOffer association updated successfully") }
                                                        .doOnError { error -> println("Failed to update Professional-JobOffer association: ${error.message}") }
                                                } else {
                                                    professionalsJobOffersRepository.insert(
                                                        professionalId = professionalId,
                                                        jobOfferId = data.jobOfferId,
                                                        finalStatusProfessional = professionalState
                                                    ).doOnSuccess { println("Professional-JobOffer association inserted successfully") }
                                                        .doOnError { error -> println("Failed to insert Professional-JobOffer association: ${error.message}") }
                                                }
                                            }.doOnSuccess { println("Find Executed successfully") }
                                            .doOnError { error -> println("Find error: ${error.message}") }

                                    } else {
                                        // Remove the entry
                                        val pJId = CompositeProfessionalsJobOffersKey(
                                            jobOfferId = data.jobOfferId,
                                            professionalId = professionalId
                                        )
                                        professionalsJobOffersRepository.deleteById(pJId)
                                            .doOnSuccess { println("Professional-JobOffer association deleted successfully") }
                                            .doOnError { error -> println("Failed to delete Professional-JobOffer association: ${error.message}") }
                                    }
                                }
                                .then() // Ensures the operation completes
                        )
                }.subscribe()

                /*customersJobOffersRepository.save(cJ)
                    .doOnSuccess { println("Customer-JobOffer association updated successfully") }
                    .doOnError { error -> println("Failed to update Customer-JobOffer association: ${error.message}") }
                    .subscribe()*/
                // Update all the professional-jobOffer join table entries

            }
            else -> {}
        }
    }
}

