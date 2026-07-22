package io.github.fengyanglin09.rulesenginelab.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "drools.debug")
public class DroolsDebugProperties {
    private boolean traceEnabled;
}
