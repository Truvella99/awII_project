package it.polito.analytics

import jakarta.annotation.Resource
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.nio.charset.Charset
import java.sql.DriverManager

@Configuration // a bean so instaciated when application starts
@EnableR2dbcRepositories  //when we introduce the entity equivalent will allow us to derive repository
@EnableTransactionManagement // let us label the service methods with transactional (totally different from jpa)
class R2DBCConfig(
    private val databaseClient: DatabaseClient // same as entity manager in jpa,
    // HERE ENTITIES ARE NOT SPECIAL CLASSES, ONLY PUT TABLE ANNOTATION TO MAP CLASS NAME TO TABLE IN DB
): InitializingBean {
    @Value("classpath:/schema.sql")
    private lateinit var schemaSql: org.springframework.core.io.Resource // file taken from resources folder

    override fun afterPropertiesSet() {
        createDatabaseIfNotExists()
        // do something before class get visible to the world
        // load the sql script
        val schema = schemaSql.inputStream.bufferedReader().use { it.readText() }

        // Split SQL statements by semicolon and execute each one
        schema.split(";")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .forEach { statement ->
                databaseClient.sql(statement).then().block()
            }

        LoggerFactory.getLogger(R2DBCConfig::class.java).info("Database Started")
    }

    private fun createDatabaseIfNotExists() {
        DriverManager.getConnection("jdbc:postgresql://localhost:5433/postgres", "myuser", "secret").use { connection ->
            val dbName = "mydatabase"  // Replace with your database name
            val statement = connection.createStatement()
            // Check if the database exists
            val resultSet = statement.executeQuery("SELECT 1 FROM pg_database WHERE datname = '$dbName'")
            // If the result set is empty, the database does not exist
            if (!resultSet.next()) {
                statement.execute("CREATE DATABASE $dbName WITH OWNER myuser")
                LoggerFactory.getLogger(R2DBCConfig::class.java).info("Database '$dbName' created.")
            } else {
                LoggerFactory.getLogger(R2DBCConfig::class.java).info("Database '$dbName' already exists.")
            }
        }
    }
}