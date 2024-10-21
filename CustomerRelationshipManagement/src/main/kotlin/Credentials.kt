package it.polito.customerrelationshipmanagement
import org.keycloak.representations.idm.CredentialRepresentation

class Credentials {

    companion object {
        fun createPasswordCredentials(password: String): CredentialRepresentation {
            val passwordCredentials = CredentialRepresentation()
            passwordCredentials.isTemporary = false
            passwordCredentials.type = CredentialRepresentation.PASSWORD
            passwordCredentials.value = password
            return passwordCredentials
        }
    }
}