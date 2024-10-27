package it.polito.customerrelationshipmanagement.services

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.Customer
import org.springframework.security.core.Authentication

interface CustomerService {

    // gaetano
    fun createCustomer(customer: CreateUpdateCustomerDTO): CustomerDTO
    fun findCustomerById(customerId: Long,authentication: Authentication): CustomerDTO
    fun updateCustomer(customerId: Long,customer: CreateUpdateCustomerDTO): CustomerDTO
    fun addCustomerNote(customerId: Long, note: CreateUpdateNoteDTO): NoteDTO
    fun deleteCustomerNote(customerId: Long, noteId: Long): CustomerDTO

    fun findCustomers(filter: String): List<CustomerDTO>
    fun getAllCustomers(): List<CustomerDTO>
}
