package org.katas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.model.TripRequest;
import org.katas.model.exceptions.InvalidTripInputException;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
class TrainTicketEstimatorTest {

TrainTicketEstimator trainEstimator;
FakeBasePriceRepository fakeBasePriceRepository;
    @BeforeEach
    public void setUp() {
        fakeBasePriceRepository = new FakeBasePriceRepository();
        trainEstimator = new TrainTicketEstimator(fakeBasePriceRepository);
    }

    @Test
    void estimateTrainsWithoutNoPassenger_ShouldReturn0() {
        TripRequest request = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("")
                        .to("")
                        .when(null)
                        .build())
                .withNoPassenger()
                .build();

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

        InvalidTripInputException exception = assertThrows(InvalidTripInputException.class, () -> {
            trainEstimator.estimate(request);
        });
        assertEquals("Date is invalid", exception.getMessage());
    }

    @Test
    void estimateTrainsWithInvalidAge_ShouldThrowException() {
        TripRequest request = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(-1)
                        .build())
                .build();

        InvalidTripInputException exception = assertThrows(InvalidTripInputException.class, () -> {
            trainEstimator.estimate(request);
        });
        assertEquals("Age is invalid", exception.getMessage());
    }

    //TODO BUG Détécté sur les enfants à corriger une fois les tests en place
//    @Test
//    void estimateTrainsWithAge_ShouldThrowException() {
//        TripRequest request = new TripRequestBuilder()
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
//        assertEquals(0, trainEstimator.estimate(request));
//    }
    @Test
    void BasePriceWithAge0_ShouldReturn0e() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(0)
                .build();

        assertEquals(0, trainEstimator.getBasePriceBasedOnAge(passenger, basePrice));
    }
    @Test
    void BasePriceWithAge2_ShouldReturn() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(2)
                .build();

        assertEquals(9, trainEstimator.getBasePriceBasedOnAge(passenger, basePrice));
    }

    @Test
    void BasePriceWithAge15_ShouldReturn160e() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(15)
                .build();

        assertEquals(60, trainEstimator.getBasePriceBasedOnAge(passenger, basePrice));
    }

    @Test
    void BasePriceWithAge37_ShouldReturn120e() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(37)
                .build();

        assertEquals(120, trainEstimator.getBasePriceBasedOnAge(passenger, basePrice));
    }

    @Test
    void BasePriceWithAge70WithoutDiscountCard_ShouldReturn80e() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(70)
                .withOutDiscount()
                .build();

        assertEquals(80, trainEstimator.getBasePriceBasedOnAge(passenger, basePrice));
    }

    @Test
    void BasePriceWithAge70WitDiscountCard_ShouldReturn60e() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .withDiscount(DiscountCard.Senior)
                .age(71)
                .build();

        assertEquals(60, trainEstimator.getBasePriceBasedOnAge(passenger, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn31Days() {
        double basePrice = 100.00;
        double priceModified = 120;
        Date in31Days = new Date(System.currentTimeMillis() + 31L * 24 * 60 * 60 * 1000);

        TripRequest request = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(30)
                        .build())
                .build();

        assertEquals(100, trainEstimator.changesBasePriceDependingOnDate(request, priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateTomorrow() {
        double basePrice = 100.00;
        double priceModified = 120;
        Date tomorrow = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

        TripRequest request = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(tomorrow)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(30)
                        .build())
                .build();

        assertEquals(220, trainEstimator.changesBasePriceDependingOnDate(request, priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn5Days() {
        double basePrice = 100.00;
        double priceModified = 120;
        Date In5Days = new Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000);

        TripRequest request = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(In5Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(30)
                        .build())
                .build();

        assertEquals(220, trainEstimator.changesBasePriceDependingOnDate(request, priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn10Days() {
        double basePrice = 100.00;
        double priceModified = 120;
        Date in10Days = new Date(System.currentTimeMillis() + 10 * 24 * 60 * 60 * 1000);

        TripRequest request = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in10Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(30)
                        .build())
                .build();

        assertEquals(140, trainEstimator.changesBasePriceDependingOnDate(request, priceModified, basePrice));
    }

    @Test
    void estimateTrainsWithCoupleAndDiscountCardCouple() {
        TripRequest request = new TripRequestBuilder()
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

        assertEquals(400, trainEstimator.estimate(request));
    }

    @Test
    void estimateTrainsWithCoupleMinorAndDiscountCardCouple() {
        TripRequest request = new TripRequestBuilder()
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

        assertEquals(320, trainEstimator.estimate(request));
    }

    @Test
    void estimateTrainsWithCoupleAndNoDiscount() {
        TripRequest request = new TripRequestBuilder()
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

        assertEquals(440, trainEstimator.estimate(request));
    }

    @Test
    void estimateTrainsWithNoCoupleAndDiscountCardHalfCouple() {
        TripRequest request = new TripRequestBuilder()
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

        assertEquals(210, trainEstimator.estimate(request));
    }
    @Test
    void estimateTrainsWithNoCoupleAndNoDiscount() {
        TripRequest request = new TripRequestBuilder()
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

        assertEquals(220, trainEstimator.estimate(request));
    }
    @Test
    void estimateTrainsWithNoCoupleMinorAndDiscountCardHalfCouple() {
        TripRequest request = new TripRequestBuilder()
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

        assertEquals(160, trainEstimator.estimate(request));
    }
    @Test
    void estimateTrainsWithDiscountCardTrainStroke() {
        TripRequest request = new TripRequestBuilder()
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

        assertEquals(1, trainEstimator.estimate(request));
    }
}