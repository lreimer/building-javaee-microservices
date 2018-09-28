package cloud.nativ.javaee.weather;

import lombok.extern.java.Log;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.enterprise.event.ObservesAsync;
import javax.jms.*;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyNamingStrategy;
import java.util.logging.Level;

/**
 * The topic beans listens for {@link CurrentWeather} events and publishes
 * these via JMS to the jms/WeatherEvents topic. We use JSON-B for marshalling
 * the message payload.
 */
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
            producer.setTimeToLive(1000 * 30); // 30 saeconds

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
