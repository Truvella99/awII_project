package it.polito.analytics.repositories
import it.polito.analytics.entities.CompositeCustomersJobOffersKey
import it.polito.analytics.entities.CustomersJobOffers
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomersJobOffersRepository: R2dbcRepository<CustomersJobOffers, CompositeCustomersJobOffersKey> {
}