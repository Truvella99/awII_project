package it.polito.customerrelationshipmanagement.entities

import jakarta.persistence.*


@Entity
 class Contact {
    @Id
    @GeneratedValue
    var id: Long = 0
    var name: String? = null
    var surname: String? = null
    var ssncode: String? = null
    var category: category? = null

    @OneToOne
    var customer: Customer? = null
    @OneToOne
    var professional: Professional? = null
    @OneToMany(mappedBy = "contact")
    val emails = mutableSetOf<Email>()

    fun addEmail(email: Email) {
        email.contact = this
        emails.add(email)
    }

    @OneToMany(mappedBy = "contact")
    val addresses = mutableSetOf<Address>()

    fun addAddress(address: Address) {
        address.contact = this
        addresses.add(address)
    }

    @OneToMany(mappedBy = "contact")
    val telephones = mutableSetOf<Telephone>()

    fun addTelephone(telephone: Telephone) {
        telephone.contact = this
        telephones.add(telephone)
    }

}
