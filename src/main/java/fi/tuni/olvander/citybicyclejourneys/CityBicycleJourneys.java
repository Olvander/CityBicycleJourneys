package fi.tuni.olvander.citybicyclejourneys;

import fi.tuni.olvander.citybicyclejourneys.journeys.BicycleJourney;
import fi.tuni.olvander.citybicyclejourneys.journeys.BicycleJourneyRepository;
import fi.tuni.olvander.citybicyclejourneys.stations.Station;
import fi.tuni.olvander.citybicyclejourneys.stations.StationRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.tools.Csv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.ResultSet;
import java.util.Optional;

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


	public synchronized void importStationsFrom(String file) {

		try {
			ResultSet rs = new Csv().read(file, null, null);

			if (rs != null) {

				while (rs.next()) {

				}
			}
		} catch (Exception e) {
			logger.info("Could not read file, please check the file name");
		}
	}

	public synchronized void importJourneysFrom(String file) {

		try {
			ResultSet rs = new Csv().read(file, null, null);

			if (rs != null) {

				while (rs.next()) {

				}
			}
			rs.close();
		} catch (Exception e) {
			logger.info("Could not read file, please check the file name");
		}
	}

}
