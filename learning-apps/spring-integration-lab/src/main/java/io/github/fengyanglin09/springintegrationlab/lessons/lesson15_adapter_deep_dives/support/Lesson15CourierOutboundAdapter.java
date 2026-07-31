package io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.support;

import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model.Lesson15CourierOutboundFrame;
import io.github.fengyanglin09.springintegrationlab.lessons.lesson15_adapter_deep_dives.model.Lesson15PartnerExport;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Outbound adapter target for the fake courier protocol.
 */
// @Component tells Spring:
// "Create one Lesson15CourierOutboundAdapter object during startup."
@Component
public class Lesson15CourierOutboundAdapter {

    private final Lesson15CourierProtocolSandbox courierProtocolSandbox;

    public Lesson15CourierOutboundAdapter(Lesson15CourierProtocolSandbox courierProtocolSandbox) {
        this.courierProtocolSandbox = courierProtocolSandbox;
    }

    public void send(Lesson15PartnerExport export, MessageHeaders headers) {
        // This is the outbound adapter boundary.
        //
        // The flow has an internal payload: Lesson15PartnerExport.
        //
        // The outbound adapter turns that internal payload back into the
        // external protocol envelope: Lesson15CourierOutboundFrame.
        //
        // Notice that it also reads headers created by the inbound adapter.
        // That is a common adapter detail: metadata from the external inbound
        // side may need to travel as headers until the outbound side needs it.
        String originalFrameId = (String) headers.get(Lesson15HeaderNames.FRAME_ID);
        String remoteSystem = (String) headers.get(Lesson15HeaderNames.REMOTE_SYSTEM);
        String contentType = (String) headers.get(Lesson15HeaderNames.CONTENT_TYPE);

        List<String> trail = new ArrayList<>(export.adapterTrail());
        trail.add("outbound-adapter:message-to-external-frame");

        courierProtocolSandbox.sendOutboundFrame(new Lesson15CourierOutboundFrame(
                "ack-" + originalFrameId,
                originalFrameId,
                remoteSystem,
                contentType,
                export.acknowledgementBody(),
                List.copyOf(trail)
        ));
    }
}
