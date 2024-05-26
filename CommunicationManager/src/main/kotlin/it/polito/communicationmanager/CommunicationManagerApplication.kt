package it.polito.communicationmanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CommunicationManagerApplication

fun main(args: Array<String>) {
    runApplication<CommunicationManagerApplication>(*args)
}
