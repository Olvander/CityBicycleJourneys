package fi.tuni.olvander.citybicyclejourneys.journeys;

import fi.tuni.olvander.citybicyclejourneys.stations.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class BicycleJourneyController {
    @Autowired
    private StationRepository stationDb;
}
