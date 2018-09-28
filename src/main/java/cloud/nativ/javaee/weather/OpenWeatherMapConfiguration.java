package cloud.nativ.javaee.weather;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Provider;

/**
 * The OpenWeatherMap API configuration.
 */
@ApplicationScoped
public class OpenWeatherMapConfiguration {

    @Inject
    @ConfigProperty(name = "weather.appid", defaultValue = "5b3f51e527ba4ee2ba87940ce9705cb5")
    private Provider<String> weatherAppId;

    @Inject
    @ConfigProperty(name = "weather.uri", defaultValue = "https://api.openweathermap.org")
    private Provider<String> weatherUri;

    public String getWeatherAppId() {
        return weatherAppId.get();
    }

    public String getWeatherUri() {
        return weatherUri.get();
    }
}
