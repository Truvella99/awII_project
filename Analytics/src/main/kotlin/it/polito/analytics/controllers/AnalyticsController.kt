package it.polito.analytics.controllers

import it.polito.analytics.dtos.CustomerDTO
import it.polito.analytics.dtos.ProfessionalDTO
import it.polito.analytics.services.AnalyticsService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
class AnalyticsController(private val analyticsService: AnalyticsService) {

    /**
     * GET /API/customers/
     *
     * Return the statistics for all the customers
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/customers/")
    @PreAuthorize("isAuthenticated() && hasRole('manager')")
    fun getCustomersAnalytics() : Flux<CustomerDTO> {
        return analyticsService.computeCustomersData()
    }

    /**
     * GET /API/professionals/
     *
     * Return the statistics for all the professionals
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/professionals/")
    @PreAuthorize("isAuthenticated() && hasRole('manager')")
    fun getProfessionalAnalytics() : Flux<ProfessionalDTO> {
        return analyticsService.computeProfessionalsData()
    }
}