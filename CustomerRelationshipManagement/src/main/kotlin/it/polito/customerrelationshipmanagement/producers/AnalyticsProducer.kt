package it.polito.customerrelationshipmanagement.producers

import it.polito.customerrelationshipmanagement.dtos.JobOfferDTO
import it.polito.customerrelationshipmanagement.dtos.ProvaDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class AnalyticsProducer(private val kafkaTemplateMap: Map<Class<*>, KafkaTemplate<String, *>>) {

    fun sendJobOffer(topic: String, jobOffer: ProvaDTO) {
        val provaTemplate = kafkaTemplateMap[ProvaDTO::class.java] as KafkaTemplate<String, ProvaDTO>;
        provaTemplate.send(topic, jobOffer)
        println("JobOfferDTO sent: $jobOffer")
    }
}
