package com.marketplace.MarketplaceItems.componentadvice;

import com.marketplace.MarketplaceItems.exception.BadRequestException;
import com.marketplace.MarketplaceItems.exception.InternalServerErrorException;
import com.marketplace.MarketplaceItems.exception.RecordNotFoundException;
import com.marketplace.MarketplaceItems.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<Object> handleBadRequestException(Exception ex) {
        String message = ex.getMessage();
        ErrorResponse errorResponse = new ErrorResponse(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler({RecordNotFoundException.class})
    public ResponseEntity<Object> handleRecordNotFoundException(Exception ex) {
        String message = ex.getMessage();
        ErrorResponse errorResponse = new ErrorResponse(message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler({InternalServerErrorException.class})
    public ResponseEntity<Object> handleInternalServerErrorException(Exception ex) {
        String message = ex.getMessage();
        ErrorResponse errorResponse = new ErrorResponse(message);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
