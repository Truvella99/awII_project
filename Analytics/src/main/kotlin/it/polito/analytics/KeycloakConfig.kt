package it.polito.analytics

import it.polito.analytics.exceptions.IllegalIdException
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt

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

        fun checkExistingUserById(uuid: String) {
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


fun getUserKeycloakIdRole(authentication: Authentication): Pair<String,String> {
    val principal = authentication.principal as Jwt
    val keycloakId = principal.getClaim<String>("sub") // Extract the Keycloak ID (subject claim)
    var keycloakRole: String = "";
    // Extract the realm roles from the JWT token
    val keycloakRoles = listOf("customer","professional","operator","manager")
    val roles = principal.getClaim<Map<String, List<String>>>("realm_access")?.get("roles") ?: emptyList()
    roles.forEach { role ->
        if (keycloakRoles.contains(role)) {
            keycloakRole = role;
        }
    }
    return keycloakId to keycloakRole
}