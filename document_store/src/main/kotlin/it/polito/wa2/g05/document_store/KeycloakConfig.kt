package it.polito.wa2.g05.document_store

import it.polito.wa2.g05.document_store.exceptions.IllegalIdException
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.context.annotation.Configuration

@Configuration
class KeycloakConfig {
    companion object {
        private var serverURL: String = "http://localhost:9090"
        private var realm: String = "CRMRealm"
        private var clientID: String = "crmclient"
        private var clientSecret: String = "UAGMutFg200hRp3pfFomluDh7GAQ8epl"
        private var keycloak: Keycloak? = null

        private fun getInstance(): Keycloak {
            if (keycloak == null) {
                keycloak = KeycloakBuilder.builder()
                    .realm(realm)
                    .serverUrl(serverURL)
                    .clientId(clientID)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .build()
            }
            return keycloak!!
        }

        fun checkExistingUser(uuid: String) {
            val keycloak = getInstance()
            try {
                // try to get the user, if not found throws an exception
                keycloak.realm(realm).users()[uuid].toRepresentation()
            } catch (e: RuntimeException) {
                throw IllegalIdException("Unable to Find Registered User with id:$uuid")
            }
        }
    }
}