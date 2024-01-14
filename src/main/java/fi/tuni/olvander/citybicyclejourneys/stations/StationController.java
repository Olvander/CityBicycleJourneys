package fi.tuni.olvander.citybicyclejourneys.stations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public class StationController {

    @Autowired
    private StationRepository stationDb;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public StationController() {}
}
