package cloud.nativ.javaee.integration;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * The central microservice configuration.
 */
@ApplicationScoped
public class CentralConfiguration {

    @Inject
    @ConfigProperty(name = "hostname", defaultValue = "localhost")
    private String hostname;

    public String getHostname() {
        return hostname;
    }

}
