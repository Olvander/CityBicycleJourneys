package fi.tuni.olvander.citybicyclejourneys.journeys;

import fi.tuni.olvander.citybicyclejourneys.stations.Station;
import fi.tuni.olvander.citybicyclejourneys.stations.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @RequestMapping(value = "api/journeysCount/", method = RequestMethod.GET)
    public synchronized ResponseEntity<Long> getAllJourneysCount() {
        HttpHeaders headers = new HttpHeaders();
        Long count;

        headers.setAccessControlAllowOrigin("*");

        if (this.allJourneys == null) {
            count = this.bicycleJourneyDb.count();
        } else {
            count = ((ArrayList<BicycleJourney>) this.allJourneys)
                    .stream()
                    .count();
        }

        return new ResponseEntity<>(count, headers, HttpStatus.OK);
    }

}
