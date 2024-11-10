package it.polito.analytics.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable

data class CompositeCustomersJobOffersKey(
    val customerId: String,
    val jobOfferId: Long
) : Serializable

@Table(name = "customers_job_offers")
data class CustomersJobOffers(
    @Id
    var id: CompositeCustomersJobOffersKey
)