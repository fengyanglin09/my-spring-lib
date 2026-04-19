package io.github.fengyanglin09.spaexceptionalertdemo.exception;

import io.github.fengyanglin09.spa.alert.autoconfigure.service.ExceptionAlertService;
import io.github.fengyanglin09.spa.web.error.core.handler.BaseGlobalExceptionHandler;
import io.github.fengyanglin09.spa.web.error.core.model.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@RequiredArgsConstructor
public class DefaultGlobalExceptionHandler extends BaseGlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {


        String message = "Unexpected server error";

        return build(HttpStatus.INTERNAL_SERVER_ERROR, message, req);
    }

}
