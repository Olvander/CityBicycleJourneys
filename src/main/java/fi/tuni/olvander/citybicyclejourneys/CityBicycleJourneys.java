package fi.tuni.olvander.citybicyclejourneys;

import fi.tuni.olvander.citybicyclejourneys.journeys.BicycleJourney;
import fi.tuni.olvander.citybicyclejourneys.journeys.BicycleJourneyRepository;
import fi.tuni.olvander.citybicyclejourneys.stations.Station;
import fi.tuni.olvander.citybicyclejourneys.stations.StationRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.tools.Csv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@SpringBootApplication
public class CityBicycleJourneys implements CommandLineRunner {

    @Autowired
	StationRepository stationDb;

	@Autowired
	BicycleJourneyRepository bicycleJourneyDb;

	Log logger = LogFactory.getLog(CityBicycleJourneys.class);

	public static void main(String[] args) {
		SpringApplication.run(CityBicycleJourneys.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		String stationsFile = "./csv/"
				+ "Helsingin_ja_Espoon_kaupunkipyöräasemat_avoin.csv";
		
		String rs05File = "./csv/2021-05.csv";
		String rs06File = "./csv/2021-06.csv";
		String rs07File = "./csv/2021-07.csv";

		if (stationDb.count() == 0) {
			importStationsFrom(stationsFile);
			logger.info("");
			logger.info("Stations imported to db");
		}

		if (bicycleJourneyDb.count() == 0) {
			logger.info("");
			logger.info("Please wait about 3 - 10 minutes until all");
			logger.info("3 bicycle journey datasets have been imported to db");
			logger.info("");

			importJourneysFrom(rs05File);
			logger.info("The first dataset (2021-05) has been imported to db");
			importJourneysFrom(rs06File);
			logger.info("The second dataset (2021-06) has been imported to db");
			importJourneysFrom(rs07File);
			logger.info("The third dataset (2021-07) has been imported to db");
			logger.info("");
			logger.info("All Bicycle Journey datasets have been imported!");
		}

		showStationsRelatedCommands();
		showJourneysRelatedCommands();
		showHowToOpenTheApp();
	}

	public void showHowToOpenTheApp() {
		logger.info("");
		logger.info("To open the app, type http://localhost:8080 in browser");
		logger.info("");
	}

	public void showStationsRelatedCommands() {
		logger.info("");
		logger.info("To get all stations in command prompt, type:");
		logger.info("curl -i http://localhost:8080/api/stations/");
		logger.info("To get one station, in this case with id 1, type:");
		logger.info("curl -i http://localhost:8080/api/stations/1/");
	}

	public void showJourneysRelatedCommands() {
		logger.info("To get one journey with id 1, type in command prompt:");
		logger.info("curl -i http://localhost:8080/api/journeys/1/");
	}

	public synchronized void importStationsFrom(String file) {

		try {
			ResultSet rs = new Csv().read(file, null, null);

			if (rs != null) {

				while (rs.next()) {
					Optional<Station> station = getStationData(rs);

					station.ifPresent(this::importBicycleStation);
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
					Optional<BicycleJourney> journey = getBicycleJourneyData(rs);

					journey.ifPresent(this::importBicycleJourney);
				}
			}
			rs.close();
		} catch (Exception e) {
			logger.info("Could not read file, please check the file name");
		}
	}



	public synchronized Optional<BicycleJourney> getBicycleJourneyData(
			ResultSet rs) throws Exception {

		Optional<BicycleJourney> bicycleJourney = Optional.empty();

		try {
			double distance = rs.getDouble(7);
			int duration = rs.getInt(8);

			if (distance >= 10 && duration >= 10) {

				String departureDateTime = rs.getString(1);
				LocalDateTime departureDate = getLocalDateTime(
						departureDateTime);
				String returnDateTime = rs.getString(2);
				LocalDateTime returnDate = getLocalDateTime(returnDateTime);
				String departureStationId = rs.getString(3);
				String returnStationId = rs.getString(5);
				BicycleJourney journey = new BicycleJourney(departureDate,
						returnDate, departureStationId, returnStationId,
						distance, duration);
				bicycleJourney = Optional.of(journey);
			}
		} catch (Exception e) {
			logger.error("Could not get all values");
			logger.error("Please check column numbers in the CSV file(s)");
		}

		return bicycleJourney;
	}

	public synchronized Optional<Station> getStationData(ResultSet rs) throws
			Exception {

		Optional<Station> stationData = Optional.empty();

		try {
			String stationID = rs.getString(2);
			String stationName = rs.getString(3);

			if (stationName.contains(",")) {
				stationName = stationName.substring(0,
						stationName.indexOf(","));
			}

			String stationAddress = rs.getString(6);
			double stationX = rs.getDouble(12);
			double stationY = rs.getDouble(13);

			Station bicycleStation = new Station(stationID, stationName,
					stationAddress, stationX, stationY);
			stationData = Optional.of(bicycleStation);

		} catch (Exception e) {
			logger.error("Could not get data from CSV file");
			logger.error("Please check the column numbers and the CSV data");
		}

		return stationData;
	}

	public synchronized void importBicycleJourney(BicycleJourney journey) {
		this.bicycleJourneyDb.save(journey);
	}

	public synchronized void importBicycleStation(Station station) {
		this.stationDb.save(station);
	}

	public synchronized LocalDateTime getLocalDateTime(String dateTime) {
		String date = dateTime.substring(0, 10);
		String time = "00:00";

		if (dateTime.contains("T")) {
			time = dateTime.substring(11);
		}
		LocalDateTime dt = LocalDateTime.of(
				LocalDate.parse(date),
				LocalTime.parse(time));

		return dt;
	}
}
