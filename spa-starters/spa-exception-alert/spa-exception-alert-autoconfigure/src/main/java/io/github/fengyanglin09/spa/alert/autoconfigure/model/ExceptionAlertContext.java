package io.github.fengyanglin09.spa.alert.autoconfigure.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;


@Getter
@Setter
@Accessors(chain = true)
@Builder
public class ExceptionAlertContext {

    private String title;
    private String summary;
    private String component;
    private String severity;
    private Throwable exception;
    private String traceId;
    private String host;
    private Instant timestamp = Instant.now();
    private Map<String, String> details = new LinkedHashMap<>();

}
