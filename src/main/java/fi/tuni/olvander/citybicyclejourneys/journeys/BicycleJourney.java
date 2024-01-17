package fi.tuni.olvander.citybicyclejourneys.journeys;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

/**
 * A Bicycle Journey class that is used when Bicycle Journeys are added to<br/>
 * the Bicycle Journey database and getting Bicycle Journey related data.
 *
 * @author  Olli Pertovaara
 * @version 2023.12.13
 * @since   1.21
 */
@Entity
public class BicycleJourney {

    /**
     * A Long type id which is auto-incremented in the Bicycle Journey.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * A LocalDateTime type departure date.
     */
    private LocalDateTime departureDate;

    /**
     * A LocalDateTime type return date.
     */
    private LocalDateTime returnDate;

    /**
     * A String type departure Station id (the reference id<br/>
     * in the Station database, not the true id).
     */
    private String departureStationId;

    /**
     * A String type return Station id (the reference id<br/>
     * in the Station database, not the true id).
     */
    private String returnStationId;

    /**
     * A double type covered distance in meters.
     */
    private double coveredDistance;

    /**
     * An integer type journey duration in seconds.
     */
    private int journeyDuration;

    /**
     * A default constructor for Bicycle Journey objects.
     */
    public BicycleJourney() {}


    /**
     * A constructor with six parameters, but no id.<br/>
     * Used for getting Bicycle Journeys from the database.
     *
     * @param departureDate      The departure date (a LocalDateTime)
     * @param returnDate         The return date (a LocalDateTime)
     * @param departureStationId The departure Station id (a String)
     * @param returnStationId    The return Station id (a String)
     * @param coveredDistance    The covered distance in meters (a double)
     * @param journeyDuration    The Journey duration in seconds (an int)
     */
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

    /**
     * A constructor with seven parameters, including the id.<br/>
     * Used for instance for importing journeys into the database.
     *
     * @param id                 The id of the Bicycle Journey (a Long)
     * @param departureDate      The departure date (a LocalDateTime)
     * @param returnDate         The return date (a LocalDateTime)
     * @param departureStationId The departure Station id (a String)
     * @param returnStationId    The return Station id (a String)
     * @param coveredDistance    The covered distance in meters (a double)
     * @param journeyDuration    The Journey duration in seconds (an int)
     */
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

    /**
     * Gets the id of type Long of the Bicycle Journey.
     *
     * @return A Long type id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id of type Long of the Bicycle Journey.
     *
     * @param id A Long type id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the departure date of type LocalDateTime.
     *
     * @return The departure date of the Bicycle Journey
     */
    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    /**
     * Sets the departure date of type LocalDateTime.
     *
     * @param departureDate The departure date of the Bicycle Journey
     */
    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }

    /**
     * Gets the return date of type LocalDateTime.
     *
     * @return The return date of the Bicycle Journey
     */
    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    /**
     * Sets the return date of type LocalDateTime.
     *
     * @param returnDate The return date of the Bicycle Journey
     */
    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    /**
     * Gets the departure Station id of type String.
     *
     * @return The departure Station reference id (a String, not the true id)
     */
    public String getDepartureStationId() {
        return departureStationId;
    }

    /**
     * Sets the departure Station id of type String.
     *
     * @param departureStationId The departure Station reference id<br/>
     *                           which is a String (not the true id)
     */
    public void setDepartureStationId(String departureStationId) {
        this.departureStationId = departureStationId;
    }

    /**
     * Gets the return Station id of type String.
     *
     * @return The return Station reference id as a String (not the true id)
     */
    public String getReturnStationId() {
        return returnStationId;
    }

    /**
     * Sets the return Station id of type String.
     *
     * @param returnStationId The return Station reference id as a String<br/>
     *                        (not the true id)
     */
    public void setReturnStationId(String returnStationId) {
        this.returnStationId = returnStationId;
    }

    /**
     * Gets the covered distance in meters (a double) of the Bicycle Journey.
     *
     * @return The covered distance in meters as a double
     */
    public double getCoveredDistance() {
        return coveredDistance;
    }

    /**
     * Sets the covered distance in meters (a double) of the Bicycle Journey.
     *
     * @param coveredDistance The covered distance in meters as a double
     */
    public void setCoveredDistance(double coveredDistance) {
        this.coveredDistance = coveredDistance;
    }

    /**
     * Gets the Bicycle Journey duration in seconds (an int).
     *
     * @return The Bicycle Journey duration in seconds as an int
     */
    public int getJourneyDuration() {
        return journeyDuration;
    }

    /**
     * Sets the Bicycle Journey duration in seconds (an int).
     *
     * @param journeyDuration The Bicycle Journey duration in seconds, an int
     */
    public void setJourneyDuration(int journeyDuration) {
        this.journeyDuration = journeyDuration;
    }
}
