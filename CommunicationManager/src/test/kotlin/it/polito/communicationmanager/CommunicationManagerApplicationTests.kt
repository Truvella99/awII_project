package it.polito.communicationmanager

import it.polito.communicationmanager.dtos.CreateEmailDTO
import it.polito.communicationmanager.services.CommunicationService
import it.polito.customerrelationshipmanagement.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommunicationManagerApplicationTests : IntegrationTest() {


    @Autowired
    private lateinit var mockMvc: MockMvc

    val BASE_URL = "http://localhost:8081/API/"

    @Test
    @Rollback
    fun sendEmail() {
        val data = """{
        "from": "aw2g52024@gmail.com",
        "to": "gagliardo9974@gmail.com",
        "subject": "new email",
        "body": "Lorem ipsum dolor sit amet consectetur adipisicing elit."
    }"""


        mockMvc.post(BASE_URL + "emails/") {
            contentType = MediaType.APPLICATION_JSON
            content = data
        }.andExpect {
            status { isCreated() }
            content { contentType("application/json") }
            jsonPath("$.from") { value("aw2g52024@gmail.com") }
            jsonPath("$.to") { value("gagliardo9974@gmail.com") }
            jsonPath("$.subject") { value("new email") }
            jsonPath("$.id") { isString() }
            jsonPath("$.id") { isNotEmpty() }
            jsonPath("$.body") {
                value("Lorem ipsum dolor sit amet consectetur adipisicing elit.")

            }
        }
    }
        @Test
        @Rollback
        fun sendEmailWithWrongSender() {
            val data = """{
        "from": "aw252024@gmail.com",
        "to": "gagliardo9974@gmail.com",
        "subject": "new email",
        "body": "Lorem ipsum dolor sit amet consectetur adipisicing elit."
    }"""

            mockMvc.post(BASE_URL + "emails/") {
                contentType = MediaType.APPLICATION_JSON
                content = data
            }.andExpect {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
                jsonPath("$.detail") { value("Wrong sender provided: aw252024@gmail.com, only aw2g52024@gmail.com can be provided.") }
            }
        }

    @Test
    @Rollback
    fun sendEmailWithWrongReceiver() {
        val data = """{
        "from": "aw2g52024@gmail.com",
        "to": "aw2g52024@gmail.com",
        "subject": "new email",
        "body": "Lorem ipsum dolor sit amet consectetur adipisicing elit."
    }"""

        mockMvc.post(BASE_URL + "emails/") {
            contentType = MediaType.APPLICATION_JSON
            content = data
        }.andExpect {
            status { isBadRequest() }
            content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.detail") { value("Wrong receiver provided: aw2g52024@gmail.com, aw2g52024@gmail.com cannot be provided.") }
        }
    }




    @Test
    @Rollback
    fun sendEmailWithWrongSenderAndReceiver() {
        val data = """{
        "from": "gagliardo9974@gmail.com",
        "to": "aw2g52024@gmail.com",
        "subject": "new email",
        "body": "Lorem ipsum dolor sit amet consectetur adipisicing elit."
    }"""

        mockMvc.post(BASE_URL + "emails/") {
            contentType = MediaType.APPLICATION_JSON
            content = data
        }.andExpect {
            status { isBadRequest() }
            content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
            jsonPath("$.detail") { value("Wrong sender provided: gagliardo9974@gmail.com, only aw2g52024@gmail.com can be provided; Wrong receiver provided: aw2g52024@gmail.com, aw2g52024@gmail.com cannot be provided.") }
        }
    }

        @Test
        @Rollback
        fun sendEmailWithInvalidParameters() {
            val data = """{
        "from": "aw252024gmail.com",
        "to": "gagliardo9974@gmail.com",
        "subject": "",
        "body": "Lorem ipsum dolor sit amet consectetur adipisicing elit."
    }"""

            mockMvc.post(BASE_URL + "emails/") {
                contentType = MediaType.APPLICATION_JSON
                content = data
            }.andExpect {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
                jsonPath("$.detail") { value("Invalid request content.") }
            }
        }


}