package it.polito.communicationmanager.exceptionHandlers

import it.polito.communicationmanager.exceptions.EmailProcessingException
import it.polito.communicationmanager.exceptions.WrongEmailException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ProblemDetailsHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(EmailProcessingException::class)
    fun handleContactAlreadyExists(e: EmailProcessingException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.message!!)
    @ExceptionHandler(WrongEmailException::class)
    fun handleContactAlreadyExists(e: WrongEmailException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)
}