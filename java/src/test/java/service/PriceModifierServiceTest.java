package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.katas.builder.PassengerBuilder;
import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.service.PriceModifierService;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceModifierServiceTest {

    PriceModifierService priceModifier;

    @BeforeEach
    public void setUp() {
        priceModifier = new PriceModifierService();
    }

    /******************************************************
     * Test de l'ajustement des prix en fonction de l'age *
     ******************************************************/
    @Test
    void givenAge0_whenApplyingAgeModifier_thenPriceShouldBeZero() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(0)
                .build();

        assertEquals(0, priceModifier.applyingAgeModifierOnPrice(passenger, basePrice));
    }

    @Test
    void givenAge1_whenApplyingAgeModifier_thenPriceShouldBeAdjustedForInfant() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(1)
                .build();

        assertEquals(9, priceModifier.applyingAgeModifierOnPrice(passenger, basePrice));
    }

    @Test
    void givenAge2_whenApplyingAgeModifier_thenPriceShouldBeAdjustedForToddlers() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(2)
                .build();

        assertEquals(9, priceModifier.applyingAgeModifierOnPrice(passenger, basePrice));
    }

    @Test
    void givenAge3_whenApplyingAgeModifier_thenPriceShouldBeAdjustedForToddlers() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(3)
                .build();

        assertEquals(9, priceModifier.applyingAgeModifierOnPrice(passenger, basePrice));
    }

    @Test
    void givenAge15_whenApplyingAgeModifier_thenPriceShouldBeAdjustedForMinors() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(15)
                .build();

        assertEquals(60, priceModifier.applyingAgeModifierOnPrice(passenger, basePrice));
    }

    @Test
    void givenAge37_whenApplyingAgeModifier_thenPriceShouldBeAdjustedForAdults() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(37)
                .build();

        assertEquals(120, priceModifier.applyingAgeModifierOnPrice(passenger, basePrice));
    }

    @Test
    void givenAge70_whenApplyingAgeModifier_thenPriceShouldBeAdjusted() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(69)
                .withOutDiscount()
                .build();

        assertEquals(120, priceModifier.applyingAgeModifierOnPrice(passenger, basePrice));
    }

    @Test
    void givenAge71_whenApplyingAgeModifier_thenPriceShouldBeAdjusted() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .withDiscount(DiscountCard.SENIOR)
                .age(71)
                .build();

        assertEquals(80, priceModifier.applyingAgeModifierOnPrice(passenger, basePrice));
    }


    /******************************************************************
     * Test de l'ajustement des prix en fonction de la date de départ *
     ******************************************************************/
    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn31Days() {
        double basePrice = 100.00;
        double priceModified = 120;
        Date in31Days = new Date(System.currentTimeMillis() + 31L * 24 * 60 * 60 * 1000);

        assertEquals(100, priceModifier.applyingDateModifierOnPrice(in31Days, priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateTomorrow() {
        double basePrice = 100.00;
        double priceModified = 120;
        Date tomorrow = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);

        assertEquals(220, priceModifier.applyingDateModifierOnPrice(tomorrow, priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn6Days() {
        double basePrice = 100.00;
        double priceModified = 100;
        Date in5Days = new Date(System.currentTimeMillis() + 6 * 24 * 60 * 60 * 1000);

        assertEquals(128, priceModifier.applyingDateModifierOnPrice(in5Days, priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn5Days() {
        double basePrice = 100.00;
        double priceModified = 100;
        Date in5Days = new Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000);

        assertEquals(200, priceModifier.applyingDateModifierOnPrice(in5Days, priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn10Days() {
        double basePrice = 100.00;
        double priceModified = 100;
        Date in10Days = new Date(System.currentTimeMillis() + 10 * 24 * 60 * 60 * 1000);

        assertEquals(120, priceModifier.applyingDateModifierOnPrice(in10Days, priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn6hours() {
        double basePrice = 100.00;
        double priceModified = 100;
        Date in6Hours = new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000) - (18 * 60 * 60 * 1000));

        assertEquals(80, priceModifier.applyingDateModifierOnPrice(in6Hours, priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn7hours() {
        double basePrice = 100.00;
        double priceModified = 100;
        Date in10Days = new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000) - (17 * 60 * 60 * 1000));

        assertEquals(200, priceModifier.applyingDateModifierOnPrice(in10Days, priceModified, basePrice));
    }

    @Test
    void UpdateBasePriceAccordingToDate_departureDateIn5hours() {
        double basePrice = 100.00;
        double priceModified = 100;
        Date in5hours = new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000) - (19 * 60 * 60 * 1000));

        assertEquals(80, priceModifier.applyingDateModifierOnPrice(in5hours, priceModified, basePrice));
    }
}
