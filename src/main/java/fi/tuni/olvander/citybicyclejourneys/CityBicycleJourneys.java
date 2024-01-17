package fi.tuni.olvander.citybicyclejourneys;

import fi.tuni.olvander.citybicyclejourneys.journeys.BicycleJourney;
import fi.tuni.olvander.citybicyclejourneys.journeys.BicycleJourneyRepository;
import fi.tuni.olvander.citybicyclejourneys.stations.Station;
import fi.tuni.olvander.citybicyclejourneys.stations.StationRepository;
import jakarta.transaction.Transactional;
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

/**
 * The main  class for the City Bicycle Journeys App.<br/><br/>
 *
 * For importing City Bicycle Stations and Bicycle Journeys into the H2<br/>
 * database as well as for showing commands for opening the app in the<br/>
 * browser and commands for REST requests.<br/><br/>
 *
 * Implements the CommandLineRunner interface.
 *
 * @author  Olli Pertovaara
 * @version 2023.12.13
 * @since   1.21
 */
@SpringBootApplication
public class CityBicycleJourneys implements CommandLineRunner {


	/**
	 * A City Bicycle Station repository instance.
	 */
	@Autowired
	StationRepository stationDb;

	/**
	 * A City Bicycle Journey repository instance.
	 */
	@Autowired
	BicycleJourneyRepository bicycleJourneyDb;

	/**
	 * An instance of Log for logging purposes.
	 */
	Log logger = LogFactory.getLog(CityBicycleJourneys.class);

	/**
	 * The main method for the CityBicycleJourneys class.
	 *
	 * @param args Command line arguments, not used
	 */
	public static void main(String[] args) {
		SpringApplication.run(CityBicycleJourneys.class, args);
	}

	/**
	 * The run method for implementing the CommandLineRunner interface.
	 *
	 * @param args       Arguments for the run method, not used
	 * @throws Exception The run method may throw an Exception
	 */
	@Override
	@Transactional
	public synchronized void run(String... args) throws Exception {

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

	/**
	 * For showing how to open the app in the browser as an info message.
	 */
	public void showHowToOpenTheApp() {
		logger.info("");
		logger.info("To open the app, type http://localhost:8080 in browser");
		logger.info("");
	}

	/**
	 * For showing Stations related commands in the console as info messages.
	 */
	public void showStationsRelatedCommands() {
		logger.info("");
		logger.info("To get all stations in command prompt, type:");
		logger.info("curl -i http://localhost:8080/api/stations/");
		logger.info("To get one station, in this case with id 1, type:");
		logger.info("curl -i http://localhost:8080/api/stations/1/");
	}

	/**
	 * For showing Journeys related commands in the console as info messages.
	 */
	public void showJourneysRelatedCommands() {
		logger.info("To get one journey with id 1, type in command prompt:");
		logger.info("curl -i http://localhost:8080/api/journeys/1/");
	}

	/**
	 * For importing City Bicycle Stations from a CSV file.
	 *
	 * @param file The CSV file name of type String
	 */
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

	/**
	 * For importing City Bicycle Journeys from a CSV file.
	 *
	 * @param file The CSV file name of type String
	 */
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

	/**
	 * For getting Bicycle Journey data from the Bicycle Journey CSV files<br/>
	 * for saving to the database. Excludes the Bicycle Journeys having a<br/>
	 * distance or a duration less than 10 meters or seconds dynamically.<br/>
	 *
	 * @param rs         A ResultSet from the Csv().read() method
	 * @return           An Optional Bicycle Journey object
	 * @throws Exception Throw this if the ResultSet data cannot be parsed
	 */
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

	/**
	 * Gets Station data from the Station CSV file for saving to database<br/>
	 * Removes possible commas and the text after the commas in Station<br/>
	 * names for a cleaner look.
	 *
	 * @param rs         A ResultSet from the Csv().read() method
	 * @return           An Optional Station object
	 * @throws Exception Throws this if the ResultSet data cannot be parsed
	 */
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

	/**
	 * Imports a City Bicycle Journey to the Bicycle Journey database.
	 *
	 * @param journey The Bicycle Journey to be imported
	 */
	public synchronized void importBicycleJourney(BicycleJourney journey) {
		this.bicycleJourneyDb.save(journey);
	}


	/**
	 * A method for importing a City Bicycle Station to the Station database.
	 *
	 * @param station The Station to be imported
	 */
	public synchronized void importBicycleStation(Station station) {
		this.stationDb.save(station);
	}

	/**
	 * A helper method. Returns a LocalDateTime object from a given String.<br/>
	 * The LocalDateTime object is set a default value "00:00" if the<br/>
	 * dateTime String does not include a time.
	 *
	 * @param dateTime A dateTime String having a date
	 * @return         A LocalDateTime object
	 */
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
