package cloud.nativ.javaee.weather;

import lombok.extern.java.Log;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.logging.Level;

/**
 * The JPA storage for {@link CurrentWeather} entities.
 */
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
