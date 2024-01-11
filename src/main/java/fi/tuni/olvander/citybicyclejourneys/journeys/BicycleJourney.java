package fi.tuni.olvander.citybicyclejourneys.journeys;

import java.time.LocalDateTime;

public class BicycleJourney {

    private Long id;
    private LocalDateTime departureDate;
    private LocalDateTime returnDate;
    private String departureStationId;
    private String returnStationId;
    private double coveredDistance;
    private int journeyDuration;

    public BicycleJourney() {}

    public BicycleJourney(LocalDateTime departureDate, LocalDateTime returnDate,
                          String departureStationId, String returnStationId,
                          double coveredDistance, int journeyDuration) {

        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.departureStationId = departureStationId;
        this.returnStationId = returnStationId;
        this.coveredDistance = coveredDistance;
        this.journeyDuration = journeyDuration;
    }

    public BicycleJourney(Long id, LocalDateTime departureDate, LocalDateTime
            returnDate, String departureStationId, String returnStationId,
                          double coveredDistance, int journeyDuration) {

        this.id = id;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
        this.departureStationId = departureStationId;
        this.returnStationId = returnStationId;
        this.coveredDistance = coveredDistance;
        this.journeyDuration = journeyDuration;
    }
}
