package it.polito.customerrelationshipmanagement
import it.polito.customerrelationshipmanagement.controllers.CustomerController
import it.polito.customerrelationshipmanagement.dtos.UserDTO
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration

@Configuration
class KeycloakConfig {
    companion object {
        private val logger = LoggerFactory.getLogger(CustomerController::class.java)

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

        private fun createPasswordCredentials(password: String): CredentialRepresentation {
            val passwordCredentials = CredentialRepresentation()
            passwordCredentials.isTemporary = false
            passwordCredentials.type = CredentialRepresentation.PASSWORD
            passwordCredentials.value = password
            return passwordCredentials
        }

        fun addUser(userDTO: UserDTO): String? {
            val credential = createPasswordCredentials(userDTO.password)
            val user = UserRepresentation().apply {
                username = userDTO.userName
                firstName = userDTO.firstname
                lastName = userDTO.lastName
                email = userDTO.email
                credentials = listOf(credential)
                isEnabled = true
            }

            val response = getInstance().realm(realm).users().create(user)

            if (response.status == 201) { // 201 Created
                val locationHeader = response.headers["Location"]?.firstOrNull()?.toString()
                val uuid = locationHeader?.substringAfterLast("/")
                logger.info("Keycloak User ${user.username} created with UUID: $uuid")
                return uuid
            } else {
                logger.info("Failed to create Keycloak User ${user.username}. Status: ${response.status}")
                return null
            }
        }
    }
}