package it.polito.customerrelationshipmanagement

import it.polito.customerrelationshipmanagement.dtos.AnalyticsCustomerProfessionalDTO
import it.polito.customerrelationshipmanagement.dtos.AnalyticsJobOfferDTO
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaProducerConfig {
    private val dtos = listOf(
        AnalyticsCustomerProfessionalDTO::class.java,
        AnalyticsJobOfferDTO::class.java
    ) // Replace with your actual classes
    private val address = "localhost:29092"

    @Bean
    fun producerFactory(): ProducerFactory<String, Any> {
        val configProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to address,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )
        val factory = DefaultKafkaProducerFactory<String, Any>(configProps)
        factory.setValueSerializer(JsonSerializer<Any>().apply {
            setAddTypeInfo(false) // Don't add type info in headers
        })
        return factory
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Any> {
        return KafkaTemplate(producerFactory())
    }

    // Generic method to create KafkaTemplate beans for any DTO class
    fun <T> kafkaTemplateForType(clazz: Class<T>): KafkaTemplate<String, T> {
        val configProps = mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to address,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java
        )
        val factory = DefaultKafkaProducerFactory<String, T>(configProps)
        factory.setValueSerializer(JsonSerializer<T>().apply {
            setAddTypeInfo(false)
        })
        return KafkaTemplate(factory)
    }

    // Method to generate KafkaTemplate beans dynamically for each class in the list
//    @Bean
//    fun kafkaTemplateList(): List<KafkaTemplate<String, *>> {
//        return dtos.map { clazz -> kafkaTemplateForType(clazz) }
//    }

    // Method to generate a Map with DTO class as key and KafkaTemplate as value
    @Bean
    fun kafkaTemplateMap(): Map<Class<*>, KafkaTemplate<String, *>> {
        return dtos.associateWith { clazz -> kafkaTemplateForType(clazz) }
    }
}

