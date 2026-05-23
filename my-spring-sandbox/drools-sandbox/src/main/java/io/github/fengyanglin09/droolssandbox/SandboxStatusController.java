package io.github.fengyanglin09.droolssandbox;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/sandbox")
public class SandboxStatusController {

    @GetMapping("/status")
    public Map<String, String> status() {
        return Map.of(
                "application", "drools-sandbox",
                "status", "ready"
        );
    }
}
