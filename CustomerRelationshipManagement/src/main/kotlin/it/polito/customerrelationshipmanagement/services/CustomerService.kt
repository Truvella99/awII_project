package it.polito.customerrelationshipmanagement.services

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.Customer
import org.springframework.security.core.Authentication

interface CustomerService {

    // gaetano
    fun createCustomer(customer: CreateUpdateCustomerDTO): CustomerDTO
    fun findCustomerById(customerId: String,authentication: Authentication): CustomerDTO
    fun updateCustomer(customerId: String,customer: CreateUpdateCustomerDTO): CustomerDTO
    fun addCustomerNote(customerId: String, note: CreateUpdateNoteDTO): NoteDTO
    fun deleteCustomerNote(customerId: String, noteId: Long): CustomerDTO

    fun findCustomers(filter: String): List<CustomerDTO>
    fun getAllCustomers(jobOffers: List<String>?): List<CustomerDTO>
}
