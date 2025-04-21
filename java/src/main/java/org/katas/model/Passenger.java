package org.katas.model;

import org.katas.exceptions.InvalidTripInputException;

import java.util.List;

public record Passenger(int age, List<DiscountCard> discounts) {
    public double applyingAgeModifierOnPrice(double basePrice) {
        double temp;
        // Tarification selon l'âge
        if (age < 1) {
            temp = 0;
        } else if (age < 4){
            temp = 9;
        } else if (!isMajor()) {
            temp = basePrice * 0.6;
        } else if (isSenior()) {
            temp = basePrice * 0.8;
            if (discounts().contains(DiscountCard.Senior)) {
                temp -= basePrice * 0.2;
            }
        } else {
            temp = basePrice * 1.2;
        }
        return temp;
    }

    public boolean isMajor() {
        return age >= 18;
    }
    public boolean isSenior() {
        return age >= 70;
    }

    public void isValid() {
        if (age < 0) {
            throw new InvalidTripInputException("Age is invalid");
        }
    }
}
