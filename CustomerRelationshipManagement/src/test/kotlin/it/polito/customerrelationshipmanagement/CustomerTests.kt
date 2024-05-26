package it.polito.customerrelationshipmanagement

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.category
import it.polito.customerrelationshipmanagement.entities.jobOfferStatus
import it.polito.customerrelationshipmanagement.services.CustomerService
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import jakarta.transaction.Transactional
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.mockito.ArgumentMatchers.contains
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.testcontainers.shaded.org.apache.commons.lang3.StringUtils.containsOnly


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerTests: IntegrationTest() {
	@Autowired
	lateinit var customerService: CustomerService

	@Autowired
	private lateinit var mockMvc: MockMvc
	val BASE_URL = "http://localhost:8080/API/"


	// ----- POST /API/customers/ -----
	/**
	 * Verify that the endpoint returns a *201 Created* status
	 * and the correctness of returned data
	 * when a new customer is successfully created
	 */
	@Test
	@Transactional
	@Rollback
	fun createCustomer() {
		val name = "Mario"
		val surname = "Rossi"
		val ssncode = "111-23-9025"
		val category = "customer"
		val email = "mario.rossi@example.com"
		val telephone = "+393312085641"
		val address = "123 Roma Street"
		val notes = listOf("Italian", "Turin", "Engineering")
		val jobOffers = listOf(
			Pair(null, CreateUpdateJobOfferDTO(
				name = "Offerta di lavoro 1",
				description = "Descrizione dell'offerta di lavoro 1",
				currentState = jobOfferStatus.created,
				currentStateNote = "Note sullo stato corrente dell'offerta di lavoro 1",
				duration = 6,
				profitMargin = 10.5,
				customerId = null,
				skills = listOf(
					CreateSkillDTO("Skill 1", null, null),
					CreateSkillDTO("Skill 2", null, null)
				),
			))
		)
		val customer = """{
			  "name": "$name",
			  "surname": "$surname",
			  "ssncode": "$ssncode",
			  "category": "$category",
			  "email": "$email",
		      "telephone": "$telephone",
			  "address": "$address",
              "notes": ${notes.map { "\"$it\"" }},
			  "jobOffers": [
				  {
					"first": null,
					"second": {
					  "name": "Job offer 1",
					  "description": "Description of job offer 1",
					  "currentState": "created",
					  "currentStateNote": "Current state note of job offer 1",
					  "duration": 6,
					  "profitMargin": 10.5,
					  "skills": [
						{
						  "skill": "Skill 1"
						},
						{
						  "skill": "Skill 2"
						}
					  ]
					}
				  }
				]
 		}""".trimIndent()

		mockMvc.post(BASE_URL + "customers/") {
			contentType = MediaType.APPLICATION_JSON
			content = customer
		}.andExpect {
			status { isCreated() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.id") { exists() }
			jsonPath("$.name") { value(name) }
			jsonPath("$.surname") { value(surname) }
			jsonPath("$.ssncode") { value(ssncode) }
			jsonPath("$.category") { value(category) }

			jsonPath("$.emails[0].email") { value(email) }
			jsonPath("$.emails[0].state") { value("active") }

			jsonPath("$.telephones[0].telephone") { value(telephone) }
			jsonPath("$.telephones[0].state") { value("active") }

			jsonPath("$.addresses[0].address") { value(address) }
			jsonPath("$.addresses[0].state") { value("active") }


			jsonPath("$.notes") { isArray() }
			notes.forEach { note ->
				jsonPath("$.notes[*].note") { contains(note) }
				jsonPath("$.notes[?(@.note == '$note')].state") { value("active") }

			}


			jsonPath("$.jobOffers") { isArray() }
			// Checks for the first job offer
			jsonPath("$.jobOffers[0].name") { value("Job offer 1") }
			jsonPath("$.jobOffers[0].description") { value("Description of job offer 1") }
			jsonPath("$.jobOffers[0].currentState") { value("created") }
			jsonPath("$.jobOffers[0].currentStateNote") { value("Current state note of job offer 1") }
			jsonPath("$.jobOffers[0].duration") { value(6) }
			jsonPath("$.jobOffers[0].profitMargin") { value(10.5) }
			jsonPath("$.jobOffers[0].skills[*].skill") { contains("Skill 1") }
			jsonPath("$.jobOffers[0].skills[*].skill") { contains("Skill 2") }
			jsonPath("$.jobOffers[0].skills[*].state") { containsOnly("active") }

			// Checks for the second job offer
			/*jsonPath("$.jobOffers[*].name") { value("Job offer 2") }
			jsonPath("$.jobOffers[*].description") { value("Description of job offer 2") }
			jsonPath("$.jobOffers[*].currentState") { value("created") }
			jsonPath("$.jobOffers[*].currentStateNote") { value("Current state note of job offer 2") }
			jsonPath("$.jobOffers[*].duration") { value(12) }
			jsonPath("$.jobOffers[*].profitMargin") { value(15.75) }
			jsonPath("$.jobOffers[*].skills[*].skill") { value("Skill 3") }
			jsonPath("$.jobOffers[*].skills[*].skill") { value("Skill 4") }*/
		}
	}


	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the request body is invalid or missing required fields
	 */
	@Test
	@Transactional
	@Rollback
	fun createCustomerInvalidBody() {
		val requestBody = """
			{
				"name": " ",
				"surname": "Rossi"
			}
		"""

		mockMvc.post("http://localhost:8080/API/customers/") {
			contentType = MediaType.APPLICATION_JSON
			content = requestBody
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			jsonPath("$.detail") { value("Invalid request content.") }
		}
	}


	// ----- POST /API/customers/{customerId}/note -----
	/**
	 * Verify the correctness of returned data
	 * when the note of the customer with the given ID is successfully updated or added
	 */
	@Test
	@Transactional
	@Rollback
	fun addCustomerNote() {
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street",
				notes = listOf(),
				jobOffers = listOf()
			)
		)
		val note = """
			{
				"note": "Test note added"
			}
		"""
		mockMvc.post(BASE_URL + "customers/${c1.id}/note") {
			contentType = MediaType.APPLICATION_JSON
			content = note
		}.andExpect {
			status { isCreated() }
			jsonPath("$.id") { exists() }
			jsonPath("$.customerId") { value(c1.id) }
			jsonPath("$.note") { value("Test note added") }
			jsonPath("$.professionalId") { value(null) }
			jsonPath("$.state") { value("active") }
		}
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the request body is invalid or missing required fields
	 */
	@Test
	@Transactional
	@Rollback
	fun addCustomerNoteInvalidId() {
		val customerId = -1

		mockMvc.post(BASE_URL + "customers/${customerId}/note") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = """
			{
				"note": "Test note added"
			}
		"""
		}.andExpect {
			status { isBadRequest() }
			jsonPath("$.detail") { value("Invalid customerId Parameter.") }
		}
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the customer with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun addCustomerNoteCustomerNotExists() {
		val newNote = "Test note"
		val customerIdNotExisting = 9999
		mockMvc.post(BASE_URL + "customers/${customerIdNotExisting}/note") {
			contentType = MediaType.APPLICATION_JSON
			content = """
				{
					"note": "$newNote"
				}
			"""
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Customer with CustomerId:$customerIdNotExisting not found") }

		}
	}


	// ----- GET /API/customers/{customerId} -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * and the correct data
	 * when the customer with the given ID exists in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun findCustomerById() {
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street",
				notes = listOf(),
				jobOffers = listOf()
			)
		)

		mockMvc.get("http://localhost:8080/API/customers/${c1.id}") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			// Check customer details
			jsonPath("$.id") { value(c1.id) }
			jsonPath("$.name") { value("Mario") }
			jsonPath("$.surname") { value("Rossi") }
			jsonPath("$.ssncode") { value("000-01-1234") }
			jsonPath("$.category") { value("customer") }
			jsonPath("$.emails[0].email") { value("mario.rossi@example.com") }
			jsonPath("$.telephones[0].telephone") { value("123-456-7801") }
			jsonPath("$.addresses[0].address") { value("123 Roma Street") }
			jsonPath("$.notes") { isArray() }
			jsonPath("$.jobOffers") { isArray() }
		}
	}

	/**
	 * Verify that the endpoint correctly handles an invalid ID
	 */
	@Test
	@Transactional
	@Rollback
	fun findCustomerByIdInvalidId() {
		val customerId = -1

		mockMvc.get("http://localhost:8080/API/customers/$customerId") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			jsonPath("$.detail") { value("Invalid customerId Parameter.") }

		}
	}

	/**
	 * Verify that the endpoint correctly handles a not existing ID
	 */
	@Test
	@Transactional
	@Rollback
	fun findNonExistingCustomer() {
		val nonExistentCustomerId = 999

		mockMvc.get("http://localhost:8080/API/customers/$nonExistentCustomerId") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			jsonPath("$.detail") { value("Customer with CustomerId:$nonExistentCustomerId not found") }

		}
	}
	// ----- PUT /API/customers/{customerId} -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * when the name of the customer with the given ID is successfully updated
	 */
	@Test
	@Transactional
	@Rollback
	fun updateCustomer() {
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street",
				notes = listOf("Italian", "Turin", "Engineering"),
				//jobOffers = listOf()
				jobOffers = listOf(
					Pair(
						null, CreateUpdateJobOfferDTO(
							name = "Offerta di lavoro 1",
							description = "Descrizione dell'offerta di lavoro 1",
							currentState = jobOfferStatus.created,
							currentStateNote = "Note sullo stato corrente dell'offerta di lavoro 1",
							duration = 6,
							profitMargin = 10.5,
							customerId = null,//associa automaticamente il customerId appena creato
							skills = listOf(
								CreateSkillDTO("Skill 1", null, null),
								CreateSkillDTO("Skill 2", null, null)
							),
							skillsToDelete = null
						)
					)
				)
			)
		)
		val updatedName = "Luigi"
		val updatedSurname = "Verdi"
		val updatedSsnCode = "111-23-9025"
		val updatedEmail = "luigi.verdi@example.com"
		val updatedTelephone = "+393312085642"
		val updatedAddress = "124 Roma Street"
		val updatedNotes = listOf("Updated Note")
		val notesToDelete = listOf(c1.notes.first().id, c1.notes.last().id)

		val customer = """{
			  "name": "$updatedName",
			  "surname": "$updatedSurname",
			  "ssncode": "$updatedSsnCode",
			  "email": "$updatedEmail",
		      "telephone": "$updatedTelephone",
			  "address": "$updatedAddress",
			  "notes": ${updatedNotes.map { "\"$it\"" }},
			  "notesToDelete": $notesToDelete,
			  "jobOffers": [
				  {
					"first":  ${c1.jobOffers.first().id},
					"second": {
					  "name": "Job offer updated",
					  "description": "Description of job offer 1 update",
					  "currentState": "created",
					  "currentStateNote": "Current state note of job offer update",
					  "duration": 7,
					  "profitMargin": 15,
					  "skills": [
						{
						  "skill": "Skill 3"
						},
						{
						  "skill": "Skill 4"
						}
					  ]
					}
				  },{
					"first":  null,
					"second": {
					  "name": "Job offer added 2",
					  "description": "Description of job offer 1 added",
					  "currentState": "created",
					  "currentStateNote": "Current state note of job offer added",
					  "duration": 7,
					  "profitMargin": 15,
					  "skills": [
						{
						  "skill": "Skill 3"
						},
						{
						  "skill": "Skill 4"
						}
					  ]
					}
				  }
				]
 		}""".trimIndent()
		mockMvc.put(BASE_URL + "customers/${c1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = customer
		}.andExpect {
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.id") { value(c1.id) }
			jsonPath("$.name") { value(updatedName) }
			jsonPath("$.surname") { value(updatedSurname) }
			jsonPath("$.ssncode") { value(updatedSsnCode) }
			jsonPath("$.category") { value("customer") }

			jsonPath("$.emails[*].email") { contains(updatedEmail) }
			jsonPath("$.emails[*].state") { containsOnly("active") }

			jsonPath("$.telephones[*].telephone") { contains(updatedTelephone) }
			jsonPath("$.telephones[*].state") { containsOnly("active") }

			jsonPath("$.addresses[*].address") { contains(updatedAddress) }
			jsonPath("$.addresses[*].state") { containsOnly("active") }

			jsonPath("$.notes") { isArray() }
			updatedNotes.forEach { note ->
				jsonPath("$.notes[*].note") { contains(note) }
				jsonPath("$.notes[*].professionalId") { containsOnly(null) }
				jsonPath("$.notes[?(@.note == '$note')].state") { value("active") }

			}
			notesToDelete.forEach { noteId ->
				jsonPath("$.notes[?(@.id == $noteId)].state") { value("deleted") }
			}

			jsonPath("$.jobOffers") { isArray() }
			// Checks for the  job offer
			jsonPath("$.jobOffers[0].name") { value("Job offer updated") }
			jsonPath("$.jobOffers[0].description") { value("Description of job offer 1 update") }
			jsonPath("$.jobOffers[0].currentState") { value("created") }
			jsonPath("$.jobOffers[0].currentStateNote") { value("Current state note of job offer update") }
			jsonPath("$.jobOffers[0].duration") { value(7) }
			jsonPath("$.jobOffers[0].profitMargin") { value(15.0) }
			jsonPath("$.jobOffers[0].skills[*].skill") { contains("Skill 1") }
			jsonPath("$.jobOffers[0].skills[*].skill") { contains("Skill 2") }
			jsonPath("$.jobOffers[0].skills[*].skill") { contains("Skill 3") }
			jsonPath("$.jobOffers[0].skills[*].skill") { contains("Skill 4") }
			jsonPath("$.jobOffers[*].skills[*].state") { containsOnly("active") }

		}
	}

	/**
	 * Verify that the endpoint returns a *404 Not Found* status
	 * when the customer with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun updateCustomerInvalidId() {
		val customerId = -1

		mockMvc.put(BASE_URL + "customers/${customerId}") {
			contentType = MediaType.APPLICATION_JSON
			content = """
			{
				"name": "Mario"
		}""".trimIndent()
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			jsonPath("$.detail") { value("Invalid customerId Parameter.") }

		}
	}

	/**
	 * Verify that the endpoint returns a *404 Not Found* status
	 * when the request body is invalid or missing required fields
	 */
	@Test
	@Transactional
	@Rollback
	fun updateNonExistingCustomer() {
		val nonExistentCustomerId = 999

		mockMvc.put(BASE_URL + "customers/${nonExistentCustomerId}") {
			contentType = MediaType.APPLICATION_JSON
			content = """
			{
				"name": "Mario"
		}""".trimIndent()
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			jsonPath("$.detail") { value("Customer with CustomerId:$nonExistentCustomerId not found") }

		}
	}
	@Test
	@Transactional
	@Rollback
	fun updateNonExistingJobOffer() {
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street",
				notes = listOf("Italian", "Turin", "Engineering"),
				jobOffers = listOf()
			)
		)

		val nonExistentJobOfferId = 999
		val customerJson = """
        {
            "name": "Mario",
            "jobOffers": [
                {
                    "first": $nonExistentJobOfferId,
                    "second": {
                        "name": "Job offer updated",
                        "description": "Description of job offer 1 update",
                        "currentStateNote": "Current state note of job offer update",
                        "duration": 7,
                        "profitMargin": 15,
                        "skills": [
                            {
                                "skill": "Skill 3"
                            },
                            {
                                "skill": "Skill 4"
                            }
                        ]
                    }
                }
            ]
        }
    """.trimIndent()

		mockMvc.put("$BASE_URL/customers/${c1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = customerJson
		}.andExpect {
			status { isNotFound() }
			jsonPath("$.detail") { value("Job Offer with Id:$nonExistentJobOfferId not found") }
		}
	}
	@Test
	@Transactional
	@Rollback
	fun updateJobOfferofOthersCustomers() {
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street",
				notes = listOf("Italian", "Turin", "Engineering"),
				jobOffers = listOf()
			)
		)
		val otherCustomer = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Luigi",
				surname = "Verdi",
				ssncode = "000-01-1235",
				category = category.customer,
				email = "luigi.verdi@gmail.com" ,
				telephone = "123-456-7801",
				address = "123 Roma Street",
				notes = listOf("Italian", "Turin", "Engineering"),
				jobOffers = listOf(
					Pair(
						null, CreateUpdateJobOfferDTO(
							name = "Offerta di lavoro 1",
							description = "Descrizione dell'offerta di lavoro 1",
							currentState = jobOfferStatus.created,
							currentStateNote = "Note sullo stato corrente dell'offerta di lavoro 1",
							duration = 6,
							profitMargin = 10.5,
							customerId = null,
							skills = listOf(
								CreateSkillDTO("Skill 1", null, null),
								CreateSkillDTO("Skill 2", null, null)
							),
						)
					)
				)
			))
		val customerJson = """
        {
            "jobOffers": [
                {
                    "first": ${otherCustomer.jobOffers.first().id},
                    "second": {
                        "name": "Job offer updated",
                        "description": "Description of job offer 1 update",
                        "currentStateNote": "Current state note of job offer update",
                        "duration": 7,
                        "profitMargin": 15,
                        "skills": [
                            {
                                "skill": "Skill 3"
                            },
                            {
                                "skill": "Skill 4"
                            }
                        ]
                    }
                }
            ]
        }
    """.trimIndent()

		mockMvc.put("$BASE_URL/customers/${c1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = customerJson
		}.andExpect {
			status { isForbidden() }
			jsonPath("$.detail") { value("Cannot Update Job Offer of Another Customer with Id:${otherCustomer.jobOffers.first().id}") }
		}
	}
	@Test
	@Transactional
	@Rollback
	fun updateInvalideNoteId() {
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street",
				notes = listOf(),
				jobOffers = listOf()
			)
		)
		val invalidId = -1
		mockMvc.put(BASE_URL + "customers/${c1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = """
			{
				"notesToDelete": [$invalidId]
		}""".trimIndent()
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			jsonPath("$.detail") { value("Invalid noteId Parameter.") }

		}
	}
	@Test
	@Transactional
	@Rollback
	fun updateExistingNoteofOtherCustomer() {
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street",
				notes = listOf(),
				jobOffers = listOf()
			)
		)
		val otherCustomer = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Luigi",
				surname = "Verdi",
				ssncode = "000-01-1235",
				category = category.customer,
				email = "luigi.verdi@gmail.com" ,
				telephone = "123-456-7801",
				address = "123 Roma Street",
				notes = listOf("Italian", "Turin", "Engineering"),
				jobOffers = listOf()))

		val otherCustomerNote = otherCustomer.notes.first().id
		mockMvc.put(BASE_URL + "customers/${c1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = """
			{
				"notesToDelete": [$otherCustomerNote]
		}""".trimIndent()
		}.andExpect {
			// --- Assert ---
			status { isForbidden() }
			jsonPath("$.detail") { value("Note with NoteId:${otherCustomerNote} does not belong to this customer.") }

		}
	}
	@Test
	@Transactional
	@Rollback
	fun updateNotExistingNote() {
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street",
				notes = listOf(),
				jobOffers = listOf()
			)
		)
		val nonExistentNoteId = 999
		mockMvc.put(BASE_URL + "customers/${c1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = """
			{
				"notesToDelete": [$nonExistentNoteId]
		}""".trimIndent()
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			jsonPath("$.detail") { value("Note with NoteId:$nonExistentNoteId not found") }

		}
	}
	@Test
	@Transactional
	@Rollback
	fun updateCustomerNoteAlreadyDeleted() {
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street",
				notes = listOf("Italian"),
				jobOffers = listOf()
			)
		)
		val noteId = c1.notes.first().id
		customerService.deleteCustomerNote(
			c1.id,
			noteId
		)

		mockMvc.put(BASE_URL + "customers/${c1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = """
			{
				"notesToDelete": [$noteId]
		}""".trimIndent()
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			jsonPath("$.detail") { value("Note with NoteId:${noteId} already deleted.")}

		}
	}

}
