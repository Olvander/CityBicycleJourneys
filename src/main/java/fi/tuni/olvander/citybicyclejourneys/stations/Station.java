package fi.tuni.olvander.citybicyclejourneys.stations;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * A Station class, that is used when Station objects are added to the<br/>
 * Station database and for getting Station related data.
 *
 * @author  Olli Pertovaara
 * @version 2023.12.13
 * @since   1.21
 */
@Entity
public class Station {

    /**
     * An integer type id which is auto-incremented in the Station database<br/>
     * and is the true id unlike stationId which is for referencing a<br/>
     * Station from the Bicycle Journey database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * A String type station reference id used together with<br/>
     * BicycleJourney objects to fetch Station data.
     */
    private String stationId;

    /**
     * A String type name of the Station object.
     */
    private String name;

    /**
     * A String type address of the Station object.
     */
    private String address;

    /**
     * A double type field x position (longitude) of the Station.
     */
    private double x;

    /**
     * A double type field y position (latitude) of the Station.
     */
    private double y;

    /**
     * A default constructor for the Station Object.
     */
    public Station() {}

    /**
     * A constructor for the Station object comprising five parameters.
     *
     * @param stationId The Station reference id (a String)
     * @param name      The Station name (a String)
     * @param address   The Station address (a String)
     * @param x         The Station x position (a double)
     * @param y         The Station y position (a double)
     */
    public Station(String stationId, String name, String address, double x,
                   double y) {

        this.stationId = stationId;
        this.name = name;
        this.address = address;
        this.x = x;
        this.y = y;
    }

    /**
     * Gets the Station id of type integer.
     *
     * @return An id (an int)
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the Station name of type String.
     *
     * @return The Station name (a String)
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the Station name of type String.
     *
     * @param name The Station name (a String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the Station address of type String.
     *
     * @return The Station name (a String)
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the Station address of type String.
     *
     * @param address The Station address (a String)
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the Station reference id of type String.
     *
     * @return The Station reference id (a String)
     */
    public String getStationId() {
        return stationId;
    }

    /**
     * Sets the Station reference id of type String.
     *
     * @param stationId The Station reference id (a String)
     */
    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    /**
     * Gets the Station x position (longitude).
     *
     * @return The Station x position (longitude) (a double)
     */
    public double getX() {
        return x;
    }

    /**
     * Sets for the Station x position (longitude).
     *
     * @param x The Station x position (longitude) (a double)
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Gets the Station y position (latitude).
     *
     * @return The Station y position (latitude) (a double)
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the Station y position (latitude).
     *
     * @param y The Station y position (latitude) (a double)
     */
    public void setY(double y) {
        this.y = y;
    }
}