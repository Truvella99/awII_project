package it.polito.analytics.repositories
import it.polito.analytics.entities.CompositeProfessionalsJobOffersKey
import it.polito.analytics.entities.ProfessionalsJobOffers
import it.polito.analytics.entities.professionalJobOfferState
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface ProfessionalsJobOffersRepository: R2dbcRepository<ProfessionalsJobOffers, CompositeProfessionalsJobOffersKey> {
    @Modifying
    @Query("INSERT INTO professionals_job_offers (professional_id, job_offer_id, final_status_professional) VALUES (:professionalId, :jobOfferId, :finalStatusProfessional)")
    fun insert(
        @Param("professionalId") professionalId: String,
        @Param("jobOfferId") jobOfferId: Long,
        @Param("finalStatusProfessional") finalStatusProfessional: professionalJobOfferState
    ): Mono<Void>

    @Modifying
    @Query("UPDATE professionals_job_offers SET final_status_professional=:finalStatusProfessional WHERE (professional_id=:professionalId AND job_offer_id=:jobOfferId)")
    fun update(
        @Param("professionalId") professionalId: String,
        @Param("jobOfferId") jobOfferId: Long,
        @Param("finalStatusProfessional") finalStatusProfessional: professionalJobOfferState
    ): Mono<Void>

    @Query("SELECT COUNT(*) > 0 FROM professionals_job_offers WHERE professional_id=:professionalId AND job_offer_id=:jobOfferId")
    fun existsById(
        @Param("professionalId") professionalId: String,
        @Param("jobOfferId") jobOfferId: Long
    ): Mono<Boolean>
}