package it.polito.customerrelationshipmanagement

import it.polito.customerrelationshipmanagement.IntegrationTest.Initializer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration


@SpringBootTest
@ContextConfiguration(initializers = [Initializer::class])
abstract class IntegrationTest {


    internal class Initializer: ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of().applyTo(applicationContext.environment)
        }
    }
}