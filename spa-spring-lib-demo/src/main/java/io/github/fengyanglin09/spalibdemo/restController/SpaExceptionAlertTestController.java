package io.github.fengyanglin09.spalibdemo.restController;

import io.github.fengyanglin09.spa.alert.autoconfigure.model.ExceptionAlertContext;
import io.github.fengyanglin09.spa.alert.autoconfigure.service.ExceptionAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;



@RestController
@RequiredArgsConstructor
public class SpaExceptionAlertTestController {

    private final ExceptionAlertService exceptionAlertService;

    @GetMapping("/test-alert")
    public String testAlert() {
        try {
            throw new RuntimeException("Demo exception from starter test");
        } catch (Exception ex) {

            Map<String, String> details = Map.of("endpoint", "/test-alert", "purpose", "starter verification");

            exceptionAlertService.sendAlert(
                    ExceptionAlertContext.builder()
                            .title("Demo Exception Alert")
                            .summary("This is a starter end-to-end test.")
                            .component("Demo Controller")
                            .severity("HIGH")
                            .exception(ex)
                            .traceId("demo-trace-123")
                            .host("local-machine")
                            .details(details).build()
//                            .detail("purpose", "starter verification")
            );
        }

        return "alert sent";
    }

}
