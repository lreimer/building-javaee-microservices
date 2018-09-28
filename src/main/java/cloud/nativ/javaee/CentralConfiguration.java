package cloud.nativ.javaee;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Provider;

/**
 * The central microservice configuration.
 */
@ApplicationScoped
public class CentralConfiguration {

    @Inject
    @ConfigProperty(name = "default.hostname", defaultValue = "localhost")
    private String defaultHostname;

    @Inject
    @ConfigProperty(name = "weather.appid", defaultValue = "5b3f51e527ba4ee2ba87940ce9705cb5")
    private Provider<String> weatherAppId;

    public String getDefaultHostname() {
        return defaultHostname;
    }

    public String getWeatherAppId() {
        return weatherAppId.get();
    }
}
