package it.polito.analytics.repositories
import it.polito.analytics.entities.CompositeProfessionalsJobOffersKey
import it.polito.analytics.entities.ProfessionalsJobOffers
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfessionalsJobOffersRepository: R2dbcRepository<ProfessionalsJobOffers, CompositeProfessionalsJobOffersKey> {
}