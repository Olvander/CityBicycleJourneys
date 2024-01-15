package fi.tuni.olvander.citybicyclejourneys.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * A class for giving advice regarding Response Entity Exceptions within<br/>
 * Controller classes. All these methods include header information which<br/>
 * allow access for all URL origins. The methods throw exceptions in<br/>
 * Controller classes when Entities cannot be found.
 *
 * @author  Olli Pertovaara
 * @version 2023.12.13
 * @since   1.21
 */
@ControllerAdvice
public class ResponseEntityExceptionHandler {

    /**
     * An exception handler method for BicycleJourneys whose ids are in a<br/>
     * proper format but cannot be found in the database.
     *
     * @param  exception An exception stating a BicycleJourney was not found.
     * @return           A Response Entity with exception information and a<br/>
     *                   NOT_FOUND HttpStatus.
     */
    @ExceptionHandler(BicycleJourneyNotFoundException.class)
    public ResponseEntity<ExceptionInfo> returnBicycleJourneyException(
            BicycleJourneyNotFoundException exception) {

        ExceptionInfo exceptionInfo = new ExceptionInfo(
                "Cannot find BicycleJourney with an id " + exception.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.setAccessControlAllowOrigin("*");

        return new ResponseEntity<>(exceptionInfo, headers,
                HttpStatus.NOT_FOUND);
    }

    /**
     * An exception handler method for Stations whose id are in a proper<br/>
     * format but cannot be found in the database.
     *
     * @param  exception An exception stating that a Station cannot be found.
     * @return           A Response Entity with exception information and a<br/>
     *                   NOT_FOUND HttpStatus.
     */
    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity<ExceptionInfo> returnStationException(
            StationNotFoundException exception) {

        ExceptionInfo exceptionInfo = new ExceptionInfo(
                "Cannot find Station with an id " + exception.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.setAccessControlAllowOrigin("*");

        return new ResponseEntity<>(exceptionInfo, headers,
                HttpStatus.NOT_FOUND);
    }

    /**
     * An exception handler method for ids that are in a wrong format.
     *
     * @param  exception An exception stating that an id is not a number
     * @return           A Response Entity with exception information and a<br/>
     *                   NOT_FOUND HttpStatus.
     */
    @ExceptionHandler(IdNotANumberException.class)
    public ResponseEntity<ExceptionInfo> returnIdException(
            IdNotANumberException exception) {

        ExceptionInfo exceptionInfo = new ExceptionInfo(
                "Error finding id " + exception.getId()
                        + " which is not a number");
        HttpHeaders headers = new HttpHeaders();
        headers.setAccessControlAllowOrigin("*");

        return new ResponseEntity<>(exceptionInfo, headers,
                HttpStatus.NOT_FOUND);
    }
}
