package fi.tuni.olvander.citybicyclejourneys.exceptions;

public class BicycleJourneyNotFoundException extends IllegalArgumentException {

    private final Long id;

    public BicycleJourneyNotFoundException(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
