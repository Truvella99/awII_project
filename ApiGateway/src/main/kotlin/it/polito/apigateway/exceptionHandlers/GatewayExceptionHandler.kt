package it.polito.apigateway.exceptionHandlers

import it.polito.apigateway.exceptions.*

import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GatewayExceptionHandler: ResponseEntityExceptionHandler() {
    @ExceptionHandler(DocStoreException :: class)
    fun handleDocStoreException(e: DocStoreException) =
        ProblemDetail.forStatusAndDetail(e.status, e.message!!)
}