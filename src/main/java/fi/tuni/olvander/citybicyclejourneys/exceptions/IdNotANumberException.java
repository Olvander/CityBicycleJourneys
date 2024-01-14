package fi.tuni.olvander.citybicyclejourneys.exceptions;

public class IdNotANumberException extends NumberFormatException {

    private final String id;

    public IdNotANumberException(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
