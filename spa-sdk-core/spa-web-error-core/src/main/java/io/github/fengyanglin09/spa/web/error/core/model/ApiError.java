package io.github.fengyanglin09.spa.web.error.core.model;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String traceId,
        List<ApiFieldError> fieldErrors
) {
    public record ApiFieldError(
            String field,
            String message
    ) {}
}
