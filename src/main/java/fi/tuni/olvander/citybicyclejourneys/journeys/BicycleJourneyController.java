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
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;

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

    public ResponseEntity<Iterable<BicycleJourney>>
    getBicycleJourneysWithResponseEntity() {

        HttpHeaders headers = new HttpHeaders();

        headers.setAccessControlAllowOrigin("*");

        return new ResponseEntity<>(this.allJourneys, headers, HttpStatus.OK);
    }

    public String getDepartureDateRangeForMonthsToDisplay(
            int[] selectedMonths) {

        StringBuilder dates = new StringBuilder();

        for (int i = 0; i < selectedMonths.length; i++) {

            if (i == 0) {
                dates.append("(");
            }

            if (i != 0) {
                dates.append(" OR ");
            }
            dates.append("DEPARTURE_DATE BETWEEN '2021-0");
            dates.append(selectedMonths[i]);
            dates.append("-01 00:00:00' AND '2021-0");
            dates.append((selectedMonths[i] + 1));
            dates.append("-01 00:00:00'");
        }
        dates.append(")");

        return dates.toString();
    }

    public LocalDateTime getLocalDateTime(String dateTime) {
        String date = dateTime.substring(0, 10);
        String time = "00:00";

        if (dateTime.contains("T")) {
            time = dateTime.substring(11);
        }
        LocalDateTime dt = LocalDateTime.of(
                LocalDate.parse(date),
                LocalTime.parse(time));

        return dt;
    }
}
