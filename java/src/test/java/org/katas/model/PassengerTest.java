package org.katas.model;

import org.junit.jupiter.api.Test;
import org.katas.builder.PassengerBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PassengerTest {

    @Test
    void BasePriceWithAge0_ShouldReturn0e() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(0)
                .build();

        assertEquals(0, passenger.applyingAgeModifierOnPrice(basePrice));
    }
    @Test
    void BasePriceWithAge2_ShouldReturn() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(2)
                .build();

        assertEquals(9, passenger.applyingAgeModifierOnPrice(basePrice));
    }

    @Test
    void BasePriceWithAge15_ShouldReturn160e() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(15)
                .build();

        assertEquals(60, passenger.applyingAgeModifierOnPrice(basePrice));
    }

    @Test
    void BasePriceWithAge37_ShouldReturn120e() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(37)
                .build();

        assertEquals(120, passenger.applyingAgeModifierOnPrice(basePrice));
    }

    @Test
    void BasePriceWithAge70WithoutDiscountCard_ShouldReturn80e() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(70)
                .withOutDiscount()
                .build();

        assertEquals(80, passenger.applyingAgeModifierOnPrice(basePrice));
    }

    @Test
    void BasePriceWithAge70WitDiscountCard_ShouldReturn60e() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .withDiscount(DiscountCard.Senior)
                .age(71)
                .build();

        assertEquals(80, passenger.applyingAgeModifierOnPrice(basePrice));
    }
}
