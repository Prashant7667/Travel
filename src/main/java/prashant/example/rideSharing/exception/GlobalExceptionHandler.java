package prashant.example.rideSharing.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import prashant.example.rideSharing.dto.ApiErrorResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleNotFound(ResourceNotFoundException ex) {
        return new ApiErrorResponse("NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleBusiness(BusinessRuleViolationException ex) {
        return new ApiErrorResponse("BUSINESS_RULE_VIOLATION", ex.getMessage());
    }

    @ExceptionHandler(ExternalServiceException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiErrorResponse handleExternal(ExternalServiceException ex) {
        return new ApiErrorResponse("EXTERNAL_SERVICE_ERROR", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleFallback(Exception ex) {
        log.error("Unhandled exception", ex);
        return new ApiErrorResponse("INTERNAL_ERROR", "Something went wrong");
    }
}
