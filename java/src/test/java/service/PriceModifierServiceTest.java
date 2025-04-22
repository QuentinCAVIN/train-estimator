package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.katas.builder.PassengerBuilder;
import org.katas.model.DiscountCard;
import org.katas.model.Passenger;
import org.katas.service.PriceModifierService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceModifierServiceTest {

    PriceModifierService priceModifier;

    @BeforeEach
    public void setUp() {
        priceModifier = new PriceModifierService();
    }

    @Test
    void BasePriceWithAge0_ShouldReturn0e() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(0)
                .build();

        assertEquals(0, priceModifier.applyingAgeModifierOnPrice(passenger, basePrice));
    }
    @Test
    void BasePriceWithAge2_ShouldReturn() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(2)
                .build();

        assertEquals(9, priceModifier.applyingAgeModifierOnPrice(passenger, basePrice));
    }

    @Test
    void BasePriceWithAge15_ShouldReturn160e() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(15)
                .build();

        assertEquals(60, priceModifier.applyingAgeModifierOnPrice(passenger, basePrice));
    }

    @Test
    void BasePriceWithAge37_ShouldReturn120e() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(37)
                .build();

        assertEquals(120, priceModifier.applyingAgeModifierOnPrice(passenger, basePrice));
    }

    @Test
    void BasePriceWithAge70WithoutDiscountCard_ShouldReturn80e() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .age(70)
                .withOutDiscount()
                .build();

        assertEquals(80, priceModifier.applyingAgeModifierOnPrice(passenger, basePrice));
    }

    @Test
    void BasePriceWithAge70WitDiscountCard_ShouldReturn60e() {
        double basePrice = 100.00;
        Passenger passenger = new PassengerBuilder()
                .withDiscount(DiscountCard.SENIOR)
                .age(71)
                .build();

        assertEquals(80, priceModifier.applyingAgeModifierOnPrice(passenger, basePrice));
    }
}
