package fi.tuni.olvander.citybicyclejourneys.exceptions;

/**
 * This class is for Exceptions where a Station id is in proper format<br/>
 * but the Station is not found in the Station database.
 *
 * @author  Olli Pertovaara
 * @version 2023.12.13
 * @since   1.21
 */
public class StationNotFoundException extends IllegalArgumentException {

    /**
     * A final id in integer format.
     **/
    private final int id;

    /**
     * A constructor for saving and returning the Station id<br/>
     * of a Station that is not found in the database.
     *
     * @param id An id in integer format to be saved and returned
     */
    public StationNotFoundException(int id) {
        this.id = id;
    }

    /**
     * A getter method for the id.
     *
     * @return An id in integer format
     */
    public int getId() {
        return id;
    }
}
