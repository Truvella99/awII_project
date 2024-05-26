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
    fun addCustomerNotes(@PathVariable("customerId") customerId: Long, @RequestBody note:CreateUpdateNoteDTO): NoteDTO {
        return customerService.addCustomerNote(customerId, note)
    }
    /**
     * GET /API/customers/{customerId}
     *
     * details of customer {customerId} or fail if it does not exist.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/customers/{customerId}")
    fun getCustomer(@PathVariable("customerId") customerId: Long): CustomerDTO {
        return customerService.findCustomerById(customerId)
    }

    /**
     * PUT /API/customers/{customerId}
     *
     * update the customer {customerId}
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/API/customers/{customerId}")
    fun updateCustomer(
        @PathVariable("customerId") customerId: Long,
        @RequestBody  @Valid customer: CreateUpdateCustomerDTO
    ): CustomerDTO {
        return customerService.updateCustomer(customerId, customer)
    }
}