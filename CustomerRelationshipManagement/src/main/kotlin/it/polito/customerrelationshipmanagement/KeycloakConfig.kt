package it.polito.customerrelationshipmanagement
import it.polito.customerrelationshipmanagement.controllers.CustomerController
import it.polito.customerrelationshipmanagement.dtos.CreateUpdateUserDTO
import it.polito.customerrelationshipmanagement.entities.category
import it.polito.customerrelationshipmanagement.exceptions.ContactException
import it.polito.customerrelationshipmanagement.exceptions.CustomerException
import it.polito.customerrelationshipmanagement.exceptions.ProfessionalException
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

        fun addUser(createUpdateUserDTO: CreateUpdateUserDTO, Category: category): String {
            try {
                val role: String = when (Category) {
                    category.customer -> category.customer.toString()
                    category.professional -> category.professional.toString()
                    else -> throw ContactException("Invalid Category.")
                }

                val credential = createPasswordCredentials(createUpdateUserDTO.password)
                val user = UserRepresentation().apply {
                    username = createUpdateUserDTO.userName
                    firstName = createUpdateUserDTO.firstname
                    lastName = createUpdateUserDTO.lastName
                    email = createUpdateUserDTO.email
                    credentials = listOf(credential)
                    isEnabled = true
                }
                val keycloak = getInstance();
                val response = keycloak.realm(realm).users().create(user)

                if (response.status == 201) { // 201 Created
                    val locationHeader = response.headers["Location"]?.firstOrNull()?.toString()
                    val uuid = locationHeader?.substringAfterLast("/")
                    // Retrieve the role from Keycloak by name
                    val roleRepresentation = keycloak.realm(realm).roles().get(role).toRepresentation()
                    // Assign the role to the user
                    keycloak.realm(realm).users().get(uuid).roles().realmLevel().add(listOf(roleRepresentation))
                    logger.info("Keycloak User ${user.username} created with UUID: $uuid")
                    return uuid!!
                } else {
                    when (Category) {
                        category.customer -> {
                            throw CustomerException("Failed to create Keycloak User ${user.username}. Status: ${response.status}")
                        }
                        category.professional -> {
                            throw ProfessionalException("Failed to create Keycloak User ${user.username}. Status: ${response.status}")
                        }
                        else -> throw ContactException("Invalid Category.")
                    }
                }
            } catch (e: RuntimeException) {
                throw ContactException("Unable to Register User ${createUpdateUserDTO.userName}.")
            }
        }

        fun updateUser(uuid: String, createUpdateUserDTO: CreateUpdateUserDTO) {
            try {
                val keycloak = getInstance()
                // get user
                val userResource = keycloak.realm(realm).users()[uuid]
                val credential = createPasswordCredentials(createUpdateUserDTO.password)
                val user = UserRepresentation().apply {
                    firstName = createUpdateUserDTO.firstname
                    lastName = createUpdateUserDTO.lastName
                    email = createUpdateUserDTO.email
                    credentials = listOf(credential)
                    isEnabled = true
                }
                // update main info
                userResource.update(user)
                // update password
                userResource.resetPassword(credential)
                logger.info("Keycloak User ${user.firstName} updated with UUID: $uuid")
            } catch (e: RuntimeException) {
                throw ContactException("Unable to Update User ${createUpdateUserDTO.firstname}.")
            }
        }
    }
}
