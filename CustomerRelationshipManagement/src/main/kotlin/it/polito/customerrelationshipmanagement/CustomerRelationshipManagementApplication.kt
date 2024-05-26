package it.polito.customerrelationshipmanagement

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CustomerRelationshipManagementApplication

fun main(args: Array<String>) {
	runApplication<CustomerRelationshipManagementApplication>(*args)
}
