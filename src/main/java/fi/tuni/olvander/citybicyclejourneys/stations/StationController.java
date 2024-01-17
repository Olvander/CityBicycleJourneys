package fi.tuni.olvander.citybicyclejourneys.stations;

import fi.tuni.olvander.citybicyclejourneys.exceptions.IdNotANumberException;
import fi.tuni.olvander.citybicyclejourneys.exceptions.StationNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;

/**
 * A Station Controller class used to make accessing City Bicycle Station<br/>
 * related data possible with the help of endpoints and other methods.
 *
 * @author  Olli Pertovaara
 * @version 2023.12.13
 * @since   1.21
 */
@Controller
public class StationController {

    /**
     * The Station Repository for interacting with the Station database.<br/>
     */
    @Autowired
    private StationRepository stationDb;

    /**
     * A Jdbc Template for interacting with the H2 database<br/>
     * which has Stations and Bicycle Journeys as tables.
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * A default constructor for the StationController class.
     */
    public StationController() {}

    /**
     * An endpoint for getting all the available Stations.
     *
     * @return A Response Entity with an Iterable Station List
     */
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

    /**
     * An endpoint for getting one single Station.
     *
     * @param id         The id of the Station
     * @return           A Response Entity with a Station with URL path id
     * @throws Exception StationNotFoundException / IdNotANumberException
     */
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

    /**
     * An endpoint for getting the total number of journeys starting from a<br/>
     * Station with the id in the URL path.
     *
     * @param id             The id in the URL path
     * @param selectedMonths 1 to 3 months selected (int values)
     * @return               A Response Entity with the Journey count (Int)
     * @throws Exception     StationNotFoundException / IdNotANumberException
     */
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

    /**
     * An endpoint for returning the total number of journeys ending at the<br/>
     * Station with the id in the URL path.
     *
     * @param id             The id of the Station whose journeys are fetched
     * @param selectedMonths 1 to 3 months selected (int values)
     * @return               A Response Entity with the Journey count (Int)
     * @throws Exception     StationNotFoundException / IdNotANumberException
     */
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

    /**
     * An endpoint to get the average distance of A Bicycle Journey<br/>
     * starting from the Station whose id is in the URL path.
     *
     * @param id             The Station id whose average distance is fetched
     * @param selectedMonths 1 to 3 selected months (int values)
     * @return               Response Entity with an avg distance as a Double
     * @throws Exception     StationNotFoundException / IdNotANumberException
     */
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

