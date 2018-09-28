package cloud.nativ.javaee.hello;

import cloud.nativ.javaee.integration.CentralConfiguration;
import org.eclipse.microprofile.metrics.Counter;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Metric;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static java.lang.System.getenv;
import static java.util.Optional.ofNullable;

/**
 * The REST resource implementation class.
 */
@ApplicationScoped
@Path("hello")
public class HelloWorldResource {

    @Inject
    @Metric(name = "helloWorldCounter", absolute = true)
    private Counter counter;

    @Inject
    private CentralConfiguration configuration;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponse(responseCode = "200", description = "The hello world response.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JsonObject.class)))
    @Operation(summary = "Do hello world.", description = "Retrieve JSON response with message and hostname.")
    @Timed(name = "helloWorld", absolute = true, unit = MetricUnits.MILLISECONDS)
    public JsonObject helloWorld() {
        counter.inc();
        String hostname = ofNullable(getenv("HOSTNAME")).orElse(configuration.getDefaultHostname());
        return Json.createObjectBuilder()
                .add("message", "Cloud Native Application Development with Java EE.")
                .add("hostname", hostname)
                .build();
    }
}
