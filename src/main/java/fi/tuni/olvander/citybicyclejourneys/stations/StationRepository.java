package fi.tuni.olvander.citybicyclejourneys.stations;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * A Station repository interface which extends CrudRepository.<br/>
 * The Repository types are Station and Integer.
 *
 * @author  Olli Pertovaara
 * @version 2023.12.13
 * @since   1.21
 */
public interface StationRepository extends CrudRepository<Station, Integer> {

    /**
     * A method that returns an Optional Station by the Station id (String)<br/>
     * instead of the real Station id.
     *
     * @param stationId A Station id which is not the same as the real id
     * @return          An Optional Station with the Station id (a String)
     */
    Optional<Station> findByStationId(String stationId);
}