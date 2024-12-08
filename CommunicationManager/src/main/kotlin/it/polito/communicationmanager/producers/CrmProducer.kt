package it.polito.communicationmanager.producers

import it.polito.communicationmanager.dtos.CreateMessageDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class CrmProducer(private val kafkaTemplateMap: Map<Class<*>, KafkaTemplate<String, *>>) {

    fun sendMessage(topic: String, message: CreateMessageDTO) {
        val template = kafkaTemplateMap[CreateMessageDTO::class.java] as KafkaTemplate<String, CreateMessageDTO>;
        template.send(topic, message)
        println("Message sent: $message")
    }

}
