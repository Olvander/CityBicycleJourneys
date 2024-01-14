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

import java.text.DecimalFormat;
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
                noOfJourneys = getNumberOfJourneysStartingFromStation(stationId,
                        selectedMonths);
            } else {
                throw new StationNotFoundException(idAsInt);
            }
        } catch (NumberFormatException e) {
            throw new IdNotANumberException(id);
        }

        return new ResponseEntity<>(noOfJourneys, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "api/stations/{id}/totalJourneysTo/",
            method = RequestMethod.GET) public ResponseEntity<Integer>
            getTotalJourneysEndingAtStation(@PathVariable String id,
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
                noOfJourneys = getNumberOfJourneysEndingAtStation(stationId,
                        selectedMonths);
            } else {
                throw new StationNotFoundException(idAsInt);
            }
        } catch (NumberFormatException e) {
            throw new IdNotANumberException(id);
        }

        return new ResponseEntity<>(noOfJourneys, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "api/stations/{id}/averageDistanceFrom/")
    public ResponseEntity<Double> getAverageDistanceStartingFromStation(
            @PathVariable String id, @RequestParam int[] selectedMonths) throws
            Exception {

        double[] avgDistanceFrom = new double[1];
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        HttpHeaders headers = new HttpHeaders();

        avgDistanceFrom[0] = 0.0;
        headers.setAccessControlAllowOrigin("*");

        try {
            int idAsInt = Integer.parseInt(id);
            Optional<Station> optionalStation = this.stationDb
                    .findById(idAsInt);

            if (optionalStation.isPresent()) {
                Station station = optionalStation.get();
                String stationId = station.getStationId();

                if (selectedMonths.length == 3) {

                    String sql = "SELECT AVG(COVERED_DISTANCE) FROM "
                            + "BICYCLE_JOURNEY WHERE (DEPARTURE_STATION_ID = '"
                            + stationId + "');";
                    avgDistanceFrom[0] = getAverageJourneyDistanceFromDb(sql);

                } else {
                    String dates = getDepartureDateRangeForSelectedMonths(
                            selectedMonths);

                    String sql = "SELECT AVG(COVERED_DISTANCE) FROM "
                            + "BICYCLE_JOURNEY WHERE (DEPARTURE_STATION_ID = '"
                            + stationId + "')" + dates + ";";

                    avgDistanceFrom[0] = getAverageJourneyDistanceFromDb(sql);
                }
            } else {
                throw new StationNotFoundException(idAsInt);
            }
        } catch (NumberFormatException e) {
            throw new IdNotANumberException(id);
        }
        avgDistanceFrom[0] = Double.parseDouble(decimalFormat.format(
                avgDistanceFrom[0] / 1000).replace(",", ".")
        );

        return new ResponseEntity<>(avgDistanceFrom[0], headers, HttpStatus.OK);
    }

    public int getNumberOfJourneysStartingFromStation(String stationId,
            int[] selectedMonths) {

        String sql = "SELECT COUNT(*) as count FROM BICYCLE_JOURNEY WHERE "
                + "(DEPARTURE_STATION_ID = '" + stationId + "');";
        int[] noOfJourneys = new int[1];

        if (selectedMonths.length >= 3) {
            noOfJourneys[0] = getNumberOfJourneysFromDb(sql);
        } else {
            String dates = getDepartureDateRangeForSelectedMonths(
                    selectedMonths);
            sql = "SELECT COUNT(*) as count FROM BICYCLE_JOURNEY WHERE "
                    + "(DEPARTURE_STATION_ID = '" + stationId + "')" + dates
                    + ";";
            noOfJourneys[0] = getNumberOfJourneysFromDb(sql);
        }

        return noOfJourneys[0];
    }

    public double getAverageJourneyDistanceFromDb(String sql) {
        double[] avgDistance = new double[1];

        jdbcTemplate.query(sql, resultSet -> {
            avgDistance[0] = resultSet.getDouble(1);
        });

        return avgDistance[0];
    }


    public int getNumberOfJourneysEndingAtStation(String stationId,
                                                  int[] selectedMonths) {

        String sql = "SELECT COUNT(*) as count FROM BICYCLE_JOURNEY WHERE "
                + "(RETURN_STATION_ID = '" + stationId + "');";
        int[] noOfJourneys = new int[1];

        if (selectedMonths.length >= 3) {
            noOfJourneys[0] = getNumberOfJourneysFromDb(sql);
        } else {
            String dates = getDepartureDateRangeForSelectedMonths(
                    selectedMonths);
            sql = "SELECT COUNT(*) as count FROM BICYCLE_JOURNEY WHERE "
                    + "(RETURN_STATION_ID = '" + stationId + "')" + dates + ";";
            noOfJourneys[0] = getNumberOfJourneysFromDb(sql);
        }

        return noOfJourneys[0];
    }


    public String getDepartureDateRangeForSelectedMonths(int[] selectedMonths) {
        StringBuilder dates = new StringBuilder();

        for (int i = 0; i < selectedMonths.length; i++) {

            if (i == 0) {
                dates.append(" AND (");
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

    public int getNumberOfJourneysFromDb(String sql) {
        int[] noOfJourneys = new int[1];

        jdbcTemplate.query(sql, resultSet -> {
            noOfJourneys[0] = resultSet.getInt(1);
        });

        return noOfJourneys[0];
    }
}
