package it.polito.customerrelationshipmanagement.controllers

import it.polito.customerrelationshipmanagement.dtos.*
import it.polito.customerrelationshipmanagement.entities.employmentState
import it.polito.customerrelationshipmanagement.entities.priority
import it.polito.customerrelationshipmanagement.entities.state
import it.polito.customerrelationshipmanagement.services.CustomerService
import it.polito.customerrelationshipmanagement.services.JobOfferService
import it.polito.customerrelationshipmanagement.services.MessageService
import it.polito.customerrelationshipmanagement.services.ProfessionalService
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
class ProfessionalController(private val professionalService: ProfessionalService){
    /**
     * POST /API/professionals
     *
     * create a new professional
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/API/professionals")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun createProfessional(@RequestBody @Valid p: CreateUpdateProfessionalDTO): ProfessionalDTO {
        return professionalService.createProfessional(p)
    }
    /**
     * POST /API/professionals/{professionalId}/note
     *
     * add a note to the professional {professionalId}
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/API/professionals/{professionalId}/note")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun addProfessionalNotes(@PathVariable("professionalId") professionalId: String, @RequestBody note:CreateUpdateNoteDTO): NoteDTO {
        return professionalService.addProfessionalNote(professionalId, note)
    }
    /**
     * GET /API/professionals/
     *
     * list all registered professionals in the DB. Allow for
     * pagination, limiting results, and filtering by content, using request parameters.
     */
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/professionals/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun listAllProfessionals(
        @RequestParam("pageNumber") pageNumber: Int?,
        @RequestParam("limit") limit: Int?,
        @RequestParam("skills") skills: List<String>?,
        @RequestParam("latitude") latitude: Double?,
        @RequestParam("longitude") longitude: Double?,
        @RequestParam("employmentState") employmentState: employmentState?
    ): List<ProfessionalDTO> {
        return professionalService.listAllProfessionals(pageNumber, limit, skills, latitude,longitude, employmentState)
    }
    /**
     * GET /API/professionals/distance/
     *
     * list registered professionals in the DB filtering by km distance.
     */
    @Validated
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/professionals/distance/")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun listProfessionalsDistance(
        @RequestParam("skills") skills: List<String>?,
        @RequestParam("latitude") latitude: Double,
        @RequestParam("longitude") longitude: Double,
        @RequestParam("km") km: Double
    ): List<ProfessionalDTO> {
        return professionalService.listProfessionalsDistance(skills, latitude, longitude, km)
    }
    /**
     * GET /API/professionals/{professionalId}
     *
     * details of professional {professionalId} or fail if it does not exist.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/professionals/{professionalId}")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager') || hasRole('professional')  )")
    fun getProfessional(@PathVariable("professionalId") professionalId: String): ProfessionalDTO {
        return professionalService.findProfessionalById(professionalId)
    }

    /**
     * GET /API/professionals/filters/{filter}
     *
     * find professionals with the properties that match the filter passed.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/professionals/filters/{filter}")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun findProfessionals(@PathVariable("filter") filter: String): List<ProfessionalDTO> {
        return professionalService.findProfessionals(filter)
    }

    /**
     * PUT /API/professionals/{professionalId}
     *
     * update professional {professionalId}
     */
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/API/professionals/{professionalId}")
    @PreAuthorize("isAuthenticated() && (hasRole('operator') || hasRole('manager'))")
    fun updateProfessional(
        @PathVariable("professionalId") professionalId: String,
        @RequestBody  @Valid professional: CreateUpdateProfessionalDTO
    ): ProfessionalDTO {
        return professionalService.updateProfessional(professionalId, professional)
    }
}