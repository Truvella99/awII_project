package it.polito.communicationmanager.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.gmail.Gmail
import it.polito.communicationmanager.controllers.CommunicationController
import it.polito.communicationmanager.dtos.*
import it.polito.communicationmanager.exceptions.EmailProcessingException
import it.polito.communicationmanager.exceptions.WrongEmailException
import it.polito.communicationmanager.producers.CrmProducer
import org.apache.camel.*
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.google.mail.GoogleMailEndpoint
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.log
import com.google.api.services.gmail.model.Message as GmailMessage

const val USER = "aw2g52024@gmail.com"

@Service
class CommunicationServiceImpl() : CommunicationService {
    // logger to log messages in the APIs
    private val logger = LoggerFactory.getLogger(CommunicationController::class.java)
    private val googleMail: Gmail = GmailConfig.gmailClient()
    private val restTemplate = RestTemplate()

    override fun sendEmail(data: CreateEmailDTO): EmailDTO {
        if (data.from != USER && data.to == USER) {
            throw WrongEmailException("Wrong sender provided: ${data.from}, only aw2g52024@gmail.com can be provided; Wrong receiver provided: ${data.to}, aw2g52024@gmail.com cannot be provided.")
        } else if (data.from != USER) {
            throw WrongEmailException("Wrong sender provided: ${data.from}, only aw2g52024@gmail.com can be provided.")
        } else if (data.to == USER) {
            throw WrongEmailException("Wrong receiver provided: ${data.to}, aw2g52024@gmail.com cannot be provided.")
        }
        try {
            val message = createMessage(data)
            val msg = googleMail.users().messages().send(USER, message).execute()
            logger.info("Email sended to Google.")
            val createMessageDTO = CreateMessageDTO(
                channel = channel.email,
                priority = priority.medium,
                date = Date(),
                subject = data.subject,
                body = data.body,
                email = data.to,
                telephone = null,
                address = null
            )
            val responseEntity = restTemplate.postForEntity(
                "http://localhost:8081/API/messages/",
                createMessageDTO,
                CreateMessageDTO::class.java
            )
            logger.info("Email sended to CRM with body: ${responseEntity.body}")
            return EmailDTO(
                id = msg.id,
                from = data.from,
                to = data.to,
                subject = data.subject,
                body = data.body,
            )
        } catch (e: RuntimeException) {
            throw EmailProcessingException("Data processing error during email sending.")
        }
    }

    private fun createMessage(emailDetails: CreateEmailDTO): GmailMessage {
        val emailContent = """
            From: ${emailDetails.from}
            To: ${emailDetails.to}
            Subject: ${emailDetails.subject}

            ${emailDetails.body}
        """.trimIndent()

        val bytes = emailContent.toByteArray()
        val encodedEmail = Base64.getUrlEncoder().encodeToString(bytes)

        return GmailMessage().apply {
            raw = encodedEmail
        }
    }
}

@Component
class EMailRoute(private val crmProducer: CrmProducer)://private val producerTemplate: ProducerTemplate) :
    RouteBuilder() {
    @EndpointInject("google-mail:messages/list")
    lateinit var ep: GoogleMailEndpoint
    private val logger = LoggerFactory.getLogger(CommunicationController::class.java)

    @Autowired
    lateinit var objectMapper: ObjectMapper // Inject ObjectMapper bean
    override fun configure() {
        from ("google-mail-stream:0?markAsRead=true&scopes=https://mail.google.com")
        //from("google-mail://messages/list?userId=aw2g52024@gmail.com&scopes=https://mail.google.com")
            .process { exchange ->
                val id = exchange.`in`.headers.get("CamelGoogleMailId").toString()
                val msg = ep.client.users().messages().get(USER, id).execute()

                val date = msg.payload.headers
                    .find { it.name.equals("Date", true) }
                    ?.value ?: ""
                val dateformat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH)
                val dateObj = dateformat.parse(date)
                //println(dateObj)

                val subject = msg.payload.headers
                    .find { it.name.equals("subject", true) }
                    ?.value ?: ""
                val from = msg.payload.headers
                    .find { it.name.equals("from", true) }
                    ?.value?.substringAfter('<')?.substringBeforeLast('>') ?: ""
                val to = msg.payload.headers
                    .find { it.name.equals("to", true) }
                    ?.value?.substringAfter('<')?.substringBeforeLast('>') ?: ""
                var body = ep.client.users().messages().get(USER, id).setFormat("RAW").execute().raw
                body = body.replace("+/","-_")
                // Construct CreateMessageDTO object
                val createMessageDTO = CreateMessageDTO(
                    channel = channel.email,  // Example value, adjust as necessary
                    priority = priority.medium,  // Example value, adjust as necessary
                    date = dateObj,
                    subject = subject,
                    body = body,
                    email = if(from != USER) from else to, // email of the sender, the receiver is always you (USER)
                    telephone = null,  // Adjust if necessary
                    address = null  // Adjust if necessary
                )
                logger.info("Sending message: $createMessageDTO") // Log the message

                /*val jsonMessage = objectMapper.writeValueAsString(createMessageDTO)

                // Send the message using HTTP POST with JSON body
                producerTemplate.sendBodyAndHeader(
                    "http://localhost:8081/API/messages/",
                    jsonMessage,
                    Exchange.CONTENT_TYPE,
                    "application/json"
                )*/
                crmProducer.sendMessage("cm-crm",createMessageDTO)
            }
    }
}

@Configuration
class GmailConfig {
    companion object {
        fun gmailClient(): Gmail {
            val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
            val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
            val credential = GoogleCredential.Builder()
                .setTransport(httpTransport)
                .setJsonFactory(jsonFactory)
                .setClientSecrets(
                    "677529689861-7hcila8392i0sc6jcbsov4raqr00bf6f.apps.googleusercontent.com",
                    "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"
                )
                .build()

            // Set refresh token
            credential.refreshToken =
                "1//04UKTwen3jvgDCgYIARAAGAQSNwF-L9Ir43Qwm18tkkbr6ZUetSEBrMF3gveb_qC3CHNIEkPq5T0gCo4odr7oGEkTTrMDnZK-fLg"
            return Gmail.Builder(httpTransport, jsonFactory, credential)
                .setApplicationName("WebAppOAuthClient")
                .build()
        }
    }
}