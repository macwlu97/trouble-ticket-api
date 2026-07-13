package com.troubleticket.trouble_ticket_api.api.exception;

import com.troubleticket.generated.model.Error;
import com.troubleticket.trouble_ticket_api.domain.exception.TroubleTicketNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(TroubleTicketNotFoundException.class)
    public ResponseEntity<Error> handleNotFound(
            TroubleTicketNotFoundException ex
    ) {

        Error error = new Error();

        error.setCode("TROUBLE_TICKET_NOT_FOUND");
        error.setMessage(ex.getMessage());
        error.setRequestId(UUID.randomUUID().toString());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }
}