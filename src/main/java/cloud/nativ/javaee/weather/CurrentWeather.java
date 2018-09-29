package cloud.nativ.javaee.weather;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "current_weather")
public class CurrentWeather {
    @Id
    @Column(name = "city", unique = true, nullable = false)
    private String city;

    @Column(name = "weather", nullable = false)
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
