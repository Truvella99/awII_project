package it.polito.apigatewayoauthserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApiGatewayOauthServerApplication

fun main(args: Array<String>) {
    runApplication<ApiGatewayOauthServerApplication>(*args)
}
