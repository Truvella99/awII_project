package it.polito.customerrelationshipmanagement.producers

import it.polito.customerrelationshipmanagement.dtos.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class AnalyticsProducer(private val kafkaTemplateMap: Map<Class<*>, KafkaTemplate<String, *>>) {

    fun sendCustomerOrProfessional(topic: String, customer: AnalyticsCustomerProfessionalDTO) {
        val template = kafkaTemplateMap[AnalyticsCustomerProfessionalDTO::class.java] as KafkaTemplate<String, AnalyticsCustomerProfessionalDTO>;
        template.send(topic, customer)
        println("Customer/Professional sent: $customer")
    }

    fun sendJobOffer(topic: String, jobOffer: AnalyticsJobOfferDTO) {
        val template = kafkaTemplateMap[AnalyticsJobOfferDTO::class.java] as KafkaTemplate<String, AnalyticsJobOfferDTO>;
        template.send(topic, jobOffer)
        println("JobOfferDTO sent: $jobOffer")
    }
}
