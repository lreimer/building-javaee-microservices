package cloud.nativ.javaee.advanced;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Map;

@RequestScoped
@Path("version")
public class VersionResource {

    /**
     * MediaType implementation for the version resource in v1.
     */
    public static final MediaType V1_MEDIA_TYPE = new MediaType("application", "vnd.version.v1+json");

    /**
     * MediaType implementation for the version resource in v2.
     */
    public static final MediaType V2_MEDIA_TYPE = new MediaType("application", "vnd.version.v2+json");

    @GET
    @Produces({"application/json; qs=0.75", "application/vnd.version.v1+json; qs=1.0"})
    @Timed(name = "v1", absolute = true, unit = MetricUnits.MILLISECONDS)
    public Response v1() {
        Map<String, String> version = Collections.singletonMap("version", "v1");
        return Response.ok(version).build();
    }

    @GET
    @Produces("application/vnd.version.v2+json")
    @Timed(name = "v2", absolute = true, unit = MetricUnits.MILLISECONDS)
    public Response v2() {
        Map<String, String> version = Collections.singletonMap("version", "v2");
        return Response.ok(version).build();
    }

}
