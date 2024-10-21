package it.polito.customerrelationshipmanagement

import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider
import org.glassfish.jersey.client.ClientConfig
import org.glassfish.jersey.client.JerseyClientBuilder
import org.glassfish.jersey.client.ClientProperties
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import javax.ws.rs.client.Client

class KeycloakConfig private constructor() {

    companion object {
        private var keycloak: Keycloak? = null
        private const val serverUrl = "http://localhost:8080/"
        private const val realm = "CRMRealm"
        private const val clientId = "crmclient"
        private const val clientSecret = "UAGMutFg200hRp3pfFomluDh7GAQ8epl"
        private const val userName = "admin"
        private const val password = "password"

        // Crea il client HTTP usando Apache HttpClient
        private fun createHttpClient(): Client {
            val httpClient: CloseableHttpClient = HttpClients.createDefault()
            val clientConfig = ClientConfig()
            clientConfig.connectorProvider(ApacheConnectorProvider()) // Configura per usare Apache HttpClient
            clientConfig.property(ClientProperties.CONNECT_TIMEOUT, 10000)
            clientConfig.property(ClientProperties.READ_TIMEOUT, 10000)

            return JerseyClientBuilder.newClient(clientConfig) // Restituisce un client JAX-RS compatibile
        }

        fun getInstance(): Keycloak {
            if (keycloak == null) {
                val httpClient = createHttpClient()

                keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(userName)
                    .password(password)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .resteasyClient(httpClient) // Usa il client HTTP personalizzato
                    .build()

                println("Keycloak instance initialized successfully")
            }
            return keycloak!!
        }
    }
}