package fi.tuni.olvander.citybicyclejourneys.journeys;

import fi.tuni.olvander.citybicyclejourneys.exceptions
        .BicycleJourneyNotFoundException;
import fi.tuni.olvander.citybicyclejourneys.exceptions.IdNotANumberException;
import fi.tuni.olvander.citybicyclejourneys.stations.Station;
import fi.tuni.olvander.citybicyclejourneys.stations.StationRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 *  A Controller class used to make accessing City Bicycle Journey related<br/>
 *  data possible with the help of endpoints and other methods.
 *
 * @author  Olli Pertovaara
 * @version 2023.12.13
 * @since   1.21
 */
@Controller
public class BicycleJourneyController {

    /**
     * An instance of the Station repository
     */
    @Autowired
    private StationRepository stationDb;

    /**
     * An instance of the Bicycle Journey repository
     */
    @Autowired
    private BicycleJourneyRepository bicycleJourneyDb;

    /**
     * A JdbcTemplate instance for interacting with the H2 database.
     */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * An Iterable of all current Bicycle Journeys. The size of it<br/>
     * depends on how many months have been selected in the UI<br/>
     * and its order varies when the UI columns have been sorted.
     */
    private Iterable<BicycleJourney> allJourneys;

    /**
     * All Stations in the H2 database. Used with the stationsMap instance<br/>
     * to get the Station faster from the database than without a HashMap.
     */
    private ArrayList<Station> stations;

    /**
     * A HashMap of types String and Station to get the Station faster.<br/>
     * The Station id is a String and the Station is the Station object.
     */
    private HashMap<String, Station> stationsMap;

    /**
     * A default constructor for the BicycleJourneyController class.
     */
    public BicycleJourneyController() {}

    /**
     * For getting the number of Bicycle Journeys in the database if the UI<br/>
     * has not yet loaded all the Bicycle Journeys or the number of the<br/>
     * Iterable allJourneys if the Journeys have been loaded at least once.
     *
     * @return A ResponseEntity with the number of Bicycle Journeys as a Long
     */
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

    /**
     * Returns all the Bicycle Journeys, either all of them<br/>
     * or a part of them according to the selected months.
     *
     * @param selectedMonths The selected months (1 to 3 months selected)
     * @return               A Response Entity with all the Bicycle Journeys
     */

    @RequestMapping(value = "api/journeys/", method = RequestMethod.GET)
    public synchronized ResponseEntity<Iterable<BicycleJourney>> getJourneys(
            @RequestParam int[] selectedMonths) {

        if (selectedMonths.length == 3) {
            this.allJourneys = this.bicycleJourneyDb.findAll();
            Collections.reverse((ArrayList<BicycleJourney>) this.allJourneys);
        } else {
            this.allJourneys = this.getJourneysBetweenDates(selectedMonths);
            Collections.reverse((ArrayList<BicycleJourney>) this.allJourneys);
        }

        return getBicycleJourneysWithResponseEntity();
    }

    /**
     * Returns a single Bicycle Journey with the id in the URL path.
     *
     * @param id         The id (a String) of a Bicycle Journey
     * @return           Returns a Response Entity with the Bicycle Journey
     * @throws Exception BicycleJourneyNotFoundException / IdNotANumberException
     */
    @RequestMapping(value = "api/journeys/{id}/", method = RequestMethod.GET)
    public synchronized ResponseEntity<BicycleJourney> getJourney(
            @PathVariable String id) throws Exception {

        HttpHeaders headers = new HttpHeaders();

        headers.setAccessControlAllowOrigin("*");

        try {
            Long idAsLong = Long.parseLong(id);
            Optional<BicycleJourney> optionalJourney = this.bicycleJourneyDb
                    .findById(idAsLong);

            if (optionalJourney.isPresent()) {
                BicycleJourney journey = optionalJourney.get();

                return new ResponseEntity<>(journey, headers, HttpStatus.OK);
            } else {
                throw new BicycleJourneyNotFoundException(idAsLong);
            }
        } catch (NumberFormatException e) {
            throw new IdNotANumberException(id);
        }
    }

    /**
     * A helper method to get a Response Entity with sorted Bicycle Journeys.
     *
     * @return A Response Entity with Bicycle Journeys
     */
    public ResponseEntity<Iterable<BicycleJourney>>
    getBicycleJourneysWithResponseEntity() {

        HttpHeaders headers = new HttpHeaders();

        headers.setAccessControlAllowOrigin("*");

        return new ResponseEntity<>(this.allJourneys, headers, HttpStatus.OK);
    }

