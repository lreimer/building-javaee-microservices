package cloud.nativ.javaee.hello;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Provider;

/**
 * The hello endpoint configuration.
 */
@ApplicationScoped
public class HelloConfiguration {

    @Inject
    @ConfigProperty(name = "hostname", defaultValue = "localhost")
    private String hostname;

    @Inject
    @ConfigProperty(name = "message", defaultValue = "Building Microservices with Java EE 8 and MicroProfile.")
    private Provider<String> message;

    public String getHostname() {
        return hostname;
    }

    public String getMessage() {
        return message.get();
    }

}
