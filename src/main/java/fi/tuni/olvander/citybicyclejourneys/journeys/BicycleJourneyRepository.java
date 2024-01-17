package fi.tuni.olvander.citybicyclejourneys.journeys;

import org.springframework.data.repository.CrudRepository;

/**
 * A Bicycle Journey Repository interface that extends CrudRepository. The<br/>
 * types for interacting with the database are BicycleJourney and a Long id.
 *
 * @author  Olli Pertovaara
 * @version 2023.12.13
 * @since   1.21
 */
public interface BicycleJourneyRepository extends CrudRepository
        <BicycleJourney, Long> {}