    /**
     * A method to get the Bicycle Journeys between dates specified by the<br/>
     * months to display parameter.
     *
     * @param monthsToDisplay 1 to 3 months (int values) selected to display
     * @return                An ArrayList with the Bicycle Journeys
     */
    public ArrayList<BicycleJourney> getJourneysBetweenDates(
            int[] monthsToDisplay) {

        if (monthsToDisplay.length >= 3) {
            this.allJourneys = bicycleJourneyDb.findAll();
            ArrayList<BicycleJourney> journeys =
                    (ArrayList<BicycleJourney>) this.allJourneys;

            return journeys;
        } else {
            ArrayList<BicycleJourney> journeys = new ArrayList<>();
            String dates =
                    getDepartureDateRangeForMonthsToDisplay(monthsToDisplay);
            String sql = "SELECT * FROM BICYCLE_JOURNEY WHERE " + dates;
            jdbcTemplate.query(sql, resultSet -> {
                journeys.add(
                        new BicycleJourney(
                                resultSet.getLong(1),
                                this.getLocalDateTime(resultSet.getString(3)),
                                this.getLocalDateTime(resultSet.getString(6)),
                                resultSet.getString(4),
                                resultSet.getString(7),
                                resultSet.getDouble(2),
                                resultSet.getInt(5)));
            });

            return journeys;
        }
    }

    /**
     * A helper method for getting a date range.<br/>
     * The selected months parameter determines the departure dates of the<br/>
     * Bicycle Journeys so that they must start during the selected month(s).
     *
     * @param selectedMonths The selected months
     * @return               The Departure dates as a String SQL query
     */
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

    /**
     * <p>For sorting Bicycle Journeys with the sort direction<br/>
     * (either ascending or descending) and with the type<br/>
     * (either return or departure) and with the<br/>
     * months to display as the parameters.</p>
     *
     * <p>Uses ArrayList sorting and comparing the Station names<br/>
     * instead of SQL sorting, to make the sorting faster.</p>
     *
     * @param sortDirection   The direction (String), ascending or descending
     * @param type            The type (a String), either return or departure
     * @param monthsToDisplay The months to display as int values
     */
    public void sortJourneys(String sortDirection, String type,
            int[] monthsToDisplay) {

        ArrayList<BicycleJourney> journeys =
                this.getJourneysBetweenDates(monthsToDisplay);

        journeys.sort((o1, o2) -> {
            Optional<Station> s1 = Optional.empty();
            Optional<Station> s2 = Optional.empty();

            if (type.equals("return")) {
                s1 = findStationFromMap(o1.getReturnStationId());
                s2 = findStationFromMap(o2.getReturnStationId());
            } else if (type.equals("departure")) {
                s1 = findStationFromMap(o1.getDepartureStationId());
                s2 = findStationFromMap(o2.getDepartureStationId());
            }

            if (s1.isEmpty() && s2.isEmpty()) {
                return 0;
            } else if (s1.isEmpty()) {
                return -1;
            } else if (s2.isEmpty()) {
                return 1;
            } else {
                return s1.get().getName().compareTo(s2.get().getName());
            }
        });

        if (sortDirection.equals("descending")) {
            Collections.reverse(journeys);
        }
        this.allJourneys = journeys;
    }

    /**
     * For getting all the Stations.
     */
    public void getAllStations() {

        if (this.stations == null) {
            this.stations = (ArrayList<Station>) this.stationDb.findAll();
        }
    }

    /**
     * For finding a Station from the Station HashMap in order<br/>
     * to hasten the sorting of the Bicycle Journeys.
     *
     * @param stationId The Station id (not the true id) of the Station
     * @return          An Optional Station to be returned
     */
    public Optional<Station> findStationFromMap(String stationId) {
        Optional<Station> station = Optional.empty();
        getAllStations();
        addStationsToHashMap();

        if (this.stationsMap.get(stationId) != null) {
            station = Optional.of(this.stationsMap.get(stationId));
        }

        return station;
    }

    /**
     * For adding Stations to the Station Hash Map in order<br/>
     * to hasten the sorting of the Bicycle Journeys.
     */
    public void addStationsToHashMap() {

        if (this.stationsMap == null) {
            HashMap<String, Station> map = new HashMap<>();

            for (int i = 0; i < stations.size(); i++) {
                map.put(this.stations.get(i).getStationId(), stations.get(i));
            }
            this.stationsMap = map;
        }
    }

    /**
     * For getting Bicycle Journeys sorted descending by departure Station.
     *
     * @param selectedMonths The selected months as int values
     * @return               A Response Entity with sorted Bicycle Journeys
     */
    @RequestMapping(value = "api/journeys/departureDesc/",
            method = RequestMethod.GET) public ResponseEntity
            <Iterable<BicycleJourney>> getJourneysSortedByDepartureStationDesc(
            @RequestParam int[] selectedMonths) {

        sortJourneys("descending", "departure", selectedMonths);

        return getBicycleJourneysWithResponseEntity();
    }

