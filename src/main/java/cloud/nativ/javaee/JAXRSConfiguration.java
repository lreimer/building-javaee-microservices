package cloud.nativ.javaee;

import org.eclipse.microprofile.auth.LoginConfig;

import javax.annotation.security.DeclareRoles;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * Configures a JAX-RS endpoint.
 */
@LoginConfig(authMethod = "MP-JWT", realmName = "MP-JWT")
@ApplicationScoped
@DeclareRoles({"architect", "developer", "jcon2018"})
@ApplicationPath("api")
public class JAXRSConfiguration extends Application {
}
