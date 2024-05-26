package it.polito.customerrelationshipmanagement

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.category
import it.polito.customerrelationshipmanagement.entities.contactInfoState
import it.polito.customerrelationshipmanagement.entities.employmentState
import it.polito.customerrelationshipmanagement.entities.jobOfferStatus
import it.polito.customerrelationshipmanagement.services.CustomerService
import it.polito.customerrelationshipmanagement.services.JobOfferService
import it.polito.customerrelationshipmanagement.services.ProfessionalService
import jakarta.transaction.Transactional
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProfessionalTests: IntegrationTest() {
    @Autowired
	lateinit var professionalService: ProfessionalService

	@Autowired
	lateinit var customerService: CustomerService

	@Autowired
	lateinit var jobOfferService: JobOfferService

	@Autowired
	private lateinit var mockMvc: MockMvc

	val BASE_URL = "http://localhost:8080/API/"


	// ----- POST /API/professionals -----
	/**
	 * Verify that the endpoint returns a *201 Created* status
	 * and the correctness of returned data
	 * when a new professional is successfully created
	 */
	@Test
	@Transactional
	@Rollback
	fun createProfessional () {
		// --- Arrange ---
		val name = "Mario"
		val surname = "Rossi"
		val ssncode = "111-23-9025"
		val category = category.professional
		val email = "mario.rossi@example.com"
		val telephone = "+393312085641"
		val address = "123 Roma Street"
		val employmentState = employmentState.not_available
		val geographicalLocation = Pair(43.17, 10.33)
		val dailyRate = 5
		val skillsData = listOf("javascript", "NodeJs")
		val skills = skillsData.map { CreateSkillDTO(it, null, null) }
		val expectedSkills = arrayOf(SkillDTO(id = 0, skill = "javascript", state = contactInfoState.active, jobOfferId = null, professionalId = null), SkillDTO(id = 0, skill = "NodeJs", state = contactInfoState.active, jobOfferId = null, professionalId = null))
		val notesData = listOf("Looking for first job", "Driving licensed")
		val notes = notesData.joinToString(",") { "\"$it\"" }
		val expectedNotes = arrayOf(NoteDTO(id = 0, note = "Looking for first job", state = contactInfoState.active, customerId = null, professionalId = null), NoteDTO(id = 0, note = "Driving licensed", state = contactInfoState.active, customerId = null, professionalId = null))

		val professionalDTO = """
			{
      		"name": "$name",
      		"surname": "$surname",
			"ssncode": "$ssncode",
			"category": "$category",
			"email": "$email",
			"telephone": "$telephone",
			"address": "$address",
			"employmentState": "$employmentState",
			"geographicalLocation": { "first": ${geographicalLocation.first}, "second": ${geographicalLocation.second} },
			"dailyRate": $dailyRate,
			"skills": [
				${
					skills.joinToString(",") { skill -> """
					  {
						"skill": "${skill.skill}",
						"professionalId": ${skill.professionalId ?: "null"},
						"jobOfferId": ${skill.jobOfferId ?: "null"}
					  }
				  	  """.trimIndent()
					}
				}
			],
			"notes": [$notes]
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "professionals") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isCreated() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.name") { value(name) }
			jsonPath("$.surname") { value(surname) }
			jsonPath("$.ssncode") { value(ssncode) }
			jsonPath("$.category") { value(category.name) }
			// Assert emails
			jsonPath("$.emails[0].email") { value(email) }
			jsonPath("$.emails[0].state") { value("active") }
			// Assert telephones
			jsonPath("$.telephones[0].telephone") { value(telephone) }
			jsonPath("$.telephones[0].state") { value("active") }
			// Assert addresses
			jsonPath("$.addresses[0].address") { value(address) }
			jsonPath("$.addresses[0].state") { value("active") }
			jsonPath("$.employmentState") { value(employmentState.name) }
			jsonPath("$.geographicalLocation.first") { value(geographicalLocation.first) }
			jsonPath("$.geographicalLocation.second") { value(geographicalLocation.second) }
			jsonPath("$.dailyRate") { value(dailyRate) }
			jsonPath("$.jobOffer") { value(null) }
			jsonPath("$.skills") { isArray() }
			jsonPath("$.notes") { isArray() }
			// Assert skills
			expectedSkills.forEach { expectedSkill ->
				val skillJsonPath = "$.skills[?(@.skill == '${expectedSkill.skill}')]"
				jsonPath(skillJsonPath) { isNotEmpty() }
				if (expectedSkill.id.toInt() != 0) {
					jsonPath("$skillJsonPath.id") { value(expectedSkill.id.toInt()) }
				} else {
					jsonPath("$skillJsonPath.id") { exists() }
				}
				jsonPath("$skillJsonPath.skill") { value(expectedSkill.skill) }
				jsonPath("$skillJsonPath.state") { value(expectedSkill.state.toString()) }
				jsonPath("$skillJsonPath.jobOfferId") { value(expectedSkill.jobOfferId) }
				jsonPath("$skillJsonPath.professionalId") { exists() }
			}
			// Assert notes
			expectedNotes.forEach { expectedNote ->
				val noteJsonPath = "$.notes[?(@.note == '${expectedNote.note}')]"
				jsonPath(noteJsonPath) { isNotEmpty() }
				if (expectedNote.id.toInt() != 0) {
					jsonPath("$noteJsonPath.id") { value(expectedNote.id.toInt()) }
				} else {
					jsonPath("$noteJsonPath.id") { exists() }
				}
				jsonPath("$noteJsonPath.note") { value(expectedNote.note) }
				jsonPath("$noteJsonPath.state") { value(expectedNote.state.toString()) }
				jsonPath("$noteJsonPath.customerId") { value(expectedNote.customerId) }
				jsonPath("$noteJsonPath.professionalId") { exists() }
			}
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the request body is missing one or more required
	 * contact creation fields (name, surname, email, ssncode)
	 */
	@Test
	@Transactional
	@Rollback
	fun createProfessional_missingContactFields () {
		// --- Arrange ---
		val category = category.professional
		val email = "mario.rossi@example.com"
		val telephone = "+393312085641"
		val address = "123 Roma Street"
		val employmentState = employmentState.not_available
		val geographicalLocation = Pair(43.17, 10.33)
		val dailyRate = 5

		val professionalDTO = """
			{
			"category": "$category",
			"email": "$email",
			"telephone": "$telephone",
			"address": "$address",
			"employmentState": "$employmentState",
			"geographicalLocation": { "first": ${geographicalLocation.first}, "second": ${geographicalLocation.second} },
			"dailyRate": $dailyRate
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "professionals") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Contact name, surname, ssncode and email cannot be empty.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the request body is missing one or more required
	 * professional creation fields (employmentState, geographicalLocation and dailyRate)
	 */
	@Test
	@Transactional
	@Rollback
	fun createProfessional_missingProfessionalFields () {
		// --- Arrange ---
		val name = "Mario"
		val surname = "Rossi"
		val ssncode = "111-23-9025"
		val category = category.professional
		val email = "mario.rossi@example.com"
		val telephone = "+393312085641"
		val address = "123 Roma Street"

		val professionalDTO = """
			{
      		"name": "$name",
      		"surname": "$surname",
			"ssncode": "$ssncode",
			"category": "$category",
			"email": "$email",
			"telephone": "$telephone",
			"address": "$address"
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "professionals") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("EmploymentState, geographicalLocation and dailyRate cannot be empty.") }
		}
	}


	// ----- POST /API/professionals/{professionalId}/note -----
	/**
	 * Verify the correctness of returned data 201 created
	 * when the note of the professional with the given ID is successfully updated or added
	 */
	@Test
	@Transactional
	@Rollback
	fun addProfessionalNote () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street",
				employmentState = employmentState.employed,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Driving licensed"
				),
				jobOfferId = null
			)
		)
		val note = """
			{
				"note": "Test note added"
			}
		"""
		mockMvc.post(BASE_URL + "professionals/${p1.id}/note") {
			contentType = MediaType.APPLICATION_JSON
			content = note
		}.andExpect {
			status { isCreated() }
			jsonPath("$.id") { exists() }
			jsonPath("$.customerId") { value(null) }
			jsonPath("$.note") { value("Test note added") }
			jsonPath("$.professionalId") { value(p1.id) }
			jsonPath("$.state") { value("active") }
		}
	}


	/**
	 * Verify that the endpoint returns a 400 Bad Request status
	 * when the professional with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun addProfessionalNoteInvalidId() {
		// --- Arrange ---
		val professionalInvalidId = -1

		mockMvc.post(BASE_URL + "professionals/${professionalInvalidId}/note") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = """
			{
				"note": "Test note added"
			}
		"""		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			jsonPath("$.detail") { value("Invalid professionalId Parameter.") }
		}
	}

	@Test
	@Transactional
	@Rollback
	fun addNoteProfessionalNotExists() {
		val newNote = "Test note"
		val professionalIdNotExisting = 9999
		mockMvc.post(BASE_URL + "professionals/${professionalIdNotExisting}/note") {
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
			jsonPath("$.detail") { value("Professional with ProfessionalId:$professionalIdNotExisting not found") }

		}
	}

	
	// ----- GET /API/professionals/ -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * and a non-empty list when all professionals are successfully
	 * retrieved from the database
	 */
	@Test
	@Transactional
	@Rollback
	fun listAllProfessionals () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Driving licensed"
				),
				jobOfferId = null
			)
		)
		val p2 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Guido",
				surname = "Bianchi",
				ssncode = "111-23-9028",
				category = category.professional,
				email = "guido.bianchi@example.com",
				telephone = "+393312085642",
				address = "456 Firenze Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.45),
				dailyRate = 4,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft Excel", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MySQL", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Five years experienced",
					"Without driving licence"
				),
				jobOfferId = null
			)
		)

		// --- Act ---
		mockMvc.get(BASE_URL + "professionals/") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$") {
					isArray()
					isNotEmpty()
				}
			jsonPath("$[*].id", containsInAnyOrder(p1.id.toInt(), p2.id.toInt()))
		}
	}
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * and a non-empty list when a professional is successfully
	 * retrieved filtering by request parameters
	 */
	@Test
	@Transactional
	@Rollback
	fun listAllProfessionalsByRequestParams () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Driving licensed"
				),
				jobOfferId = null
			)
		)
		val p2 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Guido",
				surname = "Bianchi",
				ssncode = "111-23-9028",
				category = category.professional,
				email = "guido.bianchi@example.com",
				telephone = "+393312085642",
				address = "456 Firenze Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.45),
				dailyRate = 4,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft Excel", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MySQL", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Five years experienced",
					"Without driving licence"
				),
				jobOfferId = null
			)
		)

		// --- Act ---
		mockMvc.get(BASE_URL + "professionals/?pageNumber=0&limit=10&employmentState=not_available&skills=MySQL") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$") {
				isArray()
				isNotEmpty()
			}
			jsonPath("$[0].id") { value(p2.id) }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the request parameters are invalid or missing required fields
	 */
	@Test
	@Transactional
	@Rollback
	fun listAllProfessionals_missingOnlyLatitude () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Driving licensed"
				),
				jobOfferId = null
			)
		)

		// --- Act ---
		mockMvc.get(BASE_URL + "professionals/?pageNumber=0&limit=10&employmentState=not_available&skills=MySQL&longitude=10.33") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Latitude must be provided if Longitude is provided.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the request parameters are invalid or missing required fields
	 */
	@Test
	@Transactional
	@Rollback
	fun listAllProfessionals_missingOnlyLongitude () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Driving licensed"
				),
				jobOfferId = null
			)
		)

		// --- Act ---
		mockMvc.get(BASE_URL + "professionals/?pageNumber=0&limit=10&employmentState=not_available&skills=MySQL&latitude=43.17") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Longitude must be provided if Latitude is provided.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the request parameters are invalid or missing required fields
	 */
	@Test
	@Transactional
	@Rollback
	fun listAllProfessionals_invalidPageNumberLimit () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Driving licensed"
				),
				jobOfferId = null
			)
		)

		// --- Act ---
		mockMvc.get(BASE_URL + "professionals/?pageNumber=-1&limit=0") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid pageNumber and limit Parameter.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the request parameters are invalid or missing required fields
	 */
	@Test
	@Transactional
	@Rollback
	fun listAllProfessionals_invalidLimit () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Driving licensed"
				),
				jobOfferId = null
			)
		)

		// --- Act ---
		mockMvc.get(BASE_URL + "professionals/?pageNumber=0&limit=0") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid limit Parameter.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the request parameters are invalid or missing required fields
	 */
	@Test
	@Transactional
	@Rollback
	fun listAllProfessionals_invalidPageNumber () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Driving licensed"
				),
				jobOfferId = null
			)
		)

		// --- Act ---
		mockMvc.get(BASE_URL + "professionals/?pageNumber=-1&limit=10") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid pageNumber Parameter.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the request parameters are invalid or missing required fields
	 */
	@Test
	@Transactional
	@Rollback
	fun listAllProfessionals_missingPageNumberOrLimit () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Driving licensed"
				),
				jobOfferId = null
			)
		)

		// --- Act ---
		mockMvc.get(BASE_URL + "professionals/?pageNumber=0") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("PageNumber and limit must be both provided or both not provided.") }
		}
	}


	// ----- GET /API/professionals/{professionalId} -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * when the professional with the given ID is successfully retrieved
	 */
	@Test
	@Transactional
	@Rollback
	fun findProfessionalById () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "123-456-7801",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Driving licensed"
				),
				jobOfferId = null
			)
		)

		// --- Act ---
		mockMvc.get(BASE_URL + "professionals/${p1.id}") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.id") { value(p1.id) }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the professionalId is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun findProfessionalById_invalidId () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Driving licensed"
				),
				jobOfferId = null
			)
		)

		// --- Act ---
		mockMvc.get(BASE_URL + "professionals/-1") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid professionalId Parameter.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *404 Not Found* status
	 * when the professionalId is not found
	 */
	@Test
	@Transactional
	@Rollback
	fun findProfessionalById_notFound () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Driving licensed"
				),
				jobOfferId = null
			)
		)

		// --- Act ---
		mockMvc.get(BASE_URL + "professionals/${p1.id + 1}") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Professional with ProfessionalId:${p1.id + 1} not found") }
		}
	}


	// ----- PUT /API/professionals/{professionalId} -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * when the professional with the given ID is successfully updated
	 * in almost all fields
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional1 () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Without driving licensed"
				),
				jobOfferId = null
			)
		)
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "123-456-7801",
				address = "123 ROma Street",
				notes = listOf("Italian", "Turin", "Engineering"),
				jobOffers = listOf()
			)
		)
		val jo1 = jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Junior Kotlin Developer",
				description = "Web application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 4,
				profitMargin = 6,
				customerId = c1.id,
				skills = listOf(
					CreateSkillDTO("English", null, null),
					CreateSkillDTO("Kotlin programming language", null, null)
				)
			)
		)
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.selection_phase, null, null)
		)
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.candidate_proposal, null, p1.id)
		)
		//Values to update
		val name = "Franco"
		val surname = "Gomez"
		val ssncode = "111-23-9026"
		val email = "franco.gomez@example.com"
		val telephone = "+393312085642"
		val address = "1234 Roma Street"
		val employmentState = employmentState.employed
		val geographicalLocation = Pair(43.20, 10.35)
		val dailyRate = 100.0
		val skillsData = listOf("javascript", "NodeJs")
		val skills = skillsData.map { CreateSkillDTO(it, null, null) }
		val skillsToDelete = p1.skills.map { it.id }
		val expectedSkills = p1.skills.map { it.copy(it.id, it.skill, contactInfoState.deleted, it.jobOfferId, it.professionalId) }.toMutableList()
		expectedSkills.add(SkillDTO(id = 0, skill = "javascript", state = contactInfoState.active, jobOfferId = null, professionalId = p1.id))
		expectedSkills.add(SkillDTO(id = 0, skill = "NodeJs", state = contactInfoState.active, jobOfferId = null, professionalId = p1.id))
		val notesData = listOf("Looking for second job", "Driving licensed")
		val notesDTO = notesData.joinToString(",") { "\"$it\"" }
		val notes = notesData.map { CreateUpdateNoteDTO(it) }
		val notesToDelete = p1.notes.map { it.id }
		val expectedNotes = p1.notes.map { it.copy(it.id, it.note, it.professionalId, it.customerId, contactInfoState.deleted) }.toMutableList()
		expectedNotes.add(NoteDTO(id = 0, note = "Looking for second job", state = contactInfoState.active, professionalId = p1.id, customerId = null))
		expectedNotes.add(NoteDTO(id = 0, note = "Driving licensed", state = contactInfoState.active, professionalId = p1.id, customerId = null))

		val professionalDTO = """
			{
      		"name": "$name",
      		"surname": "$surname",
			"ssncode": "$ssncode",
			"email": "$email",
			"telephone": "$telephone",
			"address": "$address",
			"employmentState": "$employmentState",
			"geographicalLocation": { "first": ${geographicalLocation.first}, "second": ${geographicalLocation.second} },
			"dailyRate": $dailyRate,
			"skills": [
				${
					skills.joinToString(",") { skill -> """
					  {
						"skill": "${skill.skill}",
						"professionalId": ${skill.professionalId ?: "null"},
						"jobOfferId": ${skill.jobOfferId ?: "null"}
					  }
				  	  """.trimIndent()
					}
				}
			],
			"skillsToDelete": [${skillsToDelete.joinToString(",")}],
			"notes": [$notesDTO],
			"notesToDelete": [${notesToDelete.joinToString(",")}],
			"jobOfferId": ${jo1.id}
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.id") { value(p1.id.toInt()) }
			jsonPath("$.name") { value(name) }
			jsonPath("$.surname") { value(surname) }
			jsonPath("$.ssncode") { value(ssncode) }
			// Assert emails
			jsonPath("$.emails[*].email", containsInAnyOrder(email, "mario.rossi@example.com"))
			// Assert telephones
			jsonPath("$.telephones[*].telephone", containsInAnyOrder(telephone, "+393312085641"))
			// Assert addresses
			jsonPath("$.addresses[*].address", containsInAnyOrder(address, "123 Roma Street"))
			jsonPath("$.employmentState") { value(employmentState.name) }
			jsonPath("$.geographicalLocation.first") { value(geographicalLocation.first) }
			jsonPath("$.geographicalLocation.second") { value(geographicalLocation.second) }
			jsonPath("$.dailyRate") { value(dailyRate) }
			jsonPath("$.jobOffer.id") { value(jo1.id.toInt()) }
			jsonPath("$.jobOffer.currentState") { value("consolidated") }
			jsonPath("$.jobOffer.professionalId") { value(p1.id.toInt()) }
			jsonPath("$.skills") { isArray() }
			// Additional assertions for each skill in the skills array
			expectedSkills.forEach { expectedSkill ->
				val skillJsonPath = "$.skills[?(@.skill == '${expectedSkill.skill}')]"
				jsonPath(skillJsonPath) { isNotEmpty() }
				if (expectedSkill.id.toInt() != 0) {
					jsonPath("$skillJsonPath.id") { value(expectedSkill.id.toInt()) }
				} else {
					jsonPath("$skillJsonPath.id") { exists() }
				}
				jsonPath("$skillJsonPath.skill") { value(expectedSkill.skill) }
				jsonPath("$skillJsonPath.state") { value(expectedSkill.state.toString()) }
				jsonPath("$skillJsonPath.jobOfferId") { value(expectedSkill.jobOfferId) }
				jsonPath("$skillJsonPath.professionalId") { value(p1.id.toInt()) }
			}
			jsonPath("$.notes") { isArray() }
			// Additional assertions for each note in the notes array
			expectedNotes.forEach { expectedNote ->
				val noteJsonPath = "$.notes[?(@.note == '${expectedNote.note}')]"
				jsonPath(noteJsonPath) { isNotEmpty() }
				if (expectedNote.id.toInt() != 0) {
					jsonPath("$noteJsonPath.id") { value(expectedNote.id.toInt()) }
				} else {
					jsonPath("$noteJsonPath.id") { exists() }
				}
				jsonPath("$noteJsonPath.note") { value(expectedNote.note) }
				jsonPath("$noteJsonPath.state") { value(expectedNote.state.toString()) }
				jsonPath("$noteJsonPath.professionalId") { value(p1.id.toInt()) }
				jsonPath("$noteJsonPath.customerId") { value(null) }
			}
		}
	}
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * when the professional with the given ID is successfully updated
	 * by changing the state of the professional from 'employed' to 'available'
	 * after a job offer is completed
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional2 () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Without driving licensed"
				),
				jobOfferId = null
			)
		)
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "123-456-7801",
				address = "123 ROma Street",
				notes = listOf("Italian", "Turin", "Engineering"),
				jobOffers = listOf()
			)
		)
		val jo1 = jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Junior Kotlin Developer",
				description = "Web application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 4,
				profitMargin = 6,
				customerId = c1.id,
				skills = listOf(
					CreateSkillDTO("English", null, null),
					CreateSkillDTO("Kotlin programming language", null, null)
				)
			)
		)
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.selection_phase, null, null)
		)
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.candidate_proposal, null, p1.id)
		)
		//Values to update
		val employmentState1 = employmentState.employed
		val employmentState2 = employmentState.available

		val professionalDTO1 = """
			{
			"employmentState": "$employmentState1",
			"jobOfferId": ${jo1.id}
			}
			""".trimIndent()

		val professionalDTO2 = """
			{
			"employmentState": "$employmentState2"
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO1
		}

		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO2
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.id") { value(p1.id.toInt()) }
			jsonPath("$.employmentState") { value(employmentState2.name) }
			jsonPath("$.jobOffer") { value(null) }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the professional ID is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_invalidId () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Without driving licensed"
				),
				jobOfferId = null
			)
		)
		val professionalDTO = """
			{
      		"name": "Franco"
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/-1") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid professionalId Parameter.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *404 Not Found* status
	 * when the professional ID is not found
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_notFound () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Without driving licensed"
				),
				jobOfferId = null
			)
		)
		val professionalDTO = """
			{
      		"name": "Franco"
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id + 1}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Professional with ProfessionalId:${p1.id + 1} not found.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when a skill to delete ID is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_invalidSkillId () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Without driving licensed"
				),
				jobOfferId = null
			)
		)

		val professionalDTO = """
			{
      		"skillsToDelete": [-1]
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid skillId Parameter.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *404 Not Found* status
	 * when a skill to delete ID is not found
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_notFoundSkillId () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Looking for first job",
					"Without driving licensed"
				),
				jobOfferId = null
			)
		)
		val skillToDelete = p1.skills[0].id + 1

		val professionalDTO = """
			{
      		"skillsToDelete": [$skillToDelete]
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Skill with SkillId:$skillToDelete not found") }
		}
	}
	/**
	 * Verify that the endpoint returns a *403 Forbidden* status
	 * when a skill to delete does not belong to the professional
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_notBelongingSkill () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Looking for first job",
					"Without driving licensed"
				),
				jobOfferId = null
			)
		)
		val p2 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Guido",
				surname = "Bianchi",
				ssncode = "111-23-9028",
				category = category.professional,
				email = "guido.bianchi@example.com",
				telephone = "+393312085642",
				address = "456 Firenze Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.45),
				dailyRate = 4,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft Excel", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MySQL", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Five years experienced",
					"Without driving licence"
				),
				jobOfferId = null
			)
		)
		val skillToDelete = p2.skills[0].id

		val professionalDTO = """
			{
      		"skillsToDelete": [$skillToDelete]
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isForbidden() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Skill with SkillId:${skillToDelete} does not belong to this professional.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when a skill to delete has already been deleted
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_alreadyDeletedSkill () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Looking for first job",
					"Without driving licensed"
				),
				jobOfferId = null
			)
		)
		val skillsToDelete = p1.skills.map { it.copy(it.id, it.skill, contactInfoState.deleted, it.jobOfferId, it.professionalId) }.toMutableList()
		val skillToDelete = skillsToDelete[0].id

		val professionalDTO = """
			{
      		"skillsToDelete": [$skillToDelete]
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}

		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Skill with SkillId:${skillToDelete} already deleted.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when a note to delete ID is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_invalidNoteId () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft PowerPoint", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MongoDB", professionalId = null, jobOfferId = null),
				),
				notes = listOf(
					"Looking for first job",
					"Without driving licensed"
				),
				jobOfferId = null
			)
		)

		val professionalDTO = """
			{
      		"notesToDelete": [-1]
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid noteId Parameter.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *404 Not Found* status
	 * when a note to delete ID is not found
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_notFoundNoteId () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Looking for first job"
				),
				jobOfferId = null
			)
		)
		val noteToDelete = p1.notes[0].id + 1

		val professionalDTO = """
			{
      		"notesToDelete": [$noteToDelete]
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Note with NoteId:$noteToDelete not found") }
		}
	}
	/**
	 * Verify that the endpoint returns a *403 Forbidden* status
	 * when a note to delete does not belong to the professional
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_notBelongingNote () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Looking for first job"
				),
				jobOfferId = null
			)
		)
		val p2 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Guido",
				surname = "Bianchi",
				ssncode = "111-23-9028",
				category = category.professional,
				email = "guido.bianchi@example.com",
				telephone = "+393312085642",
				address = "456 Firenze Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.45),
				dailyRate = 4,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "Microsoft Excel", professionalId = null, jobOfferId = null),
					CreateSkillDTO(skill = "MySQL", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Five years experienced"
				),
				jobOfferId = null
			)
		)
		val noteToDelete = p2.notes[0].id

		val professionalDTO = """
			{
      		"notesToDelete": [$noteToDelete]
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isForbidden() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Note with NoteId:${noteToDelete} does not belong to this professional.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when a note to delete has already been deleted
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_alreadyDeletedNote () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Looking for first job"
				),
				jobOfferId = null
			)
		)
		val notesToDelete = p1.notes.map { it.copy(it.id, it.note, it.customerId, it.professionalId, contactInfoState.deleted) }.toMutableList()
		val noteToDelete = notesToDelete[0].id

		val professionalDTO = """
			{
      		"notesToDelete": [$noteToDelete]
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}

		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Note with NoteId:${noteToDelete} already deleted.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *404 Not Found* status
	 * when the jobOffer ID is not found
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_notFoundJobOfferId () {
		// --- Arrange ---
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085641",
				address = "Corso Gaetano Scirea, 50, 10151 Torino TO",
				notes = listOf("Italian", "Turin", "Engineering"),
				jobOffers = listOf()
			)
		)
		val jo1 = jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Junior Kotlin Developer",
				description = "Web application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 100.0,
				profitMargin = 15.0,
				customerId = c1.id,
				skills = listOf(
					CreateSkillDTO("English", null, null),
					CreateSkillDTO("Kotlin programming language", null, null)
				)
			)
		)
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Looking for first job"
				),
				jobOfferId = null
			)
		)
		val professionalDTO = """
			{
      		"jobOfferId": ${jo1.id + 1}
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("JobOffer with JobOfferId:${jo1.id + 1} not found.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the jobOffer ID is provided correctly but the employmentState
	 * is not updated to 'employed'
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_requiredEmployedState () {
		// --- Arrange ---
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085641",
				address = "Corso Gaetano Scirea, 50, 10151 Torino TO",
				notes = listOf("Italian", "Turin", "Engineering"),
				jobOffers = listOf()
			)
		)
		val jo1 = jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Junior Kotlin Developer",
				description = "Web application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 100.0,
				profitMargin = 15.0,
				customerId = c1.id,
				skills = listOf(
					CreateSkillDTO("English", null, null),
					CreateSkillDTO("Kotlin programming language", null, null)
				)
			)
		)
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Looking for first job"
				),
				jobOfferId = null
			)
		)
		val professionalDTO = """
			{
      		"jobOfferId": ${jo1.id}
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("EmploymentState 'Employed' is required in order to link a jobOffer to the professional.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the jobOffer ID is provided correctly and the employmentState
	 * is updated to 'employed', but the professional is not yet 'available'
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_notYetAvailable () {
		// --- Arrange ---
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085641",
				address = "Corso Gaetano Scirea, 50, 10151 Torino TO",
				notes = listOf("Italian", "Turin", "Engineering"),
				jobOffers = listOf()
			)
		)
		val jo1 = jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Junior Kotlin Developer",
				description = "Web application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 100.0,
				profitMargin = 15.0,
				customerId = c1.id,
				skills = listOf(
					CreateSkillDTO("English", null, null),
					CreateSkillDTO("Kotlin programming language", null, null)
				)
			)
		)
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Looking for first job"
				),
				jobOfferId = null
			)
		)
		val professionalDTO = """
			{
      		"jobOfferId": ${jo1.id},
	  		"employmentState": "employed"
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Professional with ProfessionalId:${p1.id} is not available for work, thus cannot be linked to the JobOffer with JobOfferId:${jo1.id}.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the jobOffer ID is provided correctly and the employmentState
	 * is updated to 'employed' and the professional is 'available', but
	 * there has not been any proposal made for the professional for the
	 * provided jobOffer
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_noProposalMade () {
		// --- Arrange ---
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085641",
				address = "Corso Gaetano Scirea, 50, 10151 Torino TO",
				notes = listOf("Italian", "Turin", "Engineering"),
				jobOffers = listOf()
			)
		)
		val jo1 = jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Junior Kotlin Developer",
				description = "Web application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 100.0,
				profitMargin = 15.0,
				customerId = c1.id,
				skills = listOf(
					CreateSkillDTO("English", null, null),
					CreateSkillDTO("Kotlin programming language", null, null)
				)
			)
		)
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Looking for first job"
				),
				jobOfferId = null
			)
		)
		val professionalDTO = """
			{
      		"jobOfferId": ${jo1.id},
	  		"employmentState": "employed"
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("A proposal for candidate with ProfessionalId:${p1.id} has not been made, thus JobOffer with JobOfferId:${jo1.id} cannot be linked to the professional.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the employmentState is not updated correctly from 'not_available'
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_invalidTransition1 () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.not_available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Looking for first job"
				),
				jobOfferId = null
			)
		)
		val professionalDTO = """
			{
	  		"employmentState": "employed"
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid employmentState transition (from 'not_available' only 'available' is possible).") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the employmentState is not updated correctly from 'available'
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_invalidTransition2 () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Looking for first job"
				),
				jobOfferId = null
			)
		)
		val professionalDTO = """
			{
	  		"employmentState": "available"
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid employmentState transition (not possible to switch to the same employmentState).") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the employmentState is not updated correctly from 'employed'
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_invalidTransition3 () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.employed,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Looking for first job"
				),
				jobOfferId = null
			)
		)
		val professionalDTO = """
			{
	  		"employmentState": "not_available"
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid employmentState transition (from 'employed' only 'available' is possible).") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the employmentState is not updated correctly from 'available'
	 * because the professional has not been linked to a jobOffer
	 */
	@Test
	@Transactional
	@Rollback
	fun updateProfessional_invalidTransition4 () {
		// --- Arrange ---
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9025",
				category = category.professional,
				email = "mario.rossi@example.com",
				telephone = "+393312085641",
				address = "123 Roma Street",
				employmentState = employmentState.available,
				geographicalLocation = Pair(43.17, 10.33),
				dailyRate = 5,
				skills = listOf(
					CreateSkillDTO(skill = "English", professionalId = null, jobOfferId = null)
				),
				notes = listOf(
					"Looking for first job"
				),
				jobOfferId = null
			)
		)
		val professionalDTO = """
			{
	  		"employmentState": "employed"
			}
			""".trimIndent()

		// --- Act ---
		mockMvc.put(BASE_URL + "professionals/${p1.id}") {
			contentType = MediaType.APPLICATION_JSON
			content = professionalDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Professional with ProfessionalId:${p1.id} is not linked to any JobOffer.") }
		}
	}
}
