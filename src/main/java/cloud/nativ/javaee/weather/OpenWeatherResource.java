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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@ApplicationScoped
@Path("weather")
public class OpenWeatherResource {

    @Inject
    private OpenWeatherMapRepository repository;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @APIResponse(responseCode = "200", description = "The current weather for the city.",
            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    @Operation(summary = "Get the current weather for a city.",
            description = "Retrieves the current weather via the OpenWeatherMap API.")
    @Path("/{city}")
    public Response getWeather(
            @Parameter(name = "city", required = true, example = "Rosenheim,de", schema = @Schema(type = SchemaType.STRING))
            @PathParam("city") String city) {
        // retrieve the weather from the repository
        String weather = repository.getWeather(city);
        return Response.ok(weather).build();
    }
}
