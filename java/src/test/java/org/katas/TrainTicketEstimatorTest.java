package org.katas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.katas.builder.PassengerBuilder;
import org.katas.builder.FakeTrainTicketEstimatorBuilder;
import org.katas.builder.TripDetailsBuilder;
import org.katas.builder.TripRequestBuilder;
import org.katas.exceptions.ApiException;
import org.katas.fake.FakeBasePriceRepository;
import org.katas.model.DiscountCard;
import org.katas.model.TripRequest;
import org.katas.exceptions.InvalidTripInputException;
import org.katas.service.PriceModifierService;
import org.katas.service.TrainTicketEstimator;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
class TrainTicketEstimatorTest {

FakeTrainTicketEstimatorBuilder trainEstimatorBuilder;
FakeBasePriceRepository fakeBasePriceRepository;
PriceModifierService priceModifier;

    @BeforeEach
    public void setUp() {
        fakeBasePriceRepository = new FakeBasePriceRepository();
        trainEstimatorBuilder = new FakeTrainTicketEstimatorBuilder();
        priceModifier = new PriceModifierService();
    }

    @Test
    void backwardsCompatibilityTest() {
        TrainTicketEstimator trainTicketEstimator = new TrainTicketEstimator();
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .build())
                .withPassenger(new PassengerBuilder()
                        .build())
                .build();

        assertThrows(ApiException.class, () -> {
            trainTicketEstimator.estimate(tripRequest);
        });
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

    Date in31Days = new Date(System.currentTimeMillis() + 31L * 24 * 60 * 60 * 1000);


    //Ci-dessous test des throws
    ////////////////
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

    // TODO Prévoir la date null dans le code de prod?
    @Disabled
    @Test
    void estimateTrainsWithNullDate_ShouldThrowException() {

        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(null)
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

    //////////

    //TODO BUG BUG BUG : -20€ Quand l'utilisateur à moins de 1 an / 100 € quand le billet est prix en retard
    // Rajouter quelques billets pour corriger le bug
    @Disabled
    @Test
    void bug() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(0)
                        .withOutDiscount()
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(0, estimator.estimate());
    }


