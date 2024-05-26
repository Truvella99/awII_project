package it.polito.customerrelationshipmanagement

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.channel
import it.polito.customerrelationshipmanagement.entities.priority
import it.polito.customerrelationshipmanagement.entities.state
import it.polito.customerrelationshipmanagement.services.ContactService
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import it.polito.customerrelationshipmanagement.services.MessageService
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageTests: IntegrationTest() {
	@Autowired
	lateinit var messageService: MessageService

	@Autowired
	private lateinit var mockMvc: MockMvc


    // ----- GET /API/messages/ -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * and a non-empty list
	 * when all messages are successfully retrieved
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages1 () {
		// --- Arrange ---
		messageService.addMessage(
			CreateMessageDTO(
				channel = channel.phonecall,
				priority = priority.low,
				subject = "Test message 1",
				body = "This is a test message",
				email = null,
				telephone = "123-456-7890",
				address = null
			)
		)
		messageService.addMessage(
			CreateMessageDTO(
				channel = channel.phonecall,
				priority = priority.medium,
				subject = "Test message 2",
				body = "This is another test message",
				email = null,
				telephone = "123-456-0987",
				address = null
			)
		)

		mockMvc.get("http://localhost:8080/API/messages/") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			content {
				jsonPath("$") {
					isArray()
					isNotEmpty()
				}
			}
		}
	}

	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * and an empty list
	 * when there are no messages in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages2 () {
		mockMvc.get("http://localhost:8080/API/messages/") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			content {
				jsonPath("$") {
					isArray()
					isEmpty()
				}
			}
		}
	}


	// ----- POST /API/messages/ -----
	/**
	 * Verify that the endpoint returns a *201 Created* status
	 * when a message is successfully created
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages3 () {
		// --- Arrange ---
		// No messages in the database

		// --- Act ---
		val m1 = messageService.addMessage(
			CreateMessageDTO(
				channel = channel.phonecall,
				priority = priority.low,
				subject = "Test message 1",
				body = "This is a test message",
				email = null,
				telephone = "123-456-7890",
				address = null
			)
		)
		val result = messageService.listAllMessages(null, null, null, null)

		// --- Assert ---
		assert(result.isNotEmpty())
		assert(result.first() == m1)
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the request body is invalid or missing required fields
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages4 () {
		// --- Arrange ---
		val requestBody = """
			{
				"subject": "Test 1",
				"body": "This is a test message",
			}
		"""
		
		mockMvc.post("http://localhost:8080/API/messages/") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = requestBody
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
		}
	}


	// ----- GET /API/messages/{messageId} -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * when the message with the given ID is successfully retrieved
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages5 () {
		// --- Arrange ---
		val m1 = messageService.addMessage(
			CreateMessageDTO(
				channel = channel.phonecall,
				priority = priority.low,
				subject = "Test message 1",
				body = "This is a test message",
				email = null,
				telephone = "123-456-7890",
				address = null
			)
		)
		messageService.addMessage(
			CreateMessageDTO(
				channel = channel.phonecall,
				priority = priority.medium,
				subject = "Test message 2",
				body = "This is another test message",
				email = null,
				telephone = "123-456-0987",
				address = null
			)
		)
		messageService.addMessage(
			CreateMessageDTO(
				channel = channel.phonecall,
				priority = priority.medium,
				subject = "Test message 3",
				body = "This is yet another test message",
				email = null,
				telephone = "123-654-7890",
				address = null
			)
		)

		mockMvc.get("http://localhost:8080/API/messages/${m1.id}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			content {
				jsonPath("$") {
					isNotEmpty()
				}
			}
		}
	}

	/**
	 * Verify that the endpoint returns a *404 Not Found* status
	 * when the message with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages6 () {
		// --- Arrange ---
		val messageId = -1L

		mockMvc.get("http://localhost:8080/API/messages/$messageId") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
		}
	}


	// ----- POST /API/messages/{messageId} -----
	/**
	 * Verify that the endpoint returns a *201 Created* status
	 * when a message with the given ID is successfully created
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages7 () {
		// --- Arrange ---
		val m1 = messageService.addMessage(
			CreateMessageDTO(
				channel = channel.phonecall,
				priority = priority.low,
				subject = "Test message 1",
				body = "This is a test message",
				email = null,
				telephone = "123-456-7890",
				address = null
			)
		)

		// --- Act ---
		messageService.updateMessageState(m1.id, UpdateMessageStateDTO(state.received, "Message received"))

		// --- Assert ---
		val updatedMessage = messageService.getMessageById(m1.id)
		assert(updatedMessage.currentState == state.received)
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status 
	 * when the request body is invalid or missing required fields
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages8 () {
		// --- Arrange ---
		val m1 = messageService.addMessage(
			CreateMessageDTO(
				channel = channel.phonecall,
				priority = priority.low,
				subject = "Test message 1",
				body = "This is a test message",
				email = null,
				telephone = "123-456-7890",
				address = null
			)
		)

		mockMvc.post("http://localhost:8080/API/messages/${m1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = {}
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
		}
	}


	// ----- GET /API/messages/{messageId}/history -----
	/**
	 * Verify that the endpoint returns a *200 OK* status 
	 * when the history of the message with the given ID is successfully retrieved
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages9 () {
		// --- Arrange ---
		val m1 = messageService.addMessage(
			CreateMessageDTO(
				channel = channel.phonecall,
				priority = priority.low,
				subject = "Test message 1",
				body = "This is a test message",
				email = null,
				telephone = "123-456-7890",
				address = null
			)
		)

		mockMvc.get("http://localhost:8080/API/messages/${m1.id}/history") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
		}
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the message with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages10 () {
		// --- Arrange ---
		val invalidMessageId = -1L

		mockMvc.get("http://localhost:8080/API/messages/$invalidMessageId/history") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
		}
	}

	/**
	 * Verify that the endpoint returns a *204 No Content* status 
	 * when the message with the given ID exists but has no history
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages11 () {
		// --- Arrange ---
		val m1 = messageService.addMessage(
			CreateMessageDTO(
				channel = channel.phonecall,
				priority = priority.low,
				subject = "Test message 1",
				body = "This is a test message",
				email = null,
				telephone = "123-456-7890",
				address = null
			)
		)
		
		// --- Act ---
		val result = messageService.getChanges(m1.id)
		
		// --- Assert ---
		assert(result.isEmpty())
	}


	// ----- PUT /API/messages/{messageId}/priority -----
	/**
	 * Verify that the endpoint returns a *200 OK* status 
	 * when the priority of the message with the given ID is successfully updated
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages12 () {
		// --- Arrange ---
		val m1 = messageService.addMessage(
			CreateMessageDTO(
				channel = channel.phonecall,
				priority = priority.low,
				subject = "Test message 1",
				body = "This is a test message",
				email = null,
				telephone = "123-456-7890",
				address = null
			)
		)
		
		// --- Act ---
		val updatedPriority = UpdateMessagePriorityDTO(priority.high)
		val result = messageService.updateMessagePriority(m1.id, updatedPriority)
		
		// --- Assert ---
		assert(result.priority == updatedPriority.priority)
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status 
	 * when the message with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages13 () {
		// --- Arrange ---
		val updatedPriority = priority.high
		
		mockMvc.put("http://localhost:8080/API/messages/-1/priority") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = """
				{
					"priority": "$updatedPriority"
				}
			"""
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
		}
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the request body is invalid or missing required fields
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages14 () {
		// --- Arrange ---
		val m1 = messageService.addMessage(
			CreateMessageDTO(
				channel = channel.phonecall,
				priority = priority.low,
				subject = "Test message 1",
				body = "This is a test message",
				email = null,
				telephone = "123-456-7890",
				address = null
			)
		)
		
		mockMvc.put("http://localhost:8080/API/messages/${m1.id}/priority") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = {}
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
		}
	}


	// ----- PUT /API/messages/{messageId}/discard -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * when the message with the given ID is successfully discarded
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages15 () {
		// --- Arrange ---
		val m1 = messageService.addMessage(
			CreateMessageDTO(
				channel = channel.phonecall,
				priority = priority.low,
				subject = "Test message 1",
				body = "This is a test message",
				email = null,
				telephone = "123-456-7890",
				address = null
			)
		)
		
		// --- Act ---
		val result = messageService.deleteMessage(m1.id)
		
		// --- Assert ---
		assert(result != m1)
	}
	
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status 
	 * when the message with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun testMessages16 () {
		mockMvc.put("http://localhost:8080/API/messages/-1/discard") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = {}
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
		}
	}
}