package io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.handler;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.model.Lesson14ShipmentEvent;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.model.Lesson14ShipmentResult;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson14_observability_operations.model.Lesson14ShipmentUpdate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Normal business behavior used by the lesson 14 shipment flow.
 */
// @Component tells Spring:
// "Create one Lesson14ShipmentHandler object during startup."
@Component
public class Lesson14ShipmentHandler {

    public Lesson14ShipmentUpdate prepare(Lesson14ShipmentEvent event) {
        // This method is ordinary application behavior.
        //
        // Observability should help us see this behavior happening, but it
        // should not be mixed into the business decision itself.
        String lane = event.priority() ? "EXPEDITED" : "STANDARD";

        return new Lesson14ShipmentUpdate(
                event.shipmentId(),
                event.destination(),
                lane,
                List.of(
                        "handler:prepared-shipment",
                        "handler:selected-" + lane.toLowerCase()
                )
        );
    }

    public Lesson14ShipmentResult dispatch(Lesson14ShipmentUpdate update) {
        List<String> trail = new ArrayList<>(update.lessonTrail());
        trail.add("handler:dispatched-shipment");

        return new Lesson14ShipmentResult(
                update.shipmentId(),
                "DISPATCHED",
                update.lane(),
                List.copyOf(trail)
        );
    }
}
