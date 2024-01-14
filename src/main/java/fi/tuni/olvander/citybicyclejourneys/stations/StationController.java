package fi.tuni.olvander.citybicyclejourneys.stations;

import fi.tuni.olvander.citybicyclejourneys.exceptions.IdNotANumberException;
import fi.tuni.olvander.citybicyclejourneys.exceptions.StationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

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

    @RequestMapping(value = "api/stations/{id}/", method = RequestMethod.GET)
    public synchronized ResponseEntity<Station> getStationWithId(
            @PathVariable String id) throws Exception {

        ResponseEntity<Station> stationEntity;

        try {
            int idAsInt = Integer.parseInt(id);
            Optional<Station> optionalStation = this.stationDb
                    .findById(idAsInt);

            if (optionalStation.isPresent()) {
                Station station = optionalStation.get();
                HttpHeaders headers = new HttpHeaders();
                headers.setAccessControlAllowOrigin("*");
                stationEntity = new ResponseEntity<>(station, headers,
                        HttpStatus.OK);
            } else {
                throw new StationNotFoundException(idAsInt);
            }
        } catch (NumberFormatException e) {
            throw new IdNotANumberException(id);
        }

        return stationEntity;
    }

    @RequestMapping(value = "api/stations/{id}/totalJourneysFrom/",
            method = RequestMethod.GET) public ResponseEntity<Integer>
            getTotalJourneysStartingFromStation(@PathVariable String id,
            @RequestParam int[] selectedMonths) throws Exception {

        int noOfJourneys;
        HttpHeaders headers = new HttpHeaders();

        headers.setAccessControlAllowOrigin("*");

        try {
            int idAsInt = Integer.parseInt(id);
            Optional<Station> optionalStation = this.stationDb
                    .findById(idAsInt);

            if (optionalStation.isPresent()) {
                Station station = optionalStation.get();
                String stationId = station.getStationId();
                noOfJourneys = 0;
            } else {
                throw new StationNotFoundException(idAsInt);
            }
        } catch (NumberFormatException e) {
            throw new IdNotANumberException(id);
        }

        return new ResponseEntity<>(noOfJourneys, headers, HttpStatus.OK);
    }

    public int getNumberOfJourneysStartingFromStation(String stationId,
            int[] selectedMonths) {

        String sql = "SELECT COUNT(*) as count FROM BICYCLE_JOURNEY WHERE "
                + "(DEPARTURE_STATION_ID = '" + stationId + "');";
        int[] noOfJourneys = new int[1];

        if (selectedMonths.length >= 3) {
            noOfJourneys[0] = 0;
        } else {
            String dates = "";
            sql = "SELECT COUNT(*) as count FROM BICYCLE_JOURNEY WHERE "
                    + "(DEPARTURE_STATION_ID = '" + stationId + "')" + dates
                    + ";";
            noOfJourneys[0] = 0;
        }

        return noOfJourneys[0];
    }
}
