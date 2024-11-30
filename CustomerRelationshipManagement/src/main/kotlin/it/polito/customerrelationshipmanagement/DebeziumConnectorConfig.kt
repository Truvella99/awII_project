package it.polito.customerrelationshipmanagement

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.io.File
import java.io.IOException

@Configuration
class DebeziumConnectorConfig {

    @Bean
    @Throws(IOException::class)
    fun customerConnector(env: Environment): io.debezium.config.Configuration {
        // Temporary file for offset storage
        val offsetStorageTempFile = File.createTempFile("offsets_", ".dat")

        // Build Debezium configuration
        return io.debezium.config.Configuration.create()
            .with("name", "customer_postgres_connector")
            .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
            .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
            .with("offset.storage.file.filename", offsetStorageTempFile.absolutePath)
            .with("offset.flush.interval.ms", "60000")
            .with("database.hostname", env.getProperty("spring.datasource.url")?.let { extractHost(it) })
            .with("database.port", env.getProperty("spring.datasource.url")?.let { extractPort(it, "5432") })
            .with("database.user", env.getProperty("spring.datasource.username"))
            .with("database.password", env.getProperty("spring.datasource.password"))
            .with("database.dbname", env.getProperty("spring.datasource.url")?.let { extractDatabase(it) })
            .with("database.server.id", "10181")
            .with("database.server.name", "customer-postgres-db-server")
            .with("topic.prefix", "slave-postgresql")
            .with("database.history", "io.debezium.relational.history.MemoryDatabaseHistory")
            .with("publication.autocreate.mode", "all_tables")
            .with("plugin.name", "pgoutput")
            .with("slot.name", "dbz_customerdb_listener")
            .with("table.include.list", "public.out_box")
            .build()
    }

    private fun extractHost(url: String): String {
        val regex = Regex("jdbc:postgresql://([^:/]+)")
        return regex.find(url)?.groupValues?.get(1) ?: "localhost"
    }

    private fun extractPort(url: String, defaultPort: String): String {
        val regex = Regex("jdbc:postgresql://[^:/]+:(\\d+)")
        return regex.find(url)?.groupValues?.get(1) ?: defaultPort
    }

    private fun extractDatabase(url: String): String {
        val regex = Regex("jdbc:postgresql://[^/]+/(\\w+)")
        return regex.find(url)?.groupValues?.get(1) ?: "postgres"
    }
}
