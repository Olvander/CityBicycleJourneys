package fi.tuni.olvander.citybicyclejourneys.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResponseEntityExceptionHandler {

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
}
