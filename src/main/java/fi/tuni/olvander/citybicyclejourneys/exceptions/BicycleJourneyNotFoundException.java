package fi.tuni.olvander.citybicyclejourneys.exceptions;

/**
 * This class is for Exceptions where a Bicycle Journey id is in proper<br/>
 * format but is not found in the BicycleJourney database.
 *
 * @author  Olli Pertovaara
 * @version 2023.12.13
 * @since   1.21
 */
public class BicycleJourneyNotFoundException extends IllegalArgumentException {

    /**
     * A final id which was not found in Long format.
     */
    private final Long id;

    /**
     * A constructor for the class.
     *
     * @param id The id of the journey
     */
    public BicycleJourneyNotFoundException(Long id) {
        this.id = id;
    }

    /**
     * A getter method for the id in Long format.
     *
     * @return The id
     */
    public Long getId() {
        return id;
    }
}
