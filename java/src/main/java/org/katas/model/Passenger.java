package org.katas.model;

import org.katas.exceptions.InvalidTripInputException;

import java.util.List;

public record Passenger(int age, List<DiscountCard> discounts) {

    public double applyingAgeModifierOnPrice(double basePrice) {
        double temp;
        // Tarification selon l'âge
        if (isInfant()) {
            temp = 0;
        } else if (isToddler()){
            temp = 9;
        } else if (!isMajor()) {
            temp = basePrice * 0.6;
        } else if (isSenior()) {
            temp = basePrice * 0.8;
        } else {
            temp = basePrice * 1.2;
        }
        return temp;
    }

    public boolean isInfant() {
        return age < 1;
    }

    public boolean isToddler() {
        return age >= 1 && age <= 3 ;
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
