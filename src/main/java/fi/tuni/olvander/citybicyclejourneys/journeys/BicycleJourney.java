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
}
