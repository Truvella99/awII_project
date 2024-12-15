package it.polito.customerrelationshipmanagement

import it.polito.customerrelationshipmanagement.consumers.CmConsumer
import it.polito.customerrelationshipmanagement.dtos.CreateMessageDTO
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.listener.MessageListenerContainer
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
class KafkaConsumerConfig(
    private val cmConsumer: CmConsumer // Inject the service
) {

    private val dtoTopicMap = mapOf(
        CreateMessageDTO::class.java to "cm-crm"
    )
    // Inject the bootstrap server URL from application.yml
    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var address: String
    //private val address = "localhost:29092"

    fun <T> consumerFactory(clazz: Class<T>, groupId: String): ConsumerFactory<String, T> {
        val configProps = mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to address,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java.name,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java.name,
            ConsumerConfig.GROUP_ID_CONFIG to groupId // Add the group.id here
        )
        val jsonDeserializer = JsonDeserializer(clazz).apply {
            addTrustedPackages("*")
            setRemoveTypeHeaders(true)
            setUseTypeMapperForKey(false)
        }
        return DefaultKafkaConsumerFactory(configProps, StringDeserializer(), jsonDeserializer)
    }

    fun <T> kafkaListenerContainerFactory(clazz: Class<T>, groupId: String): ConcurrentKafkaListenerContainerFactory<String, T> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, T>()
        factory.consumerFactory = consumerFactory(clazz, groupId)
        return factory
    }

    @Bean
    fun registerDynamicListeners(): List<MessageListenerContainer> {
        return dtoTopicMap.map { (dtoClass, topic) ->
            val groupId = dtoClass.name + "-" + topic
            val listenerContainer = kafkaListenerContainerFactory(dtoClass,groupId).createContainer(topic)

            listenerContainer.apply {
                containerProperties.messageListener = MessageListener<String, Any> { record ->
                    when (dtoClass) {
                        CreateMessageDTO::class.java -> cmConsumer.saveMessage(record.value() as CreateMessageDTO)
                        else -> println("Unhandled DTO type: ${record.value()}")
                    }
                }
                start()
            }

            listenerContainer
        }
    }
}

