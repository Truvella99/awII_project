package it.polito.wa2.g05.document_store.exceptionHandlers

import it.polito.wa2.g05.document_store.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class DocumentExceptionHandler: ResponseEntityExceptionHandler() {
    @ExceptionHandler(IllegalPageNumberLimitException :: class)
    fun handleWrongParamsGetAll(e: IllegalPageNumberLimitException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.BAD_REQUEST, e.message !! )

    @ExceptionHandler(IllegalIdException :: class)
    fun handleWrongParamsGetDocument(e: IllegalIdException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.BAD_REQUEST, e.message !! )

    @ExceptionHandler(DocumentNotFoundException :: class)
    fun handleDocumentNotFound(e: DocumentNotFoundException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.NOT_FOUND, e.message !! )

    @ExceptionHandler(DocumentAlreadyExistsException :: class)
    fun handleDocumentNotFound(e: DocumentAlreadyExistsException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.FOUND, e.message !! )
}