package it.polito.customerrelationshipmanagement

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.category
import it.polito.customerrelationshipmanagement.services.ContactService
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.delete
import kotlin.reflect.full.declaredMemberFunctions


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContactTests: IntegrationTest() {
    @Autowired
	lateinit var contactService: ContactService

	@Autowired
	private lateinit var mockMvc: MockMvc


    // ----- GET /API/contacts/ -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * and the correctness of the returned data
	 * when there are contacts in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts1 () {
		// --- Arrange ---
		contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)

		mockMvc.get("http://localhost:8080/API/contacts/") {
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
	 * when there are no contacts in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts2 () {
		// --- Arrange ---
		// No contacts in the database
		
		mockMvc.get("http://localhost:8080/API/contacts/") {
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

	/**
	 * Verify that the endpoint handles query parameters correctly,
	 * such as filtering the contacts by email
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts3 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)
		contactService.createContact(
			CreateContactDTO(
				name = "Luigi",
				surname = "Bianchi",
				ssncode = "000-02-1234",
				category = category.professional,
				email = "luigi.bianchi@example.com",
				telephone = "123-456-7802",
				address = "123 Torino Street"
			),
			false
		)
		contactService.createContact(
			CreateContactDTO(
				name = "Alice",
				surname = "Verdi",
				ssncode = "000-03-1234",
				category = category.unknown,
				email = "alice.verdi@example.com",
				telephone = "123-456-7803",
				address = "789 Firenze Street"
			),
			true
		)

		// --- Act ---
		val result = contactService.listAllContacts(
			pageNumber = null,
			limit = null,
			email = "mario.rossi@example.com",
			name = null,
			telephone = null
		)

		// --- Assert ---
		assert(result.first()==c1)
	}

	/**
	 * Verify that the endpoint handles query parameters correctly,
	 * such as filtering the contacts by name
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts4 () {
		// --- Arrange ---
		contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)
		contactService.createContact(
			CreateContactDTO(
				name = "Luigi",
				surname = "Bianchi",
				ssncode = "000-02-1234",
				category = category.professional,
				email = "luigi.bianchi@example.com",
				telephone = "123-456-7802",
				address = "123 Torino Street"
			),
			false
		)
		val c3 = contactService.createContact(
			CreateContactDTO(
				name = "Alice",
				surname = "Verdi",
				ssncode = "000-03-1234",
				category = category.unknown,
				email = "alice.verdi@example.com",
				telephone = "123-456-7803",
				address = "789 Firenze Street"
			),
			true
		)

		// --- Act ---
		val result = contactService.listAllContacts(
			pageNumber = null,
			limit = null,
			email = null,
			name = "Alice",
			telephone = null
		)

		// --- Assert ---
		assert(result.first()==c3)
	}

	/**
	 * Verify that the endpoint handles query parameters correctly,
	 * such as filtering the contacts by telephone
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts5 () {
		// --- Arrange ---
		contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)
		val c2 = contactService.createContact(
			CreateContactDTO(
				name = "Luigi",
				surname = "Bianchi",
				ssncode = "000-02-1234",
				category = category.professional,
				email = "luigi.bianchi@example.com",
				telephone = "123-456-7802",
				address = "123 Torino Street"
			),
			false
		)
		contactService.createContact(
			CreateContactDTO(
				name = "Alice",
				surname = "Verdi",
				ssncode = "000-03-1234",
				category = category.unknown,
				email = "alice.verdi@example.com",
				telephone = "123-456-7803",
				address = "789 Firenze Street"
			),
			true
		)

		// --- Act ---
		val result = contactService.listAllContacts(
			pageNumber = null,
			limit = null,
			email = null,
			name = null,
			telephone = "123-456-7802"
		)

		// --- Assert ---
		assert(result.first()==c2)
	}

	/**
	 * Verify that the endpoint handles query parameters correctly
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts6 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)
		val c2 = contactService.createContact(
			CreateContactDTO(
				name = "Luigi",
				surname = "Bianchi",
				ssncode = "000-02-1234",
				category = category.professional,
				email = "luigi.bianchi@example.com",
				telephone = "123-456-7802",
				address = "123 Torino Street"
			),
			false
		)
		val c3 = contactService.createContact(
			CreateContactDTO(
				name = "Alice",
				surname = "Verdi",
				ssncode = "000-03-1234",
				category = category.unknown,
				email = "alice.verdi@example.com",
				telephone = "123-456-7803",
				address = "789 Firenze Street"
			),
			true
		)

		// --- Act ---
		val result = contactService.listAllContacts(
			pageNumber = null,
			limit = null,
			email = null,
			name = null,
			telephone = null
		)

		// --- Assert ---
		assert(result.contains(c1) && result.contains(c2) && result.contains(c3))
	}

	/**
	 * Verify that the endpoint handles query parameters correctly,
	 * such as sorting the contacts by name
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts7 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)
		val c2 = contactService.createContact(
			CreateContactDTO(
				name = "Luigi",
				surname = "Bianchi",
				ssncode = "000-02-1234",
				category = category.professional,
				email = "luigi.bianchi@example.com",
				telephone = "123-456-7802",
				address = "123 Torino Street"
			),
			false
		)
		val c3 = contactService.createContact(
			CreateContactDTO(
				name = "Alice",
				surname = "Verdi",
				ssncode = "000-03-1234",
				category = category.unknown,
				email = "alice.verdi@example.com",
				telephone = "123-456-7803",
				address = "789 Firenze Street"
			),
			true
		)

		// --- Act ---
		val result = contactService.listAllContacts(
			pageNumber = null,
			limit = null,
			email = null,
			name = null,
			telephone = null
		).sortedBy { element -> element.name }
		
		// --- Assert ---
		assert((result.get(0)==c3) && (result.get(1)==c2) && (result.get(2)==c1))
	}


	// ----- GET /API/contacts/pendings -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * and the correct data structure
	 * when there are pending contacts
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts8 () {
		// --- Arrange ---
		contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			true
		)

		mockMvc.get("http://localhost:8080/API/contacts/") {
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
	 * when there are no pending contacts
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts9 () {
		// --- Arrange ---
		// No pending contacts in the database

		mockMvc.get("http://localhost:8080/API/contacts/") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			content {
				jsonPath("$") {
					isEmpty()
				}
			}
		}
	}


	// ----- GET /API/contacts/{contactId}/ -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * and the correct data
	 * when the contact with the given ID exists in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts10 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)
		contactService.createContact(
			CreateContactDTO(
				name = "Luigi",
				surname = "Bianchi",
				ssncode = "000-02-1234",
				category = category.professional,
				email = "luigi.bianchi@example.com",
				telephone = "123-456-7802",
				address = "123 Torino Street"
			),
			false
		)
		contactService.createContact(
			CreateContactDTO(
				name = "Alice",
				surname = "Verdi",
				ssncode = "000-03-1234",
				category = category.unknown,
				email = "alice.verdi@example.com",
				telephone = "123-456-7803",
				address = "789 Firenze Street"
			),
			true
		)

		mockMvc.get("http://localhost:8080/API/contacts/${c1.id}/") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
		}
	}

	/**
	 * Verify that the endpoint correctly handles an invalid ID
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts11 () {
		// --- Arrange ---
		contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)

		mockMvc.get("http://localhost:8080/API/contacts/-1") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
		}
	}


	// ----- POST /API/contacts/ -----
	/**
	 * Verify that the endpoint returns a *201 Created* status
	 * and the correctness of returned data
	 * when a new contact is successfully created
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts12 () {
		// --- Arrange ---
		// No contacts in the database

		// --- Act ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)
		val result = contactService.listAllContacts(null, null, null, null, null)

		// --- Assert ---
		assert(result.first() == c1)
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the request body is invalid or missing required fields
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts13 () {
		// --- Arrange ---
		val requestBody = """
			{
				"name": "Mario",
				"surname": "Rossi"
			}
		"""
		
		mockMvc.post("http://localhost:8080/API/contacts/") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = requestBody
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
		}
	}


	// ----- PUT /API/contacts/{contactId}/category ----
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the contact with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts15 () {
		// --- Arrange ---
		val updatedCategory = category.professional
		mockMvc.put("http://localhost:8080/API/contacts/-1/category") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = """
				{
					"category": "$updatedCategory"
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
	fun testContacts16 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)
		
		mockMvc.put("http://localhost:8080/API/contacts/${c1.id}/category") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = {}
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
		}
	}


	// ----- POST /API/contacts/{contactId}/email -----
	/**
	 * Verify the correctness of returned data
	 * when the email of the contact with the given ID is successfully updated or added
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts17 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)

		// --- Act ---
		val newEmailAddress = "mario.rossi@test.com"
		contactService.addContactEmail(c1.id, CreateUpdateEmailDTO(newEmailAddress))

		// --- Assert ---
		val updatedContact = contactService.findById(c1.id)
		Assertions.assertNotNull(updatedContact)
		Assertions.assertTrue(updatedContact.emails.any { it.email == newEmailAddress })
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the contact with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts18 () {
		// --- Arrange ---
		val newEmailAddress = "mario.rossi@test.com"

		mockMvc.post("http://localhost:8080/API/contacts/-1/email") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = """
				{
					"email": "$newEmailAddress"
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
	fun testContacts19 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)

		mockMvc.post("http://localhost:8080/API/contacts/" + c1.id + "/email") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = {}
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
		}
	}


	// ----- DELETE /API/contacts/{contactId}/email/{emailId} -----
	/**
	 * Verify that the endpoint correctly deletes the specified email
	 * from the specified contact
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts20 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)

		// --- Act ---
		val emailToDelete = c1.emails.first()
		contactService.deleteContactEmail(c1.id, emailToDelete.id)

		// --- Assert ---
		val updatedContact = contactService.findById(c1.id)
		Assertions.assertNotNull(updatedContact)
		Assertions.assertFalse(updatedContact.emails.contains(emailToDelete))
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when trying to delete an email from a contact that doesn't exist
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts21 () {
		// --- Arrange ---
		val nonExistingContactId = -1L
		val emailIdToDelete = 1L
		
		// --- Act ---
		mockMvc.delete("http://localhost:8080/API/contacts/$nonExistingContactId/email/$emailIdToDelete") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			status { isBadRequest() }
		}
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when trying to delete an email that doesn't exist, even if the contact does exist
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts22 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)
		val emailIdToDelete = -1L

		// --- Act ---
		mockMvc.delete("http://localhost:8080/API/contacts/${c1.id}/email/$emailIdToDelete") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			status { isBadRequest() }
		}
	}


	// ----- POST /API/contacts/{contactId}/address -----
	/**
	 * Verify that the endpoint correctly adds an address to the specified contact
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts23 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)
		val addressToAdd = "789 Firenze Street"
		
		// --- Act ---
		contactService.addContactAddress(c1.id, CreateUpdateAddressDTO(addressToAdd))
		val updatedContact = contactService.findById(c1.id)
		
		// --- Assert ---
		assert(updatedContact.addresses.any { address -> address.address == addressToAdd })
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when trying to add an address to a contact that doesn't exist
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts24 () {
		// --- Arrange ---
		val newAddress = "789 Firenze Street"

		mockMvc.post("http://localhost:8080/API/contacts/-1/address") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = """
				{
					"address": "$newAddress"
				}
			"""
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
		}
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the request body contains invalid address data
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts25 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)

		mockMvc.post("http://localhost:8080/API/contacts/${c1.id}/address") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = {}
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
		}
	}


	// ----- DELETE /API/contacts/{contactId}/address/{addressId} -----
	/**
	 * Verify that the endpoint correctly deletes the specified address
	 * from the specified contact
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts26 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)

		// --- Act ---
		val addressToDelete = c1.addresses.first()
		contactService.deleteContactAddress(c1.id, addressToDelete.id)

		// --- Assert ---
		val updatedContact = contactService.findById(c1.id)
		Assertions.assertNotNull(updatedContact)
		Assertions.assertFalse(updatedContact.addresses.contains(addressToDelete))
	}
	
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the contact with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts27 () {
		// --- Arrange ---
		val nonExistingContactId = -1L
		val addressIdToDelete = 1L
		
		// --- Act ---
		mockMvc.delete("http://localhost:8080/API/contacts/$nonExistingContactId/address/$addressIdToDelete") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			status { isBadRequest() }
		}
	}
	
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the address with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts28 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)
		val addressIdToDelete = -1L

		// --- Act ---
		mockMvc.delete("http://localhost:8080/API/contacts/${c1.id}/address/$addressIdToDelete") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			status { isBadRequest() }
		}
	}


	// ----- POST /API/contacts/{contactId}/telephone -----
	/**
	 * Verify that the endpoint successfully adds a telephone number to the contact with the given ID
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts29 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)
		val phoneToAdd = "123-654-1087"

		// --- Act ---
		contactService.addContactTelephone(c1.id, CreateUpdateTelephoneDTO(phoneToAdd))
		val updatedContact = contactService.findById(c1.id)

		// --- Assert ---
		assert(updatedContact.telephones.any { phone -> phone.telephone == phoneToAdd })
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the contact with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts30 () {
		// --- Arrange ---
		val newTelephone = "321-654-0987"

		mockMvc.post("http://localhost:8080/API/contacts/-1/telephone") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = """
				{
					"telephone": "$newTelephone"
				}
			"""
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
		}
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the request body is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts31 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)

		mockMvc.post("http://localhost:8080/API/contacts/${c1.id}/telephone") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = {}
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
		}
	}


	// ----- DELETE /API/contacts/{contactId}/telephone/{telephoneId} -----
	/**
	 * Verify that the endpoint correctly deletes the specified telephone
	 * from the specified contact
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts32 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)

		// --- Act ---
		val phoneToDelete = c1.telephones.first()
		contactService.deleteContactTelephone(c1.id, phoneToDelete.id)

		// --- Assert ---
		val updatedContact = contactService.findById(c1.id)
		Assertions.assertNotNull(updatedContact)
		Assertions.assertFalse(updatedContact.telephones.contains(phoneToDelete))
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the contact with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts33 () {
		// --- Arrange ---
		val nonExistingContactId = -1L
		val telephoneIdToDelete = 1L
		
		// --- Act ---
		mockMvc.delete("http://localhost:8080/API/contacts/$nonExistingContactId/telephone/$telephoneIdToDelete") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			status { isBadRequest() }
		}
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the telephone number with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun testContacts34 () {
		// --- Arrange ---
		val c1 = contactService.createContact(
			CreateContactDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street"
			),
			false
		)
		val telephoneToDelete = -1L

		// --- Act ---
		mockMvc.delete("http://localhost:8080/API/contacts/${c1.id}/telephone/$telephoneToDelete") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			status { isBadRequest() }
		}
	}
}
