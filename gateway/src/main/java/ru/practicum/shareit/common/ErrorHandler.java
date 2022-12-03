package ru.practicum.shareit.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handlerIllegalArgument(IllegalArgumentException exception) {
        log.debug(exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public void handleBadRequest(HttpServletResponse response, Exception exception) throws IOException {
        log.debug(exception.getMessage());
        response.sendError(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(RuntimeException.class)
    public void handleException(HttpServletResponse response, Exception exception) throws IOException {
        log.debug(exception.getMessage());
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
