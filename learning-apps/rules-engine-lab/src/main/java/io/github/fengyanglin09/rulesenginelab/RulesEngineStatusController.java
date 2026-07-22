package io.github.fengyanglin09.rulesenginelab;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/rules-engine")
public class RulesEngineStatusController {

    @GetMapping("/status")
    public Map<String, String> status() {
        return Map.of(
                "application", "rules-engine-lab",
                "status", "ready"
        );
    }
}
