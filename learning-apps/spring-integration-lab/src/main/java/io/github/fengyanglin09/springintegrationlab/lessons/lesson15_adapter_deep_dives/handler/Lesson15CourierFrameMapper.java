package io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model.Lesson15CourierInboundFrame;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model.Lesson15PartnerExport;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model.Lesson15ShipmentCommand;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Maps external courier frames to internal payloads and outbound payloads.
 */
// @Component tells Spring:
// "Create one Lesson15CourierFrameMapper object during startup."
@Component
public class Lesson15CourierFrameMapper {

    public Lesson15ShipmentCommand toShipmentCommand(Lesson15CourierInboundFrame frame) {
        // The fake courier body format is:
        //
        // shipmentId|destination|serviceCode
        //
        // This string parsing is intentionally tiny. The lesson is not about
        // parsing; it is about where protocol-specific mapping belongs.
        String[] parts = frame.body().split("\\|", -1);
        String shipmentId = parts[0].trim();
        String destination = parts[1].trim();
        String serviceLevel = normalizeServiceLevel(parts[2]);

        return new Lesson15ShipmentCommand(
                shipmentId,
                destination,
                serviceLevel,
                List.of(
                        "inbound-adapter:external-frame-to-message",
                        "transform:frame-body-to-internal-command"
                )
        );
    }

    public Lesson15PartnerExport toPartnerExport(Lesson15ShipmentCommand command) {
        List<String> trail = new ArrayList<>(command.adapterTrail());
        trail.add("transform:internal-command-to-outbound-export");

        return new Lesson15PartnerExport(
                command.shipmentId(),
                command.destination(),
                command.serviceLevel(),
                "ACK|" + command.shipmentId() + "|" + command.serviceLevel(),
                List.copyOf(trail)
        );
    }

    private String normalizeServiceLevel(String serviceCode) {
        return switch (serviceCode.trim().toUpperCase(Locale.ROOT)) {
            case "EXP", "EXPRESS" -> "EXPEDITED";
            default -> "STANDARD";
        };
    }
}
