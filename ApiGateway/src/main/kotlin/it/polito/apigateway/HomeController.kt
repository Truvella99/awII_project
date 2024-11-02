package it.polito.apigateway

import it.polito.apigateway.dtos.CreateUpdateDocumentDTO
import it.polito.apigateway.dtos.MetadataDTO
import it.polito.apigateway.exceptions.DocStoreException
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.*
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.security.Principal
import java.time.LocalDateTime

@RestController
class HomeController(private val authorizedClientService: OAuth2AuthorizedClientService) {

    // nullable since can be accessed also not logged in
    @GetMapping("","/")
    fun home(principal: Principal?): Map<String, Any?> {
        return mapOf(
            "name" to "home",
            "date" to LocalDateTime.now(),
            "principal" to principal
        )
    }

    // principal: Principal ==> spring knows hot to inject
    @GetMapping("/secure")
    fun secure(): Map<String, Any?> {
        val authentication = SecurityContextHolder.getContext().authentication
        return mapOf(
            "name" to "secure",
            "date" to LocalDateTime.now(),
            "principal" to authentication.principal
        )
    }

    @GetMapping("/me")
    fun me(
        @CookieValue(name="XSRF-TOKEN", required = false)
        xsrf: String?,
        authentication: Authentication?
    ): Map<String, Any?> {
        val principal: OidcUser? = authentication?.principal as? OidcUser
        val name = principal?.preferredUsername ?: ""
        return mapOf(
            "name" to name,
            "loginUrl" to "/oauth2/authorization/crmclient",
            "logoutUrl" to "/logout",
            "principal" to principal,
            "xsrfToken" to xsrf
        )
    }

    private fun getAccessToken(authentication: Authentication): String? {
        if (authentication is OAuth2AuthenticationToken) {
            val client = authorizedClientService.loadAuthorizedClient<OAuth2AuthorizedClient>(
                authentication.authorizedClientRegistrationId,
                authentication.name
            )
            return client?.accessToken?.tokenValue
        }
        return null
    }

    @PostMapping("/API/documents", "/API/documents/")
    fun createDocument(
        @RequestPart file: MultipartFile,
        @ModelAttribute d: CreateUpdateDocumentDTO,
        authentication: Authentication
    ): MetadataDTO {
        val client = WebClient.create("http://localhost:8083")
        val uri = "/API/documents/"
        val method = client.post()
        val accessToken = getAccessToken(authentication)

        val builder = MultipartBodyBuilder()
        builder.part("file", ByteArrayResource(file.resource.contentAsByteArray)).filename(file.originalFilename!!)
            .header(HttpHeaders.CONTENT_TYPE, file.contentType.toString())
        // Add additional fields from CreateUpdateDocumentDTO
        builder.part("userId", d.userId)
        builder.part("name", d.name)
        builder.part("contentType", d.contentType)
        builder.part("creationTimestamp", d.creationTimestamp.toString()) // Convert LocalDateTime to String

        val res = method
            .uri(uri)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header("Authorization", "Bearer $accessToken")
            .body(BodyInserters.fromMultipartData(builder.build()))
            .retrieve()
            .onStatus(
                {status -> status.is3xxRedirection || status.is4xxClientError || status.is5xxServerError},
                {response ->
                    response.bodyToMono(ProblemDetail::class.java)
                        .handle { pd, sink ->
                            sink.error(DocStoreException(HttpStatus.valueOf(pd.status),pd.detail!!))
                        }
                }
            )
            .bodyToMono(MetadataDTO::class.java)
            .block()

        return res!!
    }

    @PutMapping("/API/documents", "/API/documents/")
    fun updateDocument(
        @RequestPart file: MultipartFile,
        @ModelAttribute d: CreateUpdateDocumentDTO,
        authentication: Authentication
    ): MetadataDTO {
        val client = WebClient.create("http://localhost:8083")
        val uri = "/API/documents/"
        val method = client.put()
        val accessToken = getAccessToken(authentication)

        val builder = MultipartBodyBuilder()
        builder.part("file", ByteArrayResource(file.resource.contentAsByteArray)).filename(file.originalFilename!!)
            .header(HttpHeaders.CONTENT_TYPE, file.contentType.toString())
        // Add additional fields from CreateUpdateDocumentDTO
        builder.part("userId", d.userId)
        builder.part("name", d.name)
        builder.part("contentType", d.contentType)
        builder.part("creationTimestamp", d.creationTimestamp.toString()) // Convert LocalDateTime to String

        val res = method
            .uri(uri)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header("Authorization", "Bearer $accessToken")
            .body(BodyInserters.fromMultipartData(builder.build()))
            .retrieve()
            .onStatus(
                {status -> status.is3xxRedirection || status.is4xxClientError || status.is5xxServerError},
                {response ->
                    response.bodyToMono(ProblemDetail::class.java)
                        .handle { pd, sink ->
                            sink.error(DocStoreException(HttpStatus.valueOf(pd.status),pd.detail!!))
                        }
                }
            )
            .bodyToMono(MetadataDTO::class.java)
            .block()

        return res!!
    }

}
