package fi.tuni.olvander.citybicyclejourneys.journeys;

import fi.tuni.olvander.citybicyclejourneys.stations.Station;
import fi.tuni.olvander.citybicyclejourneys.stations.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;

public class BicycleJourneyController {
    @Autowired
    private StationRepository stationDb;

    @Autowired
    private BicycleJourneyRepository bicycleJourneyDb;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Iterable<BicycleJourney> allJourneys;

    private ArrayList<Station> stations;

    public BicycleJourneyController() {}

}
