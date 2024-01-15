package fi.tuni.olvander.citybicyclejourneys.exceptions;

/**
 * A class for returning the id that is not in a number format.
 *
 * @author  Olli Pertovaara
 * @version 2023.12.13
 * @since   1.21
 */
public class IdNotANumberException extends NumberFormatException {

    /**
     * A final id in String format.
     */
    private final String id;

    /**
     * A constructor for saving and displaying the id not in a number format.
     *
     * @param id The id that is not in a number format
     */
    public IdNotANumberException(String id) {
        this.id = id;
    }

    /**
     * A getter method for the exception information.
     *
     * @return The String containing the exception information
     */
    public String getId() {
        return id;
    }
}
