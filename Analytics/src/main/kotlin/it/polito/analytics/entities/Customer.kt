package it.polito.analytics.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "customer")
data class Customer(
    @Id
    var id: String,
    var name: String,
    var surname: String
)