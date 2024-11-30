package it.polito.customerrelationshipmanagement

import io.debezium.config.Configuration
import io.debezium.data.Envelope.FieldName.*
import io.debezium.data.Envelope.Operation
import io.debezium.embedded.Connect
import io.debezium.engine.DebeziumEngine
import io.debezium.engine.RecordChangeEvent
import io.debezium.engine.format.ChangeEventFormat
import it.polito.customerrelationshipmanagement.controllers.JobOfferController
import it.polito.customerrelationshipmanagement.dtos.ProvaDTO
import it.polito.customerrelationshipmanagement.dtos.toOutBoxDTO
import it.polito.customerrelationshipmanagement.entities.eventType
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.apache.kafka.connect.data.Field
import org.apache.kafka.connect.data.Struct
import org.apache.kafka.connect.source.SourceRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.stream.Collectors
import com.fasterxml.jackson.databind.ObjectMapper
import it.polito.customerrelationshipmanagement.producers.AnalyticsProducer

@Component
class DebeziumListener(
    customerConnectorConfiguration: Configuration,
    private val analyticsProducer: AnalyticsProducer
) {
    private val executor: Executor = Executors.newSingleThreadExecutor()
    private var debeziumEngine: DebeziumEngine<RecordChangeEvent<SourceRecord>>? = null
    private val logger = LoggerFactory.getLogger(DebeziumListener::class.java)

    init {
        this.debeziumEngine = DebeziumEngine.create(ChangeEventFormat.of(Connect::class.java))
            .using(customerConnectorConfiguration.asProperties())
            .notifying { sourceRecordRecordChangeEvent: RecordChangeEvent<SourceRecord> ->
                this.handleChangeEvent(
                    sourceRecordRecordChangeEvent
                )
            }
            .build()
    }

    private fun handleChangeEvent(sourceRecordRecordChangeEvent: RecordChangeEvent<SourceRecord>) {
        val sourceRecord = sourceRecordRecordChangeEvent.record()
        val struct = sourceRecord.value() as Struct
        val outboxDto = struct.toOutBoxDTO();
        logger.info("DTO: {}",outboxDto);
        val objectMapper = ObjectMapper()
        when (outboxDto.eventType) {
            eventType.ProvaDto -> {
                val json = """{"id":1,"name":"john","surname":"Doe"}"""
                val provaDto = objectMapper.readValue(json, ProvaDTO::class.java)
                logger.info("Sending {}",provaDto);
                analyticsProducer.sendJobOffer("job-offers", provaDto)
            }
            eventType.B -> TODO()
            eventType.C -> TODO()
        }
    }

    @PostConstruct
    private fun start() {
        executor.execute(debeziumEngine!!)
    }

    @PreDestroy
    @Throws(IOException::class)
    private fun stop() {
        if (Objects.nonNull(this.debeziumEngine)) {
            debeziumEngine?.close()
        }
    }
}