package cloud.nativ.javaee.weather;

import lombok.extern.java.Log;
import org.eclipse.microprofile.metrics.annotation.Gauge;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

/**
 * The weather station broadcasts weather events via SSE to all registered listeners.
 */
@Log
@ApplicationScoped
@Path("weather-station")
public class WeatherStationResource {

    @Context
    private Sse sse;
    private SseBroadcaster sseBroadcaster;

    private AtomicLong registeredEventSinks = new AtomicLong(0);

    @PostConstruct
    public void initialize() {
        sseBroadcaster = sse.newBroadcaster();

        sseBroadcaster.onClose((eventSink) -> {
            long count = registeredEventSinks.decrementAndGet();
            LOGGER.log(Level.INFO, "Closing sink. Currently {0} events sinks listening.", count);
        });

        sseBroadcaster.onError((sseEventSink, throwable) -> {
            long count = registeredEventSinks.decrementAndGet();
            LOGGER.log(Level.WARNING, "Error on event sink. Currently {0} events sinks listening.", new Object[]{count, throwable});
        });
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void fetch(@Context SseEventSink sseEventSink) {
        LOGGER.info("Registering new SSE event sink with broadcaster.");
        sseBroadcaster.register(sseEventSink);

        long count = registeredEventSinks.incrementAndGet();
        LOGGER.log(Level.INFO, "Currently {0} events sinks listening.", count);
    }

    @Gauge(unit = "none")
    public long registeredEventSinks() {
        return registeredEventSinks.get();
    }

    public void broadcast(@ObservesAsync CurrentWeather currentWeather) {
        OutboundSseEvent broadcastEvent = sse.newEventBuilder()
                .name("event")
                .data(currentWeather.toJson())
                .mediaType(MediaType.APPLICATION_JSON_TYPE)
                .build();

        LOGGER.log(Level.INFO, "Broadcasting current weather event {0}.", broadcastEvent);
        sseBroadcaster.broadcast(broadcastEvent);
    }
}
