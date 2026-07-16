package com.troubleticket.trouble_ticket_api.api.exception;

import com.troubleticket.generated.model.Error;
import com.troubleticket.trouble_ticket_api.domain.exception.InvalidStatusTransitionException;
import com.troubleticket.trouble_ticket_api.domain.exception.ServiceNotFoundException;
import com.troubleticket.trouble_ticket_api.domain.exception.TroubleTicketNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TroubleTicketNotFoundException.class)
    public ResponseEntity<Error> handleNotFound(TroubleTicketNotFoundException ex, HttpServletRequest request) {
        Error error = new Error();
        error.setCode("TROUBLE_TICKET_NOT_FOUND");
        error.setMessage(ex.getMessage());
        error.setRequestId(resolveRequestId(request));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ServiceNotFoundException.class)
    public ResponseEntity<Error> handleServiceNotFound(ServiceNotFoundException ex, HttpServletRequest request) {
        Error error = new Error();
        error.setCode("SERVICE_NOT_FOUND");
        error.setMessage(ex.getMessage());
        error.setRequestId(resolveRequestId(request));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InvalidStatusTransitionException.class)
    public ResponseEntity<Error> handleInvalidTransition(InvalidStatusTransitionException ex, HttpServletRequest request) {
        Error error = new Error();
        error.setCode("INVALID_STATUS_TRANSITION");
        error.setMessage(ex.getMessage());
        error.setRequestId(resolveRequestId(request));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String detailedMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> String.format("Field '%s' %s (rejected value: [%s])",
                        err.getField(), err.getDefaultMessage(), err.getRejectedValue()))
                .collect(Collectors.joining(", "));

        Error error = new Error();
        error.setCode("BAD_REQUEST_PAYLOAD");
        error.setMessage("Validation failed: " + detailedMessage);
        error.setRequestId(resolveRequestId(request));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGeneralException(Exception ex, HttpServletRequest request) {
        Error error = new Error();
        error.setCode("INTERNAL_SERVER_ERROR");
        error.setMessage("An unexpected error occurred: " + ex.getMessage());
        error.setRequestId(resolveRequestId(request));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    private String resolveRequestId(HttpServletRequest request) {
        String requestId = request.getHeader("X-Request-ID");
        return (requestId != null && !requestId.isBlank()) ? requestId : UUID.randomUUID().toString();
    }
}
