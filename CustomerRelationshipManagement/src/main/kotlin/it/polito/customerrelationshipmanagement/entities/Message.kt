package it.polito.customerrelationshipmanagement.entities

import jakarta.persistence.*
import java.util.Date

@Entity
class Message {
    @Id
    @GeneratedValue
    var id: Long = 0

    lateinit var date: Date
    lateinit var channel: channel
    lateinit var priority: priority
    lateinit var currentState: state

    var subject: String? = null

    @Column(columnDefinition = "TEXT")
    var body: String? = null

    var email: String? = null

    var telephone: String? = null

    var address: String? = null

    @OneToMany(mappedBy = "message")
    val histories = mutableSetOf<History>()
    fun addHistory(history: History) {
        history.message = this;
        histories.add(history);
    }
}