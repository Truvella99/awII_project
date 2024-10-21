package it.polito.customerrelationshipmanagement

import org.glassfish.jersey.client.JerseyClientBuilder
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import javax.ws.rs.client.Client

class KeycloakConfig private constructor() {

    companion object {
        private var keycloak: Keycloak? = null
        private const val serverUrl = "http://localhost:9090/"
        private const val realm = "CRMRealm"
        private const val clientId = "crmclient"
        private const val clientSecret = "UAGMutFg200hRp3pfFomluDh7GAQ8epl"
        private const val userName = "admin"
        private const val password = "password"

        // Crea il client HTTP usando JerseyClientBuilder
        private fun createJerseyHttpClient(): Client {
            return JerseyClientBuilder.newClient()
        }

        fun getInstance(): Keycloak {
            if (keycloak == null) {
                val httpClient = createJerseyHttpClient()

                keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(userName)
                    .password(password)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .resteasyClient(httpClient) // Usa il client HTTP di Jersey
                    .build()

                println("Keycloak instance initialized successfully")
            }
            return keycloak!!
        }
    }
}