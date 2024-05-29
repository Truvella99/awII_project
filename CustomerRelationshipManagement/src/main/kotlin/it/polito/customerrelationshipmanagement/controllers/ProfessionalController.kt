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
        println(p)
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
    fun addProfessionalNotes(@PathVariable("professionalId") professionalId: Long, @RequestBody note:CreateUpdateNoteDTO): NoteDTO {
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
    @PreAuthorize("isAuthenticated()")
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
     * GET /API/professionals/{professionalId}
     *
     * details of professional {professionalId} or fail if it does not exist.
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/professionals/{professionalId}")
    @PreAuthorize("isAuthenticated()")
    fun getProfessional(@PathVariable("professionalId") professionalId: Long): ProfessionalDTO {
        return professionalService.findProfessionalById(professionalId)
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
        @PathVariable("professionalId") professionalId: Long,
        @RequestBody  @Valid professional: CreateUpdateProfessionalDTO
    ): ProfessionalDTO {
        return professionalService.updateProfessional(professionalId, professional)
    }
}