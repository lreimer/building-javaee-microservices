# Building microservices with Java EE 8 and Microprofile APIs

This repository acts as demo, workshop and template project for my talks on
building cloud-native applications and microservices with Java EE 8 and
Microprofile APIs.

## Prerequisites

* Basic programming skills and Java knowledge
* Working JDK installation, at least Java 8 (e.g. https://adoptopenjdk.net)
* IDE with Java and Java EE 8 support (e.g. IntelliJ IDEA or Visual Studio Code)
* Git Client (e.g. SourceTree or any console Git client)
* Docker for Windows or Mac installed (with Kubernetes enabled)

To get you started quickly, issue the following commands on the CLI:

```
$ git clone https://github.com/lreimer/building-javaee-microservices.git
$ git checkout getting-started

$ docker pull qaware/zulu-centos-payara-micro:8u181-5.183
$ docker pull rmohr/activemq:5.15.6
$ docker pull postgres:9.6.3
```

## 1. Getting started with Java EE 8 microservices

### Build and dependency setup for Gradle

Create a `build.gradle` file, apply the WAR plugin and add the Java EE 8
dependency (see https://mvnrepository.com/artifact/javax/javaee-api/8.0).
This is what the final result should look like:
```groovy
plugins {
    id 'war'
}

repositories { jcenter() }

dependencies {
    providedCompile 'javax:javaee-api:8.0'
    // not part of Java EE API, optional
    providedCompile 'javax.cache:cache-api:1.0.0'
}
```

### Build and dependency setup for Maven

In case you prefer Maven as build tool, create a simple project with
packaging WAR. Go to https://mvnrepository.com/artifact/javax/javaee-api/8.0
and insert the dependency definition into Maven `pom.xml` file.
```xml
<dependency>
    <groupId>javax</groupId>
    <artifactId>javaee-api</artifactId>
    <version>8.0</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>javax.cache</groupId>
    <artifactId>cache-api</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
```

### Implement simple JAX-RS application and REST resource

First, create the JAX-RS application class and add the `@ApplicationPath` annotation.
```java
@ApplicationPath("api")
public class JAXRSConfiguration extends Application {
}
```

Next, create a resource class for your Hello REST endpoint.
```java
@Path("hello")
public class HelloWorldResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JsonObject helloWorld() {
        String hostname = ofNullable(getenv("HOSTNAME")).orElse("localhost");
        return Json.createObjectBuilder()
                .add("message", "Cloud Native Application Development with Java EE.")
                .add("hostname", hostname)
                .build();
    }
}
```

## 2. Containerizing Java EE 8 microservices

### Building and running containerized Java EE 8 microservices locally

Create a new file called `Dockerfile` and add the following content:
```
FROM qaware/zulu-centos-payara-micro:8u181-5.183

CMD ["--maxHttpThreads", "25", "--addjars", "/opt/payara/libs/", "--hzconfigfile", "/opt/payara/hazelcast.xml", "--postdeploycommandfile", "/opt/payara/post-deploy.asadmin", "--name", "javaee8-service"]

COPY build/postgresql/* /opt/payara/libs/
COPY build/activemq/activemq-rar-5.15.6.rar /opt/payara/deployments/activemq-rar.rar
COPY src/main/docker/* /opt/payara/
COPY build/libs/javaee8-service.war /opt/payara/deployments/
```

Then issue the following commands to build and run the image.
```
docker build -t javaee8-service:1.0.1 .
docker run -it -p 8080:8080 javaee8-service:1.0.1
```

### Using multi-stage Docker builds for Java EE 8 microservices

You can use Docker to build your service when building the images. This maybe useful in containerized CI environments.
Create a new file called `Builderfile` and add the following content:
```
FROM azul/zulu-openjdk:8u181 as builder

RUN mkdir /codebase
COPY . /codebase/

WORKDIR /codebase
RUN ./gradlew build

FROM qaware/zulu-centos-payara-micro:8u181-5.183

CMD ["--maxHttpThreads", "25", "--addjars", "/opt/payara/libs/", "--hzconfigfile", "/opt/payara/hazelcast.xml", "--postdeploycommandfile", "/opt/payara/post-deploy.asadmin", "--name", "javaee8-service"]

COPY --from=builder /codebase/build/postgresql/* /opt/payara/libs/
COPY --from=builder /codebase/build/activemq/activemq-rar-5.15.6.rar /opt/payara/deployments/activemq-rar.rar
COPY --from=builder /codebase/src/main/docker/* /opt/payara/

COPY --from=builder /codebase/build/libs/javaee8-service.war /opt/payara/deployments/
```

Then issue the following command to build and run the image.
```
docker build -t javaee8-service:1.0.1 -f Builderfile .
docker run -it -p 8080:8080 javaee8-service:1.0.1
```

## 3. Infrastructure Composition

### Writing a `docker-compose.yml` file for Java EE 8 microservice

Create a new file called `docker-compose.yml` and add the following content:
```yaml
version: "3"

services:
  javaee8-service:
    build:
      context: .
    image: javaee8-service:1.0.1
    ports:
    - "8080:8080"
    networks:
    - jee8net

networks:
  jee8net:
    driver: bridge
```

### Building and running with Docker Compose locally

You can use Docker Compose during the local development, using the following commands:
```
docker-compose build
docker-compose up --build

docker-compose up -d --build
docker ps
docker stats
docker-compose logs -f
```

### Additional infrastructure composition

Add the following YAML to your `docker-compose.yml` to add a message queue and a database:
```yaml
  message-queue:
    image: rmohr/activemq:5.15.6
    expose:
    - "61616"       # the JMS port
    - "1883"        # the MQTT port
    - "5672"        # the AMQP port
    ports:
    - "8161:8161"   # the admin web UI
    networks:
    - jee8net

  postgres-db:
    image: "postgres:9.6.3"
    environment:
    - POSTGRES_USER=javaee8
    - POSTGRES_PASSWORD=12qwasyx
    - POSTGRES_DB=weather
    ports:
    - "5432:5432"
    networks:
    - jee8net
```

## 4. Deploying and Running Java EE on Kubernetes

### Use Docker and Docker Compose to deploy to local Kubernetes

Add the following `deploy` section to each service in your `docker-compose.yml`:
```
deploy:
  replicas: 1
  resources:
    limits:
      memory: 640M
    reservations:
      memory: 640M
```

Then enter the following commands in your console to deploy and run everything:
```
docker stack deploy --compose-file docker-compose.yml javaee8

kubectl get deployments
kubectl get pods
kubectl get services

docker stack rm javaee8
```

### Go from Docker Compose to Kubernetes with http://kompose.io

Download the latest release of Kompose from Github and put the binary on your `PATH`.
You may want to modify the conversion using labels, like
```yaml
  labels:
    kompose.service.type: nodeport
```

Then issue the following command to convert the `docker-compose.yml` into Kubernetes YAMLs.
```
kompose convert -f docker-compose.yml -o build/
```

### Deploy and Run everything on Kubernetes

Use the generated YAML files to deploy and run everything on Kubernetes.
```
kubectl apply -f src/main/kubernetes/

kubectl get deployments
kubectl get pods
kubectl get services

kubectl rm -f src/main/kubernetes/
```

## 5. Getting started with Eclipse MicroProfile APIs

### Add some MicroProfile 2.0 dependencies

```groovy
    providedCompile 'org.eclipse.microprofile.config:microprofile-config-api:1.3'
    providedCompile 'org.eclipse.microprofile.metrics:microprofile-metrics-api:1.1.1'
    providedCompile 'org.eclipse.microprofile.fault-tolerance:microprofile-fault-tolerance-api:1.1.2'
    providedCompile 'org.eclipse.microprofile.health:microprofile-health-api:1.0'
    providedCompile 'org.eclipse.microprofile.rest.client:microprofile-rest-client-api:1.1'
    providedCompile 'org.eclipse.microprofile.opentracing:microprofile-opentracing-api:1.1'
    providedCompile 'org.eclipse.microprofile.openapi:microprofile-openapi-api:1.0.1'
```

### Add MicroProfile Config implementation

Add the following configuration class and use the default hostname in the `HelloWorldResource`.

```java
@ApplicationScoped
public class CentralConfiguration {

    @Inject
    @ConfigProperty(name = "default.hostname", defaultValue = "localhost")
    private String defaultHostname;

    public String getDefaultHostname() {
        return defaultHostname;
    }
}
```

### Add MicroProfile Health implementation

Add the following `HealthCheck` implementation class to the codebase. Currently, we do not check any
actual backend, this is up to you to implement.

```java
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
```

The health endpoint is now available under `http://localhost:8080/health`. You can use it as the readiness
probe or liveness probe, depending on your setting. Extend your Kubernetes deployment definition.
```yaml
    readinessProbe:
      httpGet:
        path: /health
        port: 8080
      initialDelaySeconds: 60
      periodSeconds: 5
    livenessProbe:
      httpGet:
        path: /api/application.wadl
        port: 8080
      initialDelaySeconds: 90
      periodSeconds: 5
```

### Add MicroProfile Metrics integation

Now we are adding some basic metrics to the `HelloWorldResource`. Inject a basic counter and annotate the resource
method using `@Timed`.

```java
    @Inject
    @Metric(name = "helloWorldCounter", absolute = true)
    private Counter counter;

    @Timed(name = "helloWorld", absolute = true, unit = MetricUnits.MILLISECONDS)
    public JsonObject helloWorld() { ... }
```

The metrics endpoint is now available under `http://localhost:8080/metrics` for all metrics, or under
`http://localhost:8080/health/application` for application metrics only.

### Add MicroProfile OpenAPI definitions

First, add an Open API definition annotation to the `package-info.java`.

```java
@OpenAPIDefinition(
        info = @Info(title = "Java EE 8 Microservice API",
                contact = @Contact(name = "M.-Leander Reimer", email = "mario-leander.reimer@qaware.de"),
                license = @License(name = "MIT"),
                version = "1.0.0"),
        tags = {
                @Tag(name = "Java EE 8"),
                @Tag(name = "Eclipse MicroProfile")
        },
        servers = {
                @Server(url = "localhost:8080/api/")
        },
        externalDocs = @ExternalDocumentation(url = "www.google.com", description = "Use Google for external documentation")
)
package cloud.nativ.javaee;
```

Each REST operation also needs to be annotated, so add the following annotation to the `helloWorld()` method.
```java
    @APIResponse(responseCode = "200", description = "The hello world response.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = JsonObject.class)))
    @Operation(summary = "Do hello world.", description = "Retrieve JSON response with message and hostname.")
```

The Open API endpoint is now available under `http://localhost:8080/openapi/`. 

### Add MicroProfile RestClient implementation

In order to call the OpenWeatherMap API, we will add and implement the following typed interface.

```java
@RegisterRestClient
@Path("/data/2.5/weather")
public interface OpenWeatherMap {
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    JsonObject getWeather(@QueryParam("q") String city, @QueryParam("APPID") String appid);
}
```

Then add a repository implementation and configuration to use the REST client interface. We also make use of
JCache APIs to cache the weather response, and we leverage JSON-P to extract only certain parts from the response.

```java
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

@Log
@ApplicationScoped
public class OpenWeatherMapRepository {

    @Inject
    private OpenWeatherMapConfiguration configuration;

    private OpenWeatherMap openWeatherMap;

    @PostConstruct
    void initialize() {
        try {
            openWeatherMap = RestClientBuilder.newBuilder()
                    .baseUri(new URI(configuration.getWeatherUri()))
                    .build(OpenWeatherMap.class);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Timeout(value = 5L, unit = ChronoUnit.SECONDS)
    @Retry(delay = 500L, maxRetries = 1)
    @Fallback(fallbackMethod = "defaultWeather")
    @CacheResult(cacheName = "weatherCache")
    public String getWeather(String city) {
        JsonObject response = openWeatherMap.getWeather(city, configuration.getWeatherAppId());
        LOGGER.log(Level.INFO, "Received {0}", response);

        JsonPointer pointer = Json.createPointer("/weather/0/main");
        String weather = ((JsonString) pointer.getValue(response)).getString();

        return weather;
    }

    public String defaultWeather(String city) {
        return "Unknown";
    }
}
``` 

## 6. Asynchronous REST API implementation

Next, we add a REST API to access the weather for a given city, using the OpenWeatherMap API from the previous step.
We make use of the asynchronous JAX-RS capabilities using `@Suspended AsyncResponse asyncResponse`.

```java
@ApplicationScoped
@Path("weather")
public class WeatherResource {

    @Inject
    private OpenWeatherMapRepository repository;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @APIResponse(responseCode = "200", description = "The current weather for the city.",
            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class)))
    @Operation(summary = "Get the current weather for a city.",
            description = "Retrieves the current weather via the OpenWeatherMap API.")
    @Path("/{city}")
    public void getWeather(@Suspended final AsyncResponse asyncResponse,
                           @Parameter(name = "city", required = true, example = "Rosenheim,de",
                                   schema = @Schema(type = SchemaType.STRING))
                           @PathParam("city") String city) {

        asyncResponse.setTimeout(5, TimeUnit.SECONDS);
        asyncResponse.setTimeoutHandler(r -> r.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE).build()));
        asyncResponse.resume(Response.ok(repository.getWeather(city)).build());
    }
}
```

## 7. Asynchronous messaging with JMS

Next we send and receive current weather events using a JMS topic. We need a message-driven bean, an event payload bean
that we send as JSON, a topic sender and the application server setup. 

First we add the required setup to our application server, we need to create the connection pool and factory. 
Add the following code to the `src/main/docker/post-deploy.asadmin` file.

```
deploy --type rar --name activemq-rar /opt/payara/deployments/activemq-rar.rar

create-resource-adapter-config --property ServerUrl='tcp://message-queue:61616':UserName='admin':Password='admin' activemq-rar
create-connector-connection-pool --raname activemq-rar --connectiondefinition javax.jms.ConnectionFactory --ping false --isconnectvalidatereq true jms/activeMqConnectionPool
create-connector-resource --poolname jms/activeMqConnectionPool jms/activeMqConnectionFactory

create-admin-object --raname activemq-rar --restype javax.jms.Topic --property PhysicalName=WEATHER.EVENTS jms/WeatherEvents
```

Do not use standard Java object serialization as payload format, since this tighly couples the sender and the receiver
on the payload level. Use flexible JSON instead, combined with mime-type versioning.

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentWeather {
    private String city;

    private String weather;

    public JsonObject toJson() {
        return Json.createObjectBuilder().add("city", city).add("weather", weather).build();
    }
}
```

For sending current weather events we are using asynchronous CDI events between the repository and the following
JMS topic sender implementation. Unfortunately, the ActiveMQ RAR only supports JMS 2.0 APIs.  We are using JSON-B
to marshall the event to a JMS `TextMessage`.

```java
@Log
@Stateless
public class CurrentWeatherTopic {

    @Resource(lookup = "jms/activeMqConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/WeatherEvents")
    private Topic destination;

    private Jsonb jsonb;

    @PostConstruct
    void initialize() {
        JsonbConfig config = new JsonbConfig()
                .withFormatting(false)
                .withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_DASHES)
                .withNullValues(true);
        jsonb = JsonbBuilder.create(config);
    }

    public void publish(CurrentWeather currentWeather) {
        try (Connection connection = connectionFactory.createConnection()) {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);
            producer.setTimeToLive(1000 * 30); // 30 seconds

            TextMessage textMessage = session.createTextMessage(jsonb.toJson(currentWeather));
            textMessage.setJMSType(CurrentWeather.class.getSimpleName());
            textMessage.setStringProperty("contentType", "application/vnd.weather.v1+json");

            producer.send(textMessage);
            LOGGER.log(Level.INFO, "Sent {0} to WeatherEvents destination.", textMessage);
        } catch (JMSException e) {
            LOGGER.log(Level.WARNING, "Could not send JMS message.", e);
        }
    }

    public void observe(@ObservesAsync CurrentWeather weatherEvent) {
        publish(weatherEvent);
    }
}
```

Next, we implement the message-driven bean to receive current weather events. The `@ActivationConfigProperty` annotation 
values must match the asadmin commands. We also make use of a `messageSelector` to filter out incompatible messages.
Her we use simple JSON-B data binding.

```java
@Log
@MessageDriven(name = "CurrentWeatherMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/WeatherEvents"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "WEATHER.EVENTS"),
        @ActivationConfigProperty(propertyName = "resourceAdapter", propertyValue = "activemq-rar"),
        @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
        @ActivationConfigProperty(propertyName = "clientId", propertyValue = "javaee8-service"),
        @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "CurrentWeatherMDB"),
        @ActivationConfigProperty(propertyName = "messageSelector",
                propertyValue = "(JMSType = 'CurrentWeather') AND (contentType = 'application/vnd.weather.v1+json')")
})
public class CurrentWeatherMDB implements MessageListener {

