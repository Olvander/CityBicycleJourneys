package fi.tuni.olvander.citybicyclejourneys.exceptions;

public class StationNotFoundException extends IllegalArgumentException {
    private final int id;

    public StationNotFoundException(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
