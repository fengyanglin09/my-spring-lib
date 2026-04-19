package io.github.fengyanglin09.spa.web.error.core.handler;

import io.github.fengyanglin09.spa.web.error.core.model.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

public abstract class BaseGlobalExceptionHandler {

    protected ResponseEntity<ApiError> build(
            HttpStatus status,
            String message,
            HttpServletRequest req,
            String traceId,
            List<ApiError.ApiFieldError> fieldErrors
    ) {

        ApiError body = ApiError.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(req.getRequestURI())
                .traceId(traceId)
                .fieldErrors(fieldErrors)
                .build();

        return ResponseEntity.status(status).body(body);

    }

    protected ResponseEntity<ApiError> build(
            HttpStatus status,
            String message,
            HttpServletRequest req
    ){
        return build(status, message, req, null, null);
    }

}
