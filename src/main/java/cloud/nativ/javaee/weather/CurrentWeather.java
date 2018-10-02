package cloud.nativ.javaee.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.json.Json;
import javax.json.JsonObject;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentWeather {
    private String city;

    private String weather;

    /**
     * Use JSON-P to build a JSON structure for this instance.
     *
     * @return the {@link JsonObject}
     */
    public JsonObject toJson() {
        return Json.createObjectBuilder().add("city", city).add("weather", weather).build();
    }
}
