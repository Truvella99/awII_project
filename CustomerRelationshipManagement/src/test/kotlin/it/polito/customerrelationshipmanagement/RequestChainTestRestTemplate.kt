package it.polito.customerrelationshipmanagement

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.*
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.Rollback


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RequestChainTestRestTemplate: IntegrationTest() {
	@LocalServerPort
	private var port: Int = 0
	@Autowired
	private lateinit var restTemplate: TestRestTemplate

	@Transactional
	@Rollback
	@Test
	fun requestChain() {
		val BASE_URL = "http://localhost:${port}/API/"
		// CREATECUSTOMER

		val customerDTO = CreateUpdateCustomerDTO(
			name = "John",
			surname = "Doe",
			ssncode = "111-23-9025",
			category = category.customer,
			email = "john.doe@example.com",
			telephone = "+393312085641",
			address = "Corso Gaetano Scirea, 50, 10151 Torino TO",
			jobOffers = null, // Add job offers if needed
			notes = null // Add notes if needed
		)

		// Send request to the API endpoint
		var url = BASE_URL + "customers/"
		val requestEntityCreateCustomer = HttpEntity(customerDTO)
		val responseCreateCustomer: ResponseEntity<CustomerDTO> = restTemplate
			.postForEntity(url, requestEntityCreateCustomer, CustomerDTO::class.java)

		// Assert
		assert(responseCreateCustomer.statusCode == HttpStatus.CREATED)
		val createdCustomer = responseCreateCustomer.body!!
		assert(createdCustomer.name == "John")
		assert(createdCustomer.surname == "Doe")
		assert(createdCustomer.ssncode == "111-23-9025")
		assert(createdCustomer.category == category.customer)
		assert(createdCustomer.emails.any { it.email == "john.doe@example.com" && it.state == contactInfoState.active })
		assert(createdCustomer.telephones.any { it.telephone == "+393312085641" && it.state == contactInfoState.active })
		assert(createdCustomer.addresses.any { it.address == "Corso Gaetano Scirea, 50, 10151 Torino TO" && it.state == contactInfoState.active })


		// ADDCUSTOMERNOTE


		val noteDTO = CreateUpdateNoteDTO("note to be added.")
		// Send request to the API endpoint
		url = BASE_URL + "customers/${createdCustomer.id}/note"
		val requestEntityAddNote = HttpEntity(noteDTO)
		val responseAddNote = restTemplate
			.postForEntity(url, requestEntityAddNote, NoteDTO::class.java)
		// Assert
		assert(responseAddNote.statusCode == HttpStatus.CREATED)
		val createdNote = responseAddNote.body!!
		assert(createdNote.note == "note to be added.")
		assert(createdNote.state == contactInfoState.active)
		assert(createdNote.customerId == createdCustomer.id)
		assert(createdNote.professionalId == null)


		// FINDCUSTOMERBYID


		url = BASE_URL + "customers/${createdCustomer.id}"
		val responseFindCustomerById = restTemplate
			.getForEntity(url, CustomerDTO::class.java)
		// Assert
		val retrievedCustomer = responseFindCustomerById.body!!
		assert(responseFindCustomerById.statusCode == HttpStatus.OK)
		assert(retrievedCustomer.id == createdCustomer.id)


		// UPDATECUSTOMER


		val updatedName = "Luigi"
		val updatedSurname = "Verdi"
		val updatedSsnCode = "111-23-9025"
		val updatedEmail = "luigi.verdi@example.com"
		val updatedTelephone = "+393312085642"
		val updatedAddress = "124 Roma Street"
		val updatedNotes = listOf("Updated Note")

		val c2 = CreateUpdateCustomerDTO(
			name = updatedName,
			surname = updatedSurname,
			ssncode = updatedSsnCode,
			category = category.customer,
			email = updatedEmail,
			telephone = updatedTelephone,
			address = updatedAddress,
			notes = updatedNotes,
			jobOffers = null
		)
		url = BASE_URL + "customers/${createdCustomer.id}"
		val requestEntityUpdateCustomer = HttpEntity(c2)
		val responseUpdateCustomer = restTemplate
			.exchange(url, HttpMethod.PUT, requestEntityUpdateCustomer, CustomerDTO::class.java)
		// Assert
		assert(responseUpdateCustomer.statusCode == HttpStatus.OK)
		val updatedCustomer = responseUpdateCustomer.body!!
		assert(updatedCustomer.name == updatedName)
		assert(updatedCustomer.surname == updatedSurname)
		assert(updatedCustomer.ssncode == updatedSsnCode)
		assert(updatedCustomer.category == category.customer)
		assert(updatedCustomer.emails.any { it.email == updatedEmail && it.state == contactInfoState.active})
		assert(updatedCustomer.telephones.any { it.telephone == updatedTelephone && it.state == contactInfoState.active})
		assert(updatedCustomer.addresses.any { it.address == updatedAddress && it.state == contactInfoState.active})
		assert(updatedCustomer.notes.any { it.note == "Updated Note" && it.state == contactInfoState.active})


		// CREATEJOBOFFER


		var skills = listOf(CreateSkillDTO("javascript2",null,null),CreateSkillDTO("NodeJs2",null,null))
		val createJobOfferDTO = CreateUpdateJobOfferDTO(
			name = "JobOffer1",
			description = "javascript job",
			currentStateNote = "node developer",
			duration = 100.0,
			profitMargin = 15.0,
			customerId = createdCustomer.id,
			skills = skills
		)

		// Send request to the API endpoint
		url = BASE_URL + "joboffers/"
		val requestEntityCreateJobOffer = HttpEntity(createJobOfferDTO)
		val responseCreateJobOffer = restTemplate
			.postForEntity(url, requestEntityCreateJobOffer, JobOfferDTO::class.java)
		// Assert
		assert(responseCreateJobOffer.statusCode == HttpStatus.CREATED)
		val createdJobOffer = responseCreateJobOffer.body!!
		assert(createdJobOffer.name == "JobOffer1")
		assert(createdJobOffer.description == "javascript job")
		assert(createdJobOffer.currentState == jobOfferStatus.created)
		assert(createdJobOffer.currentStateNote == "node developer")
		assert(createdJobOffer.duration == 100.0)
		assert(createdJobOffer.profitMargin == 15.0)
		assert(createdJobOffer.customerId == createdCustomer.id)
		createdJobOffer.skills.forEach { skill ->
			val requestSkill = skills.find { it.skill == skill.skill }
			assert(skill.skill == requestSkill?.skill)
			assert(skill.state == contactInfoState.active)
		}


		// FINDJOBOFFERBYID


		url = BASE_URL + "joboffers/${createdJobOffer.id}"
		val responseFindJobOfferById = restTemplate
			.getForEntity(url, JobOfferDTO::class.java)
		// Assert
		val retrievedJobOffer = responseFindJobOfferById.body!!
		assert(responseFindJobOfferById.statusCode == HttpStatus.OK)
		assert(retrievedJobOffer.id == createdJobOffer.id)


		//UPDATEJOBOFFER

		val name = "JobOffer1"
		val description = "javascript job"
		val currentStateNote = "node developer"
		val duration = 100.0
		val profitMargin = 15.0
		val customerId = createdCustomer.id
		val skillsData = listOf("javascript", "NodeJs")
		val skillsJobOffer = skillsData.map { CreateSkillDTO(it, null, null) }
		val skillsToDelete = createdJobOffer.skills.map { it.id }
		val expectedSkills = createdJobOffer.skills.map { it.copy(it.id,it.skill,contactInfoState.deleted,it.jobOfferId,it.professionalId) }.toMutableList()
		expectedSkills.add(SkillDTO(id = 0,skill = "javascript",state = contactInfoState.active,jobOfferId = createdJobOffer.id,professionalId = null))
		expectedSkills.add(SkillDTO(id = 0,skill = "NodeJs",state = contactInfoState.active,jobOfferId = createdJobOffer.id,professionalId = null))

		url = BASE_URL + "joboffers/${createdJobOffer.id}"
		val jobOfferToUpdate = CreateUpdateJobOfferDTO(
			name = name,
			description = description,
			currentStateNote = currentStateNote,
			duration = duration,
			profitMargin = profitMargin,
			customerId = customerId,
			skills = skillsJobOffer,
			skillsToDelete = skillsToDelete
		)
		val requestEntityUpdateJobOffer = HttpEntity(jobOfferToUpdate)
		val responseUpdateJobOffer = restTemplate
			.exchange(url, HttpMethod.PUT, requestEntityUpdateJobOffer, JobOfferDTO::class.java)
		// Assert
		assert(responseUpdateJobOffer.statusCode == HttpStatus.OK)
		val updatedJobOffer = responseUpdateJobOffer.body!!
		assert(updatedJobOffer.name == name)
		assert(updatedJobOffer.description == description)
		assert(updatedJobOffer.currentStateNote == currentStateNote)
		assert(updatedJobOffer.duration == duration)
		assert(updatedJobOffer.profitMargin == profitMargin)
		assert(updatedJobOffer.customerId == customerId)


		updatedJobOffer.skills.forEach { skill ->
			val expectedSkill = expectedSkills.find { it.skill == skill.skill }
			assert(skill.skill == expectedSkill?.skill)
			assert(skill.state == expectedSkill?.state)
			assert(skill.jobOfferId == expectedSkill?.jobOfferId)
			assert(skill.professionalId == expectedSkill?.professionalId)
		}


		// GETCUSTOMEROPENJOBOFFERS


		url = BASE_URL + "joboffers/open/${createdCustomer.id}"
		val responseTypeJobOffers = object : ParameterizedTypeReference<List<JobOfferDTO>>() {}
		val responseFindJobOffersById = restTemplate
			.exchange(url, HttpMethod.GET, null, responseTypeJobOffers)
		// Assert
		val retrievedJobOffers = responseFindJobOffersById.body!!
		assert(responseFindCustomerById.statusCode == HttpStatus.OK)
		assert(retrievedJobOffers.size == 1)
		assert(retrievedJobOffers[0].id == createdJobOffer.id)


		// CREATEPROFESSIONAL

		val notes = listOf("Note1","Note2")
		skills = listOf(CreateSkillDTO("Skill1",null,null),CreateSkillDTO("Skill2",null,null))
		val professionalToCreate = CreateUpdateProfessionalDTO(
			name = "Gaetano",
			surname = "Doe",
			ssncode = "111-23-9025",
			category = category.professional,
			email = "gaetano.doe@example.com",
			telephone = "+393312085641",
			address = "Via Roma, 1, 10121 Torino TO",
			dailyRate = 100.0,
			employmentState = employmentState.available,
			geographicalLocation = Pair(0.0,0.0),
			notes = notes,
			skills = skills,
			jobOfferId = null
		)

		url = BASE_URL + "professionals"
		val requestEntityCreateProfessional = HttpEntity(professionalToCreate)
		val responseCreateProfessional = restTemplate
			.postForEntity(url, requestEntityCreateProfessional, ProfessionalDTO::class.java)
		// Assert
		val createdProfessional = responseCreateProfessional.body!!
		assert(responseCreateProfessional.statusCode == HttpStatus.CREATED)
		assert(createdProfessional.name == "Gaetano")
		assert(createdProfessional.surname == "Doe")
		assert(createdProfessional.ssncode == "111-23-9025")
		assert(createdProfessional.category == category.professional)
		assert(createdProfessional.emails.any { it.email == "gaetano.doe@example.com" && it.state == contactInfoState.active})
		assert(createdProfessional.telephones.any { it.telephone == "+393312085641" && it.state == contactInfoState.active})
		assert(createdProfessional.addresses.any { it.address == "Via Roma, 1, 10121 Torino TO" && it.state == contactInfoState.active})
		assert(createdProfessional.dailyRate == 100.0)
		assert(createdProfessional.employmentState == employmentState.available)
		assert(createdProfessional.geographicalLocation == Pair(0.0,0.0))
		createdProfessional.skills.forEach { skill->
			val Skill = skills.find { it.skill == skill.skill }
			assert(skill.skill == Skill?.skill)
			assert(skill.professionalId == createdProfessional.id)
			assert(skill.jobOfferId == null)
			assert(skill.state == contactInfoState.active)
		}


		// ADDPROFESSIONALNOTES


		val noteProfessionalDTO = CreateUpdateNoteDTO("note to be added.")
		// Send request to the API endpoint
		url = BASE_URL + "professionals/${createdProfessional.id}/note"
		val requestEntityAddNoteProfessional = HttpEntity(noteProfessionalDTO)
		val responseAddNoteProfessional = restTemplate
			.postForEntity(url, requestEntityAddNoteProfessional, NoteDTO::class.java)
		// Assert
		assert(responseAddNoteProfessional.statusCode == HttpStatus.CREATED)
		val createdNoteProfessional = responseAddNoteProfessional.body!!

		assert(createdNoteProfessional.note == "note to be added.")
		assert(createdNoteProfessional.state == contactInfoState.active)
		assert(createdNoteProfessional.customerId == null)
		assert(createdNoteProfessional.professionalId == createdProfessional.id)


		//LISTALLPROFESSIONALS


		url = BASE_URL + "professionals/"
		val responseTypeProfessionals = object : ParameterizedTypeReference<List<ProfessionalDTO>>() {}
		val responseFindAllProfessionals = restTemplate
			.exchange(url, HttpMethod.GET, null, responseTypeProfessionals)
		// Assert
		val retrievedAllProfessionals = responseFindAllProfessionals.body!!
		assert(responseFindAllProfessionals.statusCode == HttpStatus.OK)
		assert(retrievedAllProfessionals.size == 1)
		assert(retrievedAllProfessionals[0].id == createdProfessional.id)


		// FINDPROFESSIONALBYID


		url = BASE_URL + "professionals/${createdProfessional.id}"
		val responseFindProfessionalById = restTemplate
			.getForEntity(url, ProfessionalDTO::class.java)
		// Assert
		val retrievedProfessional = responseFindProfessionalById.body!!
		assert(responseFindJobOfferById.statusCode == HttpStatus.OK)
		assert(retrievedProfessional.id == createdProfessional.id)


		//UPDATEJOBOFFERSTATUS (from CREATED to SELECTION)


		val updateJobOfferStatusDTO1 = UpdateJobOfferStatusDTO(
			targetStatus = jobOfferStatus.selection_phase,
			professionalId = null,
			note = null
		)
		url = BASE_URL + "joboffers/${createdJobOffer.id}"
		val requestEntityUpdateJobOfferStatus1 = HttpEntity(updateJobOfferStatusDTO1)
		val responseUpdateJobOfferStatus1 = restTemplate
			.exchange(url, HttpMethod.POST, requestEntityUpdateJobOfferStatus1, JobOfferDTO::class.java)
		// Assert
		val updatedJobOfferStatus1 = responseUpdateJobOfferStatus1.body!!
		assert(responseUpdateJobOfferStatus1.statusCode == HttpStatus.OK)
		assert(updatedJobOfferStatus1.id == createdJobOffer.id)
		assert(updatedJobOfferStatus1.currentState == jobOfferStatus.selection_phase)
		assert(updatedJobOfferStatus1.professionalId == null)


		//UPDATEJOBOFFERSTATUS (from SELECTION to CANDIDATE_PROPOSAL)


		val updateJobOfferStatusDTO2 = UpdateJobOfferStatusDTO(
			targetStatus = jobOfferStatus.candidate_proposal,
			professionalId = createdProfessional.id,
			note = null
		)
		url = BASE_URL + "joboffers/${createdJobOffer.id}"
		val requestEntityUpdateJobOfferStatus2 = HttpEntity(updateJobOfferStatusDTO2)
		val responseUpdateJobOfferStatus2 = restTemplate
			.exchange(url, HttpMethod.POST, requestEntityUpdateJobOfferStatus2, JobOfferDTO::class.java)
		// Assert
		val updatedJobOfferStatus2 = responseUpdateJobOfferStatus2.body!!
		assert(responseUpdateJobOfferStatus2.statusCode == HttpStatus.OK)
		assert(updatedJobOfferStatus2.id == createdJobOffer.id)
		assert(updatedJobOfferStatus2.currentState == jobOfferStatus.candidate_proposal)
		assert(updatedJobOfferStatus2.professionalId == createdProfessional.id)


		// UPDATEPROFESSIONAL


		val p2 = CreateUpdateProfessionalDTO(
			name = updatedName,
			surname = updatedSurname,
			ssncode = updatedSsnCode,
			category = category.professional,
			email = updatedEmail,
			telephone = updatedTelephone,
			address = updatedAddress,
			notes = updatedNotes,
			skills = null,
			skillsToDelete = null,
			dailyRate = 120.0,
			employmentState = employmentState.employed,
			geographicalLocation = Pair(10.0,2.0),
			notesToDelete = null,
			jobOfferId = createdJobOffer.id
		)

		url = BASE_URL + "professionals/${createdProfessional.id}"
		val requestEntityUpdateProfessional = HttpEntity(p2)
		val responseUpdateProfessional = restTemplate
			.exchange(url, HttpMethod.PUT, requestEntityUpdateProfessional, ProfessionalDTO::class.java)
		// Assert
		assert(responseUpdateProfessional.statusCode == HttpStatus.OK)
		val updatedProfessional = responseUpdateProfessional.body!!
		assert(updatedProfessional.name == updatedName)
		assert(updatedProfessional.surname == updatedSurname)
		assert(updatedProfessional.ssncode == updatedSsnCode)
		assert(updatedProfessional.category == category.professional)
		assert(updatedProfessional.emails.any { it.email == updatedEmail && it.state == contactInfoState.active})
		assert(updatedProfessional.telephones.any { it.telephone == updatedTelephone && it.state == contactInfoState.active})
		assert(updatedProfessional.addresses.any { it.address == updatedAddress && it.state == contactInfoState.active})
		assert(updatedProfessional.notes.any { it.note == "Updated Note" && it.state == contactInfoState.active})
		assert(updatedProfessional.dailyRate == 120.0)
		assert(updatedProfessional.employmentState == employmentState.employed)
		assert(updatedProfessional.geographicalLocation == Pair(10.0,2.0))
		updatedProfessional.skills.forEach { skill->
			val Skill = skills.find { it.skill == skill.skill }
			assert(skill.skill == Skill?.skill)
			assert(skill.professionalId == updatedProfessional.id)
			assert(skill.jobOfferId == null)
			assert(skill.state == contactInfoState.active)
		}


		// GETPROFESSIONALACCEPTEDJOBOFFERS


		url = BASE_URL + "joboffers/accepted/${createdProfessional.id}"
		val responseTypeGetProfessionalAcceptedJobOffers = object : ParameterizedTypeReference<List<JobOfferDTO>>() {}
		val responseGetProfessionalAcceptedJobOffers = restTemplate
			.exchange(url, HttpMethod.GET, null, responseTypeGetProfessionalAcceptedJobOffers)
		// Assert
		val retrievedGetProfessionalAcceptedJobOffers = responseGetProfessionalAcceptedJobOffers.body!!
		assert(responseGetProfessionalAcceptedJobOffers.statusCode == HttpStatus.OK)
		//assert(retrievedGetProfessionalAcceptedJobOffers.size == 1)


		// GETJOBOFFERVALUE


		url = BASE_URL + "joboffers/${createdJobOffer.id}/value"
		val responseGetJobOfferValue = restTemplate.getForEntity(url, Number::class.java)

		// Assert
		val retrievedGetJobOfferValue = responseGetJobOfferValue.body!!
		assert(responseGetJobOfferValue.statusCode == HttpStatus.OK)
		assert(retrievedGetJobOfferValue.toDouble() == (updatedJobOffer.duration.toDouble() * updatedJobOffer.profitMargin.toDouble() * updatedProfessional.dailyRate.toDouble()))


		//UPDATEJOBOFFERSTATUS (from CONSOLIDATED to ABORTED)


		val updateJobOfferStatusDTO3 = UpdateJobOfferStatusDTO(
			targetStatus = jobOfferStatus.aborted,
			professionalId = null,
			note = null
		)
		url = BASE_URL + "joboffers/${createdJobOffer.id}"
		val requestEntityUpdateJobOfferStatus3 = HttpEntity(updateJobOfferStatusDTO3)
		val responseUpdateJobOfferStatus3 = restTemplate
			.exchange(url, HttpMethod.POST, requestEntityUpdateJobOfferStatus3, JobOfferDTO::class.java)
		// Assert
		val updatedJobOfferStatus3 = responseUpdateJobOfferStatus3.body!!
		assert(responseUpdateJobOfferStatus3.statusCode == HttpStatus.OK)
		assert(updatedJobOfferStatus3.id == createdJobOffer.id)
		assert(updatedJobOfferStatus3.currentState == jobOfferStatus.aborted)
		assert(updatedJobOfferStatus3.professionalId == createdProfessional.id)
		assert(createdProfessional.employmentState == employmentState.available)
		assert(createdProfessional.jobOffer == null)


		// GETABORTEDJOBOFFERS


		url = BASE_URL + "joboffers/aborted/"
		val responseTypeGetAbortedJobOffer = object : ParameterizedTypeReference<List<JobOfferDTO>>() {}
		val responseGetAbortedJobOffer = restTemplate
			.exchange(url, HttpMethod.GET, null, responseTypeGetAbortedJobOffer)
		// Assert
		val retrievedGetAbortedJobOffer = responseGetAbortedJobOffer.body!!
		assert(responseGetAbortedJobOffer.statusCode == HttpStatus.OK)
		assert(retrievedGetAbortedJobOffer.size == 1)
		assert(retrievedGetAbortedJobOffer[0].id == createdJobOffer.id)


		// LISTJOBOFFERHISTORY


		url = BASE_URL + "joboffers/${createdJobOffer.id}/history"
		val responseTypeGetJobOfferHistory = object : ParameterizedTypeReference<List<JobOfferHistoryDTO>>() {}
		val responseGetJobOfferHistory = restTemplate
			.exchange(url, HttpMethod.GET, null, responseTypeGetJobOfferHistory)
		// Assert
		val retrievedGetJobOfferHistory = responseGetJobOfferHistory.body!!
		assert(responseGetJobOfferHistory.statusCode == HttpStatus.OK)
		assert(retrievedGetJobOfferHistory.size == 4)
		assert(retrievedGetJobOfferHistory.any { it.id == createdJobOffer.id })
	}

}
