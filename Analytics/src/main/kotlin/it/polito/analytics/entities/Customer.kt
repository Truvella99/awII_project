package it.polito.analytics.entities

import jakarta.persistence.*
import java.io.Serializable

@Entity
class Customer {
    @EmbeddedId
    lateinit var key: CompositeKey
    var finalStatus: customerJobOfferState? = null
}

enum class customerJobOfferState {
    completed,aborted,created
}

@Embeddable
class CompositeKey() : Serializable {
    @Column(name = "Id", nullable = false)
    lateinit var id: String
    @Column(name = "jobOfferId", nullable = false)
    var jobOfferId: Long = 0

    constructor(id: String, jobOfferId: Long) : this() {
        this.id = id
        this.jobOfferId = jobOfferId
    }
}