package fi.tuni.olvander.citybicyclejourneys;

import fi.tuni.olvander.citybicyclejourneys.journeys.BicycleJourneyRepository;
import fi.tuni.olvander.citybicyclejourneys.stations.StationRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CityBicycleJourneys {

    @Autowired
	StationRepository stationDb;

	@Autowired
	BicycleJourneyRepository bicycleJourneyDb;

	Log logger = LogFactory.getLog(CityBicycleJourneys.class);

	public static void main(String[] args) {
		SpringApplication.run(CityBicycleJourneys.class, args);
	}



}
