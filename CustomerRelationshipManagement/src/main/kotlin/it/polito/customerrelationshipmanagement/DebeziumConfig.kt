package it.polito.customerrelationshipmanagement

import org.springdoc.core.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DebeziumConfig {
    @Bean
    fun customOpenAPI(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("debezium-master-postgresql-api") // Specify a group name
            .packagesToScan("it.polito.customerrelationshipmanagement.controllers") // Update with your package
            .build()
    }
}