package fi.tuni.olvander.citybicyclejourneys.journeys;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class BicycleJourney {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public String getDepartureStationId() {
        return departureStationId;
    }

    public void setDepartureStationId(String departureStationId) {
        this.departureStationId = departureStationId;
    }

    public String getReturnStationId() {
        return returnStationId;
    }

    public void setReturnStationId(String returnStationId) {
        this.returnStationId = returnStationId;
    }

    public double getCoveredDistance() {
        return coveredDistance;
    }

    public void setCoveredDistance(double coveredDistance) {
        this.coveredDistance = coveredDistance;
    }

    public int getJourneyDuration() {
        return journeyDuration;
    }

    public void setJourneyDuration(int journeyDuration) {
        this.journeyDuration = journeyDuration;
    }
}
