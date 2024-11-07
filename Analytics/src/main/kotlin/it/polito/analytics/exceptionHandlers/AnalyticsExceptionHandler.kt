package it.polito.analytics.exceptionHandlers

import it.polito.analytics.exceptions.IllegalIdException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class AnalyticsExceptionHandler: ResponseEntityExceptionHandler() {
    @ExceptionHandler(IllegalIdException :: class)
    fun handleWrongParamsGetDocument(e: IllegalIdException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.BAD_REQUEST, e.message !! )
}