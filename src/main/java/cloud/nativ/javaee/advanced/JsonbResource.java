package cloud.nativ.javaee.advanced;

import lombok.Data;
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

/**
 * The JSON-B showcase REST resource implementation class.
 */
@Log
@ApplicationScoped
@Path("json-b")
@Produces(MediaType.APPLICATION_JSON)
public class JsonbResource {

    private Jsonb jsonb;

    private JsonbPojo jsonbPojo;
    private CustomJsonbPojo customJsonbPojo;

    @PostConstruct
    @HEAD
    public void initialize() {
        jsonbPojo = new JsonbPojo("Hello World.", 42, LocalDate.now());

        customJsonbPojo = new CustomJsonbPojo();
        customJsonbPojo.aString = "Hello Json-B.";
        customJsonbPojo.aInteger = 42;
        customJsonbPojo.aBigDecimal = BigDecimal.ONE;

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
    public JsonbPojo marshall() {
        return jsonbPojo;
    }

    @POST
    public void unmarshall(JsonbPojo pojo) {
        LOGGER.log(Level.INFO, "Default Unmarshalled {0}", pojo);
        this.jsonbPojo = pojo;
    }

    @GET
    @Path("/custom")
    public String marshallCustom() {
        return jsonb.toJson(customJsonbPojo);
    }

    @POST
    @Path("/custom")
    public void unmarshallCustom(String jsonBody) {
        customJsonbPojo = jsonb.fromJson(jsonBody, CustomJsonbPojo.class);
        LOGGER.log(Level.INFO, "Custom Unmarshalled {0}", customJsonbPojo);
    }

    @JsonbNillable
    @JsonbPropertyOrder(value = {"message", "answerToEverything", "today"})
    public static class JsonbPojo {

        @JsonbProperty(value = "greeting", nillable = true)
        private final String message;

        @JsonbNumberFormat("#,##0.00")
        private final Integer answerToEverything;

        @JsonbDateFormat("dd.MM.yyyy")
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

    @Data
    public static class CustomJsonbPojo {

        private String aString;

        private Integer aInteger;

        private BigDecimal aBigDecimal;

        private LocalDate localDate = LocalDate.now();
        private String nullableString = null;

        @Override
        public String toString() {
            return "CustomJsonbPojo{" +
                    "aString='" + aString + '\'' +
                    ", aInteger=" + aInteger +
                    ", aBigDecimal=" + aBigDecimal +
                    ", localDate=" + localDate +
                    ", nullableString='" + nullableString + '\'' +
                    '}';
        }
    }
}