    /**
     * An endpoint for getting the average distance of a Bicycle Journey<br/>
     * ending at the Station whose id is in the URL path.
     *
     * @param id             The Station id whose average distance is fetched
     * @param selectedMonths 1 to 3 selected months (int values)
     * @return               Response Entity with an avg distance as a Double
     * @throws Exception     StationNotFoundException / IdNotFoundException
     */
    @RequestMapping(value = "api/stations/{id}/averageDistanceTo/")
    public ResponseEntity<Double> getAverageDistanceEndingAtStation(
            @PathVariable String id, int[] selectedMonths) throws Exception {

        double[] avgDistanceTo = new double[1];
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        HttpHeaders headers = new HttpHeaders();

        avgDistanceTo[0] = 0.0;
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
                            + "BICYCLE_JOURNEY WHERE (RETURN_STATION_ID = '"
                            + stationId + "')";
                    avgDistanceTo[0] = getAverageJourneyDistanceFromDb(sql);
                } else {

                    String dates = getDepartureDateRangeForSelectedMonths(
                            selectedMonths);

                    String sql = "SELECT AVG(COVERED_DISTANCE) FROM "
                            + "BICYCLE_JOURNEY WHERE (RETURN_STATION_ID = '"
                            + stationId + "')" + dates + ";";
                    avgDistanceTo[0] = getAverageJourneyDistanceFromDb(sql);
                }
            } else {
                throw new StationNotFoundException(idAsInt);
            }
        } catch (NumberFormatException e) {
            throw new IdNotANumberException(id);
        }

        avgDistanceTo[0] = Double.parseDouble(decimalFormat.format(
                avgDistanceTo[0] / 1000).replace(",", ".")
        );

        return new ResponseEntity<>(avgDistanceTo[0], headers, HttpStatus.OK);
    }

    /**
     * Returns the average distance from the Bicycle Journey database.
     *
     * @param sql The sql query to be performed
     * @return    The average distance (a Double)
     */
    public double getAverageJourneyDistanceFromDb(String sql) {
        double[] avgDistance = new double[1];

        jdbcTemplate.query(sql, resultSet -> {
            avgDistance[0] = resultSet.getDouble(1);
        });

        return avgDistance[0];
    }

    /**
     * An endpoint for returning the top 5 return Stations starting from<br/>
     * the Station whose id is in the URL path.
     *
     * @param id             The id of the Station
     * @param selectedMonths 1 to 3 selected months (int values)
     * @return               A Response Entity with the top 5 Stations
     * @throws Exception     StationNotFoundException / IdNotANumberException
     */
    @RequestMapping(value = "api/stations/{id}/top5ReturnStationsStartingFrom/")
    public ResponseEntity<ArrayList<Station>> getTop5ReturnStationsStartingFrom(
            @PathVariable String id, @RequestParam int[] selectedMonths) throws
            Exception {

        ArrayList<Station> top5Stations;
        HttpHeaders headers = new HttpHeaders();

        headers.setAccessControlAllowOrigin("*");

        try {
            int idAsInt = Integer.parseInt(id);
            Optional<Station> optionalStation = this.stationDb
                    .findById(idAsInt);

            if (optionalStation.isPresent()) {
                Station station = optionalStation.get();
                String stationId = station.getStationId();

                if (selectedMonths.length >= 3) {

                    String sql = "SELECT RETURN_STATION_ID, STATION.NAME, "
                            + "COUNT(*) FROM BICYCLE_JOURNEY INNER JOIN STATION"
                            +" ON BICYCLE_JOURNEY.RETURN_STATION_ID = "
                            + "STATION.STATION_ID WHERE (DEPARTURE_STATION_ID ="
                            + "'" + stationId + "') GROUP BY RETURN_STATION_ID "
                            + "ORDER BY COUNT(*) DESC, STATION.NAME ASC "
                            + "LIMIT 5;";

                    top5Stations = getMostPopularStations(sql);
                } else {
                    String dates = getDepartureDateRangeForSelectedMonths(
                            selectedMonths);

                    String sql = "SELECT RETURN_STATION_ID, STATION.NAME, "
                            + "COUNT(*) FROM BICYCLE_JOURNEY INNER JOIN STATION"
                            + " ON BICYCLE_JOURNEY.RETURN_STATION_ID = "
                            + "STATION.STATION_ID WHERE (DEPARTURE_STATION_ID ="
                            + "'" + stationId + "')" + dates + " GROUP BY "
                            + "RETURN_STATION_ID ORDER BY COUNT(*) DESC, "
                            + "STATION.NAME ASC LIMIT 5;";
                    top5Stations = getMostPopularStations(sql);
                }
            } else {
                throw new StationNotFoundException(idAsInt);
            }
        } catch (NumberFormatException e) {
            throw new IdNotANumberException(id);
        }

        return new ResponseEntity<>(top5Stations, headers, HttpStatus.OK);
    }

    /**
     * An endpoint for returning the top 5 departure Stations ending at the<br/>
     * Station whose id is in the URL path.
     *
     * @param id             The id of the Station
     * @param selectedMonths 1 to 3 months selected (int values)
     * @return               Response Entity with the top 5 Stations
     * @throws Exception     StationNotFoundException or IdNotFoundException
     */
    @RequestMapping(value = "api/stations/{id}/top5DepartureStationsEndingAt/")
    public ResponseEntity<ArrayList<Station>> getTop5DepartureStationsEndingAt(
            @PathVariable String id, @RequestParam int[] selectedMonths) throws
            Exception {

        ArrayList<Station> top5Stations;
        HttpHeaders headers = new HttpHeaders();

        headers.setAccessControlAllowOrigin("*");

        try {
            int idAsInt = Integer.parseInt(id);
            Optional<Station> optionalStation = this.stationDb
                    .findById(idAsInt);

            if (optionalStation.isPresent()) {
                Station station = optionalStation.get();
                String stationId = station.getStationId();

                if (selectedMonths.length >= 3) {
                    String sql = "SELECT DEPARTURE_STATION_ID, STATION.NAME, "
                            + "COUNT(*) FROM BICYCLE_JOURNEY INNER JOIN STATION"
                            + " ON BICYCLE_JOURNEY.DEPARTURE_STATION_ID = "
                            + "STATION.STATION_ID WHERE (RETURN_STATION_ID = '"
                            + stationId + "') GROUP BY DEPARTURE_STATION_ID "
                            + "ORDER BY COUNT(*) DESC, STATION.NAME ASC "
                            + "LIMIT 5;";
                    top5Stations = getMostPopularStations(sql);
                } else {
                    String dates = getDepartureDateRangeForSelectedMonths(
                            selectedMonths);

                    String sql = "SELECT DEPARTURE_STATION_ID, STATION.NAME, "
                            + " COUNT(*) FROM BICYCLE_JOURNEY INNER JOIN "
                            + "STATION ON BICYCLE_JOURNEY.DEPARTURE_STATION_ID "
                            + "= STATION.STATION_ID WHERE (RETURN_STATION_ID = "
                            + "'" + stationId + "')" + dates + " GROUP BY "
                            + "DEPARTURE_STATION_ID ORDER BY COUNT(*) DESC, "
                            + "STATION.NAME ASC LIMIT 5;";
                    top5Stations = (getMostPopularStations(sql));
                }
            } else {
                throw new StationNotFoundException(idAsInt);
            }
        } catch (NumberFormatException e) {
            throw new IdNotANumberException(id);
        }

        return new ResponseEntity<>(top5Stations, headers, HttpStatus.OK);
    }

    /**
     * Returns an ArrayList of the most popular Stations.
     *
     * @param sql The SQL query to be performed
     * @return    An ArrayList having the most popular Stations
     */
    public ArrayList<Station> getMostPopularStations(String sql) {
        ArrayList<Station> mostPopularStations = new ArrayList<>();

        jdbcTemplate.query(sql,
                resultSet -> {
                    Optional<Station> optionalStation = stationDb
                            .findByStationId(resultSet.getString(1));
                    optionalStation.ifPresent(mostPopularStations::add);
                });

        return mostPopularStations;
    }

    /**
     * Returns the number of Bicycle Journeys starting from the Station<br/>
     * having the specified Station id (not the real id but a reference id).
     *
     * @param stationId      The Station id
     * @param selectedMonths 1 to 3 months selected (int values)
     * @return               The number (an int value) of Bicycle Journeys
     */
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

    /**
     * Returns the number of Bicycle Journeys ending at the Station<br/>
     * having the specified Station id (not the real id but a reference id).
     *
     * @param stationId      The Station id
     * @param selectedMonths 1 to 3 months selected (int values)
     * @return               The Number of Journeys ending at the Station
     */
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

    /**
     * Returns a date range used in an SQL query where the departure date<br/>
     * is the determining factor for the selected months.
     *
     * @param selectedMonths The selected months as int array values
     * @return               A String that can be used in an SQL query
     */
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

    /**
     * Returns the number of Bicycle Journeys from the Bicycle Journey<br/>
     * database with a condition given in the Sql query, e.g. Journeys<br/>
     * starting from or ending at the Station.
     *
     * @param sql The SQL query used for fetching the number of Journeys
     * @return    The number (an int value) of Bicycle Journeys
     */
    public int getNumberOfJourneysFromDb(String sql) {
        int[] noOfJourneys = new int[1];

        jdbcTemplate.query(sql, resultSet -> {
            noOfJourneys[0] = resultSet.getInt(1);
        });

        return noOfJourneys[0];
    }
}
