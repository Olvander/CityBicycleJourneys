package fi.tuni.olvander.citybicyclejourneys.journeys;

import org.springframework.data.repository.CrudRepository;

public interface BicycleJourneyRepository extends CrudRepository
        <BicycleJourney, Long> {
}
