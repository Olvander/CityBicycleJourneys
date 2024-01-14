package fi.tuni.olvander.citybicyclejourneys.stations;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StationRepository extends CrudRepository<Station, Integer> {

    Optional<Station> findByStationId(String stationId);
}