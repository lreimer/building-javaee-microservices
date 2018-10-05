package cloud.nativ.javaee.advanced;

import lombok.extern.java.Log;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.*;
import javax.json.bind.config.PropertyNamingStrategy;
import javax.json.bind.config.PropertyOrderStrategy;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.logging.Level;

@Log
@ApplicationScoped
@Path("json-b")
@Produces(MediaType.APPLICATION_JSON)
public class JsonbResource {

    private Jsonb jsonb;

    private JsonbPojo defaultJsonbPojo;
    private JsonbPojo customJsonbPojo;

    @PostConstruct
    @HEAD
    public void initialize() {
        defaultJsonbPojo = new JsonbPojo("Hello World. (Default)", 42, LocalDate.now());
        customJsonbPojo = new JsonbPojo("Hello World. (Custom)", 42, LocalDate.now());

        JsonbConfig jsonbConfig = new JsonbConfig()
                .withDateFormat("dd.MM.yyyy", Locale.GERMANY)
                .withFormatting(true)
                .withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_DASHES)
                .withNullValues(true)
                .withPropertyOrderStrategy(PropertyOrderStrategy.REVERSE);

        jsonb = JsonbBuilder.newBuilder()
                .withConfig(jsonbConfig).build();
    }

    @GET
    @Path("/default")
    public JsonbPojo marshall() {
        return defaultJsonbPojo;
    }

    @POST
    @Path("/default")
    public void unmarshall(JsonbPojo pojo) {
        LOGGER.log(Level.INFO, "Default Unmarshalled {0}", pojo);
        this.defaultJsonbPojo = pojo;
    }

    @GET
    @Path("/custom")
    public String marshallCustom() {
        return jsonb.toJson(customJsonbPojo);
    }

    @POST
    @Path("/custom")
    public void unmarshallCustom(String jsonBody) {
        customJsonbPojo = jsonb.fromJson(jsonBody, JsonbPojo.class);
        LOGGER.log(Level.INFO, "Custom Unmarshalled {0}", customJsonbPojo);
    }

    @JsonbNillable
    public static class JsonbPojo {

        @JsonbProperty(value = "greeting", nillable = true)
        private final String message;

        @JsonbNumberFormat("#,##0.00")
        private final Integer answerToEverything;

        // @JsonbDateFormat("yyyy-MM-dd")
        private final LocalDate today;

        // @JsonbTransient
        // results in a NullPointerException
        // annotate getter instead
        private BigDecimal money = BigDecimal.TEN;

        @JsonbCreator
        public JsonbPojo(@JsonbProperty(value = "greeting", nillable = true) String message,
                         @JsonbProperty(value = "answerToEverything", nillable = true) Integer answerToEverything,
                         @JsonbProperty(value = "today", nillable = true) LocalDate today) {
            this.message = message;
            this.answerToEverything = answerToEverything;
            this.today = today;
        }

        public String getMessage() {
            return message;
        }

        public Integer getAnswerToEverything() {
            return answerToEverything;
        }

        public LocalDate getToday() {
            return today;
        }

        @JsonbTransient
        public BigDecimal getMoney() {
            return money;
        }

        @Override
        public String toString() {
            return "JsonbPojo{" +
                    "message='" + message + '\'' +
                    ", answerToEverything=" + answerToEverything +
                    ", today=" + today +
                    ", money=" + money +
                    '}';
        }
    }
}