    /**
     * For getting Bicycle Journeys sorted ascending by departure Station.
     *
     * @param selectedMonths The selected months as int values
     * @return               A Response Entity with sorted Bicycle Journeys
     */
    @RequestMapping(value = "api/journeys/departureAsc/",
            method =RequestMethod.GET) public ResponseEntity
            <Iterable<BicycleJourney>> getJourneysSortedByDepartureStationAsc(
            @RequestParam int[] selectedMonths) {

        sortJourneys("ascending", "departure", selectedMonths);

        return getBicycleJourneysWithResponseEntity();
    }

    /**
     * For getting Bicycle Journeys sorted descending by return Station.
     *
     * @param selectedMonths The selected months as int values
     * @return               A Response Entity with sorted Bicycle Journeys
     */
    @RequestMapping(value = "api/journeys/returnDesc/",
            method = RequestMethod.GET) public ResponseEntity
            <Iterable<BicycleJourney>> getJourneysSortedByReturnStationDesc(
            @RequestParam int[] selectedMonths) {

        sortJourneys("descending", "return", selectedMonths);

        return getBicycleJourneysWithResponseEntity();
    }

    /**
     * For getting Bicycle Journeys sorted ascending by return Station
     *
     * @param selectedMonths The selected months as int values
     * @return               A Response Entity with sorted Bicycle Journeys
     */
    @RequestMapping(value = "api/journeys/returnAsc/",
            method = RequestMethod.GET) public ResponseEntity
            <Iterable<BicycleJourney>> getJourneysSortedByReturnStationAsc(
                    @RequestParam int[] selectedMonths) {

        sortJourneys("ascending", "return", selectedMonths);

        return getBicycleJourneysWithResponseEntity();
    }

    /**
     * For getting Bicycle Journeys sorted descending by distance.
     *
     * @param selectedMonths The selected months as int values
     * @return               A Response Entity with sorted Bicycle Journeys
     */
    @RequestMapping(value = "api/journeys/distanceDesc/",
            method = RequestMethod.GET) public ResponseEntity
            <Iterable<BicycleJourney>> getJourneysSortedByDistanceDesc(
            @RequestParam int[] selectedMonths) {

        ArrayList<BicycleJourney> journeys = this.getJourneysBetweenDates(
                selectedMonths);

        journeys.sort(Comparator.comparingDouble(
                BicycleJourney::getCoveredDistance).reversed());

        this.allJourneys = journeys;

        return getBicycleJourneysWithResponseEntity();
    }

    /**
     * For getting Bicycle Journeys sorted ascending by Journey distance.
     *
     * @param selectedMonths The selected months as int values
     * @return               A Response Entity with sorted Bicycle Journeys
     */
    @RequestMapping(value = "api/journeys/distanceAsc/",
            method = RequestMethod.GET) public ResponseEntity
            <Iterable<BicycleJourney>> getJourneysSortedByDistanceAsc(
                    @RequestParam int[] selectedMonths) {

        ArrayList<BicycleJourney> journeys = this.getJourneysBetweenDates(
                selectedMonths);

        journeys.sort(Comparator.comparingDouble(
                BicycleJourney::getCoveredDistance));

        this.allJourneys = journeys;

        return getBicycleJourneysWithResponseEntity();
    }

    /**
     * For getting Bicycle Journeys sorted descending by Journey duration.
     *
     * @param selectedMonths The selected months as int values
     * @return               A Response Entity with sorted Bicycle Journeys
     */
    @RequestMapping(value = "api/journeys/durationDesc/",
            method = RequestMethod.GET) public ResponseEntity
            <Iterable<BicycleJourney>> getJourneysSortedByDurationDesc(
            @RequestParam int[] selectedMonths) {

        ArrayList<BicycleJourney> journeys = this.getJourneysBetweenDates(
                selectedMonths);

        journeys.sort(Comparator.comparingInt(
                BicycleJourney::getJourneyDuration).reversed());

        this.allJourneys = journeys;

        return getBicycleJourneysWithResponseEntity();
    }

    /**
     * For getting Bicycle Journeys sorted ascending by Journey duration.
     *
     * @param selectedMonths The selected months as int values
     * @return               A Response Entity with sorted Bicycle Journeys
     */
    @RequestMapping(value = "api/journeys/durationAsc/",
            method = RequestMethod.GET) public ResponseEntity
            <Iterable<BicycleJourney>> getJourneysSortedByDurationAsc(
                    @RequestParam int[] selectedMonths) {

        ArrayList<BicycleJourney> journeys = this.getJourneysBetweenDates(
                selectedMonths);

        journeys.sort(Comparator.comparingInt(
                BicycleJourney::getJourneyDuration));

        this.allJourneys = journeys;

        return getBicycleJourneysWithResponseEntity();
    }

    /**
     * A helper method. Returns a LocalDateTime object from given dateTime<br/>
     * String. The LocalDateTime object is set with a default value "00:00"<br/>
     * if the dateTime String has no time, meaning it started at midnight.<br/>
     *
     * @param dateTime A dateTime String having the date
     * @return         A LocalDateTime object
     */
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