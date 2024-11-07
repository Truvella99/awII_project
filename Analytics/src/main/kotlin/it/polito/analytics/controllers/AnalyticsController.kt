package it.polito.analytics.controllers

import it.polito.analytics.services.AnalyticsService
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
class AnalyticsController(private val analyticsService: AnalyticsService) {

    /**
     * GET /API/customers/{customerId}/kpi
     *
     * Return the kpi of the given customer
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/customers/{customerId}/kpi")
    //@PreAuthorize("isAuthenticated() && hasRole('manager')")
    fun getCustomerKpi(@PathVariable("customerId")customerId: String) : Float {
        return analyticsService.computeCustomerKPI(customerId)
    }

    /**
     * GET /API/professionals/{professionalId}/kpi
     *
     * Return the kpi of the given professional
     */
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/API/professionals/{professionalId}/kpi")
    //@PreAuthorize("isAuthenticated() && hasRole('manager')")
    fun getProfessionalKpi(@PathVariable("professionalId")professionalId: String) : Float {
        return analyticsService.computeProfessionalKPI(professionalId)
    }
}