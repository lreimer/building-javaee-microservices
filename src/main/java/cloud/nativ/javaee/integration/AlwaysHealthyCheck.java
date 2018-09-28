package cloud.nativ.javaee.integration;

import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

import javax.enterprise.context.ApplicationScoped;

/**
 * Simple health check that always returns UP and healthy.
 */
@ApplicationScoped
@Health
public class AlwaysHealthyCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.builder()
                .name("Java EE 8")
                .withData("message", "Always healthy!")
                .up()
                .build();
    }
}