    @Inject
    private CurrentWeatherStorage storage;
    private Jsonb jsonb;

    @PostConstruct
    void initialize() {
        JsonbConfig config = new JsonbConfig().withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_DASHES);
        jsonb = JsonbBuilder.create(config);
    }

    @Override
    public void onMessage(Message message) {
        LOGGER.log(Level.INFO, "Received inbound message {0}.", message);

        String body = getBody(message);
        if (body != null) {
            CurrentWeather currentWeather = jsonb.fromJson(body, CurrentWeather.class);
            storage.save(currentWeather);
        }
    }

    private String getBody(Message message) {
        String body = null;
        try {
            if (message instanceof TextMessage) {
                body = ((TextMessage) message).getText();
            }
        } catch (JMSException e) {
            LOGGER.log(Level.WARNING, "Could not get message body.", e);
        }
        return body;
    }
}
```   

## 8. Database persistence with JPA

To store current weather event entities we use a simple JPA based repository implementation.
```java
@Log
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@Transactional
public class CurrentWeatherStorage {

    @PersistenceContext
    private EntityManager entityManager;

    public void save(CurrentWeather currentWeather) {
        LOGGER.log(Level.INFO, "Saving {0}.", currentWeather);
        entityManager.merge(currentWeather);
    }
}
```

Next we extend the `CurrentWeather` event bean with JPA entity annotations so it can be stored with JPA successfully.
```java
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

    public JsonObject toJson() {
        return Json.createObjectBuilder().add("city", city).add("weather", weather).build();
    }
}
```

We need to add the following commands to the `src/main/docker/post-deploy.asadmin` file to create the required connection
pool and JNDI resource objects.
```
create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGConnectionPoolDataSource --restype javax.sql.ConnectionPoolDataSource --property portNumber=5432:password='12qwasyx':user='javaee8':serverName=postgres-db:databaseName='weather' PostgresPool
create-jdbc-resource --connectionpoolid PostgresPool jdbc/WeatherDb

