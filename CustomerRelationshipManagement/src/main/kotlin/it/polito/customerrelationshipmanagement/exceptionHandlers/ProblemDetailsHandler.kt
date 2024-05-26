package it.polito.customerrelationshipmanagement.exceptionHandlers

import it.polito.customerrelationshipmanagement.exceptions.*
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ProblemDetailsHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(ContactAlreadyExistsException::class)
    fun handleContactAlreadyExists(e: ContactAlreadyExistsException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.FOUND, e.message!!)

    @ExceptionHandler(EmailNotFoundException::class)
    fun handleContactEmailNotFound(e: EmailNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(AddressNotFoundException::class)
    fun handleContactAddressNotFound(e: AddressNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(TelephoneNotFoundException::class)
    fun handleContactTelephoneNotFound(e: TelephoneNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(ContactException::class)
    fun handleContactInvalidArgs(e: ContactException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(ContactNotFoundException::class)
    fun handleContactNotFound(e: ContactNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(IllegalIdException::class)
    fun handleWrongParams(e: IllegalIdException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(IllegalPageNumberLimitException::class)
    fun handleWrongParamsGetAll(e: IllegalPageNumberLimitException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(SenderNotProvidedException::class)
    fun handleWrongParamsAddMessage(e: SenderNotProvidedException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(MessageNotFoundException::class)
    fun handleMessageNotFound(e: MessageNotFoundException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.NOT_FOUND, e.message !! )

    @ExceptionHandler(IllegalStateTransitionException :: class)
    fun handleWrongParamsChangeState(e: IllegalStateTransitionException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.BAD_REQUEST, e.message !! )

    @ExceptionHandler(WrongChannelException :: class)
    fun handleWrongChannelAddMessage(e: WrongChannelException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.BAD_REQUEST, e.message !! )

    @ExceptionHandler(MultipleSendersException :: class)
    fun handleMultipleSendersAddMessage(e: MultipleSendersException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.BAD_REQUEST, e.message !! )

    @ExceptionHandler(IllegalSortParameterException :: class)
    fun handleIllegalSortParameter(e: IllegalSortParameterException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.BAD_REQUEST, e.message !! )

    @ExceptionHandler(AddressAlreadyDeletedException :: class)
    fun handleIllegalAddressAlreadyDeletedParameter(e: AddressAlreadyDeletedException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.BAD_REQUEST, e.message !! )

    @ExceptionHandler(AddressAlreadyPresentException :: class)
    fun handleIllegalAddressAlreadyPresentParameter(e: AddressAlreadyPresentException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.FOUND, e.message !! )

    @ExceptionHandler(EmailAlreadyDeletedException :: class)
    fun handleIllegalEmailAlreadyDeletedParameter(e: EmailAlreadyDeletedException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.BAD_REQUEST, e.message !! )

    @ExceptionHandler(EmailAlreadyPresentException :: class)
    fun handleIllegalEmailAlreadyPresentParameter(e: EmailAlreadyPresentException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.FOUND, e.message !! )

    @ExceptionHandler(TelephoneAlreadyDeletedException :: class)
    fun handleIllegalTelephoneAlreadyDeletedParameter(e: TelephoneAlreadyDeletedException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.BAD_REQUEST, e.message !! )

    @ExceptionHandler(TelephoneAlreadyPresentException :: class)
    fun handleIllegalTelephoneAlreadyPresentParameter(e: TelephoneAlreadyPresentException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.FOUND, e.message !! )

    @ExceptionHandler(NoDeletePermissionException :: class)
    fun handleIllegalDeletePermission(e: NoDeletePermissionException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.FORBIDDEN, e.message !! )

    //Jobffer - Customer - Professional

    @ExceptionHandler(CustomerNotFoundException::class)
    fun handleCustomerNotFound(e: CustomerNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)
    @ExceptionHandler(NoteAlreadyDeletedException :: class)
    fun handleIllegalNoteAlreadyDeletedParameter(e: NoteAlreadyDeletedException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.BAD_REQUEST, e.message !! )
    @ExceptionHandler(SkillAlreadyDeletedException :: class)
    fun handleIllegalSkillAlreadyDeletedParameter(e: SkillAlreadyDeletedException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.BAD_REQUEST, e.message !! )

    @ExceptionHandler(NoteNotFoundException::class)
    fun handleContactNoteNotFound(e: NoteNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)
    @ExceptionHandler(SkillNotFoundException::class)
    fun handleSkillNotFound(e: SkillNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(CustomerException::class)
    fun handleCustomerInvalidParameters(e: CustomerException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(ProfessionalException::class)
    fun handleProfessionalInvalidParameters(e: ProfessionalException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(IllegalGeographicalLocationException::class)
    fun handleProfessionalInvalidGeographicalLocation(e: IllegalGeographicalLocationException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(ProfessionalNotFoundException::class)
    fun handleProfessionalNotFound(e: ProfessionalNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(JobOfferNotFoundException::class)
    fun handleJobOfferNotFound(e: JobOfferNotFoundException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message!!)

    @ExceptionHandler(ProfessionalStateException::class)
    fun handleProfessionalState(e: ProfessionalStateException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(JobOfferStatusException::class)
    fun handleJobOfferStatus(e: JobOfferStatusException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message!!)

    @ExceptionHandler(IllegalCategoryTransitionException :: class)
    fun handleWrongParamsChangeCategory(e: IllegalCategoryTransitionException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.BAD_REQUEST, e.message !! )

    @ExceptionHandler(NoJobOfferPermissionException :: class)
    fun handleIllegalJobOfferPermission(e: NoJobOfferPermissionException) =
        ProblemDetail.forStatusAndDetail( HttpStatus.FORBIDDEN, e.message !! )
}