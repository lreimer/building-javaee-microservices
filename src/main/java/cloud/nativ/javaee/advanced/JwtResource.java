package cloud.nativ.javaee.advanced;

import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.security.Principal;
import java.util.Objects;

@ApplicationScoped
@Path("jwt")
@Produces(MediaType.APPLICATION_JSON)
public class JwtResource {

    @Inject
    private Principal principal;

    @Inject
    private JsonWebToken jsonWebToken;

    @GET
    @Path("/secure")
    @RolesAllowed("architect")
    public JsonObject secure() {
        return Json.createObjectBuilder()
                .add("principalName", principal.getName())
                .add("issuer", jsonWebToken.getIssuer())
                .add("groups", Json.createArrayBuilder(jsonWebToken.getGroups()))
                .build();
    }

    @GET
    public JsonObject get() {
        return Json.createObjectBuilder()
                .add("principalName", principal.getName())
                .add("issuer", Objects.toString(jsonWebToken.getIssuer(), "unknown"))
                .build();
    }
}
