package cloud.nativ.javaee.advanced;

import lombok.extern.java.Log;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.json.*;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The JSON-P showcase REST resource implementation class.
 */
@Log
@ApplicationScoped
@Path("json-p")
@Produces(MediaType.APPLICATION_JSON)
public class JsonpResource {

    private JsonArray jsonArray;

    @PostConstruct
    @HEAD
    public void initialize() {
        this.jsonArray = Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                        .add("aString", "Hello Json-P 1")
                        .add("aInteger", 42)
                        .add("aBoolean", false)
                        .add("aNullValue", JsonValue.NULL))
                .add(Json.createObjectBuilder()
                        .add("aString", "Hello Json-P 2")
                        .add("aInteger", 23)
                        .add("aBoolean", true))
                .build();
    }

    @GET
    public JsonArray marshall() {
        return jsonArray;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void unmarshall(InputStream jsonBody) {
        JsonReader reader = Json.createReader(jsonBody);
        this.jsonArray = reader.readArray();

        LOGGER.log(Level.INFO, "Unmarshalled JSON-P {0}.", jsonArray);
    }

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON_PATCH_JSON)
    public void patch(JsonArray jsonPatchArray) {
        LOGGER.log(Level.INFO, "Unmarshalled JSON-P Patch {0}.", jsonPatchArray);

        JsonPatch jsonPatch = Json.createPatchBuilder(jsonPatchArray).build();
        this.jsonArray = jsonPatch.apply(jsonArray);
        LOGGER.log(Level.INFO, "Patched {0}.", jsonArray);
    }
}
