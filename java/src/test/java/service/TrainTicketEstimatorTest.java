package service;

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
        // Utilise l'ancienne signature de la méthode estimate()
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

    /*************************************************
     * Test de validation des données de TripDetails *
     *************************************************/
    @Test
    void shouldThrowException_WhenNoPassengerProvided() {

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

    @Test
    void shouldThrowException_WhenDepartureCityIsEmpty() {
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
    void shouldThrowException_WhenArrivalCityIsEmpty() {
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


    @Test
    void shouldThrowException_WhenDateIsNull() {
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
    void shouldThrowException_WhenDateIsInPast() {
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
    void shouldThrowException_WhenPassengerAgeIsNegative() {
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


    /************************************************************
     * Bug à corriger par la prochaine équipe de développement, *
     * on se casse élever des chèvres dans le Larzac.           *
     ************************************************************/
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


    /*********************************************************************
     * Test de l'ajustement des prix en fonction des cartes de réduction *
     *********************************************************************/
    @Test
    void shouldApplyCoupleDiscount_whenTwoAdultsWithCoupleCard() {
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
    void shoulNotApplyCoupleDiscount_whenTwoMinorsWithCoupleCard() {
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
    void shouldNotApplyDiscount_whenTwoAdultsWithoutCard() {
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
    void shouldApplyHalfCoupleDiscount_whenOneAdultWithHalfCoupleCard() {
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
    void shouldNotApplyDiscount_whenOneAdultWithoutCard() {
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
    void shouldNotApplyHalfCoupleDiscount_whenMinorWithHalfCoupleCard() {
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
    void shouldApplyTrainStrokeDiscount_whenPassengerHasTrainStrokeCard() {
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
    void shouldApplyOnlyTrainStrokeDiscountt_whenTwoPassengersHaveRespectiveCards() {
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
    void shouldApplyCoupleAndSeniorDiscount_whenOneSeniorAndOneCoupleCard() {
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
    void shouldApplyDoubleSeniorCoupleDiscount_whenTwoSeniorsWithBothCards() {
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
    void shouldNotApplyCoupleDiscount_whenThreePassengersPresent() {
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

    /// /
    @Test
    void shouldApplyTrainStrokeDiscountOnly_whenPassengerHasHalfCoupleAndTrainStrokeDiscountsButIsAlone() {
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
    void shouldNoApplyDiscount_whenTwoPassengersHaveHalfCoupleDiscount() {
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
    void shouldApplyFamilyDiscount_whenThreePassengersShareSameLastNameAndOneHasFamilyCard() {
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
    void shouldIgnoreOtherDiscounts_whenFamilyDiscountIsApplied() {
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
    void shouldNotApplyFamilyDiscount_whenPassengersHaveDifferentLastNames() {
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

    //TODO Vérifier avec l'équipe de dev du Larzac s'il s'agit d'un comportement souhaité dans l'avenir
    @Test
    void ParentsShouldNotLetTheirKidsTravelALoneButItsSoCheap() {
        Date now = new Date();
        Date inFiveMinutes = new Date(now.getTime() + (5 * 60 * 1000));
        TripRequest tripRequest = new TripRequestBuilder()
                .withDetails(new TripDetailsBuilder()
                        .from("Bordeaux")
                        .to("Paris")
                        .when(in31Days)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(0)
                        .lastName("Hemery")
                        .withDiscount(DiscountCard.FAMILY)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(1)
                        .lastName("Hemery")
                        .withDiscount(DiscountCard.TRAINSTROKE)
                        .withDiscount(DiscountCard.SENIOR)
                        .withDiscount(DiscountCard.FAMILY)
                        .withDiscount(DiscountCard.HALF_COUPLE)
                        .build())
                .withPassenger(new PassengerBuilder()
                        .age(2)
                        .lastName("Hemery")
                        .withDiscount(DiscountCard.FAMILY)
                        .build())
                .build();

        fakeBasePriceRepository.setBasePrice(10000);
        TrainTicketEstimator estimator = trainEstimatorBuilder.withTripRequest(tripRequest).build();

        assertEquals(-132, estimator.estimate());
    }
}