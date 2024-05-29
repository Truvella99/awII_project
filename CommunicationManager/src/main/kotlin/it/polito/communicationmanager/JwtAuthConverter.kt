package it.polito.communicationmanager

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.JwtClaimNames
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.oauth2.jwt.Jwt

@Component
class JwtAuthConverter(
    private val properties: JwtAuthConverterProperties
) : Converter<Jwt, AbstractAuthenticationToken> {

    private val jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()

    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
        val authorities = (jwtGrantedAuthoritiesConverter.convert(jwt)?.toSet() ?: emptySet<GrantedAuthority>()) +
                extractResourceRoles(jwt)
        return JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt))
    }

    private fun getPrincipalClaimName(jwt: Jwt): String {
        val claimName = properties.principalAttribute ?: JwtClaimNames.SUB
        return jwt.getClaim(claimName) ?: ""
    }

    private fun extractResourceRoles(jwt: Jwt): Collection<GrantedAuthority> {
        return try {
            val resourceAccess = jwt.getClaim<Map<String, Any>>("realm_access")
            val resourceRoles = resourceAccess?.get("roles") as? Collection<String>
            resourceRoles?.map { role -> SimpleGrantedAuthority("ROLE_$role") }?.toSet() ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
    }
}