    // Ci-dessous déplacer dans un package de test unitaire de PriceModifierService
    /// ////////
    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn31Days() {
        double basePrice = 100.00;
        double priceModified = 120;
        Date in31Days = new Date(System.currentTimeMillis() + 31L * 24 * 60 * 60 * 1000);

        assertEquals(100, priceModifier.applyingDateModifierOnPrice(in31Days,priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateTomorrow() {
        double basePrice = 100.00;
        double priceModified = 120;
        Date tomorrow = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

        assertEquals(220, priceModifier.applyingDateModifierOnPrice(tomorrow,priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn6Days() {
        double basePrice = 100.00;
        double priceModified = 100;
        Date in5Days = new Date(System.currentTimeMillis() + 6 * 24 * 60 * 60 * 1000);

        assertEquals(128, priceModifier.applyingDateModifierOnPrice(in5Days,priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn5Days() {
        double basePrice = 100.00;
        double priceModified = 100;
        Date in5Days = new Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000);

        assertEquals(200, priceModifier.applyingDateModifierOnPrice(in5Days,priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn10Days() {
        double basePrice = 100.00;
        double priceModified = 100;
        Date in10Days = new Date(System.currentTimeMillis() + 10 * 24 * 60 * 60 * 1000);

        assertEquals(120, priceModifier.applyingDateModifierOnPrice(in10Days,priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn6hours() {
        double basePrice = 100.00;
        double priceModified = 100;
        Date in10Days = new Date(System.currentTimeMillis() + ( 24 * 60 * 60 * 1000) - (18 * 60 * 60 * 1000));

        assertEquals(200, priceModifier.applyingDateModifierOnPrice(in10Days,priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn7hours() {
        double basePrice = 100.00;
        double priceModified = 100;
        Date in10Days = new Date(System.currentTimeMillis() + ( 24 * 60 * 60 * 1000) - (17 * 60 * 60 * 1000));

        assertEquals(200, priceModifier.applyingDateModifierOnPrice(in10Days,priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn5hours() {
        double basePrice = 100.00;
        double priceModified = 100;
        Date in10Days = new Date(System.currentTimeMillis() + ( 24 * 60 * 60 * 1000) - (19 * 60 * 60 * 1000));

        assertEquals(80, priceModifier.applyingDateModifierOnPrice(in10Days,priceModified, basePrice));
    }

    ////////////

    //////// DISCOUNT TEST CI DESSOUS
    ///
    ///
    ///
    @Test
    void estimateTrainsWithCoupleAndDiscountCardCouple() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(36)
                        .withDiscount(DiscountCard.COUPLE)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(39)
                        .withDiscount(DiscountCard.COUPLE)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(160, estimator.estimate());
    }

    @Test
    void estimateTrainsWithCoupleMinorAndDiscountCardCouple() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(15)
                        .withDiscount(DiscountCard.COUPLE)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(15)
                        .withDiscount(DiscountCard.COUPLE)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(80, estimator.estimate());
    }

    @Test
    void estimateTrainsWithCoupleAndNoDiscount() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
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

        assertEquals(200, estimator.estimate());
    }

    @Test
    void estimateTrainsWithNoCoupleAndDiscountCardHalfCouple() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(36)
                        .withDiscount(DiscountCard.HALF_COUPLE)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();


        assertEquals(90, estimator.estimate());
    }
    @Test
    void estimateTrainsWithNoCoupleAndNoDiscount() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(36)
                        .withOutDiscount()
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(100, estimator.estimate());
    }
    @Test
    void estimateTrainsWithNoCoupleMinorAndDiscountCardHalfCouple() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(15)
                        .withDiscount(DiscountCard.HALF_COUPLE)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(40, estimator.estimate());
    }
    @Test
    void estimateTrainsWithDiscountCardTrainStroke() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(36)
                        .withDiscount(DiscountCard.TRAINSTROKE)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(1, estimator.estimate());
    }

    @Test
    void estimateTrainsWithDiscountCardTrainStrokeAndCouple() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(36)
                        .withDiscount(DiscountCard.TRAINSTROKE)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(36)
                        .withDiscount(DiscountCard.COUPLE)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(101, estimator.estimate());
    }

    @Test
    void estimateTrainsWithDiscountCard1SeniorAndCouple() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(65)
                        .withDiscount(DiscountCard.COUPLE)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(80)
                        .withDiscount(DiscountCard.SENIOR)
                        .withDiscount(DiscountCard.COUPLE)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(100, estimator.estimate());
    }

    @Test
    void estimateTrainsWithDiscountCard2SeniorAndCouple() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(81)
                        .withDiscount(DiscountCard.SENIOR)
                        .withDiscount(DiscountCard.COUPLE)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(80)
                        .withDiscount(DiscountCard.SENIOR)
                        .withDiscount(DiscountCard.COUPLE)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(40, estimator.estimate());
    }

    @Test
    void estimateTrainsWithDiscountCardCoupleAnd3Passengers() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(50)
                        .withDiscount(DiscountCard.COUPLE)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(30)
                        .withDiscount(DiscountCard.COUPLE)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(18)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(300, estimator.estimate());
    }

    @Test
    void estimateTrainsWithNoCoupleAndDiscountCardHalfCoupleAndTrainStoke() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(36)
                        .withDiscount(DiscountCard.HALF_COUPLE)
                        .withDiscount(DiscountCard.TRAINSTROKE)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(1, estimator.estimate());
    }

    @Test
    void estimateTrainsCoupleWithDiscountCardHalfCoupleAndHalfCouple() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(36)
                        .withDiscount(DiscountCard.HALF_COUPLE)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(36)
                        .withDiscount(DiscountCard.HALF_COUPLE)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(200, estimator.estimate());

    }

    @Test
    void estimateTrainsWithDiscountCardFamilyAnd3Passengers() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(72)
                        .lastName("Allaoui")
                        .withDiscount(DiscountCard.FAMILY)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(52)
                        .lastName("Allaoui")
                        .withOutDiscount()
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(70)
                        .lastName("Allaoui")
                        .withOutDiscount()
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(130, estimator.estimate());
    }
    @Test
    void discountCardReplacesOthersDiscounts() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(72)
                        .lastName("Allaoui")
                        .withDiscount(DiscountCard.FAMILY)
                        .withDiscount(DiscountCard.SENIOR)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(52)
                        .lastName("Allaoui")
                        .withDiscount(DiscountCard.TRAINSTROKE)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(70)
                        .lastName("Allaoui")
                        .withDiscount(DiscountCard.SENIOR)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(130, estimator.estimate());
    }

    @Test
    void discountCardFamillyDontWorkWIthDifferentNames() {
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(50)
                        .lastName("Allaoui")
                        .withDiscount(DiscountCard.FAMILY)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(52)
                        .lastName("Cavin")
                        .withOutDiscount()
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(70)
                        .lastName("Allaoui")
                        .withOutDiscount()
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(100);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(260, estimator.estimate());
    }
}