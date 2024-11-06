package it.polito.customerrelationshipmanagement.controllers

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.priority
import it.polito.customerrelationshipmanagement.entities.state
import it.polito.customerrelationshipmanagement.services.CustomerService
import it.polito.customerrelationshipmanagement.services.JobOfferService
import it.polito.customerrelationshipmanagement.services.MessageService
import jakarta.validation.Valid
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
class CustomerController(private val customerService: CustomerService){

    /**
     * POST /API/customers/
     *
     * create a new customer
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/API/customers/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun createCustomer(@RequestBody @Valid c: CreateUpdateCustomerDTO): CustomerDTO {
        return customerService.createCustomer(c)
    }
    /**
     * POST /API/customers/{customerId}/note
     *
     * add a note to the customer {customerId}
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/API/customers/{customerId}/note")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun addCustomerNotes(@PathVariable("customerId") customerId: String, @RequestBody note:CreateUpdateNoteDTO): NoteDTO {
        return customerService.addCustomerNote(customerId, note)
    }
    /**
     * GET /API/customers/{customerId}
     *
     * details of customer {customerId} or fail if it does not exist.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/customers/{customerId}")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager')  || (hasRole('customer') ) )")
    fun getCustomer(@PathVariable("customerId") customerId: String, authentication: Authentication): CustomerDTO {
        return customerService.findCustomerById(customerId,authentication)
    }

    /**
     * GET /API/customers/filters/{filter}
     *
     * find customers with the properties that match the filter passed.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/customers/filters/{filter}")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun findCustomers(@PathVariable("filter") filter: String): List<CustomerDTO> {
        return customerService.findCustomers(filter)
    }

    /**
     * PUT /API/customers/{customerId}
     *
     * update the customer {customerId}
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/API/customers/{customerId}")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun updateCustomer(
        @PathVariable("customerId") customerId: String,
        @RequestBody  @Valid customer: CreateUpdateCustomerDTO
    ): CustomerDTO {
        return customerService.updateCustomer(customerId, customer)
    }

    /**
     * GET /API/customers/
     *
     * get all the customers in the DB. Allow for filtering by jobOffers.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/customers/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun getAllCustomers(
        @RequestParam("jobOffers") jobOffers: List<String>?
    ): List<CustomerDTO> {
        return customerService.getAllCustomers(jobOffers)
    }
}