package fi.tuni.olvander.citybicyclejourneys.stations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public class StationController {

    @Autowired
    private StationRepository stationDb;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public StationController() {}

    @RequestMapping(value = "api/stations/", method = RequestMethod.GET)
    public synchronized ResponseEntity<Iterable<Station>> getStations() {

        if (stationDb.count() > 0) {
            Iterable<Station> stations = stationDb.findAll();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccessControlAllowOrigin("*");

            return new ResponseEntity<>(stations, headers, HttpStatus.OK);
        } else {

            return null;
        }
    }
}
