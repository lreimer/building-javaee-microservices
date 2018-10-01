package cloud.nativ.javaee.weather;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

/**
 * An asynchronous REST API to query the weather.
 */
@ApplicationScoped
@Path("weather")
public class WeatherResource {

    @Inject
    private OpenWeatherMapRepository repository;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @APIResponse(responseCode = "200", description = "The current weather for the city.",
            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    @Operation(summary = "Get the current weather for a city.",
            description = "Retrieves the current weather via the OpenWeatherMap API.")
    @Path("/{city}")
    public void getWeather(@Suspended final AsyncResponse asyncResponse,
                           @Parameter(name = "city", required = true, example = "Rosenheim,de",
                                   schema = @Schema(type = SchemaType.STRING))
                           @PathParam("city") String city) {

        asyncResponse.setTimeout(5, TimeUnit.SECONDS);
        asyncResponse.setTimeoutHandler(r -> r.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE).build()));
        asyncResponse.resume(Response.ok(repository.getWeather(city)).build());
    }
}
