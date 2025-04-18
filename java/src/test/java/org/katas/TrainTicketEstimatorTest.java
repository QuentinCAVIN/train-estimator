package org.katas;

import org.junit.jupiter.api.Test;
import org.katas.model.TripRequest;
import org.katas.model.exceptions.InvalidTripInputException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
class TrainTicketEstimatorTest {

    @Test
    void should_NotWork() {
        assertEquals(3, 1+2);
    }
  
    @Test
    void estimateTrainsWithoutNoPassager_ShouldReturn0() {
        TripRequest request = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("")
                        .to("")
                        .when(null)
                        .build())
                .withNoPassenger()
                .build();

        TrainTicketEstimator trainEstimator = new TrainTicketEstimator();
        assertEquals(0, trainEstimator.estimate(request));
    }

    @Test
    void estimateTrainsWithNoDepartureCity_ShouldThrowException() {
        TripRequest request = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("")
                        .to("Paris")
                        .build())
                .withPassenger(new PassengerBuilder()
                        .build())
                .build();

        TrainTicketEstimator trainEstimator = new TrainTicketEstimator();
        InvalidTripInputException exception = assertThrows(InvalidTripInputException.class, () -> {
            trainEstimator.estimate(request);
        });
        assertEquals("Start city is invalid", exception.getMessage());
    }

    @Test
    void estimateTrainsWithNoArrivalCity_ShouldThrowException() {
        TripRequest request = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("")
                        .build())
                .withPassenger(new PassengerBuilder()
                        .build())
                .build();

        TrainTicketEstimator trainEstimator = new TrainTicketEstimator();
        InvalidTripInputException exception = assertThrows(InvalidTripInputException.class, () -> {
            trainEstimator.estimate(request);
        });
        assertEquals("Destination city is invalid", exception.getMessage());

    }

    // TODO Rajouter un test qui vérifie le TripDetail avec une Date null
    @Test
    void estimateTrainsWithInvalidDate_ShouldThrowException() {
        Date yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        TripRequest request = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(yesterday)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .build())
                .build();

        TrainTicketEstimator trainEstimator = new TrainTicketEstimator();
        InvalidTripInputException exception = assertThrows(InvalidTripInputException.class, () -> {
            trainEstimator.estimate(request);
        });
        assertEquals("Date is invalid", exception.getMessage());
    }
}