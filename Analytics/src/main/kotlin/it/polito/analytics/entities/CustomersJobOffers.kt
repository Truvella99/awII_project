package it.polito.analytics.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable
import org.springframework.data.annotation.Transient;

data class CompositeCustomersJobOffersKey(
    val customerId: String,
    val jobOfferId: Long
) : Serializable

@Table(name = "customers_job_offers")
data class CustomersJobOffers(
    @Id
    @Transient
    var id: CompositeCustomersJobOffersKey,
    var finalStatusCustomer: customerJobOfferState
)