set resources.jdbc-connection-pool.PostgresPool.connection-validation-method=custom-validation
set resources.jdbc-connection-pool.PostgresPool.validation-classname=org.glassfish.api.jdbc.validation.PostgresConnectionValidation
set resources.jdbc-connection-pool.PostgresPool.is-connection-validation-required=true
set resources.jdbc-connection-pool.PostgresPool.fail-all-connections=true
```

Finally, we need to setup JPA and the persistence context by creating a `META-INF/persistence.xml` file.
```xml
<persistence version="2.1"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd">

    <persistence-unit name="currentWeather" transaction-type="JTA">
        <jta-data-source>jdbc/WeatherDb</jta-data-source>

        <class>cloud.nativ.javaee.weather.CurrentWeather</class>
        <exclude-unlisted-classes>false</exclude-unlisted-classes>
        <shared-cache-mode>NONE</shared-cache-mode>

        <properties>
            <property name="javax.persistence.schema-generation.database.action" value="drop-and-create"/>
            <property name="javax.persistence.schema-generation.create-source" value="script-then-metadata"/>
            <property name="javax.persistence.schema-generation.create-script-source" value="META-INF/create.sql"/>

            <property name="javax.persistence.sql-load-script-source" value="META-INF/weather.sql"/>

            <property name="eclipselink.logging.level" value="FINE"/>
            <property name="eclipselink.logging.parameters" value="true"/>
        </properties>
    </persistence-unit>

</persistence>
```

## 9. Reactive UIs with Server-sent Events (SSE)


## 9. Load Testing with Slapper (optional)

You need to have the Slapper load test tool installed to do this. Create a targets file with the following content:
```
GET http://localhost:8080/api/weather/Munich,de
GET http://localhost:8080/api/weather/Rosenheim,de
GET http://localhost:8080/api/weather/London,uk
GET http://localhost:8080/api/weather/Bucharest,ro
GET http://localhost:8080/api/hello
```

Issue the following command to start the load test: 
```
$ /Users/lreimer/go/bin/slapper -targets slapper.targets
```

# Maintainer

M.-Leander Reimer (@lreimer), <mario-leander.reimer@qaware.de>

# License

This software is provided under the MIT open source license, read the `LICENSE`
file for details.
