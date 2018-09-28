package cloud.nativ.javaee.weather;

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
    @Path("/{city}")
    public Response getWeather(@PathParam("city") String city) {
        String weather = repository.getWeather(city);
        return Response.ok(weather).build();
    }
}
