package it.polito.customerrelationshipmanagement

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.*
import it.polito.customerrelationshipmanagement.services.CustomerService
import it.polito.customerrelationshipmanagement.services.JobOfferService
import it.polito.customerrelationshipmanagement.services.ProfessionalService
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Assertions.assertNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.hamcrest.Matchers.*

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JobOfferTests: IntegrationTest() {
    @Autowired
	lateinit var jobOfferService: JobOfferService

	@Autowired
	lateinit var customerService: CustomerService

	@Autowired
	lateinit var professionalService: ProfessionalService

	@Autowired
	private lateinit var mockMvc: MockMvc

	val BASE_URL = "http://localhost:8080/API/"


	// ----- POST /API/joboffers/ -----
	/**
	 * Verify that the endpoint returns a *201 Created* status
	 * and the correctness of returned data
	 * when a new job offer is successfully created
	 */
	@Test
	@Transactional
	@Rollback
	fun createJobOffer() {
		// --- Arrange ---

		// --- Act ---
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
		val name = "JobOffer1"
		val description = "javascript job"
		val currentStateNote = "node developer"
		val duration = 100.0
		val profitMargin = 15.0
		val customerId = c1.id
		val skillsData = listOf("javascript", "NodeJs")
		val skills = skillsData.map { CreateSkillDTO(it, null, null) }
		val jo1DTO = """{
			"name": "$name",
			"description": "$description",
			"currentStateNote": "$currentStateNote",
			"duration": $duration,
			"profitMargin": $profitMargin,
			"customerId": $customerId,
			"skills": [
				${
			skills.joinToString(",") { skill ->
				"""
					{
						"skill": "${skill.skill}",
						"professionalId": ${skill.professionalId ?: "null"},
						"jobOfferId": ${skill.jobOfferId ?: "null"}
					}
					""".trimIndent()
			}
		}
			]
		}""".trimIndent()
		mockMvc.post(BASE_URL + "joboffers/") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jo1DTO
		}.andExpect {
			// --- Assert ---
			status { isCreated() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.id") { exists() }
			jsonPath("$.name") { value(name) }
			jsonPath("$.description") { value(description) }
			jsonPath("$.currentStateNote") { value(currentStateNote) }
			jsonPath("$.duration") { value(duration) }
			jsonPath("$.profitMargin") { value(profitMargin) }
			jsonPath("$.customerId") { value(customerId) }
			jsonPath("$.skills") { isArray() }

			// Additional assertions for each skill in the skills array
			skills.forEachIndexed { index, skill ->
				jsonPath("$.skills[$index].id") { exists() }
				jsonPath("$.skills[$index].skill") { value(skillsData[index]) }
				jsonPath("$.skills[$index].state") { value("active") }
				jsonPath("$.skills[$index].jobOfferId") { exists() }
				jsonPath("$.skills[$index].professionalId") { value(null) }
			}

		}
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the customerId is missing
	 */
	@Test
	@Transactional
	@Rollback
	fun createJobOfferNoCustomerId() {
		// --- Arrange ---
		val name = "JobOffer1"
		val description = "javascript job"
		val currentStateNote = "node developer"
		val duration = 100.0
		val profitMargin = 15.0
		val customerId = null
		val skillsData = listOf("javascript", "NodeJs")
		val skills = skillsData.map { CreateSkillDTO(it, null, null) }
		val jo1DTO = """{
			"name": "$name",
			"description": "$description",
			"currentStateNote": "$currentStateNote",
			"duration": $duration,
			"profitMargin": $profitMargin,
			"customerId": $customerId,
			"skills": [
				${
			skills.joinToString(",") { skill ->
				"""
					{
						"skill": "${skill.skill}",
						"professionalId": ${skill.professionalId ?: "null"},
						"jobOfferId": ${skill.jobOfferId ?: "null"}
					}
					""".trimIndent()
			}
		}
			]
		}""".trimIndent()
		mockMvc.post(BASE_URL + "joboffers/") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jo1DTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Customer ID is required to Link The Job Offer.") }
		}
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the customerId is missing
	 */
	@Test
	@Transactional
	@Rollback
	fun createJobOfferCustomerIdNotFound() {
		// --- Arrange ---
		val name = "JobOffer1"
		val description = "javascript job"
		val currentStateNote = "node developer"
		val duration = 100.0
		val profitMargin = 15.0
		val customerId = 1
		val skillsData = listOf("javascript", "NodeJs")
		val skills = skillsData.map { CreateSkillDTO(it, null, null) }
		val jo1DTO = """{
			"name": "$name",
			"description": "$description",
			"currentStateNote": "$currentStateNote",
			"duration": $duration,
			"profitMargin": $profitMargin,
			"customerId": $customerId,
			"skills": [
				${
			skills.joinToString(",") { skill ->
				"""
					{
						"skill": "${skill.skill}",
						"professionalId": ${skill.professionalId ?: "null"},
						"jobOfferId": ${skill.jobOfferId ?: "null"}
					}
					""".trimIndent()
			}
		}
			]
		}""".trimIndent()
		mockMvc.post(BASE_URL + "joboffers/") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jo1DTO
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Customer with CustomerId:${customerId} not found") }
		}
	}


	// ----- GET /API/joboffers/{jobOfferId} -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * and the correct data
	 * when the job offer with the given ID exists in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun findJobOfferById () {
		// --- Arrange ---
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

		mockMvc.get( BASE_URL + "joboffers/${jo1.id}") {
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
	fun findJobOfferByIdInvalidId () {
		// --- Arrange ---
		mockMvc.get(BASE_URL + "joboffers/-1") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid jobOfferId Parameter.") }
		}
	}

	@Test
	@Transactional
	@Rollback
	fun findJobOfferByIdNotFound () {
		// --- Arrange ---
		val jobOfferId = 1
		mockMvc.get(BASE_URL + "joboffers/${jobOfferId}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Job Offer with jobOfferId:${jobOfferId} not found.") }
		}
	}
	
	// ----- PUT /API/joboffers/{jobOfferId} -----
	/**
	 * Verify that the endpoint returns a *200 OK* status 
	 * when the job offer with the given ID is successfully updated
	 */
	@Test
	@Transactional
	@Rollback
	fun UpdateJobOffer() {
		// --- Arrange ---
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
		val name = "JobOffer1"
		val description = "javascript job"
		val currentStateNote = "node developer"
		val duration = 100.0
		val profitMargin = 15.0
		val customerId = c1.id
		val skillsData = listOf("javascript", "NodeJs")
		val skills = skillsData.map { CreateSkillDTO(it, null, null) }
		val skillsToDelete = jo1.skills.map { it.id }
		val expectedSkills = jo1.skills.map { it.copy(it.id,it.skill,contactInfoState.deleted,it.jobOfferId,it.professionalId) }.toMutableList()
		expectedSkills.add(SkillDTO(id = 0,skill = "javascript",state = contactInfoState.active,jobOfferId = jo1.id,professionalId = null))
		expectedSkills.add(SkillDTO(id = 0,skill = "NodeJs",state = contactInfoState.active,jobOfferId = jo1.id,professionalId = null))
		val jo1DTO = """{
			"name": "$name",
			"description": "$description",
			"currentStateNote": "$currentStateNote",
			"duration": $duration,
			"profitMargin": $profitMargin,
			"customerId": $customerId,
			"skills": [
				${
			skills.joinToString(",") { skill ->
				"""
					{
						"skill": "${skill.skill}",
						"professionalId": ${skill.professionalId ?: "null"},
						"jobOfferId": ${skill.jobOfferId ?: "null"}
					}
					""".trimIndent()
			}
		}
			],
			"skillsToDelete": [${skillsToDelete.joinToString(",")}]
		}""".trimIndent()
		mockMvc.put(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jo1DTO
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.id") { value(jo1.id) }
			jsonPath("$.name") { value(name) }
			jsonPath("$.description") { value(description) }
			jsonPath("$.currentStateNote") { value(currentStateNote) }
			jsonPath("$.duration") { value(duration) }
			jsonPath("$.profitMargin") { value(profitMargin) }
			jsonPath("$.customerId") { value(customerId) }
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
				jsonPath("$skillJsonPath.jobOfferId") { value(jo1.id.toInt()) }
				jsonPath("$skillJsonPath.professionalId") { value(expectedSkill.professionalId) }
			}

		}
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status 
	 * when the jobofferID is wrongly passed
	 */
	@Test
	@Transactional
	@Rollback
	fun UpdateJobOfferInvalidJobOfferId() {
		// --- Arrange ---
		val name = "JobOffer1"
		val description = "javascript job"
		val currentStateNote = "node developer"
		val duration = 100.0
		val profitMargin = 15.0
		val customerId = 1
		val skillsData = listOf("javascript", "NodeJs")
		val skills = skillsData.map { CreateSkillDTO(it, null, null) }
		val jo1DTO = """{
			"name": "$name",
			"description": "$description",
			"currentStateNote": "$currentStateNote",
			"duration": $duration,
			"profitMargin": $profitMargin,
			"customerId": $customerId,
			"skills": [
				${
			skills.joinToString(",") { skill ->
				"""
					{
						"skill": "${skill.skill}",
						"professionalId": ${skill.professionalId ?: "null"},
						"jobOfferId": ${skill.jobOfferId ?: "null"}
					}
					""".trimIndent()
			}
		}
			]
		}""".trimIndent()
		mockMvc.put(BASE_URL + "joboffers/-1") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jo1DTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid jobOfferId Parameter.") }
		}
	}

	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * when the job offer with the given ID is successfully updated
	 */
	@Test
	@Transactional
	@Rollback
	fun UpdateJobOfferNoCustomerId() {
		// --- Arrange ---
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
		val name = "JobOffer1"
		val description = "javascript job"
		val currentStateNote = "node developer"
		val duration = 100.0
		val profitMargin = 15.0
		val customerId = null
		val skillsData = listOf("javascript", "NodeJs")
		val skills = skillsData.map { CreateSkillDTO(it, null, null) }
		val jo1DTO = """{
			"name": "$name",
			"description": "$description",
			"currentStateNote": "$currentStateNote",
			"duration": $duration,
			"profitMargin": $profitMargin,
			"customerId": $customerId,
			"skills": [
				${
			skills.joinToString(",") { skill ->
				"""
					{
						"skill": "${skill.skill}",
						"professionalId": ${skill.professionalId ?: "null"},
						"jobOfferId": ${skill.jobOfferId ?: "null"}
					}
					""".trimIndent()
			}
		}
			]
		}""".trimIndent()
		mockMvc.put(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jo1DTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Customer ID is required to Link The Job Offer.") }

		}
	}

	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * when the job offer with the given ID is successfully updated
	 */
	@Test
	@Transactional
	@Rollback
	fun UpdateJobOfferJobOfferNotFound() {
		// --- Arrange ---
		val name = "JobOffer1"
		val description = "javascript job"
		val currentStateNote = "node developer"
		val duration = 100.0
		val profitMargin = 15.0
		val customerId = 1
		val skillsData = listOf("javascript", "NodeJs")
		val skills = skillsData.map { CreateSkillDTO(it, null, null) }
		val jo1DTO = """{
			"name": "$name",
			"description": "$description",
			"currentStateNote": "$currentStateNote",
			"duration": $duration,
			"profitMargin": $profitMargin,
			"customerId": $customerId,
			"skills": [
				${
			skills.joinToString(",") { skill ->
				"""
					{
						"skill": "${skill.skill}",
						"professionalId": ${skill.professionalId ?: "null"},
						"jobOfferId": ${skill.jobOfferId ?: "null"}
					}
					""".trimIndent()
			}
		}
			]
		}""".trimIndent()
		val jobOfferId = 2
		mockMvc.put(BASE_URL + "joboffers/${jobOfferId}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jo1DTO
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Job Offer with JobOfferId:${jobOfferId} not found.") }
		}
	}

	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * when the job offer with the given ID is successfully updated
	 */
	@Test
	@Transactional
	@Rollback
	fun UpdateJobOfferCustomerNotFound () {
		// --- Arrange ---
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
		val name = "JobOffer1"
		val description = "javascript job"
		val currentStateNote = "node developer"
		val duration = 100.0
		val profitMargin = 15.0
		val customerId = c1.id + 2
		val skillsData = listOf("javascript", "NodeJs")
		val skills = skillsData.map { CreateSkillDTO(it, null, null) }
		val jo1DTO = """{
			"name": "$name",
			"description": "$description",
			"currentStateNote": "$currentStateNote",
			"duration": $duration,
			"profitMargin": $profitMargin,
			"customerId": $customerId,
			"skills": [
				${
			skills.joinToString(",") { skill ->
				"""
					{
						"skill": "${skill.skill}",
						"professionalId": ${skill.professionalId ?: "null"},
						"jobOfferId": ${skill.jobOfferId ?: "null"}
					}
					""".trimIndent()
			}
		}
			]
		}""".trimIndent()
		mockMvc.put(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jo1DTO
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Customer with CustomerId:${customerId} not found.") }
		}
	}

	// DELETEJOBOFFERSKILL call TESTS

	@Test
	@Transactional
	@Rollback
	fun UpdateJobOfferInvalidSkillId() {
		// --- Arrange ---
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
		val name = "JobOffer1"
		val description = "javascript job"
		val currentStateNote = "node developer"
		val duration = 100.0
		val profitMargin = 15.0
		val customerId = c1.id
		val skillsData = listOf("javascript", "NodeJs")
		val skills = skillsData.map { CreateSkillDTO(it, null, null) }
		val skillsToDelete = jo1.skills.map { -it.id }
		val jo1DTO = """{
			"name": "$name",
			"description": "$description",
			"currentStateNote": "$currentStateNote",
			"duration": $duration,
			"profitMargin": $profitMargin,
			"customerId": $customerId,
			"skills": [
				${
			skills.joinToString(",") { skill ->
				"""
					{
						"skill": "${skill.skill}",
						"professionalId": ${skill.professionalId ?: "null"},
						"jobOfferId": ${skill.jobOfferId ?: "null"}
					}
					""".trimIndent()
			}
		}
			],
			"skillsToDelete": [${skillsToDelete.joinToString(",")}]
		}""".trimIndent()
		mockMvc.put(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jo1DTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid skillId Parameter.") }
		}
	}

	@Test
	@Transactional
	@Rollback
	fun UpdateJobOfferSkillNotFound() {
		// --- Arrange ---
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
		val name = "JobOffer1"
		val description = "javascript job"
		val currentStateNote = "node developer"
		val duration = 100.0
		val profitMargin = 15.0
		val customerId = c1.id
		val skillsData = listOf("javascript", "NodeJs")
		val skills = skillsData.map { CreateSkillDTO(it, null, null) }
		val skillsToDelete = jo1.skills.map { it.id * 10 }
		val jo1DTO = """{
			"name": "$name",
			"description": "$description",
			"currentStateNote": "$currentStateNote",
			"duration": $duration,
			"profitMargin": $profitMargin,
			"customerId": $customerId,
			"skills": [
				${
			skills.joinToString(",") { skill ->
				"""
					{
						"skill": "${skill.skill}",
						"professionalId": ${skill.professionalId ?: "null"},
						"jobOfferId": ${skill.jobOfferId ?: "null"}
					}
					""".trimIndent()
			}
		}
			],
			"skillsToDelete": [${skillsToDelete.joinToString(",")}]
		}""".trimIndent()
		mockMvc.put(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jo1DTO
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Skill with SkillId:${skillsToDelete[0]} not found.") }
		}
	}

	@Test
	@Transactional
	@Rollback
	fun UpdateJobOfferSkillNoPermissionToDelete() {
		// --- Arrange ---
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
		val jo2 = jobOfferService.createJobOffer(
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
		val name = "JobOffer1"
		val description = "javascript job"
		val currentStateNote = "node developer"
		val duration = 100.0
		val profitMargin = 15.0
		val customerId = c1.id
		val skillsData = listOf("javascript", "NodeJs")
		val skills = skillsData.map { CreateSkillDTO(it, null, null) }
		val skillsToDelete = jo2.skills.map { it.id }
		val jo1DTO = """{
			"name": "$name",
			"description": "$description",
			"currentStateNote": "$currentStateNote",
			"duration": $duration,
			"profitMargin": $profitMargin,
			"customerId": $customerId,
			"skills": [
				${
			skills.joinToString(",") { skill ->
				"""
					{
						"skill": "${skill.skill}",
						"professionalId": ${skill.professionalId ?: "null"},
						"jobOfferId": ${skill.jobOfferId ?: "null"}
					}
					""".trimIndent()
			}
		}
			],
			"skillsToDelete": [${skillsToDelete.joinToString(",")}]
		}""".trimIndent()
		mockMvc.put(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jo1DTO
		}.andExpect {
			// --- Assert ---
			status { isForbidden() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Skill with SkillId:${skillsToDelete[0]} does not belong to this job offer.") }
		}
	}

	@Test
	@Transactional
	@Rollback
	fun UpdateJobOfferSkillAlreadyDeleted() {
		// --- Arrange ---
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
		val name = "JobOffer1"
		val description = "javascript job"
		val currentStateNote = "node developer"
		val duration = 100.0
		val profitMargin = 15.0
		val customerId = c1.id
		val skillsData = listOf("javascript", "NodeJs")
		val skills = skillsData.map { CreateSkillDTO(it, null, null) }
		val skillsToDelete = jo1.skills.map { it.id }
		val jo1DTO = """{
			"name": "$name",
			"description": "$description",
			"currentStateNote": "$currentStateNote",
			"duration": $duration,
			"profitMargin": $profitMargin,
			"customerId": $customerId,
			"skills": [
				${
			skills.joinToString(",") { skill ->
				"""
					{
						"skill": "${skill.skill}",
						"professionalId": ${skill.professionalId ?: "null"},
						"jobOfferId": ${skill.jobOfferId ?: "null"}
					}
					""".trimIndent()
			}
		}
			],
			"skillsToDelete": [${skillsToDelete.joinToString(",")}]
		}""".trimIndent()
		mockMvc.put(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jo1DTO
		}

		mockMvc.put(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jo1DTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Skill with SkillId:${skillsToDelete[0]} already deleted.") }
		}
	}

	// ----- GET /API/joboffers/{jobOfferId}/history -----
	/**
	 * Verify that the endpoint returns a *200 OK* status 
	 * when the history of the job offer with the given ID is successfully retrieved
	 */
	@Test
	@Transactional
	@Rollback
	fun listJobOfferHistory () {
		// --- Arrange ---
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
		val note = "selection phase Started"
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.selection_phase, note, null)
		)
		mockMvc.get(BASE_URL + "joboffers/${jo1.id}/history") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.[*].id") { exists() }
			jsonPath("$.[*].state") { value(jobOfferStatus.selection_phase.toString()) }
			jsonPath("$.[*].date") { exists() }
			jsonPath("$.[*].note") { value(note) }
			jsonPath("$.[*].jobOfferId") { value(jo1.id.toInt()) }
		}
	}

	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the job offer with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun listJobOfferHistoryInvalidId() {
		// --- Arrange ---
		val invalidJobOfferId = -1L

		mockMvc.get(BASE_URL + "joboffers/$invalidJobOfferId/history") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid jobOfferId Parameter.") }
		}
	}

	/**
	 * Verify that the endpoint returns a *204 No Content* status 
	 * when the job offer with the given ID exists but has no history
	 */
	@Test
	@Transactional
	@Rollback
	fun listJobOfferHistoryJobOfferNotFound() {
		// --- Arrange ---
		val jobOfferId = 2
		mockMvc.get(BASE_URL + "joboffers/$jobOfferId/history") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Job Offer with JobOfferId:${jobOfferId} not found.") }
		}
	}

	
	// ----- GET /API/joboffers/open/{customerId} -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * when the customer with the given ID is successfully retrieved
	 */
	@Test
	@Transactional
	@Rollback
	fun getCustomerOpenJobOffers () {
		// --- Arrange ---
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
		val expected_ids = mutableListOf<Long>()
		expected_ids.add(jobOfferService.createJobOffer(
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
		).id)
		expected_ids.add(jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Junior Rust Developer",
				description = "Desktop application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 5,
				profitMargin = 6,
				customerId = c1.id,
				skills = listOf(
					CreateSkillDTO("English", null, null),
					CreateSkillDTO("Italian", null, null),
					CreateSkillDTO("Rust programming language", null, null)
				)
			)
		).id)
		expected_ids.add(jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Senior Kotlin Developer",
				description = "Web and Mobile application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 8,
				profitMargin = 12,
				customerId = c1.id,
				skills = listOf(
					CreateSkillDTO("English", null, null),
					CreateSkillDTO("Kotlin programming language", null, null)
				)
			)
		).id)

		mockMvc.get(BASE_URL + "joboffers/open/${c1.id}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$", hasSize<Int>(expected_ids.size))
			expected_ids.forEach { expectedId ->
				val jobOffer = "$.[?(@.id == '${expectedId}')]"
				jsonPath("$jobOffer.id") { value(expectedId.toInt()) }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getCustomerOpenJobOffersFiltering() {
		// --- Arrange ---
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
		val expected_ids = mutableListOf<Long>()
		expected_ids.add(jobOfferService.createJobOffer(
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
		).id)
		expected_ids.add(jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Junior Rust Developer",
				description = "Desktop application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 5,
				profitMargin = 6,
				customerId = c1.id,
				skills = listOf(
					CreateSkillDTO("English", null, null),
					CreateSkillDTO("Italian", null, null),
					CreateSkillDTO("Rust programming language", null, null)
				)
			)
		).id)
		expected_ids.add(jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Senior Kotlin Developer",
				description = "Web and Mobile application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 8,
				profitMargin = 12,
				customerId = c1.id,
				skills = listOf(
					CreateSkillDTO("English", null, null),
					CreateSkillDTO("Kotlin programming language", null, null)
				)
			)
		).id)
		val expected_size = 1
		mockMvc.get(BASE_URL + "joboffers/open/${c1.id}?pageNumber=0&limit=$expected_size") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$", hasSize<Int>(expected_size))
			jsonPath("$.[*].id") { contains(expected_ids[0] or expected_ids[1] or expected_ids[2]) }
		}
	}

	/**
	 * Verify that the endpoint returns a *404 Not Found* status
	 * when the customer with the given ID does not exist in the database
	 */
	@Test
	@Transactional
	@Rollback
	fun getCustomerOpenJobOffersInvalidCustomerId() {
		// --- Arrange ---
		val customerId = -1L

		mockMvc.get(BASE_URL + "joboffers/open/$customerId") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			jsonPath("$.detail") { value("Invalid customerId Parameter.") }
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getCustomerOpenJobOffersInvalidFiltering() {
		// --- Arrange ---
		val customerId = 1L

		mockMvc.get(BASE_URL + "joboffers/open/$customerId?pageNumber=0") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			jsonPath("$.detail") { value("PageNumber and limit must be both provided or both not provided.") }
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getCustomerOpenJobOffersCustomerNotFoundFiltering() {
		// --- Arrange ---
		val customerId = 1L

		mockMvc.get(BASE_URL + "joboffers/open/$customerId?pageNumber=0&limit=4") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			jsonPath("$.detail") { value("Customer with CustomerId:${customerId} not found.") }
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getCustomerOpenJobOffersCustomerNotFoundNoFiltering() {
		// --- Arrange ---
		val customerId = 1L

		mockMvc.get(BASE_URL + "joboffers/open/$customerId") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			jsonPath("$.detail") { value("Customer with CustomerId:${customerId} not found.") }
		}
	}

	
	// ----- GET /API/joboffers/accepted/{professionalId} -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * when the professional with the given ID is successfully retrieved
	 */
	@Test
	@Transactional
	@Rollback
	fun getProfessionalAcceptedJobOffers () {
		// --- Arrange ---
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085641",
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
		jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Senior Kotlin Developer",
				description = "Mobile application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 8,
				profitMargin = 6,
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
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085641",
				address = "123 ROma Street",
				employmentState = employmentState.available,
				geographicalLocation = Pair(45.77, 15.33),
				dailyRate = 8,
				notes = listOf("Italian", "Turin", "Engineering"),
				skills = listOf(
					CreateSkillDTO("Kotlin programming language", null, null),
					CreateSkillDTO("Rust programming language", null, null)
				),
				jobOfferId = null
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
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.consolidated, null, p1.id)
		)

		mockMvc.get(BASE_URL + "joboffers/accepted/${p1.id}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$", hasSize<Int>(1))
			jsonPath("$.[*].id") { value(jo1.id.toInt()) }
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getProfessionalAcceptedJobOffersFiltering () {
		// --- Arrange ---
		val c1 = customerService.createCustomer(
			CreateUpdateCustomerDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085641",
				address = "123 ROma Street",
				notes = listOf("Italian", "Turin", "Engineering"),
				jobOffers = listOf()
			)
		)
		val jobOffers = mutableListOf<JobOfferDTO>()
		jobOffers.add(jobOfferService.createJobOffer(
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
		))
		jobOffers.add(jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Senior Kotlin Developer",
				description = "Mobile application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 8,
				profitMargin = 6,
				customerId = c1.id,
				skills = listOf(
					CreateSkillDTO("English", null, null),
					CreateSkillDTO("Kotlin programming language", null, null)
				)
			)
		))
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085641",
				address = "123 ROma Street",
				employmentState = employmentState.available,
				geographicalLocation = Pair(45.77, 15.33),
				dailyRate = 8,
				notes = listOf("Italian", "Turin", "Engineering"),
				skills = listOf(
					CreateSkillDTO("Kotlin programming language", null, null),
					CreateSkillDTO("Rust programming language", null, null)
				),
				jobOfferId = null
			)
		)

		jobOffers.forEach{jobOffer ->
			jobOfferService.updateJobOfferStatus(
				jobOffer.id,
				UpdateJobOfferStatusDTO(jobOfferStatus.selection_phase, null, null)
			)
			jobOfferService.updateJobOfferStatus(
				jobOffer.id,
				UpdateJobOfferStatusDTO(jobOfferStatus.candidate_proposal, null, p1.id)
			)
			jobOfferService.updateJobOfferStatus(
				jobOffer.id,
				UpdateJobOfferStatusDTO(jobOfferStatus.consolidated, null, p1.id)
			)
			jobOfferService.updateJobOfferStatus(
				jobOffer.id,
				UpdateJobOfferStatusDTO(jobOfferStatus.done, null, p1.id)
			)
		}

		mockMvc.get(BASE_URL + "joboffers/accepted/${p1.id}?pageNumber=0&limit=1") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$", hasSize<Int>(1))
			jsonPath("$.[*].id") { contains(jobOffers[0].id.toInt() or jobOffers[1].id.toInt()) }
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getProfessionalAcceptedJobOffersInvalidId() {
		// --- Arrange ---
		mockMvc.get(BASE_URL + "joboffers/accepted/-1") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid professionalId Parameter.") }
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getProfessionalAcceptedJobOffersProfessionalNotFound() {
		// --- Arrange ---
		val professionalId = 1
		mockMvc.get(BASE_URL + "joboffers/accepted/${professionalId}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Professional with ProfessionalId:${professionalId} not found.") }
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getProfessionalAcceptedJobOffersProfessionalNotFoundFiltering() {
		// --- Arrange ---
		val professionalId = 1
		mockMvc.get(BASE_URL + "joboffers/accepted/${professionalId}?pageNumber=0&limit=1") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Professional with ProfessionalId:${professionalId} not found.") }
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getProfessionalAcceptedJobOffersInvalidFiltering() {
		// --- Arrange ---
		mockMvc.get(BASE_URL + "joboffers/accepted/1?pageNumber=0") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("PageNumber and limit must be both provided or both not provided.") }
		}
	}

	// ----- GET /API/joboffers/aborted/ -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * and a non-empty list
	 * when all aborted messages are successfully retrieved
	 */
	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffers() {
		// --- Arrange ---
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
		val jo2 = jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Senior Kotlin Developer",
				description = "Mobile application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 8,
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
			UpdateJobOfferStatusDTO(jobOfferStatus.aborted, null, null)
		)

		mockMvc.get(BASE_URL + "joboffers/aborted/") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			content {
				jsonPath("$", hasSize<Int>(1))
				jsonPath("$.[*].id") { value(jo1.id.toInt()) }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersCustomer() {
		// --- Arrange ---
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
		val jo2 = jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Senior Kotlin Developer",
				description = "Mobile application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 8,
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
			UpdateJobOfferStatusDTO(jobOfferStatus.aborted, null, null)
		)

		mockMvc.get(BASE_URL + "joboffers/aborted/?customerId=${c1.id}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			content {
				jsonPath("$", hasSize<Int>(1))
				jsonPath("$.[*].id") { value(jo1.id.toInt()) }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersProfessional() {
		// --- Arrange ---
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
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9026",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085641",
				address = "123 ROma Street",
				employmentState = employmentState.available,
				geographicalLocation = Pair(45.77, 15.33),
				dailyRate = 100.0,
				notes = listOf("Italian", "Turin", "Engineering"),
				skills = listOf(
					CreateSkillDTO("Kotlin programming language", null, null),
					CreateSkillDTO("Rust programming language", null, null)
				),
				jobOfferId = null
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
		val jo2 = jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Senior Kotlin Developer",
				description = "Mobile application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 8,
				profitMargin = 6,
				customerId = c1.id,
				skills = listOf(
					CreateSkillDTO("English", null, null),
					CreateSkillDTO("Kotlin programming language", null, null)
				)
			)
		)

		// --- Act ---
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.selection_phase, null, null)
		)
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.candidate_proposal, null, p1.id)
		)
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.aborted, null, p1.id)
		)

		jobOfferService.updateJobOfferStatus(
			jo2.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.aborted, null, null)
		)

		mockMvc.get(BASE_URL + "joboffers/aborted/?professionalId=${p1.id}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			content {
				jsonPath("$", hasSize<Int>(1))
				jsonPath("$.[*].id") { value(jo1.id.toInt()) }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersCustomerProfessional() {
		// --- Arrange ---
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
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085641",
				address = "123 ROma Street",
				employmentState = employmentState.available,
				geographicalLocation = Pair(45.77, 15.33),
				dailyRate = 8,
				notes = listOf("Italian", "Turin", "Engineering"),
				skills = listOf(
					CreateSkillDTO("Kotlin programming language", null, null),
					CreateSkillDTO("Rust programming language", null, null)
				),
				jobOfferId = null
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
		val jo2 = jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Senior Kotlin Developer",
				description = "Mobile application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 8,
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
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.aborted, null, p1.id)
		)

		jobOfferService.updateJobOfferStatus(
			jo2.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.aborted, null, null)
		)

		mockMvc.get(BASE_URL + "joboffers/aborted/?customerId=${c1.id}&professionalId=${p1.id}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			content {
				jsonPath("$", hasSize<Int>(2))
				jsonPath("$.[*].id") { contains(jo1.id.toInt() or jo2.id.toInt()) }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersFiltering() {
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
		val jo2 = jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Senior Kotlin Developer",
				description = "Mobile application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 8,
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
			UpdateJobOfferStatusDTO(jobOfferStatus.aborted, null, null)
		)

		jobOfferService.updateJobOfferStatus(
			jo2.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.aborted, null, null)
		)

		mockMvc.get(BASE_URL + "joboffers/aborted/?pageNumber=0&limit=1") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			content {
				jsonPath("$", hasSize<Int>(1))
				jsonPath("$.[*].id") { contains(jo1.id.toInt() or jo2.id.toInt()) }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersFilteringCustomer() {
		// --- Arrange ---
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
		val jo2 = jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Senior Kotlin Developer",
				description = "Mobile application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 8,
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
			UpdateJobOfferStatusDTO(jobOfferStatus.aborted, null, null)
		)

		mockMvc.get(BASE_URL + "joboffers/aborted/?pageNumber=0&limit=1&customerId=${c1.id}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			content {
				jsonPath("$", hasSize<Int>(1))
				jsonPath("$.[*].id") { value(jo1.id.toInt()) }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersFilteringProfessional() {
		// --- Arrange ---
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
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "111-23-9026",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085641",
				address = "123 ROma Street",
				employmentState = employmentState.available,
				geographicalLocation = Pair(45.77, 15.33),
				dailyRate = 100.0,
				notes = listOf("Italian", "Turin", "Engineering"),
				skills = listOf(
					CreateSkillDTO("Kotlin programming language", null, null),
					CreateSkillDTO("Rust programming language", null, null)
				),
				jobOfferId = null
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
		val jo2 = jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Senior Kotlin Developer",
				description = "Mobile application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 8,
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
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.aborted, null, p1.id)
		)

		jobOfferService.updateJobOfferStatus(
			jo2.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.aborted, null, null)
		)

		mockMvc.get(BASE_URL + "joboffers/aborted/?pageNumber=0&limit=1&professionalId=${p1.id}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			content {
				jsonPath("$", hasSize<Int>(1))
				jsonPath("$.[*].id") { value(jo1.id.toInt()) }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersFilteringCustomerProfessional() {
		// --- Arrange ---
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
		val p1 = professionalService.createProfessional(
			CreateUpdateProfessionalDTO(
				name = "Mario",
				surname = "Rossi",
				ssncode = "000-01-1234",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085641",
				address = "123 ROma Street",
				employmentState = employmentState.available,
				geographicalLocation = Pair(45.77, 15.33),
				dailyRate = 8,
				notes = listOf("Italian", "Turin", "Engineering"),
				skills = listOf(
					CreateSkillDTO("Kotlin programming language", null, null),
					CreateSkillDTO("Rust programming language", null, null)
				),
				jobOfferId = null
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
		val jo2 = jobOfferService.createJobOffer(
			CreateUpdateJobOfferDTO(
				name = "Senior Kotlin Developer",
				description = "Mobile application developer",
				currentState = jobOfferStatus.created,
				currentStateNote = "Just created",
				duration = 8,
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
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.aborted, null, p1.id)
		)

		jobOfferService.updateJobOfferStatus(
			jo2.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.aborted, null, null)
		)

		mockMvc.get(BASE_URL + "joboffers/aborted/?pageNumber=0&limit=2&customerId=${c1.id}&professionalId=${p1.id}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			content {
				jsonPath("$", hasSize<Int>(2))
				jsonPath("$.[*].id") { contains(jo1.id.toInt() or jo2.id.toInt()) }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersFilteringInvalidCustomerIdProfessionalId() {
		mockMvc.get(BASE_URL + "joboffers/aborted/?customerId=-1&professionalId=-1") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			content {
				jsonPath("$.detail") { value("Invalid customerId and professionalId Parameter.") }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersFilteringInvalidCustomerId() {
		mockMvc.get(BASE_URL + "joboffers/aborted/?customerId=-1") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			content {
				jsonPath("$.detail") { value("Invalid customerId Parameter.") }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersFilteringInvalidProfessionalId() {
		mockMvc.get(BASE_URL + "joboffers/aborted/?professionalId=-1") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			content {
				jsonPath("$.detail") { value("Invalid professionalId Parameter.") }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersFilteringInvalidFiltering() {
		mockMvc.get(BASE_URL + "joboffers/aborted/?pageNumber=0") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			content {
				jsonPath("$.detail") { value("PageNumber and limit must be both provided or both not provided.") }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersFilteringCustomerProfessionalCustomerNotFound() {
		val customerId = 1
		mockMvc.get(BASE_URL + "joboffers/aborted/?pageNumber=0&limit=2&customerId=${customerId}&professionalId=1") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			content {
				jsonPath("$.detail") { value("Customer with CustomerId:${customerId} not found.") }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersFilteringCustomerProfessionalProfessionalNotFound() {
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
		val professionalId = 1
		mockMvc.get(BASE_URL + "joboffers/aborted/?pageNumber=0&limit=2&customerId=${c1.id}&professionalId=${professionalId}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			content {
				jsonPath("$.detail") { value("Professional with ProfessionalId:${professionalId} not found.") }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersFilteringCustomerCustomerNotFound() {
		val customerId = 1
		mockMvc.get(BASE_URL + "joboffers/aborted/?pageNumber=0&limit=2&customerId=${customerId}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			content {
				jsonPath("$.detail") { value("Customer with CustomerId:${customerId} not found.") }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersFilteringProfessionalProfessionalNotFound() {
		val professionalId = 1
		mockMvc.get(BASE_URL + "joboffers/aborted/?pageNumber=0&limit=2&professionalId=${professionalId}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			content {
				jsonPath("$.detail") { value("Professional with ProfessionalId:${professionalId} not found.") }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersCustomerProfessionalCustomerNotFound() {
		val customerId = 1
		mockMvc.get(BASE_URL + "joboffers/aborted/?customerId=${customerId}&professionalId=1") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			content {
				jsonPath("$.detail") { value("Customer with CustomerId:${customerId} not found.") }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersCustomerProfessionalProfessionalNotFound() {
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
		val professionalId = 1
		mockMvc.get(BASE_URL + "joboffers/aborted/?customerId=${c1.id}&professionalId=${professionalId}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			content {
				jsonPath("$.detail") { value("Professional with ProfessionalId:${professionalId} not found.") }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersCustomerCustomerNotFound() {
		val customerId = 1
		mockMvc.get(BASE_URL + "joboffers/aborted/?customerId=${customerId}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			content {
				jsonPath("$.detail") { value("Customer with CustomerId:${customerId} not found.") }
			}
		}
	}

	@Test
	@Transactional
	@Rollback
	fun getAbortedJobOffersProfessionalProfessionalNotFound() {
		val professionalId = 1
		mockMvc.get(BASE_URL + "joboffers/aborted/?professionalId=${professionalId}") {
			// --- Act ---
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			content {
				jsonPath("$.detail") { value("Professional with ProfessionalId:${professionalId} not found.") }
			}
		}
	}

	// ----- POST /API/joboffers/{jobofferId} -----
	/**
	 * Verify that the endpoint returns a *200 ok* status
	 * when a job offer has changed its status correctly to 'selection_phase'
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus1 () {
		// --- Arrange ---
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
		val jobOfferDTO = """
			{
			"targetStatus": "selection_phase",
			"note": "Selecting candidates"
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.id") { value(jo1.id.toInt()) }
			jsonPath("$.currentState") { value("selection_phase") }
			jsonPath("$.currentStateNote") { value("Selecting candidates") }
			jsonPath("$.professionalId") { value(null) }
		}
	}
	/**
	 * Verify that the endpoint returns a *404 Not Found* status
	 * when the jobOffer ID is not found
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_notFound () {
		// --- Arrange ---
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
		val jobOfferDTO = """
			{
			"targetStatus": "selection_phase",
			"note": "Selecting candidates"
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id + 1}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("JobOffer with JobOfferId:${jo1.id + 1} not found.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the jobOffer ID is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_invalidId () {
		// --- Arrange ---
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
		val jobOfferDTO = """
			{
			"targetStatus": "selection_phase",
			"note": "Selecting candidates"
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/-1") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid jobOfferId Parameter.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the jobOffer status transition is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_invalidTransition1 () {
		// --- Arrange ---
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
		val jobOfferDTO = """
			{
			"targetStatus": "candidate_proposal"
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid jobOffer status transition (from 'created' only 'selection_phase/aborted' are possible).") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the jobOffer status transition is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_invalidTransition2 () {
		// --- Arrange ---
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

		val jobOfferDTO = """
			{
			"targetStatus": "consolidated"
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid jobOffer status transition (from 'selection_phase' only 'candidate_proposal/aborted' are possible).") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the jobOffer status transition is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_invalidTransition3 () {
		// --- Arrange ---
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

		val jobOfferDTO = """
			{
			"targetStatus": "candidate_proposal"
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("ProfessionalId is required for this status transition.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *404 Not Found* status
	 * when the jobOffer status transition requires a professionalId
	 * which is not found
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_invalidTransition4 () {
		// --- Arrange ---
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

		val jobOfferDTO = """
			{
			"targetStatus": "candidate_proposal",
			"professionalId": 1
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Professional with ProfessionalId:1 not found.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the jobOffer status transition is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_invalidTransition5 () {
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

		val jobOfferDTO = """
			{
			"targetStatus": "candidate_proposal",
			"professionalId": ${p1.id}
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Professional with ProfessionalId:${p1.id} is not available for work.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *200 ok* status
	 * when a candidate is found for the job offer
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_professionalFound () {
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

		val jobOfferDTO = """
			{
			"targetStatus": "candidate_proposal",
			"professionalId": ${p1.id}
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.id") { value(jo1.id.toInt()) }
			jsonPath("$.currentState") { value("candidate_proposal") }
			jsonPath("$.professionalId") { value(p1.id.toInt()) }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the jobOffer status transition is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_invalidTransition6 () {
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

		val jobOfferDTO = """
			{
			"targetStatus": "done",
			"professionalId": ${p1.id}
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid jobOffer status transition (from 'candidate_proposal' only 'consolidated/selection_phase/aborted' are possible).") }
		}
	}
	/**
	 * Verify that the endpoint returns a *200 ok* status
	 * when the jobOffer status goes back to the 'selection phase'
	 * because the professional is not available anymore
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_professionalNotAvailableAnymore1 () {
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
		professionalService.updateProfessional(
			p1.id,
			CreateUpdateProfessionalDTO(null,null,null,null,null,null,null,
				employmentState.not_available,null,null,null,null,null,null,
				null
			)
		)

		val jobOfferDTO = """
			{
			"targetStatus": "consolidated",
			"professionalId": ${p1.id}
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.id") { value(jo1.id.toInt()) }
			jsonPath("$.currentState") { value("selection_phase") }
		}
		val updatedProfessional = professionalService.findProfessionalById(p1.id)
		assertNull(updatedProfessional.jobOffer)
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the jobOffer status transition is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_invalidTransition7 () {
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

		val jobOfferDTO = """
			{
			"targetStatus": "selection_phase",
			"professionalId": ${p1.id}
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid jobOffer status transition (from 'candidate_proposal' is not possible to switch back to 'selection_phase' if the candidate is still available for work).") }
		}
	}
	/**
	 * Verify that the endpoint returns a *200 ok* status
	 * when the jobOffer status goes back to the 'selection phase'
	 * because the professional is not available anymore
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_professionalNotAvailableAnymore2 () {
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
		professionalService.updateProfessional(
			p1.id,
			CreateUpdateProfessionalDTO(null,null,null,null,null,null,null,
				employmentState.not_available,null,null,null,null,null,null,
				null
			)
		)

		val jobOfferDTO = """
			{
			"targetStatus": "selection_phase",
			"professionalId": ${p1.id}
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.id") { value(jo1.id.toInt()) }
			jsonPath("$.currentState") { value("selection_phase") }
		}
		val updatedProfessional = professionalService.findProfessionalById(p1.id)
		assertNull(updatedProfessional.jobOffer)
	}
	/**
	 * Verify that the endpoint returns a *200 ok* status
	 * when the jobOffer status is aborted from the 'candidate_proposal' status
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_aborted () {
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

		val jobOfferDTO = """
			{
			"targetStatus": "aborted",
			"professionalId": ${p1.id}
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.id") { value(jo1.id.toInt()) }
			jsonPath("$.currentState") { value("aborted") }
		}
		val updatedProfessional = professionalService.findProfessionalById(p1.id)
		assertNull(updatedProfessional.jobOffer)
	}
	/**
	 * Verify that the endpoint returns a *200 ok* status
	 * when a job offer has changed its status correctly to 'consolidated'
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus2 () {
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

		val jobOfferDTO = """
			{
			"targetStatus": "consolidated",
			"professionalId": ${p1.id}
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.id") { value(jo1.id.toInt()) }
			jsonPath("$.currentState") { value("consolidated") }
		}
		val updatedProfessional = professionalService.findProfessionalById(p1.id)
		assert(updatedProfessional.employmentState == employmentState.employed)
		assert(updatedProfessional.jobOffer?.id == jo1.id)
	}
	/**
	 * Verify that the endpoint returns a *200 ok* status
	 * when the jobOffer status goes back to the 'selection phase'
	 * because the professional is not available anymore
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_professionalNotAvailableAnymore3 () {
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
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.consolidated, null, p1.id)
		)
		professionalService.updateProfessional(
			p1.id,
			CreateUpdateProfessionalDTO(null,null,null,null,null,null,null,
				employmentState.available,null,null,null,null,null,null,
				null
			)
		)

		val jobOfferDTO = """
			{
			"targetStatus": "done",
			"professionalId": ${p1.id}
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.id") { value(jo1.id.toInt()) }
			jsonPath("$.currentState") { value("selection_phase") }
		}
		val updatedProfessional = professionalService.findProfessionalById(p1.id)
		assertNull(updatedProfessional.jobOffer)
	}
	/**
	 * Verify that the endpoint returns a *200 ok* status
	 * when a job offer has changed its status correctly to 'done'
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus3 () {
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
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.consolidated, null, p1.id)
		)

		val jobOfferDTO = """
			{
			"targetStatus": "done",
			"professionalId": ${p1.id}
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$.id") { value(jo1.id.toInt()) }
			jsonPath("$.currentState") { value("done") }
		}
		val updatedProfessional = professionalService.findProfessionalById(p1.id)
		assertNull(updatedProfessional.jobOffer)
		assert(updatedProfessional.employmentState == employmentState.available)
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the jobOffer status transition is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_invalidTransition8 () {
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
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.consolidated, null, p1.id)
		)

		val jobOfferDTO = """
			{
			"targetStatus": "candidate_proposal",
			"professionalId": ${p1.id}
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid jobOffer status transition (from 'consolidated' only 'done/aborted' are possible).") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the jobOffer status transition is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_invalidTransition9 () {
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
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.consolidated, null, p1.id)
		)
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.done, null, p1.id)
		)

		val jobOfferDTO = """
			{
			"targetStatus": "consolidated",
			"professionalId": ${p1.id}
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid jobOffer status transition (from 'done' only 'selection_phase' is possible).") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the jobOffer status transition is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun updatedJobOfferStatus_invalidTransition10 () {
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
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.consolidated, null, p1.id)
		)
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.aborted, null, p1.id)
		)

		val jobOfferDTO = """
			{
			"targetStatus": "consolidated",
			"professionalId": ${p1.id}
			}
		""".trimIndent()

		// --- Act ---
		mockMvc.post(BASE_URL + "joboffers/${jo1.id}") {
			// --- Act ---
			contentType = MediaType.APPLICATION_JSON
			content = jobOfferDTO
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid jobOffer status transition (from 'aborted' the status cannot change anymore).") }
		}
	}

	// ----- GET /API/joboffers/{jobofferId}/value -----
	/**
	 * Verify that the endpoint returns a *200 OK* status
	 * when the value of the job offer with the given ID is successfully retrieved
	 */
	@Test
	@Transactional
	@Rollback
	fun getJobOfferValue () {
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
				ssncode = "111-23-9026",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085642",
				address = "123 Roma Street",
				employmentState = employmentState.available,
				geographicalLocation = Pair(45.77, 15.33),
				dailyRate = 100.0,
				notes = listOf("Italian", "Turin", "Engineering"),
				skills = listOf(
					CreateSkillDTO("Kotlin programming language", null, null),
					CreateSkillDTO("Rust programming language", null, null)
				),
				jobOfferId = null
			)
		)

		// --- Act ---
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.selection_phase, null, null)
		)
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.candidate_proposal, null, p1.id)
		)
		jobOfferService.updateJobOfferStatus(
			jo1.id,
			UpdateJobOfferStatusDTO(jobOfferStatus.consolidated, null, p1.id)
		)

		mockMvc.get(BASE_URL + "joboffers/${jo1.id}/value") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isOk() }
			content { contentType(MediaType.APPLICATION_JSON) }
			jsonPath("$") { value(150000.0) }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the jobOfferId parameter is invalid
	 */
	@Test
	@Transactional
	@Rollback
	fun getJobOfferValue_invalidId () {
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
				ssncode = "111-23-9026",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085642",
				address = "123 Roma Street",
				employmentState = employmentState.available,
				geographicalLocation = Pair(45.77, 15.33),
				dailyRate = 100.0,
				notes = listOf("Italian", "Turin", "Engineering"),
				skills = listOf(
					CreateSkillDTO("Kotlin programming language", null, null),
					CreateSkillDTO("Rust programming language", null, null)
				),
				jobOfferId = null
			)
		)

		// --- Act ---
		mockMvc.get(BASE_URL + "joboffers/-1/value") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("Invalid jobOfferId Parameter.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *404 Not Found* status
	 * when the jobOfferId is not found
	 */
	@Test
	@Transactional
	@Rollback
	fun getJobOfferValue_notFound () {
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
				ssncode = "111-23-9026",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085642",
				address = "123 Roma Street",
				employmentState = employmentState.available,
				geographicalLocation = Pair(45.77, 15.33),
				dailyRate = 100.0,
				notes = listOf("Italian", "Turin", "Engineering"),
				skills = listOf(
					CreateSkillDTO("Kotlin programming language", null, null),
					CreateSkillDTO("Rust programming language", null, null)
				),
				jobOfferId = null
			)
		)

		// --- Act ---
		mockMvc.get(BASE_URL + "joboffers/${jo1.id + 1}/value") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isNotFound() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("JobOffer with JobOfferId:${jo1.id + 1} not found.") }
		}
	}
	/**
	 * Verify that the endpoint returns a *400 Bad Request* status
	 * when the jobOffer is not bound to a professional
	 */
	@Test
	@Transactional
	@Rollback
	fun getJobOfferValue_noProfessionalBound () {
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
				ssncode = "111-23-9026",
				category = category.customer,
				email = "mario.rossi@email.com",
				telephone = "+393312085642",
				address = "123 Roma Street",
				employmentState = employmentState.available,
				geographicalLocation = Pair(45.77, 15.33),
				dailyRate = 100.0,
				notes = listOf("Italian", "Turin", "Engineering"),
				skills = listOf(
					CreateSkillDTO("Kotlin programming language", null, null),
					CreateSkillDTO("Rust programming language", null, null)
				),
				jobOfferId = null
			)
		)

		// --- Act ---
		mockMvc.get(BASE_URL + "joboffers/${jo1.id}/value") {
			accept = MediaType.APPLICATION_JSON
		}.andExpect {
			// --- Assert ---
			status { isBadRequest() }
			content { contentType(MediaType.APPLICATION_PROBLEM_JSON) }
			jsonPath("$.detail") { value("JobOffer with JobOfferId:${jo1.id} is not bound to a professional.") }
		}
	}
}
