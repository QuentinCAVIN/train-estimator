package org.katas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.katas.builder.PassengerBuilder;
import org.katas.builder.TrainTicketEstimatorTestBuilder;
import org.katas.builder.TripDetailsBuilder;
import org.katas.builder.TripRequestBuilder;
import org.katas.fake.FakeBasePriceRepository;
import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.model.TripRequest;
import org.katas.exceptions.InvalidTripInputException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
class TrainTicketEstimatorTest {

TrainTicketEstimatorTestBuilder trainEstimatorBuilder;
FakeBasePriceRepository fakeBasePriceRepository;
    @BeforeEach
    public void setUp() {
        fakeBasePriceRepository = new FakeBasePriceRepository();
        trainEstimatorBuilder = new TrainTicketEstimatorTestBuilder(fakeBasePriceRepository);
    }

    @Test
    void estimateTrainsWithoutNoPassenger_ShouldReturn0() {

        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .build())
                .withNoPassenger()
                .build();

        InvalidTripInputException exception = assertThrows(InvalidTripInputException.class, () -> {
            trainEstimatorBuilder.withTripRequest(tripRequest).build();
        });
        assertEquals("No passenger", exception.getMessage());
    }

    @Test
    void estimateTrainsWithNoDepartureCity_ShouldThrowException() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("")
                        .to("Paris")
                        .build())
                .withPassenger(new PassengerBuilder()
                        .build())
                .build();

        InvalidTripInputException exception = assertThrows(InvalidTripInputException.class, () -> {
            trainEstimatorBuilder.withTripRequest(tripRequest).build();
        });
        assertEquals("Start city is invalid", exception.getMessage());
    }

    @Test
    void estimateTrainsWithNoArrivalCity_ShouldThrowException() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("")
                        .build())
                .withPassenger(new PassengerBuilder()
                        .build())
                .build();

        InvalidTripInputException exception = assertThrows(InvalidTripInputException.class, () -> {
            trainEstimatorBuilder.withTripRequest(tripRequest).build();
        });
        assertEquals("Destination city is invalid", exception.getMessage());

    }

    // TODO Rajouter un test qui vérifie le TripDetail avec une Date null
    @Test
    void estimateTrainsWithInvalidDate_ShouldThrowException() {
        Date yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(yesterday)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .build())
                .build();

        InvalidTripInputException exception = assertThrows(InvalidTripInputException.class, () -> {
            trainEstimatorBuilder.withTripRequest(tripRequest).build();
        });
        assertEquals("Date is invalid", exception.getMessage());
    }

    @Test
    void estimateTrainsWithInvalidAge_ShouldThrowException() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(-1)
                        .build())
                .build();


        InvalidTripInputException exception = assertThrows(InvalidTripInputException.class, () -> {
            trainEstimatorBuilder.withTripRequest(tripRequest).build();
        });
        assertEquals("Age is invalid", exception.getMessage());
    }

    //TODO BUG Détécté sur les enfants à corriger une fois les tests en place
//    @Test
//    void estimateTrainsWithAge_ShouldThrowException() {
//        new TripRequestBuilder()
//                .withDetails(new TripDetailsBuilder()
//                        .from("Bordeaux")
//                        .to("Paris")
//                        .build())
//                .withPassenger(new PassengerBuilder()
//                        .age(0)
//                        .build())
//                .build();
//
//        TrainTicketEstimatorStub trainEstimator = new TrainTicketEstimatorStub();
//        assertEquals(0, trainEstimator.estimate());
//    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn31Days() {
        double basePrice = 100.00;
        double priceModified = 120;
        Date in31Days = new Date(System.currentTimeMillis() + 31L * 24 * 60 * 60 * 1000);

        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(30)
                        .build())
                .build();
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(100, estimator.applyingDateModifierOnPrice(tripRequest, priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateTomorrow() {
        double basePrice = 100.00;
        double priceModified = 120;
        Date tomorrow = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(tomorrow)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(30)
                        .build())
                .build();

        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(220, estimator.applyingDateModifierOnPrice(tripRequest, priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn5Days() {
        double basePrice = 100.00;
        double priceModified = 120;
        Date In5Days = new Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000);

        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(In5Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(30)
                        .build())
                .build();

        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(220, estimator.applyingDateModifierOnPrice(tripRequest, priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn10Days() {
        double basePrice = 100.00;
        double priceModified = 120;
        Date in10Days = new Date(System.currentTimeMillis() + 10 * 24 * 60 * 60 * 1000);

        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in10Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(30)
                        .build())
                .build();
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(140, estimator.applyingDateModifierOnPrice(tripRequest, priceModified, basePrice));
    }

    @Test
    void estimateTrainsWithCoupleAndDiscountCardCouple() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(36)
                        .withDiscount(DiscountCard.Couple)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(39)
                        .withDiscount(DiscountCard.Couple)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(400, estimator.estimate());
    }

    @Test
    void estimateTrainsWithCoupleMinorAndDiscountCardCouple() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(15)
                        .withDiscount(DiscountCard.Couple)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(15)
                        .withDiscount(DiscountCard.Couple)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(320, estimator.estimate());
    }

    @Test
    void estimateTrainsWithCoupleAndNoDiscount() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(36)
                        .withOutDiscount()
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(39)
                        .withOutDiscount()
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(440, estimator.estimate());
    }

    @Test
    void estimateTrainsWithNoCoupleAndDiscountCardHalfCouple() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(36)
                        .withDiscount(DiscountCard.HalfCouple)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();


        assertEquals(210, estimator.estimate());
    }
    @Test
    void estimateTrainsWithNoCoupleAndNoDiscount() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(36)
                        .withOutDiscount()
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(220, estimator.estimate());
    }
    @Test
    void estimateTrainsWithNoCoupleMinorAndDiscountCardHalfCouple() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(15)
                        .withDiscount(DiscountCard.HalfCouple)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(160, estimator.estimate());
    }
    @Test
    void estimateTrainsWithDiscountCardTrainStroke() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(36)
                        .withDiscount(DiscountCard.TrainStroke)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(1, estimator.estimate());
    }
}