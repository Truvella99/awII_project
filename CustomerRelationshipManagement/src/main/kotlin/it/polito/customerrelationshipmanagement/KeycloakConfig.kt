package it.polito.customerrelationshipmanagement
import it.polito.customerrelationshipmanagement.controllers.CustomerController
import it.polito.customerrelationshipmanagement.dtos.CreateUpdateUserDTO
import it.polito.customerrelationshipmanagement.entities.category
import it.polito.customerrelationshipmanagement.exceptions.*
import org.keycloak.OAuth2Constants
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.KeycloakBuilder
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import java.io.File


@Configuration
class KeycloakConfig {
    companion object {
        private val logger = LoggerFactory.getLogger(CustomerController::class.java)

        private var serverURL: String = if(isRunningInDocker()) "http://keycloak:9090" else "http://localhost:9090";
        private var realm: String = "CRMRealm"
        private var clientID: String = "crmclient"
        private var clientSecret: String = "UAGMutFg200hRp3pfFomluDh7GAQ8epl"
        private var keycloak: Keycloak? = null

        private fun isRunningInDocker(): Boolean {
            return File("/.dockerenv").exists()
        }

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

        private fun checkExistingUserByUsername(userName: String) {
            val keycloak = getInstance()
            try {
                // try to get the user, if not found throws an exception
                val userRepresentations = keycloak.realm(realm).users().searchByUsername(userName,true)
                if (userRepresentations.isNotEmpty()) {
                    throw ContactAlreadyExistsException("Unable to Register The User: $userName is already used.")
                }
            } catch (e: RuntimeException) {
                if (e is ContactAlreadyExistsException) {
                    throw e
                }
                throw ContactException("Unable to Register User with username:$userName")
            }
        }

        fun addUser(createUpdateUserDTO: CreateUpdateUserDTO, Category: category): String {
            checkExistingUserByUsername(createUpdateUserDTO.userName!!)
            try {
                val role: String = when (Category) {
                    category.customer -> category.customer.toString()
                    category.professional -> category.professional.toString()
                    else -> throw ContactException("Invalid Category.")
                }

                val credential = createPasswordCredentials(createUpdateUserDTO.password!!)
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
                // No Username change check since I don't use it directly, so not possible to update username
                val user = UserRepresentation().apply {
                    firstName = createUpdateUserDTO.firstname
                    lastName = createUpdateUserDTO.lastName
                    email = createUpdateUserDTO.email
                    //credentials = listOf(credential)
                    isEnabled = true
                }
                // update main info
                userResource.update(user)
                // update password if present
                if (createUpdateUserDTO.password != null) {
                    val credential = createPasswordCredentials(createUpdateUserDTO.password)
                    userResource.resetPassword(credential)
                }
                logger.info("Keycloak User ${user.firstName} updated with UUID: $uuid")
            } catch (e: RuntimeException) {
                throw ContactException("Unable to Update User ${createUpdateUserDTO.firstname}.")
            }
        }

        fun checkExistingUserById(uuid: String): Boolean {
            val keycloak = getInstance()
            try {
                // try to get the user, if not found throws an exception
                keycloak.realm(realm).users()[uuid].toRepresentation()
                return true
            } catch (e: RuntimeException) {
                return false
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