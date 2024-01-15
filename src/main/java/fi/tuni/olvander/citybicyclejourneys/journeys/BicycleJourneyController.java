package fi.tuni.olvander.citybicyclejourneys.journeys;

import fi.tuni.olvander.citybicyclejourneys.exceptions.BicycleJourneyNotFoundException;
import fi.tuni.olvander.citybicyclejourneys.exceptions.IdNotANumberException;
import fi.tuni.olvander.citybicyclejourneys.stations.Station;
import fi.tuni.olvander.citybicyclejourneys.stations.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class BicycleJourneyController {
    @Autowired
    private StationRepository stationDb;

    @Autowired
    private BicycleJourneyRepository bicycleJourneyDb;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Iterable<BicycleJourney> allJourneys;

    private ArrayList<Station> stations;

    private HashMap<String, Station> stationsMap;

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

    public ResponseEntity<Iterable<BicycleJourney>>
    getBicycleJourneysWithResponseEntity() {

        HttpHeaders headers = new HttpHeaders();

        headers.setAccessControlAllowOrigin("*");

        return new ResponseEntity<>(this.allJourneys, headers, HttpStatus.OK);
    }

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

    public void getAllStations() {

        if (this.stations == null) {
            this.stations = (ArrayList<Station>) this.stationDb.findAll();
        }
    }

    public Optional<Station> findStationFromMap(String stationId) {
        Optional<Station> station = Optional.empty();
        getAllStations();
        addStationsToHashMap();

        if (this.stationsMap.get(stationId) != null) {
            station = Optional.of(this.stationsMap.get(stationId));
        }

        return station;
    }

    @RequestMapping(value = "api/journeys/departureDesc/",
            method = RequestMethod.GET) public ResponseEntity
            <Iterable<BicycleJourney>> getJourneysSortedByDepartureStationDesc(
            @RequestParam int[] selectedMonths) {

        sortJourneys("descending", "departure", selectedMonths);

        return getBicycleJourneysWithResponseEntity();
    }

    @RequestMapping(value = "api/journeys/departureAsc/",
            method =RequestMethod.GET) public ResponseEntity
            <Iterable<BicycleJourney>> getJourneysSortedByDepartureStationAsc(
                    @RequestParam int[] selectedMonths) {

        sortJourneys("ascending", "departure", selectedMonths);

        return getBicycleJourneysWithResponseEntity();
    }

    @RequestMapping(value = "api/journeys/returnDesc/",
            method = RequestMethod.GET) public ResponseEntity
            <Iterable<BicycleJourney>> getJourneysSortedByReturnStationDesc(
            @RequestParam int[] selectedMonths) {

        sortJourneys("descending", "return", selectedMonths);

        return getBicycleJourneysWithResponseEntity();
    }

    @RequestMapping(value = "api/journeys/returnAsc/",
            method = RequestMethod.GET) public ResponseEntity
            <Iterable<BicycleJourney>> getJourneysSortedByReturnStationAsc(
            @RequestParam int[] selectedMonths) {

        sortJourneys("ascending", "return", selectedMonths);

        return getBicycleJourneysWithResponseEntity();
    }

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

    public void addStationsToHashMap() {

        if (this.stationsMap == null) {
            HashMap<String, Station> map = new HashMap<>();

            for (int i = 0; i < stations.size(); i++) {
                map.put(this.stations.get(i).getStationId(), stations.get(i));
            }
            this.stationsMap = map;
        }
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
